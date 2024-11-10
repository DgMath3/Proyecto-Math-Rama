import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear los componentes principales
        Protoboard protoboard = new Protoboard();
        Controlador controlador = new Controlador();

        // Crear la escena
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1000, 600); // Ajusta el tamaño inicial según tus necesidades

        // Establecer un color de fondo sólido en el StackPane
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Crear la instancia de Cablear con el Protoboard y el Loc
        Loc loc = new Loc(protoboard.getGridPane(), Color.BLACK, null);
        GestorCables gestorcables = new GestorCables(protoboard.getGridPane(), loc, protoboard, controlador);

        HiloGestorCables hiloGestor = new HiloGestorCables(gestorcables, protoboard, controlador,
                protoboard.getGridPane());

        // Crear la barra de menú con la instancia de Cablear
        MenuBarra menuBarra = new MenuBarra(gestorcables);

        // Crear una nueva instancia de MenuOpciones
        MenuOpciones menuOpciones = new MenuOpciones(gestorcables, protoboard, controlador);

        Bateria bateria = new Bateria(loc, protoboard, controlador, protoboard.getGridPane(), gestorcables, hiloGestor);

        GestorConexiones gestorConexiones = new GestorConexiones(gestorcables);

        // Crear un AnchorPane para organizar los componentes
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(1000, 600); // Tamaño preferido del AnchorPane

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

        // Establecer un ancho fijo para el GridPane
        protoboard.getGridPane().setPrefWidth(1000); // Ancho fijo deseado

        // Añadir el GridPane y la batería al HBox
        contentLayout.getChildren().addAll(protoboard.getGridPane(), bateria.getContenedorBateria());

        // Añadir el HBox al VBox principal
        mainLayout.getChildren().add(contentLayout);

        // Añadir el VBox al AnchorPane
        anchorPane.getChildren().addAll(mainLayout);

        // Establecer anclajes para que el AnchorPane se ajuste a la ventana
        AnchorPane.setTopAnchor(mainLayout, 0.0);
        AnchorPane.setLeftAnchor(mainLayout, 0.0);
        AnchorPane.setRightAnchor(mainLayout, 0.0);
        AnchorPane.setBottomAnchor(mainLayout, 0.0);

        // Añadir el AnchorPane al StackPane raíz
        root.getChildren().add(anchorPane);

        // Configurar el Stage
        primaryStage.setTitle("Simulador de Protoboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000); // Establecer ancho mínimo de la ventana
        primaryStage.setMinHeight(600); // Establecer altura mínima de la ventana
        primaryStage.setResizable(true); // Permitir maximizar
        primaryStage.show();

        hiloGestor.iniciarActualizacionContinua(protoboard.getMatriz());

        // Manejar el cierre de la aplicación
        primaryStage.setOnCloseRequest(event -> {
            hiloGestor.detenerActualizacion();
            gestorConexiones.detener();
        });

        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Crear una pausa de 1 segundo
                PauseTransition pause = new PauseTransition(Duration.millis(100));
                pause.setOnFinished(event -> gestorcables.actualizar());

                // Iniciar la pausa
                pause.play();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
