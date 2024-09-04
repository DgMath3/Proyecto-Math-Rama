import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.image.ImageView;

public class Cable {
    private final Line linea;
    private final ImageView imageView;

    public Cable(double startX, double startY, double endX, double endY, Paint color, ImageView imageView) {
        this.linea = new Line(startX, startY, endX, endY);
        this.linea.setStroke(color);
        this.linea.setStrokeWidth(6); // Ajusta el grosor aquí

        this.imageView = imageView;

        // Asegúrate de que el cable pueda detectar clics
        this.linea.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Detecta clic derecho
                event.consume(); // Evita que el clic se propague
            }
        });
    }

    public Line getLinea() {
        return linea;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
