import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarra {
    private final MenuBar menuBar;
    private final Cablear cablear;

    public MenuBarra(Cablear cablear) {
        this.cablear = cablear;
        this.menuBar = new MenuBar();

        Menu menuAgregar = new Menu("Agregar");

        MenuItem cable_r = new MenuItem("Cable rojo");
        cable_r.setOnAction(e -> seleccionarObjeto("Cable_rojo"));

        MenuItem cable_b = new MenuItem("Cable azul");
        cable_b.setOnAction(e -> seleccionarObjeto("Cable_azul"));

        MenuItem led = new MenuItem("Led");
        led.setOnAction(e -> seleccionarObjeto("Led"));

        menuAgregar.getItems().addAll(cable_r,cable_b, led);

        menuBar.getMenus().add(menuAgregar);
    }

    private void seleccionarObjeto(String idObjeto) {
        Objeto objeto = new Objeto(idObjeto); // Crear un objeto con el id seleccionado
        cablear.setObjetoSeleccionado(objeto); // Establecer el objeto seleccionado en Cablear
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
