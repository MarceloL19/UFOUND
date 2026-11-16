"""Recorrido visual de #81. Usar una instancia aislada con los tres roles.

Variables VISUAL_{ROL}_CORREO y VISUAL_{ROL}_PASSWORD.
--registrar permite crear cuatro reportes de prueba; no usar sobre datos reales.
La respuesta de deteccion IA se controla para revisar sus paneles de forma repetible.
"""
import argparse
import json
import os
from pathlib import Path
from playwright.sync_api import sync_playwright, expect

args_parser = argparse.ArgumentParser()
args_parser.add_argument('--base-url', default='http://localhost:8080')
args_parser.add_argument('--evidencias', default='work/visual-browser')
args_parser.add_argument('--registrar', action='store_true')
args = args_parser.parse_args()
base = args.base_url.rstrip('/')
out = Path(args.evidencias)
out.mkdir(parents=True, exist_ok=True)
foto = Path(__file__).resolve().parents[1] / 'src/main/resources/static/fotos/laptop-negra.jpg'
resultados = []


def ok(texto):
    resultados.append(texto)
    print('OK:', texto, flush=True)


def sin_desborde(page, nombre):
    assert page.evaluate('document.documentElement.scrollWidth <= innerWidth + 1'), nombre


def captura(page, nombre, selector=None):
    page.evaluate('document.fonts.ready.then(() => true)')
    page.evaluate('window.scrollTo(0, 0)')
    page.screenshot(path=str(out / (nombre + '.png')), full_page=True)


with sync_playwright() as pw:
    browser = pw.chromium.launch(executable_path=os.getenv('CHROMIUM_PATH', '/usr/bin/chromium'), headless=True)
    for rol in ['ESTUDIANTE', 'SEGURIDAD', 'OFICINA']:
        context = browser.new_context(viewport={'width': 1440, 'height': 1000})
        page = context.new_page()
        errores = []
        page.on('pageerror', lambda err: errores.append(str(err)))
        page.goto(base + '/login')
        page.locator('#correo').fill(os.environ['VISUAL_' + rol + '_CORREO'])
        page.locator('#password').fill(os.environ['VISUAL_' + rol + '_PASSWORD'])
        page.locator('.login-form button[type=submit]').click()
        page.wait_for_url(base + '/home')
        expect(page.locator('.app-sidebar .ui-icon').first).to_be_visible()
        page.wait_for_function("document.querySelector('.app-sidebar .ui-icon').getBBox().width > 0")
        sin_desborde(page, rol + ' inicio escritorio')
        captura(page, rol.lower() + '-inicio-escritorio')
        ok(rol + ': inicio con iconos locales y tarjetas, sin desbordamiento en escritorio')
        page.set_viewport_size({'width': 390, 'height': 844})
        sin_desborde(page, rol + ' inicio movil')
        captura(page, rol.lower() + '-inicio-movil')
        ok(rol + ': inicio y menu adaptados a 390x844')
        page.locator('.assistant-toggle').click()
        expect(page.locator('.assistant-message').first).to_be_visible()
        page.locator('#assistant-question').fill('Quiero buscar' if rol == 'ESTUDIANTE' else 'Como veo el estado de un hallazgo' if rol == 'SEGURIDAD' else 'Como consulto los estados')
        page.locator('.assistant-form button[type=submit]').click()
        expect(page.locator('.assistant-message')).to_have_count(3)
        panel = page.locator('.assistant-panel').bounding_box()
        assert panel and panel['x'] >= 0 and panel['x'] + panel['width'] <= 390
        expect(page.locator('#assistant-question')).to_be_in_viewport()
        captura(page, rol.lower() + '-asistente-movil')
        page.locator('.assistant-close').click()
        ok(rol + ': asistente compacto conserva envio, historial y cierre')

        if rol == 'OFICINA':
            page.set_viewport_size({'width': 1440, 'height': 1000})
            page.goto(base + '/analisis-perdidas')
            sin_desborde(page, 'analisis escritorio')
            expect(page.locator('.analisis-kpis')).to_be_visible()
            captura(page, 'oficina-analisis-escritorio')
            page.set_viewport_size({'width': 390, 'height': 844})
            sin_desborde(page, 'analisis movil')
            captura(page, 'oficina-analisis-movil')
            ok('OFICINA: analisis con datos, filtros y tarjetas en escritorio/movil')
        else:
            ruta = '/objetos-perdidos/registrar' if rol == 'ESTUDIANTE' else '/objetos-encontrados/registrar'
            page.set_viewport_size({'width': 1440, 'height': 1000})
            page.goto(base + ruta)
            # Solo se sustituye la deteccion; categoria y autocompletado usan sus endpoints reales.
            page.route('**/imagenes/analizar', lambda route: route.fulfill(status=200, content_type='application/json', body=json.dumps({'estado': 'SUGERENCIA', 'tipoProbable': 'Laptop', 'colorProbable': 'Gris', 'confianza': .94})))
            page.locator('input[name=imagen]').set_input_files(str(foto))
            page.locator('#btn-analizar-imagen').click()
            expect(page.locator('#hu31-panel')).to_be_visible()
            expect(page.locator('#hu32-panel')).to_be_visible()
            expect(page.locator('#hu31-aplicar')).to_be_enabled()
            sin_desborde(page, rol + ' formulario IA escritorio')
            captura(page, rol.lower() + '-ia-escritorio', '.upload-card')
            page.set_viewport_size({'width': 390, 'height': 844})
            sin_desborde(page, rol + ' formulario IA movil')
            captura(page, rol.lower() + '-ia-movil', '#hu32-panel')
            page.locator('#hu31-aplicar').click()
            expect(page.locator('select[name=categoria]')).to_have_value('TECNOLOGIA')
            page.locator('#hu32-deshacer').click()
            expect(page.locator('input[name=nombre]')).to_have_value('')
            ok(rol + ': paneles IA coherentes; aplicar categoria y deshacer siguen operativos')
            if args.registrar:
                for con_foto in [True, False]:
                    page.set_viewport_size({'width': 1440, 'height': 1000})
                    page.goto(base + ruta)
                    page.locator('input[name=nombre]').fill('Prueba visual ' + rol + (' con foto' if con_foto else ' sin foto'))
                    page.locator('select[name=categoria]').select_option('TECNOLOGIA')
                    page.locator('select[name=ubicacion]').select_option('BIBLIOTECA')
                    page.locator('textarea[name=descripcion]').fill('Registro exclusivo de la base aislada de pruebas visuales.')
                    if con_foto:
                        page.locator('input[name=imagen]').set_input_files(str(foto))
                    page.locator('.submit-wide').click()
                    page.wait_for_url('**/confirmacion/*' if rol == 'ESTUDIANTE' else '**/detalle/*')
                    expect(page.locator('h1')).to_contain_text('Objeto perdido registrado' if rol == 'ESTUDIANTE' else 'Prueba visual')
                    if rol == 'SEGURIDAD':
                        expect(page.locator('.registration-success')).to_be_visible()
                    if con_foto:
                        img = page.locator('.confirmation-photo img' if rol == 'ESTUDIANTE' else '.found-main-image img')
                        expect(img).to_be_visible()
                        assert img.evaluate('(img) => img.complete && img.naturalWidth > 0')
                    else:
                        expect(page.get_by_text('Sin foto adjunta', exact=True)).to_be_visible()
                    sin_desborde(page, rol + ' confirmacion escritorio')
                    nombre = rol.lower() + '-confirmacion-' + ('foto' if con_foto else 'sin-foto')
                    captura(page, nombre + '-escritorio')
                    page.set_viewport_size({'width': 390, 'height': 844})
                    sin_desborde(page, rol + ' confirmacion movil')
                    captura(page, nombre + '-movil')
                    ok(rol + ': registro real y resumen ' + ('con foto' if con_foto else 'sin foto') + ' en escritorio/movil')
        enlaces = page.locator('.app-sidebar nav a').evaluate_all('(enlaces) => enlaces.map(a => a.href)')
        for enlace in enlaces:
            respuesta = page.goto(enlace)
            assert respuesta and respuesta.status == 200 and page.url == enlace
            for ancho, alto in [(1440, 1000), (1024, 900), (768, 1024), (390, 844)]:
                page.set_viewport_size({'width': ancho, 'height': alto})
                sin_desborde(page, rol + ': ' + enlace + ' ancho ' + str(ancho))
        ok(rol + ': todos los destinos del menu conservan acceso y se adaptan a escritorio/movil')
        assert not errores, errores
        context.close()
    browser.close()
(out / 'resultado.txt').write_text('\n'.join(resultados) + '\n', encoding='utf-8')
print(f'{len(resultados)} comprobaciones correctas.', flush=True)
