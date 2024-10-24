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
    private List<Cable> cablesAnteriores; // Almacena la lista anterior de cables para verificar cambios

    // Constructor
    public HiloGestorCables(GestorCables gestorCables, Protoboard protoboard, Controlador controlador,
            GridPane gridPane) {
        this.gestorCables = gestorCables;
        this.scheduler = Executors.newScheduledThreadPool(1); // 1 hilo programado
        this.running = false; // Inicialmente no está corriendo
        this.gridPane = gridPane;
        this.protoboard = protoboard; // Inicializar protoboard
        this.controlador = controlador; // Inicializar controlador
        this.cablesAnteriores = gestorCables.obtenerCables(); // Inicializa con la lista actual de cables
    }

    // Método para iniciar la ejecución periódica con mayor velocidad
    public void iniciarActualizacionContinua(String[][] matrizEnergia, long periodoMilisegundos) {
        if (!running) {
            running = true;
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    // Verifica si la lista de cables ha cambiado
                    List<Cable> cablesActuales = gestorCables.obtenerCables();
                    if (!cablesActuales.equals(cablesAnteriores)) {
                        reiniciarActualizacion(matrizEnergia, periodoMilisegundos); // Reinicia si hay cambios
                    } else {
                        actualizarObjetos(matrizEnergia); // Método que actualiza objetos si no hay cambios
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, periodoMilisegundos, TimeUnit.MILLISECONDS); // Empieza de inmediato
        }
    }

    // Método para reiniciar la actualización cuando cambian los cables
    private void reiniciarActualizacion(String[][] matrizEnergia, long periodoMilisegundos) {
        detenerActualizacion(); // Detiene la ejecución actual
        cablesAnteriores = gestorCables.obtenerCables(); // Actualiza la lista de cables
        iniciarActualizacionContinua(matrizEnergia, periodoMilisegundos); // Reinicia la actualización
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
        if (gestorCables.Espera()) {
            return; // No hacer nada si está en espera
        }

        List<Cable> cables = gestorCables.obtenerCables();

        if (cables.isEmpty()) {
            return; // No hacer nada si la lista está vacía
        }

        // Crear un ExecutorService para procesar en paralelo
        int numHilos = 7;
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
