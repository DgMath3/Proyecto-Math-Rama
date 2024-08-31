import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear los componentes principales
        Protoboard protoboard = new Protoboard();
        Bateria bateria = new Bateria();
        Loc loc = new Loc(protoboard.getGridPane(), Color.BLACK, null);

        // Crear la escena
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1200, 800); // Ajusta el tamaño según tus necesidades
        
        // Crear la instancia de Cablear con la escena
        Cablear cablear = new Cablear(protoboard.getGridPane(), loc);
        
        // Crear la barra de menú con la instancia de Cablear
        MenuBarra menuBarra = new MenuBarra(cablear);
        
        // Crear el controlador
        Controlador controlador = new Controlador();

        // Crear la imagen de fondo
        Image fondoImagen = new Image("file:C:\\Users\\Felipe\\Desktop\\proyecto\\resources\\fondo.png");
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(fondoImagen, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background fondo = new Background(backgroundImage);
        root.setBackground(fondo);

        // Crear un VBox para organizar los componentes verticalmente
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setSpacing(20);
        mainLayout.setPadding(new Insets(10, 20, 10, 20));

        // Añadir la barra de menú
        mainLayout.getChildren().add(menuBarra.getMenuBar());

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

        // Configurar los botones de la batería
        bateria.botonVerde.setOnAction(e -> {
            loc.getFilaActual();
            loc.getColumnaActual();
            protoboard.cambiarColor(loc.getFilaActual(), loc.getColumnaActual(), Color.GREEN);
            controlador.actualizarBuses(protoboard.getGridPane()); // Actualizar buses después de cambiar el color
            controlador.ActualizarProtoboard(protoboard.getGridPane()); // Actualizar protoboard después de cambiar el color
        });
        bateria.botonRojo.setOnAction(e -> {
            loc.getFilaActual();
            loc.getColumnaActual();
            protoboard.cambiarColor(loc.getFilaActual(), loc.getColumnaActual(), Color.RED);
            controlador.actualizarBuses(protoboard.getGridPane()); // Actualizar buses después de cambiar el color
            controlador.ActualizarProtoboard(protoboard.getGridPane()); // Actualizar protoboard después de cambiar el color
        });

        // Configurar el Stage
        primaryStage.setTitle("Simulador de Protoboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
