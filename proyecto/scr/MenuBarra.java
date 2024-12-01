import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
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
        cable_r.setOnAction(e -> seleccionarObjeto("Cable_rojo", " "));

        MenuItem cable_b = new MenuItem("Cable azul");
        cable_b.setOnAction(e -> seleccionarObjeto("Cable_azul", " "));

        MenuItem led = new MenuItem("Led");
        led.setOnAction(e -> mostrarListaColores());

        MenuItem siwtch = new MenuItem("Switch (pequeño)");
        siwtch.setOnAction(e -> seleccionarObjeto("Switch", " "));

        MenuItem resistor = new MenuItem("Resistor");
        resistor.setOnAction(e -> seleccionarObjeto("resistor", " "));

        MenuItem chip = new MenuItem("Chip AND");
        chip.setOnAction(e -> chips("AND"));

        MenuItem chip2 = new MenuItem("Chip OR");
        chip2.setOnAction(e -> chips("OR"));

        MenuItem chip3 = new MenuItem("Chip NOT");
        chip3.setOnAction(e -> chips("NOT"));

        MenuItem display = new MenuItem("Display");
        display.setOnAction(e -> seleccionarObjeto("Display", " "));

        menuAgregar.getItems().addAll(cable_r, cable_b, led, siwtch, resistor, chip, chip2, chip3, display);

        menuBar.getMenus().add(menuAgregar);
    }

    private void mostrarListaColores() {
        Platform.runLater(() -> {
            // Lista de colores disponibles
            List<String> colores = Arrays.asList("Rojo", "Verde", "Azul", "Amarillo", "Azul");

            // Crear el diálogo de elección de color
            ChoiceDialog<String> dialog = new ChoiceDialog<>(colores.get(0), colores);
            dialog.setTitle("Seleccionar Color");
            dialog.setHeaderText("Elija un color para el LED");
            dialog.setContentText("Colores disponibles:");

            // Mostrar el diálogo y obtener el resultado
            Optional<String> resultado = dialog.showAndWait();
            resultado.ifPresent(colorSeleccionado -> {
                seleccionarObjeto("Led", colorSeleccionado); // Llama a una función para cambiar el color del LED
            });
        });
    }

    private void chips(String Objeto) {
        while (true) {
            int valor = (int) GestorCables.solicitarValor("Chip " + Objeto, "seleccione el tamaño\n actual: ", 7);
            if (valor > 3 && valor < 31) {
                cablear.setlargo(valor);
                break;
            }
        }
        seleccionarObjeto(Objeto, "x");
    }


    private void seleccionarObjeto(String idObjeto, String led) {

        Objeto objeto = new Objeto(idObjeto, led); // Crear un objeto con el id seleccionado
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
