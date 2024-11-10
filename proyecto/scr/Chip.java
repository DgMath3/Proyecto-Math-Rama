import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Chip {
    private final ImageView[] imageViews;
    private final int filaInicio;
    private final int columnaInicio;
    private final int filaFin;
    private final int columnaFin;
    private final int largo;
    private final String id; 

    public Chip(int filaInicio, int columnaInicio, int filaFin, int columnaFin, int largo, String id) {
        this.filaInicio = filaInicio;
        this.columnaInicio = columnaInicio;
        this.filaFin = filaFin;
        this.columnaFin = columnaFin;
        this.largo = largo;
        this.id = id; 
        this.imageViews = new ImageView[17];
    }

    public String getId() {
        return id;
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

    public int getlargo(){
        return largo;
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

    public String getTipoChip() {
        return this.id; // Suponiendo que `tipo` es un campo en tu clase Chip que guarda "AND", "OR" o "NOT"
    }
    
}
