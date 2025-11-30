package org.blackequity.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AdminPasswordValidator {

    private static final Logger logger = LoggerFactory.getLogger(AdminPasswordValidator.class);

    @ConfigProperty(name = "coupon.admin.password")
    String adminPassword;

    /**
     * Valida si la contraseña proporcionada es correcta
     */
    public boolean isValidAdminPassword(String providedPassword) {
        if (providedPassword == null || providedPassword.trim().isEmpty()) {
            logger.warn("Intento de acceso admin sin contraseña");
            return false;
        }

        boolean isValid = adminPassword.equals(providedPassword);

        if (!isValid) {
            logger.warn("Intento de acceso admin con contraseña incorrecta");
        } else {
            logger.info("Acceso admin validado exitosamente");
        }

        return isValid;
    }

    /**
     * Valida y lanza excepción si la contraseña es incorrecta
     */
    public void validateAdminPasswordOrThrow(String providedPassword) {
        if (!isValidAdminPassword(providedPassword)) {
            throw new AdminPasswordException("Contraseña de administrador incorrecta o ausente");
        }
    }
}
