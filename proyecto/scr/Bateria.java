import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

//crear clase bateria la cual usara la imagen de resources/bateria.jpg para mostrarla a la derecha del gridpane del protoboard con dos botones, uno arriba de color verde y otro abajo de color rojo
public class Bateria{
    private VBox contenedorBateria;
    public ImageView bateriaImagen;
    public Button botonRojo;
    public Button botonVerde;

    public Bateria(){
        contenedorBateria = new VBox();
        Image bateriaImagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\bateria.png"); 


        ImageView bateriaImageView = new ImageView(bateriaImagen); 
        bateriaImageView.setFitWidth(100); 
        bateriaImageView.setPreserveRatio(true); 
        Button botonVerde = new Button("+");
        botonVerde.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        Button botonRojo = new Button("-");
        botonRojo.setStyle("-fx-background-color: red; -fx-text-fill: white;");


        contenedorBateria.getChildren().addAll(botonVerde,bateriaImageView,botonRojo);

    contenedorBateria.setAlignment(Pos.CENTER);
    contenedorBateria.setSpacing(10);
    contenedorBateria.setPadding(new Insets(10));
    }

    public VBox getContenedorBateria(){
        return contenedorBateria;
    }
}
