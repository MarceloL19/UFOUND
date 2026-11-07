const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const path = require('node:path');
const code = fs.readFileSync(path.join(__dirname, '../src/main/resources/static/js/hu31-categoria.js'), 'utf8');

function fixture(fetchBody, replyNow = true) {
  const listeners = new Map();
  const dom = () => ({listeners: {}, addEventListener(name, cb) {this.listeners[name]=cb;}, dispatchEvent(ev) {if (this.listeners[ev.type]) this.listeners[ev.type](ev);}});
  const categoria = dom();
  categoria.options = [{value:'TECNOLOGIA'}, {value:'ACCESORIOS'}, {value:'BOTELLAS'}, {value:'DOCUMENTOS'}];
  categoria.value = 'DOCUMENTOS';
  const panel = {hidden:true};
  const mensaje = {textContent:''};
  const boton = dom(); boton.disabled = true;
  const imagen = dom();
  let resolveFetch;
  const response = {ok:true,json:async()=>fetchBody};
  const fetchFn = () => replyNow ? Promise.resolve(response) : new Promise(resolve => {resolveFetch = () => resolve(response);});
  const document = {
    querySelector(selector){return selector.includes('categoria')?categoria:imagen;},
    getElementById(id){return ({'hu31-panel':panel, 'hu31-mensaje':mensaje, 'hu31-aplicar':boton})[id];},
    addEventListener(name,cb){listeners.set(name,cb);},
    dispatchEvent(ev){return listeners.get(ev.type)(ev);}
  };
  class Event {constructor(type,options){this.type=type; this.bubbles=options?.bubbles;}}
  vm.runInNewContext(code, {document, fetch:fetchFn, Event, console});
  return {document,categoria,panel,mensaje,boton,imagen,resolve:() => resolveFetch?.()};
}
const flush = () => new Promise(resolve => setImmediate(resolve));

test('HU31 sugiere sin modificar el formulario hasta confirmar y deja corregir', async () => {
 const f=fixture({estado:'SUGERENCIA',categoria:'ACCESORIOS',etiqueta:'Accesorios'});
 await f.document.dispatchEvent({type:'ufound:imagen-analizada',detail:{estado:'SUGERENCIA',tipoProbable:'Mochila'}});
 assert.equal(f.categoria.value, 'DOCUMENTOS');
 assert.equal(f.panel.hidden,false);
 f.boton.dispatchEvent({type:'click'});
 assert.equal(f.categoria.value,'ACCESORIOS');
 f.categoria.value='DOCUMENTOS';
 assert.equal(f.categoria.value,'DOCUMENTOS');
});
test('HU31 no presenta categoría inventada aunque backend la devuelva', async () => {
 const f=fixture({estado:'SUGERENCIA',categoria:'MOCHILAS',etiqueta:'Mochilas'});
 await f.document.dispatchEvent({type:'ufound:imagen-analizada',detail:{estado:'SUGERENCIA',tipoProbable:'Mochila'}});
 assert.equal(f.panel.hidden,true);
 assert.equal(f.categoria.value,'DOCUMENTOS');
});
test('HU31 ignora una sugerencia antigua si cambia la foto', async () => {
 const f=fixture({estado:'SUGERENCIA',categoria:'ACCESORIOS',etiqueta:'Accesorios'},false);
 const pending=f.document.dispatchEvent({type:'ufound:imagen-analizada',detail:{estado:'SUGERENCIA',tipoProbable:'Mochila'}});
 f.imagen.dispatchEvent({type:'change'});
 f.resolve();
 await pending;
 assert.equal(f.panel.hidden,true);
 assert.equal(f.categoria.value,'DOCUMENTOS');
});
test('HU31 no altera la categoría ante ausencia de detección', async () => {
 const f=fixture({estado:'SIN_SUGERENCIA',categoria:null,etiqueta:null});
 await f.document.dispatchEvent({type:'ufound:imagen-analizada',detail:{estado:'SIN_DETECCION',tipoProbable:null}});
 assert.equal(f.categoria.value,'DOCUMENTOS');
 assert.equal(f.panel.hidden,true);
});
