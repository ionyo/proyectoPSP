/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProyectoPSP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author ionyo
 */
public class Cliente {

    public static final int puerto = 4444;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException {
        int opcion = 0;
        InetAddress direccion = InetAddress.getLocalHost();
        Socket servidor = new Socket(direccion, puerto);
        DataInputStream dis = null;
        DataOutputStream dos = null;
        String rutaServidor = System.getProperty("user.home");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("La ruta por defecto es" + rutaServidor);
        do {
            System.out.println("Selecciona lo que quieres hacer escribiendo el numero de la opcion:"
                    + "\n1. Listar"
                    + "\n2. Enviar"
                    + "\n3. Recibir"
                    + "\n4. Salir"
                    + "\nopcion: ");
            opcion = Integer.parseInt(br.readLine());
            dos = new DataOutputStream(servidor.getOutputStream());
            dos.writeInt(opcion);
            switch (opcion) {
                case 1:
                    dis = new DataInputStream(servidor.getInputStream());
                    int numeroArchivos = dis.readInt();
                    for (int i = 0; i < numeroArchivos; i++) {
                        dis = new DataInputStream(servidor.getInputStream());
                        System.out.println(dis.readUTF().toString());
                    }
                    break;
                case 2:
                    System.out.println("Ruta del archivo para enviar: ");
                    String rutaArchivo = br.readLine();
                    // Creamos el archivo que vamos a enviar
                    File archivo = new File(rutaArchivo);
                    // Obtenemos el tamaño del archivo            
                    int tamañoArchivo = (int) archivo.length();
                    //flujo de salida
                    dos = new DataOutputStream(servidor.getOutputStream());
                    System.out.println("Enviando Archivo: " + archivo.getName());
                    // Enviamos el nombre del archivo             
                    dos.writeUTF(archivo.getName());
                    // Enviamos el tamaño del archivo            
                    dos.writeInt(tamañoArchivo);
                    // Creamos flujo de entrada para realizar la lectura del archivo en bytes
                    FileInputStream fis = new FileInputStream(rutaArchivo);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    // Creamos el flujo de salida para enviar los datos del archivo en bytes
                    BufferedOutputStream bos = new BufferedOutputStream(servidor.getOutputStream());
                    // Creamos un array de tipo byte con el tamaño del archivo 
                    byte[] buffer = new byte[tamañoArchivo];
                    // Leemos el archivo y lo introducimos en el array de bytes             
                    bis.read(buffer);
                    // Realizamos el envio de los bytes que conforman el archivo
                    for (int i = 0; i < buffer.length; i++) {
                        bos.write(buffer[i]);
                    }
                    System.out.println("Archivo Enviado: " + archivo.getName());
                    bis.close();
                    bos.close();
                    break;
                case 3:
                    System.out.println("Nombre del archivo que quieres descargar: ");
                    String nombreArchivo = br.readLine();
                    System.out.println("Ruta donde quieres guardarlo: ");
                    String rutaGuardado = br.readLine();
                    
                    dos = new DataOutputStream(servidor.getOutputStream());
                    dos.writeUTF(nombreArchivo);

                    // Creamos flujo de entrada para leer los datos que envia el cliente
                    dis = new DataInputStream(servidor.getInputStream());
                    // Obtenemos el tamaño del archivo               
                    int tam = dis.readInt();
                    System.out.println("Recibiendo archivo " + nombreArchivo);
                    // Creamos flujo de salida, este flujo nos sirve para                
                    // indicar donde guardaremos el archivo               
                    FileOutputStream fos = new FileOutputStream(rutaGuardado + "/" + nombreArchivo);
                    //Ruta donde se va a guardar el archivo
                    BufferedOutputStream out = new BufferedOutputStream(fos);
                    BufferedInputStream in = new BufferedInputStream(servidor.getInputStream());
                    // Creamos el array de bytes para leer los datos del archivo
                    byte[] buff = new byte[tam];
                    // Obtenemos el archivo mediante la lectura de bytes enviados
                    for (int i = 0; i < buff.length; i++) {
                        buff[i] = (byte) in.read();
                    }
                    // Escribimos el archivo                
                    out.write(buff);
                    // Cerramos flujos               
                    out.flush();
                    in.close();
                    out.close();
                    break;
                default:
                    break;
            }
        } while (!"4".equals(opcion));

        servidor.close();
        dis.close();
        dos.close();

    }

}
