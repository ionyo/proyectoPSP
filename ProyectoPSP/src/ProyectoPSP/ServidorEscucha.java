/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProyectoPSP;

import ProyectoPSP.Servidor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ionyo
 */
public class ServidorEscucha {

    private static int puerto = 4444;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(puerto);
        Socket cliente;
        while (true) {
            cliente = server.accept();
            System.out.println("Cliente conectado");
            Servidor se = new Servidor(cliente);
            se.run();
        }
    }

}
