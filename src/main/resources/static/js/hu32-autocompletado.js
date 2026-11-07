"use strict";

(() => {
    const campos = {
        nombre: document.querySelector('input[name="nombre"]'),
        categoria: document.querySelector('select[name="categoria"]'),
        descripcion: document.querySelector('textarea[name="descripcion"]')
    };
    const etiquetas = {nombre: "Título", categoria: "Categoría", descripcion: "Descripción"};
    const imagen = document.querySelector('input[name="imagen"]');
    const panel = document.getElementById("hu32-panel");
    const mensaje = document.getElementById("hu32-mensaje");
    const lista = document.getElementById("hu32-lista");
    const botonDeshacer = document.getElementById("hu32-deshacer");

    if (!campos.nombre || !campos.categoria || !campos.descripcion || !imagen
            || !panel || !mensaje || !lista || !botonDeshacer) {
        return;
    }

    let version = 0;
    // Solo guarda los campos que todavía muestran el valor sugerido (sin editar por el usuario).
    const autocompletados = new Map();

    function quitarMarca(nombre) {
        const registro = autocompletados.get(nombre);
        if (!registro) return;
        registro.insignia.remove();
        campos[nombre].classList.remove("hu32-sugerido");
        autocompletados.delete(nombre);
        botonDeshacer.hidden = autocompletados.size === 0;
    }

    function marcar(nombre, anterior, sugerido) {
        const insignia = document.createElement("span");
        insignia.className = "hu32-insignia";
        insignia.textContent = "Sugerido por IA";
        campos[nombre].insertAdjacentElement("beforebegin", insignia);
        campos[nombre].classList.add("hu32-sugerido");
        autocompletados.set(nombre, {anterior, sugerido, insignia});
        botonDeshacer.hidden = false;
    }

    function restaurarSinEditar() {
        const pendientes = Array.from(autocompletados.entries());
        pendientes.forEach(([nombre, registro]) => {
            quitarMarca(nombre);
            campos[nombre].value = registro.anterior;
        });
        return pendientes.length;
    }

    function mostrarPanel(texto, items) {
        mensaje.textContent = texto;
        lista.replaceChildren();
        items.forEach(item => {
            const elemento = document.createElement("li");
            elemento.textContent = item;
            lista.appendChild(elemento);
        });
        botonDeshacer.hidden = autocompletados.size === 0;
        panel.hidden = false;
    }

    function ocultarPanel() {
        panel.hidden = true;
        mensaje.textContent = "";
        lista.replaceChildren();
    }

    function textoVisible(nombre, valor) {
        if (nombre !== "categoria") return valor;
        const opcion = Array.from(campos.categoria.options).find(o => o.value === valor);
        return opcion ? opcion.textContent.trim() : valor;
    }

    function aplicar(resultado) {
        const sugeridos = resultado && resultado.estado === "AUTOCOMPLETADO" && resultado.campos
            ? resultado.campos : {};
        const completados = [];
        const conservados = [];

        Object.keys(campos).forEach(nombre => {
            const valor = sugeridos[nombre];
            if (typeof valor !== "string" || valor === "") return;
            if (nombre === "categoria"
                    && !Array.from(campos.categoria.options).some(o => o.value === valor)) {
                return;
            }

            // Lo que el usuario ya escribió o eligió prevalece sobre la sugerencia.
            const registro = autocompletados.get(nombre);
            if (!registro && campos[nombre].value.trim() !== "") {
                conservados.push(etiquetas[nombre]);
                return;
            }

            const anterior = registro ? registro.anterior : campos[nombre].value;
            quitarMarca(nombre);
            campos[nombre].value = valor;
            marcar(nombre, anterior, valor);
            completados.push(etiquetas[nombre] + ": " + textoVisible(nombre, valor));
        });

        const items = completados.slice();
        if (conservados.length > 0) {
            items.push("Se conservó lo que ya habías ingresado en: " + conservados.join(", ") + ".");
        }

        if (completados.length > 0) {
            mostrarPanel("Se completaron solo los campos respaldados por la imagen. Revísalos y corrígelos "
                    + "si hace falta antes de registrar. La ubicación debes indicarla tú.", items);
        } else if (conservados.length > 0) {
            mostrarPanel("No se modificó ningún campo porque ya los habías completado.", items);
        } else {
            mostrarPanel((resultado && resultado.mensaje)
                    || "El análisis no respalda ningún campo. Completa el formulario manualmente.", []);
        }
    }

    // Editar un campo sugerido lo convierte en un valor confirmado por el usuario.
    Object.keys(campos).forEach(nombre => {
        const alEditar = () => {
            const registro = autocompletados.get(nombre);
            if (registro && campos[nombre].value !== registro.sugerido) {
                quitarMarca(nombre);
            }
        };
        campos[nombre].addEventListener("input", alEditar);
        campos[nombre].addEventListener("change", alEditar);
    });

    // Las sugerencias provienen de la foto anterior: se retiran las que nadie confirmó editándolas.
    imagen.addEventListener("change", () => {
        version++;
        if (restaurarSinEditar() > 0) {
            mostrarPanel("Cambiaste la fotografía: se retiraron las sugerencias anteriores "
                    + "que no habías modificado.", []);
        } else {
            ocultarPanel();
        }
    });

    document.addEventListener("ufound:imagen-analizada", async (evento) => {
        const analisis = evento.detail;
        const versionActual = version;
        if (!analisis || analisis.estado !== "SUGERENCIA") {
            return;
        }

        try {
            const respuesta = await fetch("/hu32/autocompletar", {
                method: "POST",
                credentials: "same-origin",
                headers: {"Content-Type": "application/json", "Accept": "application/json"},
                body: JSON.stringify({
                    estado: analisis.estado,
                    tipoProbable: analisis.tipoProbable,
                    colorProbable: analisis.colorProbable
                })
            });
            if (versionActual !== version) return;
            if (!respuesta.ok) throw new Error("Error consultando el autocompletado");
            const resultado = await respuesta.json();
            if (versionActual !== version) return;
            aplicar(resultado);
        } catch (error) {
            // El autocompletado es una ayuda: si falla, el registro manual sigue disponible.
            console.warn("No se pudo autocompletar el formulario.", error);
        }
    });

    botonDeshacer.addEventListener("click", () => {
        restaurarSinEditar();
        mostrarPanel("Se restauraron los valores que tenías antes del autocompletado.", []);
    });
})();
