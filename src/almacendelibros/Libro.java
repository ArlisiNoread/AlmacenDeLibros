
package almacendelibros;


public class Libro {

    String nombre, autor, tipo, descripcion;
    double precio;

    public Libro(String nombre, String autor, String tipo, String descripcion, double precio) {
        this.nombre = nombre;
        this.autor = autor;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Libro{" + "nombre=" + nombre + ", autor=" + autor + ", tipo=" + tipo + ", descripcion=" + descripcion + ", precio=" + precio + '}';
    }
}
