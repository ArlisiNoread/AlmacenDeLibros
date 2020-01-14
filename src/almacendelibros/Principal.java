package almacendelibros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class Principal {

    int puerto = 1234;
    String ip = "localhost";

    public static void main(String[] args) {
        new Almacen(1234).activarServidor();
        new Principal().menu();
    }

    public void menu() {
        Scanner scan = new Scanner(System.in);
        boolean flagOut = false;
        String option;
        while (!flagOut) {
            System.out.println("Menú: Seleccione opción");
            System.out.println("1.- Ver Lista De Libros");
            System.out.println("2.- Guardar Libro");
            System.out.println("3.- Borrar Libro");
            System.out.println("4.- Salir");

            option = scan.nextLine().trim();

            switch (option) {
                case "1":
                    try {
                        OutputStream s1out;
                        Socket s1 = new Socket(this.ip, this.puerto);
                        s1out = s1.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(s1out);
                        JSONObject mensaje = new JSONObject();
                        mensaje.put("opcion", "1");
                        dos.writeUTF(mensaje.toJSONString());

                        InputStream s1In = s1.getInputStream();
                        DataInputStream dis = new DataInputStream(s1In);

                        String st = new String(dis.readUTF());
                        //System.out.println(st);

                        JSONParser parser = new JSONParser();
                        JSONObject json = null;
                        try {
                            json = (JSONObject) parser.parse(st);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }

                        JSONArray jArray = (JSONArray) json.get("libros");
                        if (jArray.size() == 0) {
                            System.out.println("No hay Libros en la lista.");
                        } else {
                            ArrayList<Libro> libros = new ArrayList<>();
                            for (int x = 0; x < jArray.size(); x++) {
                                JSONObject subObj = new JSONObject();
                                subObj = (JSONObject) jArray.get(x);
                                libros.add(new Libro((String) subObj.get("nombre"), (String) subObj.get("autor"), (String) subObj.get("tipo"), (String) subObj.get("descripcion"), (double) subObj.get("precio")));
                            }

                            libros.forEach((l) -> System.out.println(l));
                        }

                        dos.close();
                        s1out.close();
                        dis.close();
                        s1In.close();
                        s1.close();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "2":
                    //Guardar Libro
                    System.out.println("Nombre: ");
                    String nombre = scan.nextLine();
                    System.out.println("Autor: ");
                    String autor = scan.nextLine();
                    System.out.println("Tipo: ");
                    String tipo = scan.nextLine();
                    System.out.println("Descripcion: ");
                    String descripcion = scan.nextLine();
                    System.out.println("Precio:");
                    double precio = Double.parseDouble(scan.nextLine());

                    JSONObject jcadena = new JSONObject();
                    jcadena.put("nombre", nombre);
                    jcadena.put("autor", autor);
                    jcadena.put("tipo", tipo);
                    jcadena.put("descripcion", descripcion);
                    jcadena.put("precio", precio);

                    JSONObject jmensaje = new JSONObject();
                    jmensaje.put("opcion", "2");
                    jmensaje.put("libro", jcadena);

                    OutputStream s1out;
                    Socket s1 = null;
                    try {
                        s1 = new Socket(this.ip, this.puerto);
                        s1out = s1.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(s1out);
                        dos.writeUTF(jmensaje.toJSONString());

                        InputStream s1In = s1.getInputStream();
                        DataInputStream dis = new DataInputStream(s1In);
                        System.out.println(new String(dis.readUTF()));

                        dos.close();
                        s1out.close();
                        dis.close();
                        s1In.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    break;
                case "3":
                    break;
                case "4":
                    System.out.println("");
                    System.out.println("Cerrando Sistema");
                    flagOut = true;
                    break;
            }

        }

    }
}
