const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const path = require('node:path');
const code = fs.readFileSync(path.join(__dirname, '../src/main/resources/static/js/hu32-autocompletado.js'), 'utf8');

function elemento(extra = {}) {
  return Object.assign({
    listeners: {}, value: '', hidden: false, textContent: '', children: [], clases: new Set(),
    addEventListener(name, cb) { this.listeners[name] = cb; },
    dispatchEvent(ev) { if (this.listeners[ev.type]) this.listeners[ev.type](ev); },
    classList: null,
    insertAdjacentElement(_pos, el) { el.parent = this; this.insignia = el; },
    remove() { if (this.parent) this.parent.insignia = null; },
    replaceChildren() { this.children = []; },
    appendChild(el) { this.children.push(el); }
  }, extra);
}
function conClases(el) {
  el.classList = { add: c => el.clases.add(c), remove: c => el.clases.delete(c) };
  return el;
}

function fixture(fetchBody, replyNow = true) {
  const listeners = new Map();
  const nombre = conClases(elemento());
  const descripcion = conClases(elemento());
  const categoria = conClases(elemento({
    options: [{value: '', textContent: 'Seleccionar...'}, {value: 'ACCESORIOS', textContent: 'Accesorios'},
              {value: 'TECNOLOGIA', textContent: 'Tecnologia'}, {value: 'UTILES', textContent: 'Utiles'}]
  }));
  const ubicacion = elemento();
  const imagen = elemento();
  const panel = elemento({hidden: true});
  const mensaje = elemento();
  const lista = elemento();
  const deshacer = elemento({hidden: true});
  let resolveFetch;
  const peticiones = [];
  const response = {ok: true, json: async () => fetchBody};
  const fetchFn = (_url, opciones) => {
    peticiones.push(JSON.parse(opciones.body));
    return replyNow ? Promise.resolve(response) : new Promise(r => { resolveFetch = () => r(response); });
  };
  const porSelector = {'input[name="nombre"]': nombre, 'select[name="categoria"]': categoria,
                       'textarea[name="descripcion"]': descripcion, 'input[name="imagen"]': imagen};
  const document = {
    querySelector: s => porSelector[s],
    getElementById: id => ({'hu32-panel': panel, 'hu32-mensaje': mensaje, 'hu32-lista': lista,
                            'hu32-deshacer': deshacer})[id],
    createElement: () => elemento(),
    addEventListener(name, cb) { listeners.set(name, cb); },
    dispatchEvent(ev) { return listeners.get(ev.type)(ev); }
  };
  vm.runInNewContext(code, {document, fetch: fetchFn, console});
  return {document, nombre, categoria, descripcion, ubicacion, imagen, panel, mensaje, lista, deshacer,
          peticiones, resolve: () => resolveFetch?.()};
}

const respuestaMochila = {estado: 'AUTOCOMPLETADO', mensaje: 'ok',
  campos: {nombre: 'Mochila negra', categoria: 'ACCESORIOS', descripcion: 'Mochila de color negro.'}};
const analisisMochila = {type: 'ufound:imagen-analizada',
  detail: {estado: 'SUGERENCIA', tipoProbable: 'Mochila', colorProbable: 'Negro'}};

test('HU32 completa solo los campos respaldados y marca cada uno como sugerido', async () => {
  const f = fixture(respuestaMochila);
  await f.document.dispatchEvent(analisisMochila);
  assert.equal(f.nombre.value, 'Mochila negra');
  assert.equal(f.categoria.value, 'ACCESORIOS');
  assert.equal(f.descripcion.value, 'Mochila de color negro.');
  assert.equal(f.ubicacion.value, '');
  assert.ok(f.nombre.clases.has('hu32-sugerido'));
  assert.ok(f.nombre.insignia);
  assert.equal(f.panel.hidden, false);
  assert.equal(f.deshacer.hidden, false);
  assert.deepEqual(f.peticiones[0], {estado: 'SUGERENCIA', tipoProbable: 'Mochila', colorProbable: 'Negro'});
});

test('HU32 no sobrescribe lo que el usuario ya había ingresado', async () => {
  const f = fixture(respuestaMochila);
  f.nombre.value = 'Mi mochila Totto';
  f.categoria.value = 'UTILES';
  await f.document.dispatchEvent(analisisMochila);
  assert.equal(f.nombre.value, 'Mi mochila Totto');
  assert.equal(f.categoria.value, 'UTILES');
  assert.equal(f.descripcion.value, 'Mochila de color negro.');
  assert.ok(f.lista.children.some(li => li.textContent.includes('Título, Categoría')));
});

test('HU32 conserva la edición del usuario: deja de ser sugerencia y no se deshace', async () => {
  const f = fixture(respuestaMochila);
  await f.document.dispatchEvent(analisisMochila);
  f.nombre.value = 'Mochila negra Totto';
  f.nombre.dispatchEvent({type: 'input'});
  assert.ok(!f.nombre.clases.has('hu32-sugerido'));
  assert.equal(f.nombre.insignia, null);
  f.deshacer.dispatchEvent({type: 'click'});
  assert.equal(f.nombre.value, 'Mochila negra Totto');
  assert.equal(f.categoria.value, '');
  assert.equal(f.descripcion.value, '');
});

test('HU32 retira sugerencias no editadas al cambiar la foto e ignora respuestas antiguas', async () => {
  const f = fixture(respuestaMochila, false);
  const pendiente = f.document.dispatchEvent(analisisMochila);
  f.imagen.dispatchEvent({type: 'change'});
  f.resolve();
  await pendiente;
  assert.equal(f.nombre.value, '');
  assert.equal(f.panel.hidden, true);
});

test('HU32 no completa una categoría que no existe en el select', async () => {
  const f = fixture({estado: 'AUTOCOMPLETADO', campos: {nombre: 'Mochila', categoria: 'MOCHILAS'}});
  await f.document.dispatchEvent(analisisMochila);
  assert.equal(f.nombre.value, 'Mochila');
  assert.equal(f.categoria.value, '');
});

test('HU32 ignora análisis sin detección', async () => {
  const f = fixture(respuestaMochila);
  await f.document.dispatchEvent({type: 'ufound:imagen-analizada', detail: {estado: 'SIN_DETECCION'}});
  assert.equal(f.peticiones.length, 0);
  assert.equal(f.nombre.value, '');
  assert.equal(f.panel.hidden, true);
});
