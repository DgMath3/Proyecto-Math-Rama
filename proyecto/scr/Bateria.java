import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Bateria {
    private VBox contenedorBateria;
    public ImageView bateriaImagen;
    public Button botonRojo;
    public Button botonVerde;

    public Bateria() {
        contenedorBateria = new VBox();
        bateriaImagen = new ImageView(new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\bateria.png")); 
        bateriaImagen.setFitWidth(100); 
        bateriaImagen.setPreserveRatio(true); 
        
        botonVerde = new Button("+");
        botonVerde.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        
        botonRojo = new Button("-");
        botonRojo.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        
        contenedorBateria.getChildren().addAll(botonVerde, bateriaImagen, botonRojo);

        contenedorBateria.setAlignment(Pos.CENTER);
        contenedorBateria.setSpacing(10);
        contenedorBateria.setPadding(new Insets(10));
    }

    public VBox getContenedorBateria() {
        return contenedorBateria;
    }
    
}
