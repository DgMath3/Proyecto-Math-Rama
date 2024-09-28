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

    @SuppressWarnings("static-access")
    private void cargarImagenYColor() {
        switch (id) {
            case "Cable_azul":
                imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\nada.png");
                color = Color.DARKBLUE;
                pasa = true;
                largo = 99;
                break;
            case "Cable_rojo":
                imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\nada.png");
                color = color.DARKRED;
                pasa = true;
                largo = 99;
                break;
            case "Led":
                imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\led_off.png");
                color = Color.DARKGRAY;
                pasa = true;
                largo = 3;
                break;
            case "Switch":
                imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\switch_off.png");
                color = Color.BLACK;
                pasa = false;
                largo = 4;
                break;
            case "SwitchOn":
                imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\switch_on.png");
                color = Color.BLACK;
                pasa = true;
                largo = 4;
                break;
            case "cablegen+":
                imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\nada.png");
                color = Color.DARKBLUE;
                pasa = true;
                largo = 99;
                break;
            case "cablegen-":
                imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\nada.png");
                color = Color.DARKRED;
                pasa = true;
                largo = 99;
                break;
            default:
                imagen = null;
                color = Color.BLACK;
                pasa = true;
                largo = 99;
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

    public boolean getpasa() {
        return pasa;
    }

    public int getLargo() {
        return largo;
    }

    // Método para alternar la imagen
    public void alternarLed() {
        if (id.equals("Led")) {
            imagen = new Image("file:C:\\Users\\ramit\\OneDrive\\Escritorio\\proyecto\\resources\\led_on.png");
        }
    }

    public boolean isLedActivado() {
        return ledActivado;
    }

    public void setLedActivado(boolean ledActivado) {
        this.ledActivado = ledActivado;
    }
}
