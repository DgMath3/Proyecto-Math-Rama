import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class HiloGestorCables {

    private final GestorCables gestorCables;
    private final ScheduledExecutorService scheduler;
    private final GridPane gridPane;
    private volatile boolean running; // Indica si el hilo está en ejecución
    private Protoboard protoboard;
    private Controlador controlador;

    // Constructor
    public HiloGestorCables(GestorCables gestorCables, Protoboard protoboard, Controlador controlador,
            GridPane gridPane) {
        this.gestorCables = gestorCables;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.running = false;
        this.gridPane = gridPane;
        this.protoboard = protoboard;
        this.controlador = controlador;
    }

    // Método para iniciar la ejecución periódica con mayor velocidad
    public void iniciarActualizacionContinua(String[][] matrizEnergia) {
        if (!running) {
            running = true;
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    actualizarObjetos(matrizEnergia);
                    actualizarChips(matrizEnergia);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
        }
    }
    
    // Método para detener la ejecución
    public void detenerActualizacion() {
        if (!running) {
            return; // No hace nada si ya está detenido
        }
        running = false;
        scheduler.shutdownNow(); // Detiene inmediatamente todas las tareas en ejecución

        try {
            if (!scheduler.awaitTermination(0, TimeUnit.SECONDS)) {
                // Si las tareas no terminan instantáneamente, forzamos el cierre
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Método para actualizar objetos
    public void actualizarObjetos(String[][] matrizEnergia) {
        List<Cable> cables = gestorCables.obtenerCables();

        if (cables.isEmpty()) {
            return; // No hacer nada si la lista está vacía
        }
        ExecutorService executor = Executors.newFixedThreadPool(1);

        for (Cable cable : cables) {
            executor.submit(() -> {
                try {
                    procesarCable(cable, matrizEnergia);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // metodo para los chips 
    public void actualizarChips(String[][] matrizEnergia) {
        List<Chip> chips = gestorCables.obtenerChips();

        if (chips.isEmpty()) {
            return; // No hacer nada si la lista está vacía
        }
        ExecutorService executor = Executors.newFixedThreadPool(1);

        for (Chip chip : chips) {
            executor.submit(() -> {
                try {
                    procesarChips(chip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Método para procesar el cable
    private void procesarCable(Cable cable, String[][] matrizEnergia) {
        if (cable.getObjeto().getpasa()) {
            actualizarEnergia(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(),  cable.getColumnaFin(), matrizEnergia);
        }
    }

    // Método para actualizar energía respetando el estado de la batería
    private void actualizarEnergia(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
            String[][] matrizEnergia) {

        if (matrizEnergia[filaInicio][columnaInicio].equals("+") && matrizEnergia[filaFin][columnaFin].equals("|")) {
            matrizEnergia[filaFin][columnaFin] = "+";
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")
                && matrizEnergia[filaFin][columnaFin].equals("|")) {
            matrizEnergia[filaFin][columnaFin] = "-";
        } else if (matrizEnergia[filaFin][columnaFin].equals("+")
                && matrizEnergia[filaInicio][columnaInicio].equals("|")) {
            matrizEnergia[filaInicio][columnaInicio] = "+";
        } else if (matrizEnergia[filaFin][columnaFin].equals("-")
                && matrizEnergia[filaInicio][columnaInicio].equals("|")) {
            matrizEnergia[filaInicio][columnaInicio] = "-";
        }

        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        protoboard.actualizarMatriz(gridPane);
    }

    // Método para aplicar colores al protoboard
    private void aplicarColoresProtoboard(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
            String[][] matrizEnergia) {
        if (matrizEnergia[filaInicio][columnaInicio].equals("+")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.BLUE);
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.RED);
        }

        if (matrizEnergia[filaFin][columnaFin].equals("+")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.BLUE);
        } else if (matrizEnergia[filaFin][columnaFin].equals("-")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.RED);
        }

        controlador.actualizarBuses(protoboard.getGridPane());
        controlador.ActualizarProtoboard(protoboard.getGridPane());
    }

    private void procesarChips(Chip chip){
        if (true){

        }
    }
}
