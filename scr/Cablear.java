import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Cablear {
    private final GridPane gridPane;
    private final Pane drawingPane;
    private final Loc loc;
    private double startX, startY;
    private boolean drawing = false;
    private boolean cablearActivo = false;
    private Objeto objetoSeleccionado = null; // Objeto seleccionado para el color

    public Cablear(GridPane gridPane, Loc loc) {
        this.gridPane = gridPane;
        this.loc = loc;
        this.drawingPane = new Pane();
        this.drawingPane.setMouseTransparent(true);
        this.gridPane.getChildren().add(drawingPane);

        Eventos();
    }

    private void Eventos() {
        gridPane.setOnMouseClicked(this::handleClick);
    }

    private void handleClick(MouseEvent event) {
        if (!cablearActivo || objetoSeleccionado == null) {
            return; // Si no está activo o no hay objeto seleccionado, no hacer nada
        }

        int rowIndex = loc.getFilaActual();
        int colIndex = loc.getColumnaActual();

        if (rowIndex == -1 || colIndex == -1) return;

        double cellWidth = gridPane.getWidth() / gridPane.getColumnConstraints().size();
        double cellHeight = gridPane.getHeight() / gridPane.getRowConstraints().size();
        double cellCenterX = colIndex * cellWidth + (cellWidth / 2);
        double cellCenterY = rowIndex * cellHeight + (cellHeight / 2);

        if (!drawing) {
            startX = cellCenterX;
            startY = cellCenterY;
            drawing = true;
        } else {
            dibujarCable(startX, startY, cellCenterX, cellCenterY);
            drawing = false;
            cablearActivo = false; // Desactiva la funcionalidad de cablear después de dibujar
            objetoSeleccionado = null; // Limpiar la selección del objeto después de usarlo
        }
    }

    private void dibujarCable(double startX, double startY, double endX, double endY) {
        Line linea = new Line(startX, startY, endX, endY);
        if (objetoSeleccionado != null) {
            linea.setStroke(objetoSeleccionado.getColor()); // Usar el color del objeto
        } else {
            linea.setStroke(Color.BLACK); // Color por defecto si no hay objeto seleccionado
        }
        linea.setStrokeWidth(2);

        drawingPane.getChildren().add(linea);

        if (objetoSeleccionado != null && objetoSeleccionado.getImagen() != null) {
            ImageView imageView = new ImageView(objetoSeleccionado.getImagen());
            double cellSize = gridPane.getWidth() / gridPane.getColumnConstraints().size();
            imageView.setFitWidth(cellSize);
            imageView.setFitHeight(cellSize);

            imageView.setLayoutX((startX + endX) / 2 - imageView.getFitWidth() / 2);
            imageView.setLayoutY((startY + endY) / 2 - imageView.getFitHeight() / 2);

            drawingPane.getChildren().add(imageView);
        }
    }

    public void activarCablear(boolean activar) {
        this.cablearActivo = activar;
    }

    public void setObjetoSeleccionado(Objeto objeto) {
        this.objetoSeleccionado = objeto;
        this.cablearActivo = true; // Activar la funcionalidad de cablear al seleccionar un objeto
    }
}
