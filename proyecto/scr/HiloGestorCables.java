import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public HiloGestorCables(GestorCables gestorCables, Protoboard protoboard, Controlador controlador, GridPane gridPane) {
        this.gestorCables = gestorCables;
        this.scheduler = Executors.newScheduledThreadPool(40); // 1 hilo programado
        this.running = false; // Inicialmente no está corriendo
        this.gridPane = gridPane;
        this.protoboard = protoboard; // Inicializar protoboard
        this.controlador = controlador; // Inicializar controlador
    }

    // Método para iniciar la ejecución periódica
    public void iniciarActualizacionContinua(String[][] matrizEnergia, long periodoSegundos) {
        if (running) {
            System.out.println("La actualización ya está en ejecución.");
            return; // Evita reiniciar si ya está corriendo
        }
        running = true;

        scheduler.scheduleAtFixedRate(() -> {
            try {
                actualizarObjetos(matrizEnergia); // Llamar a la función actualizarObjetos
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, periodoSegundos, TimeUnit.SECONDS); // 0 significa que empieza de inmediato
    }

    // Método para detener la ejecución
    public void detenerActualizacion() {
        if (!running) {
            System.out.println("La actualización ya está detenida.");
            return; // No hace nada si ya está detenido
        }
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {  // Espera un máximo de 5 segundos para que los hilos terminen
                scheduler.shutdownNow();  // Forzar la detención si no termina
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow(); // En caso de interrupción, también forzar la detención
            Thread.currentThread().interrupt(); // Volver a lanzar la excepción si el hilo fue interrumpido
        }
    }

    public void actualizarObjetos(String[][] matrizEnergia) {
        List<Cable> cables = gestorCables.obtenerCables(); // Obtener la lista de cables
    
        // Verificar si la lista de cables está vacía
        if (cables.isEmpty()) {
            return; // No hacer nada si la lista está vacía
        }
    
        // Filtrar cables que no sean de tipo "cableargen+" o "cableargen-"
        List<Cable> cablesFiltrados = cables.stream()
                .filter(cable -> !"cableargen+".equals(cable.getObjeto().getId())
                        && !"cableargen-".equals(cable.getObjeto().getId()))
                .collect(Collectors.toList());
    
        // Crear un pool de hilos con hasta 7 hilos concurrentes
        ExecutorService executor = Executors.newFixedThreadPool(7);
    
        // Enviar una tarea por cada cable filtrado
        for (Cable cable : cablesFiltrados) {
            if (cable.getStartX() != 1090) {
                executor.submit(() -> {
                    try {
                        procesarCable(cable, matrizEnergia);
                    } catch (Exception e) {
                        e.printStackTrace(); // Manejar excepciones individuales aquí
                    }
                });
                
                // Introducir un desfase de 1 ms antes de enviar la siguiente tarea
                try {
                    Thread.sleep(1); // Desfase de 1 ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restaura el estado de interrupción
                    break; // Salir del bucle si se interrumpe el hilo
                }
            }
        }
    
        // Apagar el ExecutorService después de completar las tareas
        executor.shutdown();
    }
    
    

    private void procesarCable(Cable cable, String[][] matrizEnergia) {
        if (cable == null || matrizEnergia == null) {
            System.out.println("Cable o matriz de energía son nulos.");
            return; // Salir si el cable o la matriz son nulos
        }
    
        int filaInicio = cable.getFilaInicio();
        int columnaInicio = cable.getColumnaInicio();
        int filaFin = cable.getFilaFin();
        int columnaFin = cable.getColumnaFin();
        Objeto objeto = cable.getObjeto();
    
        if (objeto == null) {
            System.out.println("El objeto del cable es nulo.");
            return; // Salir si el objeto es nulo
        }
    
        String idObjeto = objeto.getId();
    
        // Verificación de cables
        if (idObjeto.equals("Cable_azul") || idObjeto.equals("Cable_rojo") || idObjeto.equals("SwitchOn")) {
            actualizarEnergia(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        }
    
        // Verificación de LEDs
        if (idObjeto.equals("Led") && !objeto.isLedActivado()) {
            gestorCables.led(cable, matrizEnergia);
        }
    }
    

    // Método para actualizar energía respetando el estado de la batería
    private void actualizarEnergia(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
                                   String[][] matrizEnergia) {
        if (!bateriaEncendida) {
            // Si la batería está apagada, no transmitir energía positiva ni negativa
            matrizEnergia[filaFin][columnaFin] = "|";
            matrizEnergia[filaInicio][columnaInicio] = "|";
        } else {
            // Si la batería está encendida, comportamiento normal
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

        // Aplicar colores al protoboard después de la actualización de energía
        aplicarColoresProtoboard(filaInicio, columnaInicio, filaFin, columnaFin, matrizEnergia);
        protoboard.actualizarMatriz(gridPane, bateriaEncendida);
    }

    private void aplicarColoresProtoboard(int filaInicio, int columnaInicio, int filaFin, int columnaFin,
            String[][] matrizEnergia) {
        // Aplicar el color en el punto de inicio
        if (matrizEnergia[filaInicio][columnaInicio].equals("+")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.BLUE, bateriaEncendida);
        } else if (matrizEnergia[filaInicio][columnaInicio].equals("-")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.RED, bateriaEncendida);
        }

        // Aplicar el color en el punto de fin
        if (matrizEnergia[filaFin][columnaFin].equals("+")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.BLUE, bateriaEncendida);
        } else if (matrizEnergia[filaFin][columnaFin].equals("-")) {
            protoboard.cambiarColor(filaFin, columnaFin, Color.RED, bateriaEncendida);
        }

        // Actualizar el protoboard y los buses para que reflejen los cambios en las
        // filas y columnas
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
