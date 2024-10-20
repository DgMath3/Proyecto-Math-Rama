import javafx.animation.PauseTransition;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;

public class MenuBarra {
    private final MenuBar menuBar;
    private final GestorCables cablear;
    private Runnable onObjetoSeleccionado;
    private boolean seleccionBloqueada = false;

    public MenuBarra(GestorCables cablear) {
        this.cablear = cablear;
        this.menuBar = new MenuBar();

        Menu menuAgregar = new Menu("Agregar");

        MenuItem cable_r = new MenuItem("Cable rojo");
        cable_r.setOnAction(e -> manejarSeleccion("Cable_rojo"));

        MenuItem cable_b = new MenuItem("Cable azul");
        cable_b.setOnAction(e -> manejarSeleccion("Cable_azul"));

        MenuItem led = new MenuItem("Led");
        led.setOnAction(e -> manejarSeleccion("Led"));

        MenuItem siwtch = new MenuItem("Switch (pequeño)");
        siwtch.setOnAction(e -> manejarSeleccion("Switch"));

        MenuItem resistor = new MenuItem("Resistor");
        resistor.setOnAction(e -> manejarSeleccion("resistor"));

        MenuItem chip = new MenuItem("Chip");
        chip.setOnAction(e -> manejarSeleccion("chip"));

        menuAgregar.getItems().addAll(cable_r, cable_b, led, siwtch, resistor, chip);

        menuBar.getMenus().add(menuAgregar);
    }

    private void manejarSeleccion(String idObjeto) {
        if (seleccionBloqueada) {
            return; // Si la selección está bloqueada, ignorar el clic
        }

        // Crear un temporizador de 1 segundo para evitar múltiples selecciones rápidas
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(event -> seleccionBloqueada = false); // Desbloquear después del segundo

        // Ejecutar la selección de objeto
        seleccionarObjeto(idObjeto);

        // Bloquear nuevas selecciones y empezar el temporizador
        seleccionBloqueada = true;
        pausa.play();
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
