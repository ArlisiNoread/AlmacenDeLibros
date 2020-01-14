package almacendelibros;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.json.simple.JSONObject;

public class Almacen {

    ArrayList<Libro> libros;
    ManejoDeLibros manejoDeLibros;
    int puerto;

    public Almacen(int puerto) {
        libros = new ArrayList<>();
        manejoDeLibros = new ManejoDeLibros(this.libros);
        this.puerto = puerto;
    }

    public void activarServidor() {
        this.manejoDeLibros.inicializarYChecarArchivo();
        //new Servidor(this.puerto);
    }

}

class ManejoDeLibros {

    ArrayList<Libro> libros;

    public ManejoDeLibros(ArrayList<Libro> libros) {
        this.libros = libros;
    }

    public void inicializarYChecarArchivo() {
        try {
            File myObj = new File("listaDeLibros.txt");
            if (myObj.createNewFile()) {
                System.out.println("Archivo creado: " + myObj.getName());
            } else {
                System.out.println("El archivo ya existe.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void agregarLibro(Libro libro){
        this.libros.add(libro);
        
    }
    
    private void escribirEnArchivo(){
        
    }
}

class Servidor extends Thread {

    int puerto;

    public Servidor(int puerto) {
        this.puerto = puerto;
    }

    @Override
    public void run() {
        ServerSocket server = null;

        try {
            server = new ServerSocket(this.puerto);
            System.out.println("Servidor ejecutándose");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        while (true) {
            try {
                Socket sPrivado = server.accept();
                new ConexionPrivada(sPrivado).start();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class ConexionPrivada extends Thread {

    protected Socket socket;

    public ConexionPrivada(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        OutputStream s1out;
        try {
            s1out = this.socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(s1out);
            dos.writeUTF("");
            dos.close();
            s1out.close();
            this.socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}

class Libro {

    JSONObject json;
    String nombre, autor, tipo, descripcion;
    double precio;
}
