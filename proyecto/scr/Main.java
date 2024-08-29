import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear los componentes
        Protoboard protoboard = new Protoboard();
        Bateria bateria = new Bateria();
        Loc loc = new Loc(protoboard.getGridPane(), Color.BLACK, Color.GRAY, 1);
        Cablear cablear = new Cablear(protoboard.getGridPane(), loc);
        MenuBarra menuBarra = new MenuBarra(cablear);

        // Cargar la imagen de fondo
        Image fondoImagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\fondo.png"); // Asegúrate de que la ruta sea correcta
        ImageView fondoImageView = new ImageView(fondoImagen);
        fondoImageView.setFitWidth(920);  // Ajustar el tamaño al de la escena
        fondoImageView.setFitHeight(600);

        // Crear un contenedor StackPane para poner el fondo
        StackPane fondoPane = new StackPane();
        fondoPane.getChildren().add(fondoImageView);
        
        // Crear el contenedor BorderPane para los componentes
        BorderPane root = new BorderPane();
        root.setTop(menuBarra.getMenuBar());
        root.setCenter(protoboard.getGridPane());
        root.setRight(bateria.getContenedorBateria());

        // Añadir el contenedor BorderPane al StackPane
        fondoPane.getChildren().add(root);

        // Crear la escena
        Scene scene = new Scene(fondoPane, 920, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulador de Protoboard");

        // Configurar el Stage
        primaryStage.setResizable(false); // Desactiva el redimensionamiento de la ventana
        primaryStage.setFullScreen(false); // Asegura que el modo de pantalla completa está desactivado

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
