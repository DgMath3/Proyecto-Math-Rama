import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Objeto {
    private String id;
    private Image imagen;
    private Color color; 
    private boolean pasa;
    private boolean switchState;

    public Objeto(String id) {
        this.id = id;
        this.switchState = false; 
        cargarImagenYColor();
    }

    @SuppressWarnings("static-access")
    private void cargarImagenYColor() {
        switch (id) {
            case "Cable_azul":
                imagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\nada.png");
                color = Color.DARKBLUE;
                pasa = true; 
                break;
            case "Cable_rojo":
                imagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\nada.png");
                color = color.DARKRED; 
                pasa = true; 
                break;
            case "Led":
                imagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\led_off.png");
                color = Color.DARKGRAY;
                pasa = true; 
                break;
            case "Switch":
                imagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\switch_off.png"); 
                color = Color.BLACK;
                pasa = false; 
                break;
            default:
                imagen = null;
                color = Color.BLACK; 
                pasa = true; 
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

    public boolean getpasa(){
        return pasa;
    }

    // Método para alternar la imagen
    public void alternarImagen() {
        if (id.equals("Switch")) {
            switchState = !switchState; // Alternar el estado
            if (switchState) {
                imagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\switch_on.png");
                pasa = true; 
            } else {
                imagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\switch_off.png");
                pasa = false;
            }
        }
    }
}
