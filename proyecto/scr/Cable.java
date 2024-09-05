import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.image.ImageView;

public class Cable {
    private final Line linea;
    private final ImageView imageView;
    private final Objeto objeto; // Nuevo atributo para asociar un objeto
    private boolean pasaEnergia;

    public Cable(double startX, double startY, double endX, double endY, Paint color, ImageView imageView, Objeto objeto, boolean pasaEnergia) {
        this.linea = new Line(startX, startY, endX, endY);
        this.linea.setStroke(color);
        this.linea.setStrokeWidth(7); // Ajusta el grosor aquí

        this.imageView = imageView;
        this.objeto = objeto; // Inicializar el objeto
        this.pasaEnergia = pasaEnergia;

        this.linea.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Detecta clic derecho
                event.consume(); // Evita que el clic se propague
            }
        });
    }

    // Métodos para obtener las coordenadas de la línea
    public double getStartX() {
        return linea.getStartX();
    }

    public double getStartY() {
        return linea.getStartY();
    }

    public double getEndX() {
        return linea.getEndX();
    }

    public double getEndY() {
        return linea.getEndY();
    }

    public Line getLinea() {
        return linea;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public boolean PasaEnergia(){
        return pasaEnergia;
    }

    public Objeto getObjeto() {
        return objeto; // Nuevo método para obtener el objeto asociado
    }
}
