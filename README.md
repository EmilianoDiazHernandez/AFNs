# Simulador de Autómatas Finitos No Deterministas (AFN)

Este proyecto es una aplicación de escritorio para simular el comportamiento de Autómatas Finitos No Deterministas (AFN). Permite a los usuarios definir estados, transiciones y evaluar si una cadena de entrada es aceptada por el autómata.

## Características

*   Creación y eliminación de estados.
*   Definición de estados iniciales y finales.
*   Adición de transiciones entre estados.
*   Evaluación de cadenas de entrada.
*   Interfaz gráfica intuitiva construida con Jetpack Compose.

## Tecnologías Utilizadas

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
*   **Framework UI:** [Jetpack Compose for Desktop](https://www.jetbrains.com/lp/compose-mpp/)
*   **Build Tool:** [Gradle](https://gradle.org/)

## Cómo Ejecutar

Para compilar y ejecutar la aplicación, sigue estos pasos:

1.  **Clona el repositorio:**
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd <NOMBRE_DEL_DIRECTORIO>
    ```

2.  **Asegúrate de tener permisos de ejecución para el Gradle Wrapper.** Si no los tienes, ejecútalos:
    ```bash
    chmod +x ./gradlew
    ```

3.  **Ejecuta la aplicación usando Gradle:**
    ```bash
    ./gradlew run
    ```

## Capturas de Pantalla

| | |
|:-------------------------:|:-------------------------:|
|<img width="1920" height="1080" alt="Screenshot from 2026-02-18 20-25-20" src="https://github.com/user-attachments/assets/eac346a5-e95d-40ad-a3b5-3d630bcec519" />|<img width="1920" height="1080" alt="Screenshot from 2026-02-18 20-25-39" src="https://github.com/user-attachments/assets/7f44a750-a768-47b8-b839-034f5b9e0d74" />|
| *Vista principal de la aplicación.* | *Definiendo estados y transiciones.* |
|<img width="1920" height="1080" alt="Screenshot from 2026-02-18 20-25-41" src="https://github.com/user-attachments/assets/a3c2fdd0-6f45-4750-9cc9-97cdac90e842" />|<img width="1920" height="1080" alt="Screenshot from 2026-02-18 20-25-59" src="https://github.com/user-attachments/assets/11ae1ba6-bf9f-49ee-b619-acd928feeeed" />|
| *Evaluando una cadena de entrada.* | *Resultado de la evaluación.* |
