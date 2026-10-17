
"use strict";

const botonAnalizar = document.getElementById("btn-analizar-imagen");
const resultadoAnalisis = document.getElementById("resultado-analisis-imagen");
const campoImagen = document.querySelector('input[name="imagen"]');

if (botonAnalizar && resultadoAnalisis && campoImagen) {

    function mostrarResultado(mensaje, estado = "") {
        resultadoAnalisis.textContent = mensaje;
        resultadoAnalisis.className =
            "image-analyze-message " + estado;
    }

    campoImagen.addEventListener("change", () => {
        mostrarResultado("");
    });

    botonAnalizar.addEventListener("click", async () => {

        const archivo = campoImagen.files?.[0];

        if (!archivo) {
            mostrarResultado(
                "Selecciona una imagen antes de analizarla.",
                "error"
            );
            return;
        }

        const datos = new FormData();
        datos.append("imagen", archivo);

        botonAnalizar.disabled = true;
        mostrarResultado("Validando imagen...", "processing");

        try {
            const respuesta = await fetch("/imagenes/analizar", {
                method: "POST",
                body: datos,
                credentials: "same-origin",
                headers: {
                    Accept: "application/json"
                }
            });

            const tipoRespuesta =
                respuesta.headers.get("content-type") || "";

            if (!tipoRespuesta.includes("application/json")) {
                throw new Error(
                    "El servidor no devolvió una respuesta JSON."
                );
            }

            const resultado = await respuesta.json();

            if (!respuesta.ok) {
                mostrarResultado(
                    resultado.mensaje ||
                    "No se pudo validar la imagen.",
                    "error"
                );
                return;
            }

            mostrarResultado(
                "Imagen validada. Formato: " +
                resultado.formato +
                ". Lista para un análisis posterior.",
                "success"
            );

        } catch (error) {
            console.error("Error al validar imagen:", error);

            mostrarResultado(
                "No se pudo completar la validación. " +
                "Puedes continuar con el registro manual.",
                "error"
            );

        } finally {
            botonAnalizar.disabled = false;
        }
    });
}
