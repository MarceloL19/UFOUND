
"use strict";

const botonAnalizar = document.getElementById("btn-analizar-imagen");
const resultadoAnalisis = document.getElementById("resultado-analisis-imagen");
const campoImagen = document.querySelector('input[name="imagen"]');

if (botonAnalizar && resultadoAnalisis && campoImagen) {

    let versionImagen = 0;

    function mostrarResultado(mensaje, estado = "") {
        resultadoAnalisis.textContent = mensaje;
        resultadoAnalisis.className =
            "image-analyze-message " + estado;
    }

    campoImagen.addEventListener("change", () => {
        versionImagen++;
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
        const versionSolicitud = versionImagen;

        botonAnalizar.disabled = true;
        mostrarResultado("Validando y analizando imagen...", "processing");

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
            if (versionSolicitud !== versionImagen) {
                return;
            }

            if (!respuesta.ok) {
                if (respuesta.status === 400) {
                    campoImagen.value = "";
                }

                mostrarResultado(
                    resultado.mensaje ||
                    "No se pudo analizar la imagen. Puedes continuar con el registro manual.",
                    "error"
                );

                return;
            }

            if (resultado.estado === "SUGERENCIA") {
                // HU-31 consume la detección ya realizada; no vuelve a ejecutar el modelo.
                document.dispatchEvent(new CustomEvent("ufound:imagen-analizada", {detail: resultado}));
                const confianza = Math.round(resultado.confianza * 100);
                const color = resultado.colorProbable || "no determinado";
                mostrarResultado(
                    "Sugerencia de IA: tipo " + resultado.tipoProbable +
                    ", color " + color +
                    ". Confianza orientativa del detector: " + confianza +
                    "%. Confirma o corrige la información en el formulario antes de registrar.",
                    "success"
                );
            } else {
                mostrarResultado(
                    resultado.mensaje ||
                    "No hubo detección suficiente. Completa el registro manualmente.",
                    "processing"
                );
            }

        } catch (error) {
            console.error("Error al analizar imagen:", error);
            if (versionSolicitud !== versionImagen) {
                return;
            }

            mostrarResultado(
                "No se pudo completar el análisis. Puedes continuar con el registro manual.",
                "error"
            );

        } finally {
            botonAnalizar.disabled = false;
        }
    });
}
