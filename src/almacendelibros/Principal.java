package almacendelibros;

import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        new Almacen(1234).activarServidor();
        //new Principal().menu();
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
                    break;
                case "2":
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
