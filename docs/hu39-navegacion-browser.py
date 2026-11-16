"""Pruebas de HU-39 en Chromium contra una instancia de prueba aislada.

Requiere Playwright y las variables HU39_{ROL}_CORREO / HU39_{ROL}_PASSWORD.
Solo navega y conversa; no registra objetos ni cambia estados.
"""
import argparse
import os
from pathlib import Path
from urllib.parse import urlparse

from playwright.sync_api import sync_playwright, expect

parser = argparse.ArgumentParser()
parser.add_argument('--base-url', default='http://localhost:8080')
parser.add_argument('--evidencias', default='work/hu39-browser')
args = parser.parse_args()
base = args.base_url.rstrip('/')
salida = Path(args.evidencias)
salida.mkdir(parents=True, exist_ok=True)
casos = {
    'ESTUDIANTE': [
        ('Quiero reportar algo que perdi', '/objetos-perdidos/registrar'),
        ('Quiero buscar hallazgos', '/buscar'),
        ('Quiero ver mis reportes', '/objetos-perdidos/mis-reportes'),
        ('Como hago seguimiento de mi reporte', '/objetos-perdidos/mis-reportes'),
        ('Quiero ver mis coincidencias', '/coincidencias'),
        ('Quiero ver mis notificaciones', '/notificaciones'),
        ('Llevame al inicio', '/home'),
    ],
    'SEGURIDAD': [
        ('Quiero registrar un hallazgo', '/objetos-encontrados/registrar'),
        ('Quiero consultar hallazgos', '/objetos-encontrados'),
        ('Como veo el estado de un hallazgo', '/objetos-encontrados'),
        ('Llevame al inicio', '/home'),
    ],
    'OFICINA': [
        ('Quiero consultar estados', '/oficina/estados'),
        ('Quiero cambiar el estado de un objeto', '/oficina/estados'),
        ('Quiero ver estadisticas', '/analisis-perdidas'),
        ('Llevame al inicio', '/home'),
    ],
}
resultados = []


def ok(texto):
    resultados.append(texto)
    print('OK:', texto, flush=True)


def enviar(page, texto):
    expect(page.locator('.assistant-form button[type=submit]')).to_be_enabled()
    page.locator('#assistant-question').fill(texto)
    page.locator('.assistant-form button[type=submit]').click()


def navegar(page, texto, ruta):
    with page.expect_response(lambda r: r.url.endswith('/ayuda/llegada') and r.request.method == 'POST') as llegada:
        enviar(page, texto)
    assert llegada.value.status == 200
    estado = llegada.value.json()
    assert estado['navegacion'] is None
    assert estado['mensajes'][-1]['texto'].startswith('Abrí el apartado ')
    assert any(m['texto'] == texto for m in estado['mensajes'])
    expect(page.locator('.assistant-panel')).to_be_visible()
    expect(page.locator('.assistant-message').last).to_contain_text('Abrí el apartado ')
    assert urlparse(page.url).path == urlparse(base).path + ruta
    expect(page.locator('.assistant-form button[type=submit]')).to_be_enabled()


with sync_playwright() as pw:
    browser = pw.chromium.launch(executable_path=os.getenv('CHROMIUM_PATH', '/usr/bin/chromium'), headless=True)
    for rol, recorridos in casos.items():
        contexto = browser.new_context(viewport={'width': 1366, 'height': 900})
        page = contexto.new_page()
        errores = []
        page.on('pageerror', lambda err: errores.append(str(err)))
        page.goto(base + '/login')
        page.locator('#correo').fill(os.environ['HU39_' + rol + '_CORREO'])
        page.locator('#password').fill(os.environ['HU39_' + rol + '_PASSWORD'])
        page.locator('.login-form button[type=submit]').click()
        page.wait_for_url(base + '/home')
        page.locator('.assistant-toggle').click()
        expect(page.locator('.assistant-message')).to_have_count(1)
        for texto, ruta in recorridos:
            navegar(page, texto, ruta)
            ok(rol + ': ' + texto + ' -> ' + ruta)
        antes = page.locator('.assistant-message').count()
        page.reload()
        expect(page.locator('.assistant-panel')).to_be_visible()
        expect(page.locator('.assistant-message')).to_have_count(antes)
        assert urlparse(page.url).path.endswith('/home')
        ok(rol + ': recarga conserva historial sin repetir llegada')

        if rol == 'ESTUDIANTE':
            for consulta in ['Quiero buscar y ver mis notificaciones', 'Quiero registrar un objeto encontrado']:
                enviar(page, consulta)
                expect(page.locator('.assistant-message').last).to_contain_text('¿A qué apartado quieres ir?')
                assert page.url == base + '/home'
            ok('ESTUDIANTE: ambiguedad y solicitud de otro rol no navegan')
        if rol in ['ESTUDIANTE', 'SEGURIDAD']:
            navegar(page, recorridos[0][0], recorridos[0][1])
            page.locator('input[name=nombre]').fill('Dato de prueba sin guardar')
            cancelar = lambda dialog: dialog.dismiss()
            page.on('dialog', cancelar)
            enviar(page, 'Llevame al inicio')
            expect(page.locator('.assistant-error')).to_contain_text('Navegación cancelada')
            expect(page.locator('input[name=nombre]')).to_have_value('Dato de prueba sin guardar')
            assert page.url == base + recorridos[0][1]
            page.remove_listener('dialog', cancelar)
            page.once('dialog', lambda dialog: dialog.accept())
            navegar(page, 'Llevame al inicio', '/home')
            ok(rol + ': cancelar conserva datos y aceptar permite salir')

        page.screenshot(path=str(salida / (rol.lower() + '-escritorio.png')))
        page.set_viewport_size({'width': 390, 'height': 844})
        navegar(page, recorridos[0][0], recorridos[0][1])
        panel = page.locator('.assistant-panel').bounding_box()
        assert panel and panel['x'] >= 0 and panel['y'] >= 0
        assert panel['x'] + panel['width'] <= 390 and panel['y'] + panel['height'] <= 844
        expect(page.locator('#assistant-question')).to_be_in_viewport()
        page.screenshot(path=str(salida / (rol.lower() + '-movil.png')))
        ok(rol + ': panel y entrada visibles en movil 390x844')
        if rol == 'ESTUDIANTE':
            page.route('**/ayuda/conversacion', lambda route: route.abort())
            enviar(page, 'Quiero buscar')
            expect(page.locator('.assistant-error')).to_contain_text('No se pudo enviar')
            expect(page.locator('#assistant-question')).to_have_value('Quiero buscar')
            page.unroute('**/ayuda/conversacion')
            ok('ESTUDIANTE: error de red conserva pregunta y permite reintentar')
            page.request.get(base + '/logout')
            enviar(page, 'Quiero buscar')
            page.wait_for_url(base + '/login')
            ok('ESTUDIANTE: sesion vencida redirige al login')
        assert not errores, errores
        contexto.close()
    browser.close()
(salida / 'resultado.txt').write_text('\n'.join(resultados) + '\n', encoding='utf-8')
print(f'{len(resultados)} comprobaciones de navegador correctas.', flush=True)
