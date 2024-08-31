import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class Cable {
    private final Line linea;
    private final ImageView imageView;
    private final Cablear cablear; 
    private static final double GROSOR_CABLE = 4; // Ajusta el grosor 

    public Cable(double startX, double startY, double endX, double endY, Paint color, ImageView imageView, Cablear cablear) {
        this.linea = new Line(startX, startY, endX, endY);
        this.linea.setStroke(color);
        this.linea.setStrokeWidth(GROSOR_CABLE); // Establece el grosor de la línea

        this.imageView = imageView;
        this.cablear = cablear; 

        // Añadir el evento de clic al cable para eliminarlo
        this.linea.setOnMouseClicked(this::eliminarCable);
    }

    private void eliminarCable(MouseEvent event) {
        cablear.eliminarCable(this); // Llamar al método de Cablear para eliminar el cable
    }

    public Line getLinea() {
        return linea;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
