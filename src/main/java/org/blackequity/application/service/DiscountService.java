package org.blackequity.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.blackequity.domain.dto.ApplyDiscountCommand;
import org.blackequity.domain.dto.ApplyDiscountResult;
import org.blackequity.domain.dto.LinkOrderCouponCommand;
import org.blackequity.domain.dto.OrderItemDto;
import org.blackequity.domain.enums.AppliesToType;
import org.blackequity.domain.enums.DiscountType;
import org.blackequity.domain.model.DiscountCouponEntity;
import org.blackequity.domain.model.DiscountUsageEntity;
import org.blackequity.domain.repository.discount.DiscountCouponRepository;
import org.blackequity.domain.repository.discount.DiscountUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class DiscountService {

    private static final Logger logger = LoggerFactory.getLogger(DiscountService.class);

    @Inject
    DiscountCouponRepository couponRepository;

    @Inject
    DiscountUsageRepository usageRepository;

    @Inject
    AdminPasswordValidator passwordValidator;

    /**
     * Aplica un cupón de descuento a una orden (previsualización/cálculo)
     */
    public ApplyDiscountResult applyDiscount(ApplyDiscountCommand command) {
        logger.info("Aplicando cupón: {}", command.getCode());

        // Validaciones básicas
        if (command.getCode() == null || command.getCode().trim().isEmpty()) {
            return createInvalidResult("El código del cupón es requerido");
        }

        if (command.getSubtotal() == null || command.getSubtotal().compareTo(BigDecimal.ZERO) <= 0) {
            return createInvalidResult("El subtotal debe ser mayor a cero");
        }

        // Buscar cupón (case insensitive)
        Optional<DiscountCouponEntity> couponOpt = couponRepository.findByCodeIgnoreCase(command.getCode());
        if (couponOpt.isEmpty()) {
            return createInvalidResult("El cupón no existe");
        }

        DiscountCouponEntity coupon = couponOpt.get();

        // Validar que esté activo
        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            return createInvalidResult("El cupón no está activo");
        }

        // Validar fechas
        LocalDate orderDate = command.getOrderDateTime() != null
            ? command.getOrderDateTime().toLocalDate()
            : LocalDate.now();

        if (coupon.getValidFrom() != null && orderDate.isBefore(coupon.getValidFrom())) {
            return createInvalidResult("El cupón aún no es válido. Válido desde: " + coupon.getValidFrom());
        }

        if (coupon.getValidTo() != null && orderDate.isAfter(coupon.getValidTo())) {
            return createInvalidResult("El cupón ha expirado. Válido hasta: " + coupon.getValidTo());
        }

        // Validar día de la semana
        if (coupon.getValidWeekdays() != null && !coupon.getValidWeekdays().trim().isEmpty()) {
            DayOfWeek currentDay = command.getOrderDateTime() != null
                ? command.getOrderDateTime().getDayOfWeek()
                : LocalDateTime.now().getDayOfWeek();

            List<String> validDays = Arrays.stream(coupon.getValidWeekdays().split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

            String currentDayStr = currentDay.toString().substring(0, 3); // MON, TUE, etc.

            if (!validDays.contains(currentDayStr)) {
                return createInvalidResult("El cupón no es válido para " + currentDay.toString());
            }
        }

        // Calcular base del descuento según applies_to_type
        BigDecimal baseAmount = calculateBaseAmount(coupon, command.getItems(), command.getSubtotal());

        if (baseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            String message = getAppliesErrorMessage(coupon);
            return createInvalidResult(message);
        }

        // Calcular monto del descuento
        BigDecimal discountAmount = calculateDiscountAmount(coupon, baseAmount);

        // Calcular nuevo subtotal
        BigDecimal newSubtotal = command.getSubtotal().subtract(discountAmount);
        if (newSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            newSubtotal = BigDecimal.ZERO;
        }

        // Crear mensaje descriptivo
        String message = createSuccessMessage(coupon, discountAmount);

        // Retornar resultado exitoso
        ApplyDiscountResult result = new ApplyDiscountResult();
        result.setValid(true);
        result.setDiscountCode(coupon.getCode());
        result.setDiscountType(coupon.getDiscountType());
        result.setDiscountValue(coupon.getDiscountValue());
        result.setDiscountAmount(discountAmount);
        result.setNewSubtotal(newSubtotal);
        result.setMessage(message);

        logger.info("Cupón aplicado exitosamente. Descuento: ${}", discountAmount);
        return result;
    }

    /**
     * Registra que una orden usó un cupón
     */
    @Transactional
    public void linkOrderWithCoupon(LinkOrderCouponCommand command) {
        logger.info("Registrando uso de cupón {} para orden {}", command.getCode(), command.getOrderId());

        // Validaciones
        if (command.getOrderId() == null) {
            throw new IllegalArgumentException("El ID de la orden es requerido");
        }

        if (command.getCode() == null || command.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del cupón es requerido");
        }

        // Buscar cupón
        Optional<DiscountCouponEntity> couponOpt = couponRepository.findByCodeIgnoreCase(command.getCode());
        if (couponOpt.isEmpty()) {
            throw new IllegalArgumentException("El cupón no existe: " + command.getCode());
        }

        DiscountCouponEntity coupon = couponOpt.get();

        // Verificar si ya existe un uso para esta combinación orden-cupón
        Optional<DiscountUsageEntity> existingUsage =
            usageRepository.findByOrderIdAndCouponId(command.getOrderId(), coupon.getId());

        if (existingUsage.isPresent()) {
            logger.warn("Ya existe un uso del cupón {} para la orden {}", command.getCode(), command.getOrderId());
            throw new IllegalStateException("El cupón ya fue aplicado a esta orden");
        }

        // Crear nuevo registro de uso
        DiscountUsageEntity usage = new DiscountUsageEntity();
        usage.setOrderId(command.getOrderId());
        usage.setCoupon(coupon);
        usage.setDiscountCode(coupon.getCode());
        usage.setSubtotalBeforeDiscount(command.getSubtotalBeforeDiscount());
        usage.setDiscountAmount(command.getDiscountAmount());
        usage.setTotalAfterDiscount(command.getTotalAfterDiscount());

        usageRepository.save(usage);

        logger.info("Uso de cupón registrado exitosamente para orden {}", command.getOrderId());
    }

    // ========== Métodos auxiliares ==========

    private BigDecimal calculateBaseAmount(DiscountCouponEntity coupon, List<OrderItemDto> items, BigDecimal subtotal) {
        if (coupon.getAppliesToType() == AppliesToType.ORDER) {
            // Se aplica al total de la orden
            return subtotal;
        }

        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (coupon.getAppliesToType() == AppliesToType.PRODUCT) {
            // Se aplica solo a items con el productId especificado
            return items.stream()
                .filter(item -> coupon.getAppliesToId() != null &&
                               coupon.getAppliesToId().equals(item.getProductId()))
                .map(item -> item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (coupon.getAppliesToType() == AppliesToType.CATEGORY) {
            // Se aplica solo a items con la categoryId especificada
            return items.stream()
                .filter(item -> coupon.getAppliesToId() != null &&
                               coupon.getAppliesToId().equals(item.getCategoryId()))
                .map(item -> item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateDiscountAmount(DiscountCouponEntity coupon, BigDecimal baseAmount) {
        BigDecimal discountAmount;

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            // Descuento porcentual
            discountAmount = baseAmount
                .multiply(coupon.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // Descuento fijo (FIXED)
            discountAmount = coupon.getDiscountValue();
            // No puede descontar más de lo que cuesta
            if (discountAmount.compareTo(baseAmount) > 0) {
                discountAmount = baseAmount;
            }
        }

        return discountAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private String createSuccessMessage(DiscountCouponEntity coupon, BigDecimal discountAmount) {
        StringBuilder msg = new StringBuilder();
        msg.append("Se aplicó el cupón ").append(coupon.getCode().toUpperCase()).append(": ");

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            msg.append(coupon.getDiscountValue()).append("% de descuento");
        } else {
            msg.append("$").append(coupon.getDiscountValue()).append(" de descuento");
        }

        if (coupon.getAppliesToType() == AppliesToType.PRODUCT) {
            msg.append(" en producto específico");
        } else if (coupon.getAppliesToType() == AppliesToType.CATEGORY) {
            msg.append(" en categoría específica");
        } else {
            msg.append(" en toda la orden");
        }

        msg.append(". Descuento total: $").append(discountAmount);

        if (coupon.getName() != null && !coupon.getName().isEmpty()) {
            msg.append(" (").append(coupon.getName()).append(")");
        }

        return msg.toString();
    }

    private String getAppliesErrorMessage(DiscountCouponEntity coupon) {
        if (coupon.getAppliesToType() == AppliesToType.PRODUCT) {
            return "El cupón no aplica: no hay productos elegibles en la orden (producto ID: " +
                   coupon.getAppliesToId() + ")";
        } else if (coupon.getAppliesToType() == AppliesToType.CATEGORY) {
            return "El cupón no aplica: no hay productos de la categoría elegible en la orden (categoría ID: " +
                   coupon.getAppliesToId() + ")";
        }
        return "El cupón no aplica a esta orden";
    }

    private ApplyDiscountResult createInvalidResult(String message) {
        ApplyDiscountResult result = new ApplyDiscountResult();
        result.setValid(false);
        result.setMessage(message);
        result.setDiscountAmount(BigDecimal.ZERO);
        result.setNewSubtotal(BigDecimal.ZERO);
        return result;
    }

    // ========== Métodos de administración ==========

    /**
     * Obtiene todos los cupones activos y vigentes (sin password)
     */
    public List<DiscountCouponEntity> getActiveCoupons() {
        logger.debug("Obteniendo cupones activos");

        LocalDate today = LocalDate.now();

        return couponRepository.findAllActive().stream()
            .filter(coupon -> {
                // Filtrar cupones vencidos
                if (coupon.getValidTo() != null && today.isAfter(coupon.getValidTo())) {
                    return false;
                }
                // Filtrar cupones que aún no son válidos
                if (coupon.getValidFrom() != null && today.isBefore(coupon.getValidFrom())) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo cupón (requiere password admin)
     */
    @Transactional
    public DiscountCouponEntity createCoupon(String adminPassword, DiscountCouponEntity coupon) {
        logger.info("Creando nuevo cupón: {}", coupon.getCode());

        // Validar password de administrador
        passwordValidator.validateAdminPasswordOrThrow(adminPassword);

        // Validar que el código no exista
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new IllegalArgumentException("Ya existe un cupón con el código: " + coupon.getCode());
        }

        // Validaciones de negocio
        validateCouponData(coupon);

        // Guardar
        return couponRepository.save(coupon);
    }

    /**
     * Actualiza un cupón existente (requiere password admin)
     */
    @Transactional
    public DiscountCouponEntity updateCoupon(String adminPassword, Long id, DiscountCouponEntity updatedData) {
        logger.info("Actualizando cupón ID: {}", id);

        // Validar password de administrador
        passwordValidator.validateAdminPasswordOrThrow(adminPassword);

        // Buscar cupón existente
        DiscountCouponEntity existing = couponRepository.findCouponById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cupón no encontrado con ID: " + id));

        // Actualizar campos
        existing.setCode(updatedData.getCode());
        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setDiscountType(updatedData.getDiscountType());
        existing.setDiscountValue(updatedData.getDiscountValue());
        existing.setAppliesToType(updatedData.getAppliesToType());
        existing.setAppliesToId(updatedData.getAppliesToId());
        existing.setValidFrom(updatedData.getValidFrom());
        existing.setValidTo(updatedData.getValidTo());
        existing.setValidWeekdays(updatedData.getValidWeekdays());
        existing.setIsActive(updatedData.getIsActive());

        // Validaciones de negocio
        validateCouponData(existing);

        // Guardar
        return couponRepository.save(existing);
    }

    /**
     * Desactiva un cupón (requiere password admin)
     */
    @Transactional
    public DiscountCouponEntity deactivateCoupon(String adminPassword, Long id) {
        logger.info("Desactivando cupón ID: {}", id);

        // Validar password de administrador
        passwordValidator.validateAdminPasswordOrThrow(adminPassword);

        // Buscar cupón
        DiscountCouponEntity coupon = couponRepository.findCouponById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cupón no encontrado con ID: " + id));

        // Desactivar
        coupon.setIsActive(false);

        return couponRepository.save(coupon);
    }

    /**
     * Lista todos los cupones con filtros opcionales (requiere password admin)
     */
    public List<DiscountCouponEntity> listAllCoupons(String adminPassword, String status) {
        logger.info("Listando cupones con filtro: {}", status);

        // Validar password de administrador
        passwordValidator.validateAdminPasswordOrThrow(adminPassword);

        if (status == null || status.equalsIgnoreCase("all")) {
            return couponRepository.findAllCoupons();
        }

        if (status.equalsIgnoreCase("active")) {
            return couponRepository.findByActiveStatus(true);
        }

        if (status.equalsIgnoreCase("inactive")) {
            return couponRepository.findByActiveStatus(false);
        }

        if (status.equalsIgnoreCase("expired")) {
            LocalDate today = LocalDate.now();
            return couponRepository.findAllCoupons().stream()
                .filter(coupon -> coupon.getValidTo() != null && today.isAfter(coupon.getValidTo()))
                .collect(Collectors.toList());
        }

        return couponRepository.findAllCoupons();
    }

    /**
     * Valida los datos de un cupón
     */
    private void validateCouponData(DiscountCouponEntity coupon) {
        if (coupon.getCode() == null || coupon.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del cupón es requerido");
        }

        if (coupon.getName() == null || coupon.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cupón es requerido");
        }

        if (coupon.getDiscountType() == null) {
            throw new IllegalArgumentException("El tipo de descuento es requerido");
        }

        if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor del descuento debe ser mayor a cero");
        }

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE &&
            coupon.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("El descuento porcentual no puede ser mayor al 100%");
        }

        if (coupon.getAppliesToType() == null) {
            throw new IllegalArgumentException("El tipo de aplicación es requerido");
        }

        if ((coupon.getAppliesToType() == AppliesToType.PRODUCT ||
             coupon.getAppliesToType() == AppliesToType.CATEGORY) &&
            coupon.getAppliesToId() == null) {
            throw new IllegalArgumentException("Debe especificar el ID del producto/categoría");
        }

        if (coupon.getValidFrom() != null && coupon.getValidTo() != null &&
            coupon.getValidFrom().isAfter(coupon.getValidTo())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }
}
