"use strict";

(() => {
    const categoria = document.querySelector('select[name="categoria"]');
    const panel = document.getElementById("hu31-panel");
    const mensaje = document.getElementById("hu31-mensaje");
    const botonAplicar = document.getElementById("hu31-aplicar");
    const imagen = document.querySelector('input[name="imagen"]');

    if (!categoria || !panel || !mensaje || !botonAplicar || !imagen) {
        return;
    }

    let version = 0;
    let propuesta = null;

    function borrarPropuesta() {
        propuesta = null;
        panel.hidden = true;
        botonAplicar.disabled = true;
        mensaje.textContent = "";
    }

    // Una fotografía nueva invalida las sugerencias pendientes de la anterior.
    imagen.addEventListener("change", () => {
        version++;
        borrarPropuesta();
    });

    document.addEventListener("ufound:imagen-analizada", async (evento) => {
        const analisis = evento.detail;
        const versionActual = version;
        borrarPropuesta();
        if (!analisis || analisis.estado !== "SUGERENCIA" || !analisis.tipoProbable) {
            return;
        }

        try {
            const respuesta = await fetch("/hu31/sugerir-categoria", {
                method: "POST",
                credentials: "same-origin",
                headers: {"Content-Type": "application/json", "Accept": "application/json"},
                body: JSON.stringify({estado: analisis.estado, tipoProbable: analisis.tipoProbable})
            });
            if (versionActual !== version) return;
            if (!respuesta.ok) throw new Error("Error consultando categoría");
            const resultado = await respuesta.json();
            if (versionActual !== version) return;
            if (resultado.estado !== "SUGERENCIA" || !resultado.categoria) return;

            // Solo se ofrece una categoría realmente presente en el select del catálogo.
            const disponible = Array.from(categoria.options)
                    .some(opcion => opcion.value === resultado.categoria);
            if (!disponible) return;

            propuesta = resultado.categoria;
            mensaje.textContent = "HU-31 sugiere: " + resultado.etiqueta +
                    ". Puedes usarla o elegir otra categoría manualmente.";
            panel.hidden = false;
            botonAplicar.disabled = false;
        } catch (error) {
            // HU-31 es opcional: nunca bloquea el registro manual ni altera HU-30.
            console.warn("No se pudo obtener la categoría sugerida.", error);
        }
    });

    botonAplicar.addEventListener("click", () => {
        if (!propuesta) return;
        categoria.value = propuesta;
        categoria.dispatchEvent(new Event("change", {bubbles: true}));
        mensaje.textContent = "Categoría aplicada. Puedes cambiarla antes de registrar.";
        // No se agrega ningún campo oculto: se envía exclusivamente el select original.
    });
})();
