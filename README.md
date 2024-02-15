# Query_dist usando el patron de diseño Publisher and Suscribe / ZeroC
## query_dist un proyecto cliente-servidor en Java que usa ZeroC como midelware y Gradle para la compilacion.

Importante: Este proyecto presenta posibilidades de mejora en cuanto al manejo de las ecepcciones, los fines de este proyecto es presentar una especie de base/guia para los nuevos usuarios de ZeroC 3.7.4 usando Java 11.

Problema: Se tiene una base de datos muy grande que presenta problemas de memoria a la hora de hacer alguna consulta.
Solucion: Dividir dicha base de datos en n cantidades para distribuir la carga de las consultas, para esto configurariamos n+1 nodos. Donde n son los clientes que se encargan de las consultas y el restante es el servidor que maneraja los clientes y los datos finales de las consultas.
------------------------------------------
Requisitos:
Java 11
Gradle 8.6
ZeroC 3.7.4
Finalmente los n+1 nodos
Compilacion:
Desde la raiz del proyecto usar el comando, -gradlew shadowjar- se generaran dos jar uno en client/build/libs/mi-aplicacion.jar y server/build/libs/mi-aplicacion.jar.
Este metodo de compilacion empaqueta todas las dependecias al compilar, esto con el fin de que los .jar puedan ser ejecutados en cualquier equipo.

Ejecucion Server:
En en directorio donde se encuente el .jar del servidor es importante que el .sql que se quiera distribuir se encuente en la ruta ./files/test.sql de tal manera que presente esta estructura.

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
