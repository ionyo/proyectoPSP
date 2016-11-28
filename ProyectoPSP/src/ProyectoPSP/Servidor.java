package ProyectoPSP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ionyo
 */
public class Servidor implements Runnable {

    private String rutaServidor = System.getProperty("user.home");
    private String rutaCliente = "";
    private Socket cliente;
    private static int puerto = 4444;

    public Servidor(Socket cliente) {
        this.cliente = cliente;
    }

    public void listar(DataOutputStream dos) {
        File rutaDestino = new File(rutaServidor);
        // Array de ficheros/carpetas contenidos en el directorio
        File[] listaFicheros = rutaDestino.listFiles();
        // Enviamos al cliente la cantidad de ficheros que tenemos
        try {
            dos = new DataOutputStream(cliente.getOutputStream());
            dos.writeInt(listaFicheros.length);
            // Le enviamos la lista
            for (int i = 0; i < listaFicheros.length; i++) {
                dos.writeUTF(listaFicheros[i].getName());
            }
        } catch (IOException ex) {
            System.out.println("Algo fallo al listar");
        }

    }

    public void enviar(DataOutputStream dos, DataInputStream dis) throws IOException {
        //Recibimos el nombre del archivo
        dis = new DataInputStream(cliente.getInputStream());
        String nombreArchivo = dis.readUTF();
        String rutaArchivo = rutaServidor + "/" + nombreArchivo;
        // Creamos el archivo que vamos a enviar
        File archivo = new File(rutaArchivo);
        // Obtenemos el tamaño del archivo            
        int tamañoArchivo = (int) archivo.length();
        //flujo de salida
        dos = new DataOutputStream(cliente.getOutputStream());
        System.out.println("Enviando Archivo: " + archivo.getName());
        
        // Enviamos el tamaño del archivo            
        dos.writeInt(tamañoArchivo);
        // Creamos flujo de entrada para realizar la lectura del archivo en bytes
        FileInputStream fis = new FileInputStream(rutaArchivo);
        BufferedInputStream bis = new BufferedInputStream(fis);
        // Creamos el flujo de salida para enviar los datos del archivo en bytes
        BufferedOutputStream bos = new BufferedOutputStream(cliente.getOutputStream());
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

    }

    public void recibir(DataInputStream dis) throws IOException {

        // Creamos flujo de entrada para leer los datos que envia el cliente
        dis = new DataInputStream(cliente.getInputStream());
        // Obtenemos el nombre del archivo               
        String nombreArchivo = dis.readUTF().toString();
        // Obtenemos el tamaño del archivo               
        int tam = dis.readInt();
        System.out.println("Recibiendo archivo " + nombreArchivo);
        // Creamos flujo de salida, este flujo nos sirve para                
        // indicar donde guardaremos el archivo               
        FileOutputStream fos = new FileOutputStream(rutaServidor + "/" + nombreArchivo);
        //Ruta donde se va a guardar el archivo
        BufferedOutputStream out = new BufferedOutputStream(fos);
        BufferedInputStream in = new BufferedInputStream(cliente.getInputStream());
        // Creamos el array de bytes para leer los datos del archivo
        byte[] buffer = new byte[tam];
        // Obtenemos el archivo mediante la lectura de bytes enviados
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) in.read();
        }

        // Escribimos el archivo                
        out.write(buffer);
        // Cerramos flujos               
        in.close();
        out.close();
    }

    @Override
    public void run() {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        int opcion = 0;
        //Recibe un int con la opcion
        do {
            try {
                dis = new DataInputStream(cliente.getInputStream());
                opcion = dis.readInt();
                switch (opcion) {
                    case 1:
                        listar(dos);
                        break;
                    case 2:
                        recibir(dis);
                        break;
                    case 3:
                        enviar(dos, dis);
                        break;
                    default:
                        throw new AssertionError();
                }
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (!"4".equals(opcion));
        try {
            cliente.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
