package clientearchivos;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteArchivoss {

    private static final String HOST = "127.0.0.1";
    private static final int PUERTO = 5000;
    private static final String CARPETA_DESCARGAS = "descargas";

    public static void main(String[] args) throws IOException {
        new File(CARPETA_DESCARGAS).mkdirs();
        Scanner scanner = new Scanner(System.in);

        Socket socket = new Socket(HOST, PUERTO);
        BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter salida = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);

        System.out.println("[CLIENTE] Conectado al servidor.\n");

        while (true) {
            System.out.println("--- MENÚ DE TRANSFERENCIA DE ARCHIVOS ---");
            System.out.println("1. Listar archivos (LIST)");
            System.out.println("2. Descargar archivo (GET)");
            System.out.println("3. Salir (FIN)");
            System.out.print("Opción: ");
            String opcion = scanner.nextLine().trim();

            if (opcion.equals("1")) {
                salida.println("LIST");
                System.out.println("\n" + leerRespuesta(entrada) + "\n");

            } else if (opcion.equals("2")) {
                System.out.print("Nombre del archivo: ");
                String nombre = scanner.nextLine().trim();
                salida.println("GET " + nombre);

                String respuesta = leerRespuesta(entrada);
                if (respuesta.startsWith("ARCHIVO|")) {
                    int salto = respuesta.indexOf("\n");
                    String contenido = respuesta.substring(salto + 1);
                    PrintWriter escritor = new PrintWriter(
                            new File(CARPETA_DESCARGAS + File.separator + nombre));
                    escritor.print(contenido);
                    escritor.close();
                    System.out.println("[CLIENTE] Archivo '" + nombre + "' descargado.\n");
                } else {
                    System.out.println(respuesta + "\n");
                }

            } else if (opcion.equals("3")) {
                salida.println("FIN");
                System.out.println(leerRespuesta(entrada));
                break;
            }
        }

        socket.close();
        System.out.println("[CLIENTE] Finalizado.");
    }

    private static String leerRespuesta(BufferedReader entrada) throws IOException {
        StringBuilder respuesta = new StringBuilder();
        String linea;
        while ((linea = entrada.readLine()) != null) {
            if (linea.equals("FIN_RESPUESTA")) break;
            if (respuesta.length() > 0) respuesta.append("\n");
            respuesta.append(linea);
        }
        return respuesta.toString();
    }
}