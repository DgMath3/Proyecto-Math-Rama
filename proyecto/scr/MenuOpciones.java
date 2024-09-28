import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuOpciones {
    private final MenuBar menuBar;

    public MenuOpciones(GestorCables cables) {
        menuBar = new MenuBar();
        crearMenu();
    }

    private void crearMenu() {
        // Crear un menú de opciones
        Menu menuOpciones = new Menu("Opciones");

        // Opción para abrir configuraciones
        MenuItem configuracionesItem = new MenuItem("Configuraciones");
        configuracionesItem.setOnAction(e -> mostrarConfiguraciones());

        // Opción para abrir ayuda
        MenuItem ayudaItem = new MenuItem("Ayuda");
        ayudaItem.setOnAction(e -> mostrarAyuda());


        // Añadir las opciones al menú
        menuOpciones.getItems().addAll(configuracionesItem, ayudaItem);

        // Añadir el menú a la barra de menú
        menuBar.getMenus().add(menuOpciones);
    }

    private void mostrarConfiguraciones() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Configuraciones");
        alert.setHeaderText("Opciones de Configuración");
        alert.setContentText("Aquí puedes ajustar las configuraciones de la aplicación.");
        alert.showAndWait();
    }

    private void mostrarAyuda() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Ayuda");
        alert.setHeaderText("Sección de Ayuda");
        alert.setContentText("Aquí puedes encontrar información sobre cómo usar la aplicación.");
        alert.showAndWait();
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
