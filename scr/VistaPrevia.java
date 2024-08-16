import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;


public class VistaPrevia {

    private ImageView vistaPreviaImagen;
    private GridPane protoboard;
    private final int numFilas;
    private final int numColumnas;
    private boolean orientacionHorizontal = true; // Orientación inicial
    private double anguloRotacion = 90; // Ángulo de rotación actual

    public VistaPrevia(GridPane protoboard, int numFilas, int numColumnas) {
        if (protoboard == null) {
            throw new IllegalArgumentException("El protoboard no puede ser nulo");
        }
        this.protoboard = protoboard;
        this.numFilas = numFilas;
        this.numColumnas = numColumnas;

        // Configurar el manejador de eventos para las teclas
        if (protoboard.getScene() != null) {
            protoboard.getScene().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.H) {
                    orientacionHorizontal = true;
                    actualizarVistaPrevia(); // Actualizar la vista previa con la nueva orientación
                } else if (e.getCode() == KeyCode.V) {
                    orientacionHorizontal = false;
                    actualizarVistaPrevia(); // Actualizar la vista previa con la nueva orientación
                } else if (e.getCode() == KeyCode.R) {
                    rotarVistaPrevia(); // Rotar la pieza
                }
            });
        }
    }

    public void seleccionarComponente(String tipoComponente) {
        Image imagen;
        switch (tipoComponente) {
            case "LED":
                imagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\scr\\resources\\led.jpg");
                break;
            default:
                return;
        }

        if (vistaPreviaImagen != null) {
            protoboard.getChildren().remove(vistaPreviaImagen);
        }

        vistaPreviaImagen = new ImageView(imagen);
        actualizarVistaPrevia(); // Configura el tamaño inicial según la orientación

        protoboard.setOnMouseMoved(e -> {
            int columna = (int) (e.getX() / (protoboard.getWidth() / numColumnas));
            int fila = (int) (e.getY() / (protoboard.getHeight() / numFilas));
            actualizarVistaPrevia(fila, columna);
        });

        protoboard.setOnMouseClicked(e -> {
            protoboard.setOnMouseMoved(null); // Detener el movimiento de la vista previa
            vistaPreviaImagen = null; // Restablecer la vista previa de la imagen
        });
    }

    private void actualizarVistaPrevia() {
        if (vistaPreviaImagen != null) {
            protoboard.getChildren().remove(vistaPreviaImagen);
        }

        vistaPreviaImagen.setFitHeight(orientacionHorizontal ? 40 : 20);
        vistaPreviaImagen.setFitWidth(orientacionHorizontal ? 20 : 40);

        vistaPreviaImagen.setRotate(anguloRotacion); // Aplicar rotación inicial

        if (!protoboard.getChildren().contains(vistaPreviaImagen)) {
            protoboard.getChildren().add(vistaPreviaImagen);
        }
    }

    private void actualizarVistaPrevia(int fila, int columna) {
        GridPane.setColumnIndex(vistaPreviaImagen, columna);
        GridPane.setRowIndex(vistaPreviaImagen, fila);

        if (orientacionHorizontal) {
            GridPane.setColumnSpan(vistaPreviaImagen, 2);
            GridPane.setRowSpan(vistaPreviaImagen, 1);
        } else {
            GridPane.setColumnSpan(vistaPreviaImagen, 1);
            GridPane.setRowSpan(vistaPreviaImagen, 2);
        }

        if (!protoboard.getChildren().contains(vistaPreviaImagen)) {
            protoboard.getChildren().add(vistaPreviaImagen);
        }

        vistaPreviaImagen.setRotate(anguloRotacion); // Aplicar rotación actual
    }

    private void rotarVistaPrevia() {
        if (vistaPreviaImagen != null) {
            anguloRotacion += 90; // Incrementar el ángulo de rotación
            if (anguloRotacion >= 360) {
                anguloRotacion -= 360; // Mantener el ángulo dentro del rango 0-359
            }
            vistaPreviaImagen.setRotate(anguloRotacion); // Aplicar la nueva rotación
        }
    }
}
