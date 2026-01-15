# Casos de Uso: Descuento de Inventario No Vendido

A continuación, se describen los casos de uso para la nueva funcionalidad de "Descuento de Inventario No Vendido", utilizando el formato especificado.

---

### Caso de Uso 1: Registrar Producto Vencido

**Nombre del Caso (obligatorio)**
Registrar la pérdida de 20 arepas por vencimiento.

**Contexto Funcional**
El sistema debe permitir dar de baja productos que han superado su fecha de caducidad y ya no son aptos para la venta. Estos productos deben descontarse del inventario para mantener la consistencia de los datos.

**Escenario**
Un empleado del inventario revisa el stock de arepas y detecta que 20 unidades se han vencido y deben ser desechadas. Procede a registrar esta pérdida en el sistema.

**Pasos detallados de prueba (obligatorio)**
1.  **Paso 1:** El usuario navega al nuevo módulo "Descuento de Inventario No Vendido" en la aplicación.
    *   *(Se adjuntaría imagen del menú de navegación)*
2.  **Paso 2:** En el formulario de registro, selecciona el producto "Arepa" de la lista de productos.
3.  **Paso 3:** En el campo "Cantidad", ingresa el valor `20`.
4.  **Paso 4:** En el campo "Motivo", selecciona la opción "Vencido".
5.  **Paso 5:** En el campo "Registrado por", ingresa su nombre de usuario (ej: "juan.perez").
6.  **Paso 6:** En el campo "Observaciones", añade el texto: "Arepas del lote #12345, se botaron a la basura".
    *   *(Se adjuntaría imagen del formulario completo antes de enviar)*
7.  **Paso 7:** El usuario hace clic en el botón "Registrar".

**Resultado Esperado (obligatorio)**
*   El sistema muestra un mensaje de confirmación indicando que el registro se ha guardado correctamente.
*   El nuevo registro aparece en la tabla del historial de consumos, mostrando todos los datos ingresados.
*   El stock del producto "Arepa" en el inventario general se ha reducido en 20 unidades.

---

### Caso de Uso 2: Registrar Almuerzo de Empleados

**Nombre del Caso (obligatorio)**
Registrar consumo de productos para almuerzo de empleados.

**Contexto Funcional**
El negocio ofrece como beneficio a sus empleados el almuerzo, utilizando productos del inventario. Este consumo debe registrarse para justificar la salida de stock que no corresponde a una venta.

**Escenario**
El encargado de cocina prepara hamburguesas para 4 empleados. Utiliza 4 panes de hamburguesa, 4 porciones de carne, y 4 bebidas (Coca-Cola 400ml). Debe registrar estos productos como "Beneficio para empleados".

**Pasos detallados de prueba (obligatorio)**
1.  **Paso 1:** El usuario accede al módulo "Descuento de Inventario No Vendido".
2.  **Paso 2:** Realiza el primer registro para los alimentos:
    *   Selecciona el producto "Pan de hamburguesa".
    *   Ingresa "Cantidad": `4`.
    *   Selecciona "Motivo": "Beneficio empleados - Alimento".
    *   Ingresa "Registrado por": "cocina_user".
    *   Añade "Observaciones": "Almuerzo del día Jueves".
    *   Hace clic en "Registrar".
3.  **Paso 3:** Repite el proceso para la carne:
    *   Selecciona "Carne de hamburguesa".
    *   Ingresa "Cantidad": `4`.
    *   Mismo motivo, usuario y observaciones.
    *   Hace clic en "Registrar".
4.  **Paso 4:** Realiza un tercer registro para las bebidas:
    *   Selecciona el producto "Coca-Cola 400ml".
    *   Ingresa "Cantidad": `4`.
    *   Selecciona "Motivo": "Beneficio empleados - Bebida".
    *   Mismo usuario y observaciones.
    *   Hace clic en "Registrar".

**Resultado Esperado (obligatorio)**
*   El sistema guarda exitosamente los tres registros de consumo.
*   El historial de consumos muestra tres nuevas entradas, una por cada producto, con los motivos correspondientes ("Beneficio empleados - Alimento" y "Beneficio empleados - Bebida").
*   El inventario se actualiza automáticamente:
    *   El stock de "Pan de hamburguesa" se reduce en 4.
    *   El stock de "Carne de hamburguesa" se reduce en 4.
    *   El stock de "Coca-Cola 400ml" se reduce en 4.

---

### Caso de Uso 3: Registrar Producto Dañado

**Nombre del Caso (obligatorio)**
Registrar yucas dañadas que no se pueden reutilizar.

**Contexto Funcional**
Algunos productos, como las yucas fritas, no pueden guardarse para el día siguiente porque su calidad se degrada. Los productos que se desechan al final del día por esta razón deben ser registrados como pérdida.

**Escenario**
Al final de la jornada, sobran 1.5 kg de yuca frita que no se vendieron y no se pueden guardar. El encargado de cierre de día debe registrar esta pérdida.

**Pasos detallados de prueba (obligatorio)**
1.  **Paso 1:** El usuario ingresa a la pantalla de "Descuento de Inventario No Vendido".
2.  **Paso 2:** Selecciona el producto "Yuca frita" de la lista.
3.  **Paso 3:** Ingresa la "Cantidad": `1.5`. (Este paso valida que se pueden usar decimales).
4.  **Paso 4:** Selecciona el "Motivo": "Dañado/Perdido".
5.  **Paso 5:** Ingresa su usuario en "Registrado por".
6.  **Paso 6:** En "Observaciones", escribe: "Sobrante del final del día, no se puede recalentar".
7.  **Paso 7:** Presiona el botón "Registrar".

**Resultado Esperado (obligatorio)**
*   El sistema procesa y guarda el registro de consumo sin errores.
*   El historial de consumos muestra el registro de la yuca frita con la cantidad `1.5` y el motivo "Dañado/Perdido".
*   El stock del producto "Yuca frita" se reduce en 1.5 unidades, reflejando la pérdida real.
