import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Objeto {
    private String id;
    private Image imagen;
    private Color color;
    private int largo;
    private boolean pasa;
    private boolean ledActivado = false;

    public Objeto(String id) {
        this.id = id;
        cargarImagenYColor();
    }

    private void cargarImagenYColor() {
        switch (id) {
            case "Cable_azul":
                imagen = cargarImagen("/resources/nada.png");
                color = Color.DARKBLUE;
                pasa = true;
                largo = 99;
                break;
            case "Cable_rojo":
                imagen = cargarImagen("/resources/nada.png");
                color = Color.DARKRED;
                pasa = true;
                largo = 99;
                break;
            case "Led":
                imagen = cargarImagen("/resources/led_off.png");
                color = Color.DARKGRAY;
                pasa = true;
                largo = 3;
                break;
            case "Switch":
                imagen = cargarImagen("/resources/switch_off.png");
                color = Color.BLACK;
                pasa = false;
                largo = 4;
                break;
            case "SwitchOn":
                imagen = cargarImagen("/resources/switch_on.png");
                color = Color.BLACK;
                pasa = true;
                largo = 4;
                break;
            case "cablegen+":
                imagen = cargarImagen("/resources/nada.png");
                color = Color.DARKBLUE;
                pasa = true;
                largo = 99;
                break;
            case "cablegen-":
                imagen = cargarImagen("/resources/nada.png");
                color = Color.DARKRED;
                pasa = true;
                largo = 99;
                break;
            default:
                imagen = cargarImagen("/resources/default_image.png"); // Imagen por defecto
                color = Color.BLACK;
                pasa = true;
                largo = 99;
                break;
        }
    }

    private Image cargarImagen(String ruta) {
        try {
            return new Image(getClass().getResourceAsStream(ruta));
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
            return null; // O asignar una imagen por defecto
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

    public boolean getpasa() {
        return pasa;
    }

    public int getLargo() {
        return largo;
    }

    public void alternarLed() {
        if (id.equals("Led")) {
            imagen = ledActivado ? cargarImagen("/resources/led_off.png") : cargarImagen("/resources/led_on.png");
            ledActivado = !ledActivado; // Alternar el estado
        }
    }

    public boolean isLedActivado() {
        return ledActivado;
    }

    public void setLedActivado(boolean ledActivado) {
        this.ledActivado = ledActivado;
    }
}
