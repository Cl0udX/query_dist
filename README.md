# Query_dist usando el patrón de diseño Publisher and Suscribe / ZeroC

## Descripción
Query_dist es un proyecto cliente-servidor en Java que utiliza ZeroC como middleware y Gradle para la compilación.

### Importante:
Este proyecto presenta posibilidades de mejora en cuanto al manejo de las excepciones. El propósito de este proyecto es presentar una especie de base/guía para los nuevos usuarios de ZeroC 3.7.4 usando Java 11.

### Problema:
Se tiene una base de datos muy grande que presenta problemas de memoria a la hora de hacer alguna consulta.

### Solución:
Dividir dicha base de datos en n cantidades para distribuir la carga de las consultas. Para esto configuraremos n+1 nodos, donde n son los clientes que se encargan de las consultas y el restante es el servidor que maneja los clientes y los datos finales de las consultas.
#------------------------------------------------------------------------------------------
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
├── mi-aplicacion.jar
└── files/
    └── test.sql

final mente ejecutamos, java -jar mi-aplicacion.jar.

Basicamente la carpeta files debe de estar donde el usario se encuente desde la terminal.

Ej: si ejecuto el server desde la carpeta root del proyecto seria asi:
java -jar server/build/libs/mi-aplicacion.jar
y la carpeta files debe de estar en la carpeta root del pryecto, osea donde esta parado desde la terminal

query_dist/
├── files/
|   └── test.sql
└── server/
    └── build/
        └── libs/
            └── mi-aplicacion.jar

Ejecucion en los clientes:
java -jar mi-aplicacion -h <ip-server> -d prod
si el parametro -h no se pasa por defecto el cliente trara de conectarse con localhost
si el parametro -d no se pasa por defecto el cliente usara la configuracion de produccion prod-properties.cfg

Las configuraciones tanto del server y del cliente se encuentran en los .cfg de ambos proyectos.
La configuracion para la conexion a la base de datos esta del lado del cliente en el .cfg
Del lado del ciente hay dos archivos de configuracion. dev- es con el que yo hacia pruebas.


Ejecucion de la aplicacion:
Cuando ya los clientes deseados esten conectados al server solo queda usar el comando
run-<nombre del archivo .sql>

El archivo resultante quedara del lado del server en la carpeta /files/.

Importante: Verificar que se pueda hacer ping a los clientes para garantizar funcionamiento.
