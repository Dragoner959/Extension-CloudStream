# CloudStream plugin starter

Este repositorio ya está preparado como una base segura para crear un plugin de CloudStream y publicarlo en GitHub.

## Qué incluye
- Estructura mínima para un plugin de CloudStream
- Clase principal lista para extenderse con tu propio parser
- Workflow de GitHub Actions para compilar el archivo del plugin automáticamente
- Instrucciones para publicar el proyecto en GitHub y probarlo en CloudStream

## Limitación importante
No voy a ayudar a implementar scrapers para contenido adulto, sitios con material no autorizado, piratería ni formas de evadir anuncios o restricciones. La base está pensada para fuentes legales, públicas o con permiso explícito.

## Estructura esperada
```text
.
├── .github/
│   └── workflows/
│       └── build.yml
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   └── main/
│       └── kotlin/
│           └── com/
│               └── githubextension/
│                   └── cloudstream/
│                       └── SafeExamplePlugin.kt
```

## Cómo empezar
1. Ajusta el paquete y el nombre del plugin en el archivo fuente.
2. Reemplaza el placeholder por un parser real para una fuente legal y autorizada.
3. Compila el proyecto con Gradle o usa el workflow de GitHub Actions.
4. Sube el repositorio a GitHub y descarga el archivo .jar generado.
5. En CloudStream, instala el plugin desde el .jar o desde una fuente que lo distribuya.

## Cómo compilar localmente
```bash
gradle build
```

## Cómo publicar en GitHub
1. Crea un repositorio nuevo en GitHub.
2. Conecta el proyecto remoto:
   ```bash
   git init
   git branch -M main
   git remote add origin <tu-url-del-repo>
   git add .
   git commit -m "Initial plugin scaffold"
   git push -u origin main
   ```
3. En GitHub, revisa la pestaña Actions para descargar el artefacto compilado.

## Próximos pasos recomendados
- Añadir un parser real para una fuente legal y pública.
- Implementar `getMainPage`, `search` y `loadLinks` con tus propios endpoints.
- Probar la extensión en CloudStream y revisar que no rompa la app.
- Si quieres una fuente concreta, yo puedo ayudarte a estructurar el código para una API oficial o un sitio con permiso explícito.
