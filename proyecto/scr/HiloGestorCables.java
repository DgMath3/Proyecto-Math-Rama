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
    private final Protoboard protoboard;
    private final Controlador controlador;

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
        List<Chip> chips = gestorCables.obtenerChips(); // Obtener la lista de chips desde GestorCables

        if (cables.isEmpty() && chips.isEmpty()) {
            return; // No hacer nada si no hay cables ni chips
        }

        ExecutorService executor = Executors.newFixedThreadPool(1);

        // Procesar cables
        for (Cable cable : cables) {
            executor.submit(() -> {
                try {
                    procesarCable(cable, matrizEnergia);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Procesar chips
        for (Chip chip : chips) {
            executor.submit(() -> {
                try {
                    procesarChip(chip, matrizEnergia);
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

    // Método para procesar un chip
    private void procesarChip(Chip chip, String[][] matrizEnergia) {
        String tipoChip = chip.getTipoChip();

        // Lógica específica para el chip según su tipo (AND, OR, NOT)
        switch (tipoChip) {
        case "AND":
            procesarChipAND(chip, matrizEnergia);
            break;
        case "OR":
            procesarChipOR(chip, matrizEnergia);
            break;
        case "NOT":
            procesarChipNOT(chip, matrizEnergia);
            break;
        }
    }

    // Procesamiento de un chip AND
    // Procesamiento de un chip AND
    private void procesarChipAND(Chip chip, String[][] matrizEnergia) {
        int filaInicio = chip.getFilaInicio();
        int columnaInicio = chip.getColumnaInicio();
        int filaFin = chip.getFilaFin();
        int columnaFin = chip.getColumnaFin();

        // Lógica: ambas entradas deben ser positivas o negativas para producir una
        // salida
        if (matrizEnergia[filaInicio][columnaInicio].equals("+") && matrizEnergia[filaFin][columnaFin].equals("-")) {
            for (int i = columnaInicio + 1; i < columnaFin - 2; i++) {
                if (matrizEnergia[filaInicio][i].equals("") && matrizEnergia[filaInicio][i + 1].equals("")) {
                    matrizEnergia[filaFin - 1][columnaFin - 3] = "+";
                    protoboard.cambiarColor(filaFin, columnaFin, Color.BLUE);
                    controlador.actualizarBuses(protoboard.getGridPane());
                    controlador.ActualizarProtoboard(protoboard.getGridPane());
                    protoboard.actualizarMatriz(gridPane);
                }
            }
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")
                && matrizEnergia[filaInicio][columnaFin].equals("-")) {
            matrizEnergia[filaFin][columnaFin] = "-";
        } else {
            matrizEnergia[filaFin][columnaFin] = "|"; // Neutro si no cumple condiciones
        }

        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
    }

    // Procesamiento de un chip OR
    private void procesarChipOR(Chip chip, String[][] matrizEnergia) {
        int filaInicio = chip.getFilaInicio();
        int columnaInicio = chip.getColumnaInicio();
        int filaFin = chip.getFilaFin();
        int columnaFin = chip.getColumnaFin();

        // Lógica: una entrada positiva o negativa produce una salida
        if (matrizEnergia[filaInicio][columnaInicio].equals("+") || matrizEnergia[filaInicio][columnaFin].equals("+")) {
            matrizEnergia[filaFin][columnaFin] = "+";
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")
                || matrizEnergia[filaInicio][columnaFin].equals("-")) {
            matrizEnergia[filaFin][columnaFin] = "-";
        } else {
            matrizEnergia[filaFin][columnaFin] = "|"; // Neutro si no cumple condiciones
        }

        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
    }

    // Procesamiento de un chip NOT
    private void procesarChipNOT(Chip chip, String[][] matrizEnergia) {
        int filaInicio = chip.getFilaInicio();
        int columnaInicio = chip.getColumnaInicio();
        int filaFin = chip.getFilaFin();
        int columnaFin = chip.getColumnaFin();

        // Lógica: invierte la entrada
        if (matrizEnergia[filaInicio][columnaInicio].equals("+")) {
            matrizEnergia[filaFin][columnaFin] = "-";
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")) {
            matrizEnergia[filaFin][columnaFin] = "+";
        } else {
            matrizEnergia[filaFin][columnaFin] = "|"; // Neutro si no hay entrada
        }

        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
    }

    // Método para procesar el cable
    private void procesarCable(Cable cable, String[][] matrizEnergia) {
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();
        Objeto objeto = cable.getObjeto();

        // Verificación de cables
        if (objeto.getpasa()) {
            actualizarEnergia(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
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
}
