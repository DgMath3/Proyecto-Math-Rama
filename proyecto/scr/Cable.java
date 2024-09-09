import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.image.ImageView;

public class Cable {
    private final Line linea;
    private final ImageView imageView;
    private final Objeto objeto; // Nuevo atributo para asociar un objeto
    private boolean pasaEnergia;
    private final int filaInicio; // Nueva variable para la fila de inicio
    private final int columnaInicio; // Nueva variable para la columna de inicio
    private final int filaFin; // Nueva variable para la fila de fin
    private final int columnaFin; // Nueva variable para la columna de fin

    public Cable(double startX, double startY, double endX, double endY, Paint color, ImageView imageView, Objeto objeto, boolean pasaEnergia, int filaInicio, int columnaInicio, int filaFin, int columnaFin) {
        this.linea = new Line(startX, startY, endX, endY);
        this.linea.setStroke(color);
        this.linea.setStrokeWidth(7); // Ajusta el grosor aquí

        this.imageView = imageView;
        this.objeto = objeto; // Inicializar el objeto
        this.pasaEnergia = pasaEnergia;
        
        // Inicializa las filas y columnas
        this.filaInicio = filaInicio;
        this.columnaInicio = columnaInicio;
        this.filaFin = filaFin;
        this.columnaFin = columnaFin;

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

    public boolean pasa() {
        return pasaEnergia;
    }

    public Objeto getObjeto() {
        return objeto; // Nuevo método para obtener el objeto asociado
    }

    // Métodos para obtener las filas y columnas
    public int getFilaInicio() {
        return filaInicio;
    }

    public int getColumnaInicio() {
        return columnaInicio;
    }

    public int getFilaFin() {
        return filaFin;
    }

    public int getColumnaFin() {
        return columnaFin;
    }
}
