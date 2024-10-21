import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Chip {
    private final ImageView[] imageViews; // Arreglo para almacenar las imágenes del chip
    private final int filaInicio;
    private final int columnaInicio;
    private final int filaFin;
    private final int columnaFin;

    // Constructor
    public Chip(int filaInicio, int columnaInicio, int filaFin, int columnaFin) {
        this.filaInicio = filaInicio;
        this.columnaInicio = columnaInicio;
        this.filaFin = filaFin;
        this.columnaFin = columnaFin;
        this.imageViews = new ImageView[12];
    }

    // Método para agregar un ImageView al arreglo
    public void setImageView(int index, ImageView imageView) {
        if (index >= 0 && index < imageViews.length) {
            imageViews[index] = imageView; // Guardar el ImageView en el índice correcto
        }
    }

    // Método para obtener un ImageView por índice
    public ImageView getImageView(int index) {
        if (index >= 0 && index < imageViews.length) {
            return imageViews[index];
        }
        return null;
    }

    // Getters para las posiciones del chip
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

    // Método para eliminar las imágenes del chip
    public void eliminarImagenes() {
        for (ImageView imageView : imageViews) {
            if (imageView != null && imageView.getParent() != null) {
                ((Pane) imageView.getParent()).getChildren().remove(imageView); // Remover la imagen del Pane
            }
        }
    }
}
