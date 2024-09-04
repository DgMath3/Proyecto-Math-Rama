import javafx.scene.image.ImageView; // Asegúrate de importar ImageView
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class Cablear {
    private final GridPane gridPane;
    private final Pane drawingPane;
    private final Loc loc;
    private double startX, startY;
    private boolean drawing = false;
    private boolean cablearActivo = false;
    private Objeto objetoSeleccionado = null;
    private List<Cable> cables; // Lista para almacenar los cables

    public Cablear(GridPane gridPane, Loc loc) {
        this.gridPane = gridPane;
        this.loc = loc;
        this.drawingPane = new Pane();
        this.drawingPane.setMouseTransparent(false);
        this.gridPane.getChildren().add(drawingPane);

        // Inicializar la lista de cables
        this.cables = new ArrayList<>();

        // Asegurarse de que el tamaño del Pane de dibujo sea correcto
        this.drawingPane.setPrefSize(gridPane.getWidth(), gridPane.getHeight());

        configurarEventos();
    }

    public void configurarEventos() {
        gridPane.setOnMouseClicked(this::handleClick);
        drawingPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClickOnPane);
    }

    private void handleClick(MouseEvent event) {
        if (!cablearActivo || objetoSeleccionado == null) {
            return; // Si no está activo o no hay objeto seleccionado, no hace nada
        }

        // Esperar brevemente para asegurar que el GridPane se actualice
        PauseTransition pause = new PauseTransition(Duration.millis(50));
        pause.setOnFinished(e -> {
            // Usar el método de Loc para verificar si el clic está dentro del GridPane
            if (loc.estaDentroDelGridPane(event.getX(), event.getY())) {

                double clickX = event.getX();
                double clickY = event.getY();

                if (!drawing) {
                    startX = clickX - 5;
                    startY = clickY - 11;
                    drawing = true;
                } else {
                    dibujarCable(startX, startY, clickX - 6, clickY - 4);
                    drawing = false;
                    cablearActivo = false; // Desactiva la funcionalidad de cablear después de dibujar
                    objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
                }
            }
        });
        pause.play();
    }

    private void dibujarCable(double startX, double startY, double endX, double endY) {
        Paint color = objetoSeleccionado != null ? objetoSeleccionado.getColor() : Color.BLACK;
        ImageView imageView = objetoSeleccionado != null ? new ImageView(objetoSeleccionado.getImagen()) : null;
        
        // Crear y agregar el cable a la lista
        Cable cable = new Cable(startX, startY, endX, endY, color, imageView);
        cables.add(cable);

        drawingPane.getChildren().add(cable.getLinea());
        cable.getLinea().toFront();

        if (imageView != null) {
            double cellSize = gridPane.getWidth() / gridPane.getColumnConstraints().size();
            imageView.setFitWidth(cellSize);
            imageView.setFitHeight(cellSize);

            imageView.setLayoutX((startX + endX) / 2 - imageView.getFitWidth() / 2);
            imageView.setLayoutY((startY + endY) / 2 - imageView.getFitHeight() / 2);

            drawingPane.getChildren().add(imageView);
            imageView.toFront();
        }
    }

    private void handleMouseClickOnPane(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) { // Detecta clic derecho
            Cable cableToRemove = null;
            for (Cable cable : cables) {
                if (cable.getLinea().contains(event.getX(), event.getY())) {
                    cableToRemove = cable;
                    break;
                }
            }
            if (cableToRemove != null) {
                eliminarCable(cableToRemove);
                event.consume(); // Evita que el evento se propague
            }
        }
    }

    public void activarCablear(boolean activar) {
        this.cablearActivo = activar;
    }

    public void setObjetoSeleccionado(Objeto objeto) {
        this.objetoSeleccionado = objeto;
        this.cablearActivo = true; // Activar la funcionalidad de cablear al seleccionar un objeto
    }

    // Método para eliminar un cable
    public void eliminarCable(Cable cable) {
        drawingPane.getChildren().remove(cable.getLinea());
        if (cable.getImageView() != null) {
            drawingPane.getChildren().remove(cable.getImageView());
        }
        cables.remove(cable);
    }

    // Método para eliminar todos los cables
    public void eliminarTodosLosCables() {
        for (Cable cable : cables) {
            eliminarCable(cable);
        }
    }

    // Método para obtener la lista de cables
    public List<Cable> getCables() {
        return cables;
    }
}
