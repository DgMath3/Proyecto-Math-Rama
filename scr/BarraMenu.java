import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class BarraMenu {

    private MenuBar barraMenu;
    private VistaPrevia vistaPrevia;

    public BarraMenu(VistaPrevia vistaPrevia) {
        this.vistaPrevia = vistaPrevia;
        crearMenu();
    }

    private void crearMenu() {
        barraMenu = new MenuBar();
        Menu menuComponentes = new Menu("Componentes");

        // Crear un elemento de menú para el LED
        MenuItem itemLed = new MenuItem("Agregar LED");
        itemLed.setOnAction(e -> vistaPrevia.seleccionarComponente("LED"));
        menuComponentes.getItems().add(itemLed);

        // Agregar el menú de componentes al menú principal
        barraMenu.getMenus().add(menuComponentes);
    }

    public MenuBar getBarraMenu() {
        return barraMenu;
    }
}
