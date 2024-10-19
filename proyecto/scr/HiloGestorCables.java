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
    private boolean bateriaEncendida = true;

    // Constructor
    public HiloGestorCables(GestorCables gestorCables, Protoboard protoboard, Controlador controlador,
            GridPane gridPane) {
        this.gestorCables = gestorCables;
        this.scheduler = Executors.newScheduledThreadPool(1); // 1 hilo programado
        this.running = false; // Inicialmente no está corriendo
        this.gridPane = gridPane;
        this.protoboard = protoboard; // Inicializar protoboard
        this.controlador = controlador; // Inicializar controlador
    }

    // Método para iniciar la ejecución periódica con mayor velocidad
    public void iniciarActualizacionContinua(String[][] matrizEnergia, long periodoMilisegundos) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                actualizarObjetos(matrizEnergia); // Método que actualiza objetos
                actualizarObjetosInverso(matrizEnergia); // Método que actualiza objetos en sentido inverso
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, periodoMilisegundos, TimeUnit.MILLISECONDS); // 0 significa que empieza de inmediato
    }

    // Método para detener la ejecución
    public void detenerActualizacion() {
        if (!running) {
            return; // No hace nada si ya está detenido
        }
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(0, TimeUnit.SECONDS)) { // Espera un máximo de 5 segundos para que los hilos
                                                                    // terminen
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Método para actualizar objetos
    public void actualizarObjetos(String[][] matrizEnergia) {
        if (gestorCables.Espera()) {
            return; // No hacer nada si está en espera
        }

        List<Cable> cables = gestorCables.obtenerCables();

        if (cables.isEmpty()) {
            return; // No hacer nada si la lista está vacía
        }

        // Crear un ExecutorService para procesar en paralelo
        int numHilos = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);

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
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Método para actualizar objetos en orden inverso
    public void actualizarObjetosInverso(String[][] matrizEnergia) {
        if (gestorCables.Espera()) {
            return; // No hacer nada si está en espera
        }

        List<Cable> cables = gestorCables.obtenerCables();

        if (cables.isEmpty()) {
            return; // No hacer nada si la lista está vacía
        }

        int numHilos = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);

        for (int i = cables.size() - 1; i >= 0; i--) {
            Cable cable = cables.get(i);
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
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
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
        if (objeto.getpasa()) {
            actualizarEnergia(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        }
    }

    // Método para actualizar energía respetando el estado de la batería
    private void actualizarEnergia(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
            String[][] matrizEnergia) {
        if (!bateriaEncendida) {
            matrizEnergia[filaFin][columnaFin] = "|";
            matrizEnergia[filaInicio][columnaInicio] = "|";
        } else {
            if (matrizEnergia[filaInicio][columnaInicio].equals("+") &&
                    matrizEnergia[filaFin][columnaFin].equals("|")) {
                matrizEnergia[filaFin][columnaFin] = "+";
            } else if (matrizEnergia[filaInicio][columnaInicio].equals("-") &&
                    matrizEnergia[filaFin][columnaFin].equals("|")) {
                matrizEnergia[filaFin][columnaFin] = "-";
            } else if (matrizEnergia[filaFin][columnaFin].equals("+") &&
                    matrizEnergia[filaInicio][columnaInicio].equals("|")) {
                matrizEnergia[filaInicio][columnaInicio] = "+";
            } else if (matrizEnergia[filaFin][columnaFin].equals("-") &&
                    matrizEnergia[filaInicio][columnaInicio].equals("|")) {
                matrizEnergia[filaInicio][columnaInicio] = "-";
            }
        }

        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        protoboard.actualizarMatriz(gridPane, bateriaEncendida);
    }

    // Método para aplicar colores al protoboard
    private void aplicarColoresProtoboard(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
            String[][] matrizEnergia) {
        if (matrizEnergia[filaInicio][columnaInicio].equals("+")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.BLUE, bateriaEncendida);
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.RED, bateriaEncendida);
        }

        if (matrizEnergia[filaFin][columnaFin].equals("+")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.BLUE, bateriaEncendida);
        } else if (matrizEnergia[filaFin][columnaFin].equals("-")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.RED, bateriaEncendida);
        }

        controlador.actualizarBuses(protoboard.getGridPane());
        controlador.ActualizarProtoboard(protoboard.getGridPane());
    }

    // Método para establecer el estado de la batería
    public void setBateriaEncendida(boolean estado) {
        this.bateriaEncendida = estado;
        System.out.println("Estado de la batería actualizado a: " + (estado ? "Encendida" : "Apagada"));
    }

    // Método para obtener el estado de la batería
    public boolean isBateriaEncendida() {
        return this.bateriaEncendida;
    }
}
