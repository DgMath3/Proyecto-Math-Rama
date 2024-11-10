import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarra {
    private final MenuBar menuBar;
    private final GestorCables cablear;
    private Runnable onObjetoSeleccionado;;

    public MenuBarra(GestorCables cablear) {
        this.cablear = cablear;
        this.menuBar = new MenuBar();

        Menu menuAgregar = new Menu("Agregar");

        MenuItem cable_r = new MenuItem("Cable rojo");
        cable_r.setOnAction(e -> seleccionarObjeto("Cable_rojo"));

        MenuItem cable_b = new MenuItem("Cable azul");
        cable_b.setOnAction(e -> seleccionarObjeto("Cable_azul"));

        MenuItem led = new MenuItem("Led");
        led.setOnAction(e -> seleccionarObjeto("Led"));

        MenuItem siwtch = new MenuItem("Switch (pequeño)");
        siwtch.setOnAction(e -> seleccionarObjeto("Switch"));

        MenuItem resistor = new MenuItem("Resistor");
        resistor.setOnAction(e -> seleccionarObjeto("resistor"));

        MenuItem chip = new MenuItem("Chip AND");
        chip.setOnAction(e -> seleccionarObjeto("AND"));

        MenuItem chip2 = new MenuItem("Chip OR");
        chip2.setOnAction(e -> seleccionarObjeto("OR"));

        MenuItem chip3 = new MenuItem("Chip NOT");
        chip3.setOnAction(e -> seleccionarObjeto("NOT"));

        menuAgregar.getItems().addAll(cable_r, cable_b, led, siwtch, resistor, chip, chip2, chip3);

        menuBar.getMenus().add(menuAgregar);
    }

    private void seleccionarObjeto(String idObjeto) {

        Objeto objeto = new Objeto(idObjeto); // Crear un objeto con el id seleccionado
        cablear.setObjetoSeleccionado(objeto); // Establecer el objeto seleccionado en Cablear

        if (onObjetoSeleccionado != null) {
            onObjetoSeleccionado.run();
        }

    }

    public void setOnObjetoSeleccionado(Runnable onObjetoSeleccionado) {
        this.onObjetoSeleccionado = onObjetoSeleccionado;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
