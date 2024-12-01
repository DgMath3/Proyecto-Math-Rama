import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Display {
    private int fila;
    private int columna;
    private Line[] segmentos;
    private Circle punto;
    private Rectangle rectangulo;
    public Display(int fila, int columna, Line[] segmentos, Circle punto, Rectangle rectangulo){
        this.fila = fila;
        this.columna = columna;
        this.segmentos = segmentos;
        this.punto = punto;
        this.rectangulo = rectangulo;
    }

    public int getfila(){ return fila;}
    public int getcolumna(){ return columna;}
    public Line[] getsegmentos () { return segmentos;}
    public Circle getpunto() { return punto;}
    public Rectangle getrectangulo() { return rectangulo;}
}
