const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');

const jsDir = path.join(__dirname, '../src/main/resources/static/js');
const hu30Code = fs.readFileSync(path.join(jsDir, 'analisis-imagen.js'), 'utf8');
const hu31Code = fs.readFileSync(path.join(jsDir, 'hu31-categoria.js'), 'utf8');
const tick = () => new Promise(resolve => setImmediate(resolve));
const deferred = () => {
    let resolve;
    let reject;
    const promise = new Promise((ok, fail) => {resolve = ok; reject = fail;});
    return {promise, resolve, reject};
};
const imageResponse = (estado, tipoProbable = null, status = 200) => ({
    ok: status >= 200 && status < 300,
    status,
    headers: {get: key => key === 'content-type' ? 'application/json' : null},
    json: async () => ({estado, tipoProbable, colorProbable: 'Negro', confianza: .86,
        mensaje: estado === 'ERROR' ? 'El análisis falló.' : 'Sin detección.'})
});
const categoryResponse = (categoria = 'ACCESORIOS') => ({
    ok: true,
    json: async () => ({estado: 'SUGERENCIA', categoria, etiqueta: 'Accesorios'})
});

function fixture() {
    function target() {
        const handlers = new Map();
        return {
            addEventListener(name, handler) {
                if (!handlers.has(name)) handlers.set(name, []);
                handlers.get(name).push(handler);
            },
            dispatchEvent(event) {
                return Promise.all((handlers.get(event.type) || []).map(fn => fn(event)));
            }
        };
    }
    const documento = target();
    const botonAnalizar = Object.assign(target(), {disabled: false});
    const resultadoAnalisis = {textContent: '', className: ''};
    const imagen = Object.assign(target(), {files: [{name: 'ejemplo.webp'}], value: 'ejemplo.webp'});
    const categoria = Object.assign(target(), {
        value: 'DOCUMENTOS',
        options: ['ACCESORIOS', 'UTILES', 'TECNOLOGIA', 'DOCUMENTOS'].map(value => ({value}))
    });
    const panel = {hidden: true};
    const mensaje = {textContent: ''};
    const aplicar = Object.assign(target(), {disabled: true});
    const queueImage = [];
    const queueCategory = [];
    const calls = [];
    const http = (url) => {
        calls.push(url);
        const q = url === '/imagenes/analizar' ? queueImage :
            url === '/hu31/sugerir-categoria' ? queueCategory : null;
        if (!q || !q.length) throw new Error('Unexpected fetch: ' + url);
        return Promise.resolve(q.shift());
    };
    documento.getElementById = id => ({
        'btn-analizar-imagen': botonAnalizar,
        'resultado-analisis-imagen': resultadoAnalisis,
        'hu31-panel': panel,
        'hu31-mensaje': mensaje,
        'hu31-aplicar': aplicar
    })[id];
    documento.querySelector = selector => selector.includes('select') ? categoria : imagen;
    class FakeEvent {
        constructor(type, options) { this.type = type; this.bubbles = !!options?.bubbles; }
    }
    class FakeCustomEvent extends FakeEvent {
        constructor(type, options) {super(type, options); this.detail = options?.detail;}
    }
    class FakeFormData { append() {} }
    const ctx = {document: documento, fetch: http, Event: FakeEvent,
        CustomEvent: FakeCustomEvent, FormData: FakeFormData,
        console: {warn() {}, error() {}}};
    vm.runInNewContext(hu30Code, ctx);
    vm.runInNewContext(hu31Code, ctx);
    return {
        panel, categoria, aplicar, mensaje, resultadoAnalisis, imagen, calls,
        queueImage, queueCategory,
        click: () => botonAnalizar.dispatchEvent({type: 'click'}),
        apply: () => aplicar.dispatchEvent({type: 'click'}),
        change: () => imagen.dispatchEvent({type: 'change'}),
        settled: async () => {await tick(); await tick();}
    };
}

async function suggest(f) {
    f.queueImage.push(imageResponse('SUGERENCIA', 'Maleta'));
    f.queueCategory.push(categoryResponse());
    await f.click();
    await f.settled();
    assert.equal(f.panel.hidden, false);
    assert.equal(f.aplicar.disabled, false);
    assert.equal(f.categoria.value, 'DOCUMENTOS');
}

test('integración: segundo análisis de la misma foto sin detección limpia la sugerencia y conserva selección manual', async () => {
    const f = fixture();
    await suggest(f);
    await f.apply();
    assert.equal(f.categoria.value, 'ACCESORIOS');
    f.categoria.value = 'UTILES';
    f.queueImage.push(imageResponse('SIN_DETECCION'));
    const actual = f.click();
    assert.equal(f.panel.hidden, true, 'se limpia al comenzar, no al finalizar');
    assert.equal(f.aplicar.disabled, true);
    assert.equal(f.categoria.value, 'UTILES');
    await actual;
    await f.settled();
    assert.equal(f.panel.hidden, true);
    assert.equal(f.aplicar.disabled, true);
    assert.equal(f.categoria.value, 'UTILES');
    assert.equal(f.calls.filter(x => x === '/hu31/sugerir-categoria').length, 1);
});

test('integración: error HTTP en reanálisis no conserva sugerencia ni modifica categoría', async () => {
    const f = fixture();
    await suggest(f);
    f.categoria.value = 'UTILES';
    f.queueImage.push(imageResponse('ERROR', null, 503));
    await f.click();
    await f.settled();
    assert.equal(f.panel.hidden, true);
    assert.equal(f.aplicar.disabled, true);
    assert.equal(f.categoria.value, 'UTILES');
});

test('integración: fallo de red en reanálisis limpia propuesta y permite registro manual', async () => {
    const f = fixture();
    await suggest(f);
    f.categoria.value = 'UTILES';
    f.queueImage.push(Promise.reject(new Error('Sin conexión')));
    await f.click();
    await f.settled();
    assert.equal(f.panel.hidden, true);
    assert.equal(f.aplicar.disabled, true);
    assert.equal(f.categoria.value, 'UTILES');
});

test('integración: respuesta pendiente de HU-31 no reactiva sugerencia tras segundo análisis sin detección', async () => {
    const f = fixture();
    const pendiente = deferred();
    f.queueImage.push(imageResponse('SUGERENCIA', 'Maleta'));
    f.queueCategory.push(pendiente.promise);
    await f.click();
    await f.settled();
    assert.equal(f.panel.hidden, true);
    f.queueImage.push(imageResponse('SIN_DETECCION'));
    await f.click();
    pendiente.resolve(categoryResponse());
    await f.settled();
    assert.equal(f.panel.hidden, true);
    assert.equal(f.aplicar.disabled, true);
    assert.equal(f.categoria.value, 'DOCUMENTOS');
});

test('integración: respuesta atrasada de HU-30 no altera el resultado de la foto nueva', async () => {
    const f = fixture();
    const primera = deferred();
    f.queueImage.push(primera.promise);
    const ejecutando = f.click();
    await f.change();
    assert.equal(f.aplicar.disabled, true);
    f.imagen.files = [{name: 'otra.webp'}];
    f.queueImage.push(imageResponse('SIN_DETECCION'));
    await f.click();
    primera.resolve(imageResponse('SUGERENCIA', 'Maleta'));
    await ejecutando;
    await f.settled();
    assert.equal(f.panel.hidden, true);
    assert.equal(f.aplicar.disabled, true);
    assert.equal(f.calls.filter(x => x === '/hu31/sugerir-categoria').length, 0);
});

test('integración: volver a analizar sin archivo también elimina propuesta antigua', async () => {
    const f = fixture();
    await suggest(f);
    f.imagen.files = [];
    await f.click();
    await f.settled();
    assert.equal(f.panel.hidden, true);
    assert.equal(f.aplicar.disabled, true);
    assert.equal(f.categoria.value, 'DOCUMENTOS');
});

test('integración: una respuesta antigua de HU-31 no reemplaza una sugerencia nueva', async () => {
    const f = fixture();
    const antigua = deferred();
    f.queueImage.push(imageResponse('SUGERENCIA', 'Maleta'));
    f.queueCategory.push(antigua.promise);
    await f.click();
    await f.settled();
    f.queueImage.push(imageResponse('SUGERENCIA', 'Maleta'));
    f.queueCategory.push(categoryResponse());
    await f.click();
    await f.settled();
    assert.equal(f.panel.hidden, false);
    antigua.resolve(categoryResponse('DOCUMENTOS'));
    await f.settled();
    await f.apply();
    assert.equal(f.categoria.value, 'ACCESORIOS');
});


test('integración: cambiar la imagen durante un análisis permite iniciar uno nuevo', async () => {
    const f = fixture();
    const primera = deferred();
    f.queueImage.push(primera.promise);
    const enCurso = f.click();
    await f.change();
    f.queueImage.push(imageResponse('SIN_DETECCION'));
    await f.click();
    primera.resolve(imageResponse('SUGERENCIA', 'Maleta'));
    await enCurso;
    await f.settled();
    assert.equal(f.panel.hidden, true);
    assert.equal(f.aplicar.disabled, true);
});
