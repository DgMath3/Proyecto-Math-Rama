import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Objeto {
    private String id;
    private Image imagen;
    private Color color; // Añadir atributo para el color

    public Objeto(String id) {
        this.id = id;
        cargarImagenYColor();
    }

    private void cargarImagenYColor() {
        switch (id) {
            case "Cable_azul":
                imagen = new Image("file:C:\\Users\\matia\\OneDrive\\Escritorio\\proyecto\\resources\\nada.png");
                color = Color.BLUE; // Asignar un color para el cable
                break;
            case "Cable_rojo":
                imagen = new Image("file:C:\\Users\\matia\\OneDrive\\Escritorio\\proyecto\\resources\\nada.png");
                color = Color.RED; // Asignar un color para el cable
                break;
            case "Led":
                imagen = new Image("file:C:\\Users\\matia\\OneDrive\\Escritorio\\proyecto\\resources\\led.png");
                color = Color.GRAY;
                break;
            default:
                imagen = null;
                color = Color.BLACK; // Color por defecto
                break;
        }
    }

    public Image getImagen() {
        return imagen;
    }

    public Color getColor() {
        return color;
    }

    public String getId() {
        return id;
    }
}
