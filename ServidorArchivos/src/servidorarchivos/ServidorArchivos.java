package servidorarchivos;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ServidorArchivos {

    private static final int PUERTO = 5000;
    private static final String CARPETA_ARCHIVOS = "archivos_compartidos";

    public static void main(String[] args) throws IOException {
        ServerSocket servidorSocket = new ServerSocket(PUERTO);
        System.out.println("[SERVIDOR] Escuchando en el puerto " + PUERTO);

        while (true) {
            Socket clienteSocket = servidorSocket.accept();
            System.out.println("[SERVIDOR] Cliente conectado: "
                    + clienteSocket.getInetAddress() + ":" + clienteSocket.getPort());
            new HiloCliente(clienteSocket).start();
        }
    }

    static class HiloCliente extends Thread {
        private final Socket socket;

        public HiloCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream()), true);
            ) {
                String peticion;
                while ((peticion = entrada.readLine()) != null) {
                    String[] partes = peticion.split("\\s+", 2);
                    String comando = partes[0].toUpperCase();

                    switch (comando) {
                        case "LIST":
                            File carpeta = new File(CARPETA_ARCHIVOS);
                            File[] archivos = carpeta.listFiles();
                            StringBuilder sb = new StringBuilder("Archivos disponibles:\n");
                            for (File f : archivos) {
                                if (f.isFile()) {
                                    sb.append("  - ").append(f.getName())
                                      .append(" (").append(f.length()).append(" bytes)\n");
                                }
                            }
                            salida.println(sb.toString().trim());
                            salida.println("FIN_RESPUESTA");
                            break;

                        case "GET":
                            String nombreArchivo = partes[1].trim();
                            File archivo = new File(CARPETA_ARCHIVOS + File.separator + nombreArchivo);
                            if (!archivo.exists()) {
                                salida.println("ERROR: El archivo '" + nombreArchivo + "' no existe.");
                                salida.println("FIN_RESPUESTA");
                            } else {
                                String contenido = new String(Files.readAllBytes(archivo.toPath()));
                                salida.println("ARCHIVO|" + nombreArchivo + "|" + archivo.length());
                                salida.println(contenido);
                                salida.println("FIN_RESPUESTA");
                                System.out.println("[SERVIDOR] Archivo '" + nombreArchivo + "' enviado.");
                            }
                            break;

                        case "FIN":
                            salida.println("Conexion finalizada.");
                            salida.println("FIN_RESPUESTA");
                            socket.close();
                            return;
                    }
                }
            } catch (IOException e) {
                System.out.println("[SERVIDOR] Cliente desconectado.");
            }
        }
    }
}

