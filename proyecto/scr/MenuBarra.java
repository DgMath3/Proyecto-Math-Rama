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

        MenuItem chip = new MenuItem("Chip AND");
        chip.setOnAction(e -> manejarSeleccion("chip AND"));

        MenuItem chip2 = new MenuItem("Chip OR");
        chip2.setOnAction(e -> manejarSeleccion("chip OR"));

        MenuItem chip3 = new MenuItem("Chip NOT");
        chip3.setOnAction(e -> manejarSeleccion("chip NOT"));

        MenuItem sitch2 = new MenuItem("Switch (grande)");
        sitch2.setOnAction(e -> manejarSeleccion("Switch2"));

        menuAgregar.getItems().addAll(cable_r, cable_b, led, siwtch, resistor, chip, chip2, chip3,sitch2);

        menuBar.getMenus().add(menuAgregar);
    }

    private void manejarSeleccion(String idObjeto) {
        if (seleccionBloqueada) {
            return;
        }
    
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(event -> seleccionBloqueada = false);
    
        // Definir ID específico para cada chip según el menú seleccionado
        String idEspecifico;
        switch (idObjeto) {
            case "chip NOT":
                idEspecifico = "NOT";
                break;
            case "chip OR":
                idEspecifico = "OR";
                break;
            case "chip AND":
                idEspecifico = "AND";
                break;
            default:
                idEspecifico = idObjeto; // Otros elementos no cambian el ID
                break;
        }
    
        seleccionarObjeto(idEspecifico);
    
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
