import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear los componentes principales
        Protoboard protoboard = new Protoboard();
        Controlador controlador = new Controlador(); 

        // Crear la escena
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1200, 800); // Ajusta el tamaño según tus necesidades

        // Crear la instancia de Cablear con el Protoboard y el Loc
        Loc loc = new Loc(protoboard.getGridPane(), Color.BLACK, null);
        GestorCables gestorcables = new GestorCables(protoboard.getGridPane(), loc, protoboard, controlador);

        HiloGestorCables hiloGestor = new HiloGestorCables(gestorcables, protoboard, controlador, protoboard.getGridPane());

        // Crear un Timeline para actualizaciones periódicas
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            // Llama a la lógica de actualización aquí
            hiloGestor.actualizarObjetos(protoboard.getMatriz());
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Crear la barra de menú con la instancia de Cablear
        MenuBarra menuBarra = new MenuBarra(gestorcables);
        
        // Crear una nueva instancia de MenuOpciones
        MenuOpciones menuOpciones = new MenuOpciones(gestorcables, protoboard,controlador);

        Bateria bateria = new Bateria(loc, protoboard, controlador, protoboard.getGridPane(), gestorcables);

        // Crear la imagen de fondo
        Image fondoImagen = new Image("/resources/fondo.png");
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(fondoImagen, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background fondo = new Background(backgroundImage);
        root.setBackground(fondo);

        // Crear un VBox para organizar los componentes verticalmente
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setSpacing(20);
        mainLayout.setPadding(new Insets(10, 20, 10, 20));

        // Crear un HBox para organizar los menús en una fila
        HBox menuLayout = new HBox();
        menuLayout.getChildren().addAll(menuBarra.getMenuBar(), menuOpciones.getMenuBar());
        
        // Añadir el HBox de menús al VBox principal
        mainLayout.getChildren().add(menuLayout);

        // Crear un HBox para organizar el protoboard y la batería horizontalmente
        HBox contentLayout = new HBox();
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.setSpacing(50); // Espacio entre el protoboard y la batería

        // Añadir el protoboard y la batería al HBox
        contentLayout.getChildren().addAll(protoboard.getGridPane(), bateria.getContenedorBateria());

        // Añadir el HBox al VBox principal
        mainLayout.getChildren().add(contentLayout);

        // Añadir el VBox al StackPane raíz
        root.getChildren().add(mainLayout);

        // Configurar el Stage
        primaryStage.setTitle("Simulador de Protoboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            hiloGestor.detenerActualizacion(); // Detener el hilo
            // Aquí puedes realizar otras acciones de limpieza si es necesario
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
