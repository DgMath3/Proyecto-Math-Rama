import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.image.ImageView;

public class Cable {
    private final Line linea;
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private ImageView imageView;
    private final Objeto objeto; 
    private boolean pasaEnergia;
    private final int filaInicio;
    private final int columnaInicio;
    private final int filaFin;
    private final int columnaFin;
    private final double valor;
    private final String color;

    public Cable(double startX, double startY, double endX, double endY, Paint color, Objeto objeto,ImageView imageView, boolean pasaEnergia, int filaInicio, int columnaInicio, int filaFin, int columnaFin, double valor, String color_led) {
        this.linea = new Line(startX, startY, endX, endY);
        this.linea.setStroke(color);
        this.linea.setStrokeWidth(8); // Ajusta el grosor aquí
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.imageView = imageView;
        this.valor = valor;
        this.color = color_led;
        this.objeto = objeto;
        this.pasaEnergia = pasaEnergia;
        this.filaInicio = filaInicio;
        this.columnaInicio = columnaInicio;
        this.filaFin = filaFin;
        this.columnaFin = columnaFin;
        this.linea.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
            }
        });
    }

    public String getColorled() { return color;}
    public double getStartX() { return linea.getStartX();}
    public double getStartY() { return linea.getStartY();}
    public double getEndX() { return linea.getEndX();}
    public double getEndY() { return linea.getEndY();}
    public double StartX() { return startX; }
    public double StartY() {return startY;}
    public double EndX() { return endX;}
    public double EndY() { return endY;}
    public Line getLinea() { return linea;}
    public boolean pasa() { return pasaEnergia;}
    public Objeto getObjeto() { return objeto;}
    public ImageView getImageView() { return imageView;}
    public int getFilaInicio() { return filaInicio;}
    public int getColumnaInicio() { return columnaInicio;}
    public int getFilaFin() { return filaFin;}
    public int getColumnaFin() { return columnaFin;}
    public double getvalor() { return valor;}
}
