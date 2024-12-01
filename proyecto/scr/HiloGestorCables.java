import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

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
    public HiloGestorCables(GestorCables gestorCables, Protoboard protoboard, Controlador controlador, GridPane gridPane) {
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
        List<Chip> chips = gestorCables.obtenerChips();  // Obtener la lista de chips desde GestorCables

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
    private void procesarChipAND(Chip chip, String[][] matrizEnergia) {
        int filaInicio = chip.getFilaInicio();
        int columnaInicio = chip.getColumnaInicio();
        int filaFin = chip.getFilaFin();
        int columnaFin = chip.getColumnaFin();

        // Lógica: ambas entradas deben ser positivas o negativas para producir una salida
        if (matrizEnergia[filaInicio][columnaInicio].equals("+") &&
            matrizEnergia[filaFin][columnaFin].equals("-")) {
            for (int i = columnaInicio + 1; i < columnaFin ; i = i + 3){
                if (matrizEnergia[filaInicio][i].equals("+") && matrizEnergia[filaInicio][i + 1].equals("+")){
                    if ((i+2)>columnaFin){
                        break;
                    }
                    matrizEnergia[filaInicio][i+2] = "+";
                    protoboard.cambiarColor(filaInicio, i+2, Color.BLUE);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
                else if(matrizEnergia[filaInicio][i].equals("-") && matrizEnergia[filaInicio][i + 1].equals("-")){
                    matrizEnergia[filaFin][i+2] = "-";
                    protoboard.cambiarColor(filaInicio, i+2, Color.RED);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
            }
            for(int i = columnaInicio ; i < columnaFin - 1 ; i = i + 3){
                if (matrizEnergia[filaFin][i].equals("+") && matrizEnergia[filaFin][i + 1].equals("+")){
                    if ((i+3)>columnaFin){
                        break;
                    }
                    matrizEnergia[filaFin][i+2] = "+";
                    protoboard.cambiarColor(filaFin, i+2, Color.BLUE);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
                else if(matrizEnergia[filaFin][i].equals("-") && matrizEnergia[filaFin][i + 1].equals("-")){
                    matrizEnergia[filaFin][i+2] = "-";
                    protoboard.cambiarColor(filaFin, i+2, Color.RED);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
            }
        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        protoboard.actualizarMatriz(gridPane);
    }
}

    // Procesamiento de un chip OR
    private void procesarChipOR(Chip chip, String[][] matrizEnergia) {
        int filaInicio = chip.getFilaInicio();
        int columnaInicio = chip.getColumnaInicio();
        int filaFin = chip.getFilaFin();
        int columnaFin = chip.getColumnaFin();

        if (matrizEnergia[filaInicio][columnaInicio].equals("+") &&
            matrizEnergia[filaFin][columnaFin].equals("-")) {
            for (int i = columnaInicio + 1; i < columnaFin ; i = i + 3){
                if (matrizEnergia[filaInicio][i].equals("+") && matrizEnergia[filaInicio][i + 1].equals("|") || matrizEnergia[filaInicio][i].equals("|") && matrizEnergia[filaInicio][i + 1].equals("+")){
                    if ((i+2)>columnaFin){
                        break;
                    }
                    matrizEnergia[filaInicio][i+2] = "+";
                    protoboard.cambiarColor(filaInicio, i+2, Color.BLUE);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
                else if(matrizEnergia[filaInicio][i].equals("-") && matrizEnergia[filaInicio][i + 1].equals("|") || matrizEnergia[filaInicio][i].equals("|") && matrizEnergia[filaInicio][i + 1].equals("-")){
                    matrizEnergia[filaFin][i+2] = "-";
                    protoboard.cambiarColor(filaInicio, i+2, Color.RED);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
            }
            for(int i = columnaInicio ; i < columnaFin - 1 ; i = i + 3){
                if (matrizEnergia[filaFin][i].equals("+") && matrizEnergia[filaFin][i + 1].equals("|") || matrizEnergia[filaFin][i].equals("|") && matrizEnergia[filaFin][i + 1].equals("+")){
                    if ((i+3)>columnaFin){
                        break;
                    }
                    matrizEnergia[filaFin][i+2] = "+";
                    protoboard.cambiarColor(filaFin, i+2, Color.BLUE);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
                else if(matrizEnergia[filaFin][i].equals("-") && matrizEnergia[filaFin][i + 1].equals("|") || matrizEnergia[filaFin][i].equals("|") && matrizEnergia[filaFin][i + 1].equals("-")){
                    matrizEnergia[filaFin][i+2] = "-";
                    protoboard.cambiarColor(filaFin, i+2, Color.RED);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 2;
                }
            }
        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        protoboard.actualizarMatriz(gridPane);
    }
}


    // Procesamiento de un chip NOT
    private void procesarChipNOT(Chip chip, String[][] matrizEnergia) {
        int filaInicio = chip.getFilaInicio();
        int columnaInicio = chip.getColumnaInicio();
        int filaFin = chip.getFilaFin();
        int columnaFin = chip.getColumnaFin();

        // Lógica: ambas entradas deben ser positivas o negativas para producir una salida
        if (matrizEnergia[filaInicio][columnaInicio].equals("+") &&
            matrizEnergia[filaFin][columnaFin].equals("-")) {
            
            // Revisión de la fila de inicio
            for (int i = columnaInicio + 1; i < columnaFin; i++) {
                // Si la celda actual es positiva y la celda anterior no tiene energía, pasa la energía
                if (matrizEnergia[filaInicio][i].equals("+")) { 
                    matrizEnergia[filaInicio][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaInicio-1][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaInicio-2][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaInicio-3][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaInicio-4][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    protoboard.cambiarColor(filaInicio, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaInicio-1, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaInicio-2, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaInicio-3, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaInicio-4, i + 1, Color.LIGHTGRAY);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 1;
                } 
                // Si la celda es neutra (|), pasa energía positiva
                else if (matrizEnergia[filaInicio][i].equals("|")) {
                    matrizEnergia[filaInicio][i + 1] = "+"; // Se pasa energía positiva
                    protoboard.cambiarColor(filaInicio, i + 1, Color.BLUE);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 1;
                }
            }

            // Revisión de la fila de fin
            for (int i = columnaInicio; i < columnaFin - 1; i++) {
                // Si la celda es positiva y la celda anterior no tiene energía, pasa la energía
                if (matrizEnergia[filaFin][i].equals("+") ) { 
                    matrizEnergia[filaFin][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaFin+1][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaFin+2][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaFin+3][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    matrizEnergia[filaFin+4][i + 1] = "|"; // Se coloca el estado de "sin energía"
                    protoboard.cambiarColor(filaFin, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaFin+1, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaFin+2, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaFin+3, i + 1, Color.LIGHTGRAY);
                    protoboard.cambiarColor(filaFin+4, i + 1, Color.LIGHTGRAY);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 1;
                }
                // Si la celda es neutra (|), pasa energía positiva
                else if (matrizEnergia[filaFin][i].equals("|")) {
                    matrizEnergia[filaFin][i + 1] = "+"; // Se pasa energía positiva
                    protoboard.cambiarColor(filaFin, i + 1, Color.BLUE);
                    protoboard.actualizarMatriz(gridPane);
                    i = i + 1;
                }
            }

            // Aplicar colores después de procesar
            aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
            protoboard.actualizarMatriz(gridPane);
        }
    }




    // Método para procesar el cable
    private void procesarCable(Cable cable, String[][] matrizEnergia) {
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();
        Objeto objeto = cable.getObjeto();

        // Verificación de cables
        if (objeto.getpasa() && gestorCables.getestado()) {
            actualizarEnergia(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        } if (objeto.getId().equals("Led")){
            if (matrizEnergia[filaInicio][columnaInicio].equals("+") && matrizEnergia[filaFin][columnaFin].equals("-") ||
         matrizEnergia[filaInicio][columnaInicio].equals("-") && matrizEnergia[filaFin][columnaFin].equals("+")){
            Platform.runLater(() -> {
                gestorCables.eliminarCable(cable, false);
                gestorCables.redibujar(filaInicio, columnaInicio, filaFin, columnaFin, columnaInicio, 
                    cable.getStartX(), cable.getStartX(), new Objeto("Led_on", cable.getColorled()));
            });
        }
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