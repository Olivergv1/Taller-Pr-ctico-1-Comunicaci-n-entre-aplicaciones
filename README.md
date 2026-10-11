El presente informe documenta el desarrollo e implementación de un sistema cliente-servidor para la transferencia de archivos. 
El proyecto consiste en dos aplicaciones independientes escritas en Java: un Servidor TCP multihilo capaz de atender múltiples clientes simultáneamente
y un Cliente TCP interactivo que permite al usuario listar y descargar archivos remotos mediante un protocolo de comandos en texto plano.
El sistema implementa comunicación entre procesos distribuidos mediante el uso de Sockets TCP/IP, donde el servidor pone a disposición una carpeta de archivos compartidos 
y el cliente puede interactuar con ellos a través de tres comandos fundamentales: LIST (para listar archivos), GET (para descargar un archivo) y FIN (para cerrar la conexión).

Para este proyecto se eligió el protocolo TCP (Transmission Control Protocol) sobre UDP por las siguientes razones:
