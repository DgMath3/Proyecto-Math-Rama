import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuOpciones {
    private final MenuBar menuBar;
    private final GestorCables gestorCables; // Referencia a GestorCables
    private final String directorioCables = "cables_data"; // Directorio dentro del proyecto
    private final Updown updown; // Referencia a Updown
    private final Protoboard protoboard;
    private final Controlador controlador;

    public MenuOpciones(GestorCables cables, Protoboard protoboard, Controlador controlador) {
        this.gestorCables = cables; // Inicializar gestorCables
        this.updown = new Updown(); // Inicializar Updown
        this.protoboard = protoboard;
        this.controlador = controlador;
        menuBar = new MenuBar();
        crearDirectorioSiNoExiste(); // Crear la carpeta de almacenamiento si no existe
        crearMenu();
    }

    // Crear el menú con las opciones de guardar, cargar y borrar
    private void crearMenu() {
        Menu menuOpciones = new Menu("Opciones");

        MenuItem guardarItem = new MenuItem("Guardar");
        guardarItem.setOnAction(e -> guardar());

        MenuItem cargarItem = new MenuItem("Cargar");
        cargarItem.setOnAction(e -> cargar());

        MenuItem borrarItem = new MenuItem("Borrar datos");
        borrarItem.setOnAction(e -> borrarDatosGuardados()); // Nuevo item para borrar

        menuOpciones.getItems().addAll(guardarItem, cargarItem, borrarItem);

        menuBar.getMenus().add(menuOpciones);
    }

    // Método para crear el directorio donde se almacenarán los archivos
    private void crearDirectorioSiNoExiste() {
        Path path = Paths.get(directorioCables);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
                System.out.println("Directorio creado: " + directorioCables);
            } catch (Exception e) {
                System.err.println("Error al crear el directorio: " + e.getMessage());
            }
        }
    }

    // Método para guardar los cables usando Updown
    private void guardar() {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Guardar Cables");
            dialog.setHeaderText("Guardar configuración de cables");
            dialog.setContentText("Por favor, ingrese el nombre del archivo:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(nombreArchivo -> {
                String rutaArchivo = directorioCables + File.separator + nombreArchivo + ".dat";
                updown.guardar(gestorCables.obtenerCables(), gestorCables.obtenerChips(), rutaArchivo);
                mostrarAlerta(AlertType.INFORMATION, "Guardar", "Éxito", "Cables guardados exitosamente en " + rutaArchivo);
            });
        });
    }

    private void cargar() {
        Platform.runLater(() -> {
            File directorio = new File(directorioCables);
            File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".dat"));

            if (archivos == null || archivos.length == 0) {
                mostrarAlerta(AlertType.ERROR, "Cargar", "Error", "No hay archivos disponibles para cargar.");
                return;
            }

            List<String> nombresArchivos = new ArrayList<>();
            for (File archivo : archivos) {
                nombresArchivos.add(archivo.getName().replace(".dat", ""));
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(nombresArchivos.get(0), nombresArchivos);
            dialog.setTitle("Cargar Cables");
            dialog.setHeaderText("Seleccione el archivo a cargar");
            dialog.setContentText("Archivos disponibles:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(nombreArchivo -> {
                // Confirmar antes de eliminar los cables antiguos
                Alert confirmacion = new Alert(AlertType.CONFIRMATION);
                confirmacion.setTitle("Confirmar carga");
                confirmacion.setHeaderText("¿Está seguro de que desea cargar nuevos cables?");
                confirmacion.setContentText("Esto eliminará todos los cables existentes.");

                Optional<ButtonType> respuesta = confirmacion.showAndWait();
                if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                    // Eliminar cables existentes solo si el usuario confirma
                    List<Cable> cablesAEliminar = new ArrayList<>(gestorCables.obtenerCables()); // Crea una copia
                    for (Cable cable : cablesAEliminar) {
                        gestorCables.eliminarCable(cable, false);
                    }
                    List<Chip> chipsAEliminar = new ArrayList<>(gestorCables.obtenerChips());
                    for (Chip chip : chipsAEliminar){
                        gestorCables.eliminarChip(chip);
                    }

                    gestorCables.eliminarEnergiaSinConexiones(protoboard.getMatriz());

                    // Cargar nuevos cables desde el archivo
                    String rutaArchivo = directorioCables + File.separator + nombreArchivo + ".dat";
                    updown.cargar(gestorCables, rutaArchivo, protoboard, controlador);
                    mostrarAlerta(AlertType.INFORMATION, "Cargar", "Éxito",
                            "Cables cargados exitosamente desde " + rutaArchivo);
                } else {
                    mostrarAlerta(AlertType.INFORMATION, "Carga cancelada", "Información",
                            "No se han cargado nuevos cables.");
                }
            });
        });
    }

    // Nuevo método para borrar archivos guardados
    private void borrarDatosGuardados() {
        Platform.runLater(() -> {
            File directorio = new File(directorioCables);
            File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".dat"));

            if (archivos == null || archivos.length == 0) {
                mostrarAlerta(AlertType.ERROR, "Borrar", "Error", "No hay archivos disponibles para borrar.");
                return;
            }

            List<String> nombresArchivos = new ArrayList<>();
            for (File archivo : archivos) {
                nombresArchivos.add(archivo.getName().replace(".dat", ""));
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(nombresArchivos.get(0), nombresArchivos);
            dialog.setTitle("Borrar Cables");
            dialog.setHeaderText("Seleccione el archivo a borrar");
            dialog.setContentText("Archivos disponibles:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(nombreArchivo -> {
                Alert confirmacion = new Alert(AlertType.CONFIRMATION);
                confirmacion.setTitle("Confirmar borrado");
                confirmacion.setHeaderText("¿Está seguro de que desea borrar este archivo?");
                confirmacion.setContentText("Esta acción no se puede deshacer.");

                Optional<ButtonType> respuesta = confirmacion.showAndWait();
                if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                    String rutaArchivo = directorioCables + File.separator + nombreArchivo + ".dat";
                    File archivoAEliminar = new File(rutaArchivo);
                    if (archivoAEliminar.delete()) {
                        mostrarAlerta(AlertType.INFORMATION, "Borrar", "Éxito", "Archivo borrado exitosamente.");
                    } else {
                        mostrarAlerta(AlertType.ERROR, "Borrar", "Error", "No se pudo borrar el archivo.");
                    }
                }
            });
        });
    }

    // Método para mostrar alertas en la interfaz
    private void mostrarAlerta(AlertType tipo, String titulo, String encabezado, String contenido) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(encabezado);
            alert.setContentText(contenido);
            alert.showAndWait();
        });
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
