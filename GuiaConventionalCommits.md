## Guía Completa para Crear Commits Adecuados con Conventional Commits y Emojis

Esta guía se basa en la especificación Conventional Commits y las prácticas recomendadas extraídas de las fuentes que proporcionaste. Su objetivo es ayudarte a crear mensajes de commit claros, consistentes y útiles tanto para humanos como para herramientas automatizadas.

### ¿Qué son Conventional Commits?

Conventional Commits es una **convención estandarizada** para los mensajes de commit. Proporciona un conjunto de **reglas sencillas** para crear un historial de commit **explícito**. Esto mejora la **claridad, consistencia y colaboración** entre desarrolladores, y facilita la creación de **herramientas automatizadas** sobre el historial de commits. Esta convención se alinea con el **Versionado Semántico (SemVer)**.

### Estructura Básica de un Mensaje de Commit Convencional

Un mensaje de commit que sigue esta especificación **DEBE** tener la siguiente estructura, aunque algunos componentes son opcionales:

`<tipo>[ámbito opcional]: <descripción>`
`[cuerpo opcional]`
`[pie de página(s) opcional(es)]`

Vamos a detallar cada elemento:

#### 1. Tipo (`<tipo>`)

*   El tipo es un **sustantivo** que define la **propósito** del commit.
*   DEBE ser el **prefijo** del commit, seguido de un **signo de exclamación opcional**, y un **obligatorio** colon y espacio (`: `).
*   Los tipos **principales** definidos en la especificación por su correlación con SemVer son:
    *   **`fix`**: Indica que se **corrige un error** (bug) en el código. Un commit de tipo `fix` se correlaciona con un **cambio de PARCHE** (PATCH) en SemVer. El tipo `fix` DEBE usarse cuando se corrige un error.
    *   **`feat`**: Indica que se introduce una **nueva característica** o funcionalidad. Un commit de tipo `feat` se correlaciona con un **cambio MENOR** (MINOR) en SemVer. El tipo `feat` DEBE usarse cuando se añade una nueva funcionalidad.
*   Se **PERMITEN** otros tipos de commit, como los recomendados por convenciones basadas en la de Angular:
    *   **`build`**: Cambios que afectan el **sistema de compilación** o dependencias externas.
    *   **`chore`**: Cubre **tareas misceláneas** o de mantenimiento que no modifican el código fuente ni los archivos de prueba.
    *   **`ci`**: Se refiere a cambios en los archivos y scripts de configuración de **Integración Continua (CI/CD)**.
    *   **`docs`**: Indica cambios en la **documentación**.
    *   **`style`**: Se refiere a cambios que no afectan la **funcionalidad** del código, como formateo, espacios en blanco o linting.
    *   **`refactor`**: Indica cambios en la **estructura o legibilidad** del código que no corrigen un error ni añaden una característica.
    *   **`test`**: Se utiliza para añadir **pruebas** faltantes o corregir pruebas existentes.
    *   **`perf`**: Utilizado para cambios de código que mejoran el **rendimiento**.
    *   **`revert`**: Indica que se **revierte** un commit previamente realizado.
    *   **`improvement`**: Recomendado por algunas convenciones para commits que **mejoran** una implementación actual sin añadir nuevas funcionalidades ni corregir errores [Según tu fuente, no está en las otras].
*   Tipos distintos a `fix` y `feat` **NO tienen un efecto implícito en el Versionado Semántico** a menos que incluyan un `BREAKING CHANGE`.
*   Se pueden usar **cualquier sustantivo** como tipo, pero es **RECOMENDADO ser consistente** en su uso.
*   La especificación NO establece si el tipo debe estar en mayúsculas o minúsculas; la consistencia es clave.

#### 2. Ámbito (`[ámbito opcional]`)

*   El ámbito es **OPCIONAL**.
*   Se puede proporcionar después del `tipo` para dar **información contextual adicional**.
*   DEBE consistir en un **sustantivo** que describe una sección del código y estar encerrado **entre paréntesis**.
*   **Ejemplos**: `feat(parser):`, `fix(ui):`, `test(api):`, `build(docker):`.
*   Es útil en proyectos **monorepo** para especificar el paquete modificado.

#### 3. Descripción (`<descripción>`)

*   La descripción es un **resumen corto** de los cambios en el código.
*   DEBE seguir **inmediatamente después** de los dos puntos y el espacio (`: `) del prefijo `tipo[/ámbito][!]`.
*   Se RECOMIENDA mantenerla **corta**, idealmente **bajo 50 caracteres**.
*   Se RECOMIENDA usar el **tono imperativo** (tiempo presente). Ejemplo: `fix: resolve issue` (Correcto) vs `fixed issue` (Incorrecto).
*   Debe ser **específica**. Ejemplo: `feat(ui): add tooltip to icons` (Correcto) vs `feat: update UI` (Incorrecto).

#### 4. Cuerpo (`[cuerpo opcional]`)

*   El cuerpo es **OPCIONAL**.
*   Proporciona **información contextual adicional** sobre los cambios.
*   Si se incluye, DEBE comenzar **una línea en blanco** después de la descripción.
*   Es de **formato libre** y PUEDE consistir en cualquier número de **párrafos** separados por líneas en blanco.
*   Es un buen lugar para añadir **detalles o explicaciones más largas** que no caben en la descripción corta.

#### 5. Pie de Página(s) (`[pie de página(s) opcional(es)]`)

*   Uno o más pies de página son **OPCIONALES**.
*   PUEDEN incluirse **una línea en blanco** después del cuerpo (o después de la descripción si no hay cuerpo).
*   Contienen **meta-información** sobre el commit.
*   Siguen una convención similar al formato de **git trailer**. Cada pie de página DEBE consistir en un **token de palabra**, seguido por un **separador** (`: ` o ` #`), seguido de un **valor**.
*   El token del pie de página DEBE usar `-` en lugar de espacios (ej. `Acked-by`). Una excepción es `BREAKING CHANGE`, que PUEDE usarse como token.
*   El valor del pie de página PUEDE contener espacios y líneas en blanco.
*   Se utilizan comúnmente para:
    *   Indicar **Cambios Disruptivos** (ver sección siguiente).
    *   Referenciar **issues** o tareas en sistemas de seguimiento (ej. `Fixes #123`, `Refs #456`). Se RECOMIENDA poner estas referencias en el pie de página en lugar del título.

### Cambios Disruptivos (BREAKING CHANGE)

*   Un commit que introduce un **cambio en la API que rompe la compatibilidad anterior** es un `BREAKING CHANGE`.
*   Un commit con un `BREAKING CHANGE` se correlaciona con un **cambio MAYOR** (MAJOR) en SemVer, **independientemente del tipo** de commit.
*   Los cambios disruptivos **DEBEN indicarse** de una de estas dos maneras:
    1.  Añadiendo `BREAKING CHANGE:` seguido de una descripción en el **pie de página** del commit. El texto `BREAKING CHANGE` DEBE estar en **mayúsculas**. `BREAKING-CHANGE` es sinónimo de `BREAKING CHANGE` cuando se usa como token en un pie de página.
    2.  Añadiendo un **`!` inmediatamente después del tipo o ámbito** en la línea de descripción. Si se usa `!`, la descripción del commit PUEDE usarse para describir el cambio disruptivo, y el pie de página `BREAKING CHANGE:` PUEDE omitirse.

*   **Ejemplos de BREAKING CHANGE**:
    *   `feat: allow provided config object to extend other configs`
        `BREAKING CHANGE: extends key in config file is now used for extending other config files`
    *   `feat!: send an email to the customer when a product is shipped`
    *   `feat(api)!: send an email to the customer when a product is shipped`
    *   `chore!: drop support for Node 6`
        `BREAKING CHANGE: use JavaScript features not available in Node 6.`
    *   `feat(api)!: rename getUser to fetchUser`
        `BREAKING CHANGE: getUser is replaced by fetchUser to improve naming consistency.`

### Correlación con Versionado Semántico (SemVer)

La especificación Conventional Commits encaja perfectamente con SemVer, permitiendo la determinación automática de los cambios de versión:

*   Los commits de tipo **`fix`** se traducen en **lanzamientos de PARCHE (PATCH)**.
*   Los commits de tipo **`feat`** se traducen en **lanzamientos MENORES (MINOR)**.
*   Los commits con **`BREAKING CHANGE`** (indicado con `!` o en el pie de página), **independientemente de su tipo**, se traducen en **lanzamientos MAYORES (MAJOR)**.
*   Otros tipos no tienen un efecto implícito en SemVer a menos que incluyan un `BREAKING CHANGE`.

### Reglas Clave de la Especificación (Resumen)

Basado en la especificación completa:

*   Los commits DEBEN comenzar con un tipo, seguido de un ámbito opcional, un signo de exclamación opcional, dos puntos y un espacio.
*   El tipo `feat` DEBE usarse cuando se añade una nueva funcionalidad.
*   El tipo `fix` DEBE usarse cuando se corrige un error.
*   Un ámbito PUEDE añadirse después del tipo, encerrado entre paréntesis.
*   Una descripción DEBE seguir inmediatamente después de los dos puntos y el espacio.
*   Un cuerpo más extenso PUEDE añadirse después de la descripción corta.
*   El cuerpo PUEDE contener cualquier número de párrafos separados por líneas en blanco.
*   Una o más notas al pie PUEDEN añadirse después del cuerpo.
*   Las palabras clave de las notas al pie DEBEN usar `-` en lugar de espacios, excepto para `BREAKING CHANGE`.
*   Las notas al pie PUEDEN contener espacios y líneas en blanco.
*   Los cambios disruptivos DEBEN indicarse en el prefijo tipo/ámbito (`!`) o como nota al pie (`BREAKING CHANGE:`).
*   Si se incluye como pie de página, un cambio disruptivo DEBE comenzar con el texto en mayúsculas `BREAKING CHANGE:`, seguido de una descripción.
*   Si se incluye en el prefijo, los cambios disruptivos DEBEN indicarse con un `!` inmediatamente antes de los dos puntos. Si se usa `!`, el pie de página `BREAKING CHANGE:` PUEDE omitirse.
*   Tipos distintos a `feat` y `fix` PUEDEN usarse.
*   Las unidades de información en Conventional Commits NO DEBEN tratarse como sensibles a mayúsculas/minúsculas, excepto "BREAKING CHANGE" que DEBE ser en mayúsculas.
*   "BREAKING-CHANGE" DEBE ser sinónimo de "BREAKING CHANGE" cuando se usa como palabra clave en una nota al pie.

### Beneficios de Usar Conventional Commits

Implementar Conventional Commits ofrece múltiples ventajas:

*   **Generación Automática de CHANGELOGs**: Herramientas pueden analizar el historial de commits y generar archivos `CHANGELOG` legibles y organizados por versión.
*   **Versionado Automático**: Permite que herramientas determinen automáticamente la versión SemVer adecuada (MAJOR, MINOR, PATCH) basándose en los tipos de commits y los cambios disruptivos.
*   **Comunicación Clara y Colaboración Mejorada**: Los mensajes consistentes y estructurados facilitan que los miembros del equipo entiendan la naturaleza y el propósito de los cambios rápidamente.
*   **Revisiones de Código Eficientes**: Al tener mensajes claros, los revisores pueden entender mejor el contexto de los cambios propuestos.
*   **Facilita la Contribución**: Un historial estructurado es más fácil de explorar, lo que ayuda a nuevos colaboradores a entender el proyecto.
*   **Automatización de Procesos**: Permite activar automáticamente procesos de build y despliegue/publicación.
*   **Fomenta Commits Atómicos**: La convención anima a realizar commits que contengan un solo tipo de cambio (ej. no mezclar un `fix` con un `feat` o `style`), lo que simplifica las revisiones, los rebases y la resolución de conflictos.

### Consideraciones Adicionales y Mejores Prácticas

*   **Múltiples Cambios**: Si un commit contiene más de un tipo de cambio, RECOMIENDA separarlos en commits distintos si es posible. Si se combinan (ej. usando squash en un PR), asegúrate de que el mensaje del merge commit refleje todos los cambios relevantes, posiblemente listándolos en el cuerpo o pie de página.
*   **Flujos de Trabajo con Pull Requests (PR)**: En equipos, es una práctica común que los commits individuales durante el desarrollo sean menos estrictos, pero se **enfoque la aplicación de la convención en el título (y cuerpo) del PR**, especialmente al usar flujos de trabajo de "squash + merge". El título del PR se convierte en el mensaje del commit final en la rama principal.
*   **Integración con Sistemas de Gestión de Tareas**: Es muy útil incluir referencias a los tickets o issues relevantes en el mensaje del commit. Esto facilita la trazabilidad entre el código y la tarea. Como se mencionó, RECOMIENDA poner estas referencias en el pie de página o el cuerpo en lugar del título.
*   **Fase de Desarrollo Inicial**: RECOMIENDA actuar como si el producto ya estuviera lanzado. Tus compañeros de equipo, o tú mismo en el futuro, necesitarán saber qué cambios se hicieron.
*   **Corrección de Errores en Commits**: Si usas un tipo incorrecto, puedes corregirlo usando `git rebase -i` antes de hacer push o fusionar. Si un commit no cumple la especificación y llega al historial, simplemente significa que las herramientas basadas en la especificación lo ignorarán.

### ¡Añadiendo Emojis! (Para hacerlo más atractivo visualmente)

Aquí es donde integramos tu requisito de los emojis. Si bien **los emojis no son parte de la especificación oficial de Conventional Commits** [Basado en la ausencia en las reglas detalladas y especificación], son una **convención complementaria** a menudo utilizada **junto con** Conventional Commits para añadir un toque visual y permitir identificar el tipo de cambio de un vistazo rápido.

*   La convención **Gitemoji** es un ejemplo de esto, donde un emoji representa el tipo de commit.
*   Puedes añadir el emoji **al principio de la línea de descripción**, antes del tipo, o incluso **en lugar del tipo** si tu equipo lo acuerda (aunque usarlo *además* del tipo es más compatible con las herramientas de Conventional Commits).

Aquí tienes algunos ejemplos comunes basados en la convención Gitemoji mencionada, usándolos **antes del tipo**:

*   ✨ **`feat`**: Una nueva característica.
    *   `✨ feat: add login functionality` [Similar a 21, 72]
*   🐛 **`fix`**: Corrección de un error.
    *   `🐛 fix(ui): resolve button alignment issue` [Similar a 21, 73]
*   📚 **`docs`**: Cambios en la documentación.
    *   `📚 docs(readme): update installation steps` [Similar a 21, 73]
*   💡 **`style`**: Cambios de estilo, formato.
    *   `💡 style(css): apply consistent spacing` [Similar a 21, 73]
*   ♻️ **`refactor`**: Refactorización de código.
    *   `♻️ refactor(backend): improve API response handling` [Similar a 21, 74]
*   ✅ **`test`**: Añadir o modificar pruebas.
    *   `✅ test(api): add unit tests for user routes` [Similar a 21, 74]
*   🧹 **`chore`**: Tareas de mantenimiento.
    *   `🧹 chore(deps): update eslint to v8.5.0` [Similar a 22, 75]
*   📦 **`build`**: Cambios en el sistema de compilación.
    *   `📦 build(docker): update Dockerfile` [Similar a 66, 75]
*   ⚙️ **`ci`**: Cambios en la CI/CD.
    *   `⚙️ ci: add GitHub Actions workflow` [Similar a 59, 76]
*   🚀 **`perf`**: Mejoras de rendimiento.
    *   `🚀 perf: optimize database queries` [Similar a 74]
*   ⏪ **`revert`**: Revertir un commit.
    *   `⏪ revert: revert commit a1b2c3d` [Similar a 66, 76]
*   🚨 **`BREAKING CHANGE`**: Si el commit es disruptivo (puedes añadir el emoji *además* de usar `!` o el pie de página).
    *   `🚨 feat!: send an email to the customer` [Similar a 44]
    *   `🚨 chore: upgrade library to v2.0.0`
        `BREAKING CHANGE: The API has changed.` [Similar a 89]

Usar emojis es una preferencia del equipo y puede hacer el historial de commits más **visual y atractivo**.

### Herramientas de Apoyo para Usar Conventional Commits (y Emojis)

Existen diversas herramientas que facilitan la adopción y aplicación de Conventional Commits:

*   **Herramientas de CLI (Línea de Comandos)**:
    *   **Commitizen**: Te guía paso a paso para crear mensajes de commit que sigan la convención.
    *   **Commitlint**: Permite configurar reglas y **validar** tus mensajes de commit. Puedes usarlo con Git hooks para asegurar que solo se acepten commits válidos.
    *   **Husky**: Facilita la configuración de Git hooks (como `commit-msg`) para ejecutar herramientas como Commitlint antes de que se cree un commit.
    *   **Cocogitto**: Una caja de herramientas CLI para Conventional Commits y SemVer. Ayuda a crear commits verificados, maneja el auto-versionado, genera changelogs y se integra con GitHub.
*   **Herramientas de Automatización de Releases**:
    *   **Semantic Release**: Automatiza completamente el versionado y la generación de changelogs basándose en los commits convencionales. Puede publicarte automáticamente en registros de paquetes, crear releases en GitHub, etc..
    *   **Conventional Changelog**: Herramientas específicas para generar archivos `CHANGELOG` a partir de los commits.
*   **Extensiones/Plugins para IDEs**:
    *   Muchos IDEs (como VSCode, IDEA) tienen plugins que te ayudan a escribir mensajes de commit convencionales, a menudo incluyendo soporte para emojis. La extensión **Conventional Commit para VSCode** es un ejemplo que ayuda con diálogos interactivos.
*   **Integración con Plataformas (GitHub, GitLab, etc.)**:
    *   Se pueden configurar **GitHub Actions** u otras tuberías de CI/CD para validar los títulos de los PRs o los mensajes de commit (ej. usando Commitlint o herramientas como Cocogitto).
