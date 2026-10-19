
"use strict";

const botonAnalizar = document.getElementById("btn-analizar-imagen");
const resultadoAnalisis = document.getElementById("resultado-analisis-imagen");
const campoImagen = document.querySelector('input[name="imagen"]');

if (botonAnalizar && resultadoAnalisis && campoImagen) {

    function limpiarImagenValid() {
        campoImagen.value = "";

    }
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
                campoImagen.value = "";

                mostrarResultado(
                    (resultado.mensaje || "No se pudo validar la imagen.") +
                    " El archivo fue retirado. Puedes continuar " +
                    "con el registro manual.",
                    "error"
                );

                return;
            }


            if (resultado.estado === "SUGERENCIA") {

                const tipo = resultado.tipoProbable || "No determinado";

                const color = resultado.colorProbable
                    ? resultado.colorProbable.toLowerCase()
                    : "No determinado";

                const confianza = Number.isFinite(resultado.confianza)
                    ? (resultado.confianza * 100).toFixed(2).replace(".", ",") + " %"
                    : "No disponible";

                mostrarResultado("", "success");

                const titulo = document.createElement("strong");
                titulo.className = "analysis-title";
                titulo.textContent = "✓ Análisis de imagen completado";

                const descripcion = document.createElement("span");
                descripcion.className = "analysis-description";
                descripcion.textContent =
                    "La inteligencia artificial ha identificado las siguientes características:";

                resultadoAnalisis.append(titulo, descripcion);

                function agregarDato(etiqueta, valor) {
                    const fila = document.createElement("span");
                    fila.className = "analysis-row";

                    const nombre = document.createElement("span");
                    nombre.className = "analysis-label";
                    nombre.textContent = etiqueta;

                    const contenido = document.createElement("strong");
                    contenido.className = "analysis-value";
                    contenido.textContent = valor;

                    fila.append(nombre, contenido);
                    resultadoAnalisis.appendChild(fila);
                }

                agregarDato("Objeto probable", tipo);
                agregarDato("Color probable", color);
                agregarDato("Confianza del modelo", confianza);

                const aviso = document.createElement("span");
                aviso.className = "analysis-notice";
                aviso.textContent =
                    "Esta información es una sugerencia automática. " +
                    "Verifica y corrige los datos antes de registrar el objeto.";

                resultadoAnalisis.appendChild(aviso);

            } else if (resultado.estado === "SIN_DETECCION") {

                mostrarResultado(
                    "La fotografía es válida, pero no se identificó " +
                    "un objeto con suficiente confianza. " +
                    "Puedes completar el registro manualmente.",
                    "processing"
                );

            } else {

                mostrarResultado(
                    resultado.mensaje ||
                    "Imagen válida. Puedes continuar con el registro manual.",
                    "processing"
                );
            }


        } catch (error) {
            console.error("Error al validar imagen:", error);

            campoImagen.value = "";

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
