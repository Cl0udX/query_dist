# Query_dist usando el patrón de diseño Publisher and Suscribe / ZeroC

## Descripción
Query_dist es un proyecto cliente-servidor en Java que utiliza ZeroC como middleware y Gradle para la compilación.

### Importante:
Este proyecto presenta posibilidades de mejora en cuanto al manejo de las excepciones. El propósito de este proyecto es presentar una especie de base/guía para los nuevos usuarios de ZeroC 3.7.4 usando Java 11.

### Problema:
Se tiene una base de datos muy grande que presenta problemas de memoria a la hora de hacer alguna consulta.

### Solución:
Dividir dicha base de datos en n cantidades para distribuir la carga de las consultas. Para esto configuraremos n+1 nodos, donde n son los clientes que se encargan de las consultas y el restante es el servidor que maneja los clientes y los datos finales de las consultas.

## Requisitos:
- Java 11
- Gradle 8.6
- ZeroC 3.7.4
- Finalmente, los n+1 nodos

## Compilación:
Desde la raíz del proyecto, usar el comando `./gradlew shadowjar`. Se generarán dos JAR, uno en `client/build/libs/mi-aplicacion.jar` y otro en `server/build/libs/mi-aplicacion.jar`.

Este método de compilación empaqueta todas las dependencias al compilar, con el fin de que los JAR puedan ser ejecutados en cualquier equipo.

## Ejecución del Servidor:
En el directorio donde se encuentre el JAR del servidor, es importante que el archivo `.sql` que se quiera distribuir se encuentre en la ruta `./files/test.sql`. La estructura debe ser la siguiente:


mi-directorio/
|-- mi-aplicacion.jar
`-- files/
    `-- test.sql

Finalmente, ejecutamos `java -jar mi-aplicacion.jar`.

---------------

Básicamente, la carpeta `files` debe estar donde el usuario se encuentre desde la terminal.

Por ejemplo, si ejecutas el servidor desde la carpeta raíz del proyecto, sería así:

`java -jar server/build/libs/mi-aplicacion.jar`

Y la carpeta `files` debe estar en la carpeta raíz del proyecto, es decir, donde estás parado desde la terminal:

`query_dist/
├── files/
|   └── test.sql
└── server/
    └── build/
        └── libs/
            └── mi-aplicacion.jar`

## Ejecución en los Clientes:
`java -jar mi-aplicacion -h <ip-server> -d prod`

Si el parámetro `-h` no se pasa, por defecto el cliente tratará de conectarse con `localhost`. Si el parámetro `-d` no se pasa, por defecto el cliente usará la configuración de producción `prod-properties.cfg`.

Las configuraciones tanto del servidor como del cliente se encuentran en los archivos `.cfg` de ambos proyectos.

La configuración para la conexión a la base de datos está del lado del cliente en el archivo `.cfg`. Del lado del cliente hay dos archivos de configuración.

Nota:`dev-` es con el que yo hacía pruebas.

### Ejecución de la Aplicación:
Cuando ya los clientes deseados estén conectados al servidor, solo queda usar el comando `run-<nombre del archivo .sql>`. El archivo resultante quedará del lado del servidor en la carpeta `/files/`.

### Importante:
Verificar que se pueda hacer ping a los clientes para garantizar su funcionamiento.

