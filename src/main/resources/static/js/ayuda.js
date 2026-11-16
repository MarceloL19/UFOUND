(() => {
    // Si no existe el archivo subido ni una foto predeterminada con el mismo nombre, se muestra el aviso.
    document.querySelectorAll('img[src*="/uploads/"], .search-object-image img, .found-main-image img, .confirmation-photo img').forEach((img) => {
        const mostrarAusente = () => {
            if (img.dataset.sinFoto) return;
            img.dataset.sinFoto = 'true';
            img.hidden = true;
            const aviso = document.createElement('span');
            aviso.className = 'foto-ausente';
            aviso.textContent = 'Foto no disponible';
            img.insertAdjacentElement('afterend', aviso);
        };
        img.addEventListener('error', mostrarAusente, { once: true });
        if (img.complete && img.naturalWidth === 0) mostrarAusente();
    });

    const widget = document.querySelector('.assistant-widget');
    if (!widget) return;

    const panel = widget.querySelector('.assistant-panel');
    const toggle = widget.querySelector('.assistant-toggle');
    const cerrar = widget.querySelector('.assistant-close');
    const mensajes = widget.querySelector('.assistant-messages');
    const temas = widget.querySelector('.assistant-topics');
    const formulario = widget.querySelector('.assistant-form');
    const pregunta = widget.querySelector('#assistant-question');
    const enviar = formulario.querySelector('button[type="submit"]');
    const error = widget.querySelector('.assistant-error');
    const api = widget.dataset.api;
    const llegadaApi = widget.dataset.llegadaApi;
    const clave = 'ufound:ulises:' + api + ':' + widget.dataset.usuario;
    const claveLlegada = clave + ':llegada';

    // Comparar los valores detecta tambien autocompletados y selecciones de archivos.
    function valores(form) {
        return JSON.stringify(Array.from(form.elements).filter(campo => campo.name && !campo.disabled
            && !['submit', 'button', 'hidden'].includes(campo.type)).map(campo => {
            if (campo.type === 'file') return [campo.name, Array.from(campo.files).map(f => [f.name, f.size, f.lastModified])];
            if (campo.type === 'checkbox' || campo.type === 'radio') return [campo.name, campo.checked];
            if (campo.multiple) return [campo.name, Array.from(campo.selectedOptions).map(o => o.value)];
            return [campo.name, campo.value];
        }));
    }
    const formularios = Array.from(document.forms).filter(form => form !== formulario);
    const iniciales = new Map(formularios.map(form => [form, valores(form)]));

    function guardar(clave, valor) {
        try {
            if (valor === null) sessionStorage.removeItem(clave);
            else sessionStorage.setItem(clave, valor);
            return true;
        } catch (_error) {
            return false;
        }
    }

    function leer(clave) {
        try { return sessionStorage.getItem(clave); } catch (_error) { return null; }
    }

    function mostrarError(texto) {
        error.textContent = texto;
        error.hidden = !texto;
    }

    function renderizar(estado) {
        mensajes.replaceChildren();
        estado.mensajes.forEach((mensaje) => {
            const articulo = document.createElement('article');
            articulo.className = 'assistant-message' + (mensaje.emisor === 'usuario' ? ' assistant-message-user' : '');
            if (mensaje.emisor === 'asistente' && mensaje.texto.startsWith('Abrí el apartado ')) {
                articulo.classList.add('assistant-message-arrival');
            }
            const autor = document.createElement('strong');
            autor.textContent = mensaje.emisor === 'usuario' ? 'Tú' : 'Ulises';
            const texto = document.createElement('p');
            texto.textContent = mensaje.texto;
            articulo.append(autor, texto);
            mensajes.append(articulo);
        });
        mensajes.scrollTop = mensajes.scrollHeight;

        temas.replaceChildren();
        estado.temas.forEach((tema) => {
            const boton = document.createElement('button');
            boton.type = 'button';
            boton.textContent = tema.titulo;
            boton.addEventListener('click', () => {
                pregunta.value = tema.pregunta;
                pregunta.focus();
            });
            temas.append(boton);
        });
        if (estado.preguntaSugerida && !pregunta.value) pregunta.value = estado.preguntaSugerida;
        mostrarError(estado.error || '');
    }

    async function solicitar(url, opciones) {
        const respuesta = await fetch(url, { credentials: 'same-origin', ...opciones });
        if (respuesta.redirected && new URL(respuesta.url).pathname.endsWith('/login')) {
            window.location.assign(respuesta.url);
            return null;
        }
        if (!(respuesta.headers.get('content-type') || '').includes('application/json')) {
            throw new Error('Respuesta inesperada');
        }
        const estado = await respuesta.json();
        renderizar(estado);
        return respuesta.ok ? estado : null;
    }

    async function abrir() {
        panel.hidden = false;
        guardar(clave, 'abierto');
        toggle.setAttribute('aria-expanded', 'true');
        toggle.setAttribute('aria-label', 'Cerrar asistente de ayuda Ulises');
        const tema = widget.dataset.autoOpen === 'true' ? new URLSearchParams(location.search).get('tema') : null;
        try {
            await solicitar(api + (tema ? '?tema=' + encodeURIComponent(tema) : ''));
        } catch (_error) {
            mostrarError('No se pudo cargar la conversación. Inténtalo nuevamente.');
        }
        pregunta.focus();
    }

    function cerrarPanel() {
        panel.hidden = true;
        guardar(clave, null);
        toggle.setAttribute('aria-expanded', 'false');
        toggle.setAttribute('aria-label', 'Abrir asistente de ayuda Ulises');
        toggle.focus();
    }

    toggle.addEventListener('click', () => panel.hidden ? abrir() : cerrarPanel());
    cerrar.addEventListener('click', cerrarPanel);
    document.addEventListener('keydown', (evento) => {
        if (evento.key === 'Escape' && !panel.hidden) cerrarPanel();
    });
    pregunta.addEventListener('keydown', (evento) => {
        if (evento.key === 'Enter' && !evento.shiftKey && !evento.isComposing) {
            evento.preventDefault();
            formulario.requestSubmit();
        }
    });
    formulario.addEventListener('submit', async (evento) => {
        evento.preventDefault();
        const texto = pregunta.value.trim();
        if (!texto || texto.length > 300) {
            mostrarError('Escribe una pregunta de 1 a 300 caracteres.');
            return;
        }
        if (enviar.disabled) return;
        enviar.disabled = true;
        try {
            const correcto = await solicitar(api, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ pregunta: texto })
            });
            if (correcto) {
                pregunta.value = '';
                if (correcto.navegacion) await navegar(correcto.navegacion);
            }
        } catch (_error) {
            mostrarError('No se pudo enviar la pregunta. Inténtalo nuevamente.');
        } finally {
            enviar.disabled = false;
            pregunta.focus();
        }
    });

    async function confirmarLlegada(navegacion) {
        await solicitar(llegadaApi, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ token: navegacion.token, ruta: location.pathname })
        });
    }

    async function navegar(navegacion) {
        const destino = new URL(navegacion.ruta, location.origin);
        const base = new URL(api, location.origin).pathname.replace(/\/ayuda\/conversacion$/, '');
        // La ruta se decide en el servidor; el navegador rechaza otros origenes y rutas ajenas a la app.
        if (destino.origin !== location.origin || !destino.pathname.startsWith(base + '/')
                || !navegacion.ruta.startsWith('/') || navegacion.ruta.startsWith('//')) {
            throw new Error('Destino inesperado');
        }
        if (destino.pathname === location.pathname && !location.search) {
            await confirmarLlegada(navegacion);
            return;
        }
        const hayCambios = formularios.some(form => valores(form) !== iniciales.get(form));
        if (hayCambios && !window.confirm('Tienes cambios sin guardar. ¿Quieres salir de este apartado y descartarlos?')) {
            mostrarError('Navegación cancelada. Tus datos siguen en el formulario.');
            return;
        }
        if (!guardar(claveLlegada, JSON.stringify(navegacion))) {
            mostrarError('No pude preparar la navegación. Tu navegador debe permitir el almacenamiento de sesión para conservar la ayuda al llegar.');
            return;
        }
        window.location.assign(destino.href);
    }

    async function iniciar() {
        const pendiente = leer(claveLlegada);
        guardar(claveLlegada, null);
        if (widget.dataset.autoOpen !== 'true' && leer(clave) !== 'abierto' && !pendiente) return;
        await abrir();
        if (pendiente) {
            try {
                const navegacion = JSON.parse(pendiente);
                if (navegacion.ruta === location.pathname) await confirmarLlegada(navegacion);
            } catch (_error) {
                mostrarError('No pude confirmar la llegada. Puedes hacer otra consulta.');
            }
        }
    }

    iniciar();
})();
