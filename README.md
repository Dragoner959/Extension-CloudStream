# CloudStream plugin starter

Este repositorio ya está preparado como una base segura para crear un plugin de CloudStream y publicarlo en GitHub.

## Qué incluye
- Estructura mínima para un plugin de CloudStream
- Clase principal lista para extenderse con tu propio parser
- Workflow de GitHub Actions para compilar el archivo del plugin automáticamente
- Instrucciones para publicar el proyecto en GitHub y probarlo en CloudStream

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
