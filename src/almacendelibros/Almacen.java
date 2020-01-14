package almacendelibros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        //this.manejoDeLibros.agregarLibrosDeEjemplo();
        //this.manejoDeLibros.escribirEnArchivo();
        new Servidor(this.puerto, this.libros, this.manejoDeLibros).start();
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
            Scanner myReader = new Scanner(myObj);

            if (!myReader.hasNextLine()) {
                myReader.close();
                FileWriter myWriter = new FileWriter("listaDeLibros.txt");
                JSONObject job = new JSONObject();
                job.put("libros", new JSONArray());
                System.out.println(job.toJSONString());
                myWriter.write(job.toJSONString());
                myWriter.close();
            } else {
                myReader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        leerDeArchivo();

    }

    public void agregarLibro(Libro libro) {
        this.libros.add(libro);
        this.escribirEnArchivo();
    }

    public void agregarLibrosDeEjemplo() {
        this.libros.add(new Libro("Harry", "Potta", "Magia", "Patata", 58.5));
        this.libros.add(new Libro("Harry3", "Potta2", "Magia1", "Patata1", 56.5));
        this.libros.add(new Libro("Harr223y3", "Potsdta2", "Magdfia1", "Patsdfata1", 526.5));

    }

    public void imprimirLibros() {
        System.out.println("Imprimiendo libros:");
        this.libros.forEach((l) -> System.out.println(l));
    }

    public void leerDeArchivo() {
        String cadenaJson = "";
        try {
            File myObj = new File("listaDeLibros.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                cadenaJson += data;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(cadenaJson);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        JSONArray jArray = (JSONArray) json.get("libros");

        for (int x = 0; x < jArray.size(); x++) {
            JSONObject subObj = new JSONObject();
            subObj = (JSONObject) jArray.get(x);
            this.libros.add(new Libro((String) subObj.get("nombre"), (String) subObj.get("autor"), (String) subObj.get("tipo"), (String) subObj.get("descripcion"), (double) subObj.get("precio")));
        }

    }

    public void escribirEnArchivo() {
        JSONObject obj = new JSONObject();
        JSONArray list = new JSONArray();

        for (int x = 0; x < libros.size(); x++) {
            JSONObject subObj = new JSONObject();
            subObj.put("nombre", libros.get(x).nombre);
            subObj.put("autor", libros.get(x).autor);
            subObj.put("tipo", libros.get(x).tipo);
            subObj.put("descripcion", libros.get(x).descripcion);
            subObj.put("precio", libros.get(x).precio);
            list.add(subObj);
        }
        obj.put("libros", list);

        String cadenaJson = obj.toJSONString();
        try {
            FileWriter myWriter = new FileWriter("listaDeLibros.txt");
            //System.out.println("Revisión de acentos " + cadenaJson);
            myWriter.write(cadenaJson);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class Servidor extends Thread {

    int puerto;
    ArrayList<Libro> libros;
    ManejoDeLibros manejoDeLibros;
    boolean bandera = true;

    public Servidor(int puerto, ArrayList<Libro> libros, ManejoDeLibros manejoDeLibros) {
        this.puerto = puerto;
        this.libros = libros;
        this.manejoDeLibros = manejoDeLibros;
    }
    
    public void apagarServidor(){
        System.exit(0);
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
        while (this.bandera) {
            try {
                Socket sPrivado = server.accept();
                new ConexionPrivada(sPrivado, this.libros, this.manejoDeLibros, this).start();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class ConexionPrivada extends Thread {

    protected Socket socket;
    ArrayList<Libro> libros;
    ManejoDeLibros manejoDeLibros;
    Servidor servidor;

    public ConexionPrivada(Socket socket, ArrayList<Libro> libros, ManejoDeLibros manejoDeLibros, Servidor servidor) {
        this.socket = socket;
        this.libros = libros;
        this.manejoDeLibros = manejoDeLibros;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        InputStream slin;
        OutputStream s1out;
        try {
            slin = this.socket.getInputStream();
            DataInputStream dis = new DataInputStream(slin);
            String st = new String(dis.readUTF());
            JSONParser par = new JSONParser();
            JSONObject jCadena = (JSONObject) par.parse(st);
            st = (String) jCadena.get("opcion");
            String ret = "";

            if (st.equals("1")) {
                JSONObject obj = new JSONObject();
                JSONArray list = new JSONArray();

                for (int x = 0; x < libros.size(); x++) {
                    JSONObject subObj = new JSONObject();
                    subObj.put("nombre", libros.get(x).nombre);
                    subObj.put("autor", libros.get(x).autor);
                    subObj.put("tipo", libros.get(x).tipo);
                    subObj.put("descripcion", libros.get(x).descripcion);
                    subObj.put("precio", libros.get(x).precio);
                    list.add(subObj);
                }
                obj.put("libros", list);

                String cadenaJson = obj.toJSONString();
                ret += cadenaJson;

            } else if (st.equals("2")) {
                JSONObject libroAMeter = (JSONObject) jCadena.get("libro");

                this.manejoDeLibros.agregarLibro(new Libro((String) libroAMeter.get("nombre"),
                        (String) libroAMeter.get("autor"), (String) libroAMeter.get("tipo"),
                        (String) libroAMeter.get("descripcion"),
                        (double) libroAMeter.get("precio")));

                ret = "Libro Agregado";

            } else if (st.equals("4")) {
                ret = "Servidor Apagado";
                servidor.apagarServidor();
            }

            s1out = this.socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(s1out);
            dos.writeUTF(ret);
            
            dis.close();
            slin.close();
            dos.close();
            s1out.close();
            this.socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ConexionPrivada.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
