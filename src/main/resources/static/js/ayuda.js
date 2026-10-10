(() => {
    // Si no existe el archivo subido ni una foto predeterminada con el mismo nombre, se muestra el aviso.
    document.querySelectorAll('img[src*="/uploads/"]').forEach((img) => {
        const mostrarAusente = () => {
            if (img.dataset.sinFoto) return;
            img.dataset.sinFoto = 'true';
            img.hidden = true;
            const aviso = document.createElement('span');
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

    function mostrarError(texto) {
        error.textContent = texto;
        error.hidden = !texto;
    }

    function renderizar(estado) {
        mensajes.replaceChildren();
        estado.mensajes.forEach((mensaje) => {
            const articulo = document.createElement('article');
            articulo.className = 'assistant-message' + (mensaje.emisor === 'usuario' ? ' assistant-message-user' : '');
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
        const estado = await respuesta.json();
        renderizar(estado);
        return respuesta.ok;
    }

    async function abrir() {
        panel.hidden = false;
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
        enviar.disabled = true;
        try {
            const correcto = await solicitar(api, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ pregunta: texto })
            });
            if (correcto) pregunta.value = '';
        } catch (_error) {
            mostrarError('No se pudo enviar la pregunta. Inténtalo nuevamente.');
        } finally {
            enviar.disabled = false;
            pregunta.focus();
        }
    });

    if (widget.dataset.autoOpen === 'true') abrir();
})();
