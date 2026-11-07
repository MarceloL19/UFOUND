"use strict";

const botonAnalizar = document.getElementById("btn-analizar-imagen");
const resultadoAnalisis = document.getElementById("resultado-analisis-imagen");
const campoImagen = document.querySelector('input[name="imagen"]');

if (botonAnalizar && resultadoAnalisis && campoImagen) {

    // Se invalida tanto al cambiar de foto como al repetir el análisis de la misma foto.
    let versionImagen = 0;

    function mostrarResultado(mensaje, estado = "") {
        resultadoAnalisis.textContent = mensaje;
        resultadoAnalisis.className = "image-analyze-message " + estado;
    }

    function notificarInicio() {
        document.dispatchEvent(new CustomEvent("ufound:imagen-analisis-iniciado"));
    }

    function notificarResultado(resultado) {
        // HU-31 también necesita conocer los errores y los resultados sin detección.
        document.dispatchEvent(new CustomEvent("ufound:imagen-analizada", {detail: resultado}));
    }

    campoImagen.addEventListener("change", () => {
        versionImagen++;
        // Si cambió la foto durante una solicitud, se puede analizar la nueva.
        // El finally de la solicitud anterior no debe interferir con la nueva.
        botonAnalizar.disabled = false;
        notificarInicio();
        mostrarResultado("");
    });

    botonAnalizar.addEventListener("click", async () => {
        // Primero se retira cualquier sugerencia anterior, aunque no haya archivo.
        const versionSolicitud = ++versionImagen;
        notificarInicio();

        const archivo = campoImagen.files?.[0];
        if (!archivo) {
            const mensaje = "Selecciona una imagen antes de analizarla.";
            mostrarResultado(mensaje, "error");
            notificarResultado({estado: "ERROR", mensaje});
            return;
        }

        const datos = new FormData();
        datos.append("imagen", archivo);

        botonAnalizar.disabled = true;
        mostrarResultado("Validando y analizando imagen...", "processing");

        try {
            const respuesta = await fetch("/imagenes/analizar", {
                method: "POST",
                body: datos,
                credentials: "same-origin",
                headers: {Accept: "application/json"}
            });
            if (versionSolicitud !== versionImagen) return;

            const tipoRespuesta = respuesta.headers.get("content-type") || "";
            if (!tipoRespuesta.includes("application/json")) {
                throw new Error("El servidor no devolvió una respuesta JSON.");
            }

            const resultado = await respuesta.json();
            if (versionSolicitud !== versionImagen) return;

            if (!respuesta.ok) {
                if (respuesta.status === 400) {
                    campoImagen.value = "";
                }
                const mensaje = resultado.mensaje ||
                    "No se pudo analizar la imagen. Puedes continuar con el registro manual.";
                notificarResultado({estado: "ERROR", mensaje});
                mostrarResultado(mensaje, "error");
                return;
            }

            // Comunicar también SIN_DETECCION; HU-31 descarta la propuesta previa.
            notificarResultado(resultado);
            if (resultado.estado === "SUGERENCIA" && resultado.tipoProbable) {
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
            // Una respuesta tardía de una solicitud anterior nunca cambia la interfaz.
            if (versionSolicitud !== versionImagen) return;
            console.error("Error al analizar imagen:", error);
            const mensaje =
                "No se pudo completar el análisis. Puedes continuar con el registro manual.";
            notificarResultado({estado: "ERROR", mensaje});
            mostrarResultado(mensaje, "error");
        } finally {
            if (versionSolicitud === versionImagen) {
                botonAnalizar.disabled = false;
            }
        }
    });
}
