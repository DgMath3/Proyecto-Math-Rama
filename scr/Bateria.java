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
    private Button botonRojo;
    private Button botonVerde;

    public Bateria(){
        contenedorBateria = new VBox();
        Image bateriaImagen = new Image("file:src/resources/bateria.png");

        ImageView bateriaImageView = new ImageView(bateriaImagen); // Envolver la imagen en un ImageView
        Button botonVerde = new Button("+");
        botonVerde.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        Button botonRojo = new Button("-");
        botonRojo.setStyle("-fx-background-color: red; -fx-text-fill: white;");


        contenedorBateria.getChildren().addAll(bateriaImageView, botonVerde, botonRojo); // Añadir ImageView en lugar de Image
    contenedorBateria.setAlignment(Pos.CENTER);
    contenedorBateria.setSpacing(10);
    contenedorBateria.setPadding(new Insets(10));
    }

    public VBox getContenedorBateria(){
        return contenedorBateria;
    }
}