import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VerificadorConexiones {
    private final GestorCables gestorCables;
    private volatile boolean running;

    // Constructor
    public VerificadorConexiones(GestorCables gestorCables) {
        this.gestorCables = gestorCables;
        this.running = true;
    }

    // Método para iniciar la búsqueda continua
    public void iniciarBusquedaContinua(String[][] matrizEnergia) {
        if (running) {
            System.out.println("La búsqueda ya está en ejecución.");
            return; // Evita reiniciar si ya está corriendo
        }
        running = true;

        new Thread(() -> {
            while (running) {
                try {
                    buscarConexiones(matrizEnergia); // Realizar la búsqueda de conexiones
                    Thread.sleep(100); // Espera 1 segundo entre cada ciclo de búsqueda
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Método para detener la búsqueda
    public void detenerBusqueda() {
        if (!running) {
            System.out.println("La búsqueda ya está detenida.");
            return; // No hace nada si ya está detenida
        }
        running = false;
    }

    // Método para buscar conexiones
    public void buscarConexiones(String[][] matrizEnergia) {
        List<Cable> cables = gestorCables.obtenerCables(); // Obtener la lista de cables

        // Verificar si la lista de cables está vacía
        if (cables.isEmpty()) {
            return;
        }
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Enviar una tarea por cada cable encontrado que sea de tipo "cableargen+" o "cableargen-"
        for (Cable cableGen : cables) {
            String tipoCable = cableGen.getObjeto().getId(); // Puede ser "cableargen+" o "cableargen-"
            if (tipoCable.equals("cableargen+") || tipoCable.equals("cableargen-")) {
                executor.submit(() -> {
                    try {
                        procesarConexion(cableGen, matrizEnergia);
                    } catch (Exception e) {
                        e.printStackTrace(); // Manejar excepciones individuales aquí
                    }
                });
            }
        }
        executor.shutdown();
    }

    // Método para procesar cada conexión a partir de un cable "cableargen+" o "cableargen-"
    private void procesarConexion(Cable cableGen, String[][] matrizEnergia) {
        String tipoCable = cableGen.getObjeto().getId(); // Puede ser "cableargen+" o "cableargen-"
        List<Cable> listaConexiones = buscarCablesConectados(cableGen, tipoCable, matrizEnergia);

        // Llamar a la función que repasa toda la lista de conexiones
        repasarConexiones(listaConexiones, tipoCable);
    }

    private List<Cable> buscarCablesConectados(Cable cableInicial, String tipoCable, String[][] matrizEnergia) {
        List<Cable> cablesConectados = new ArrayList<>(); // Lista de cables conectados
    
        // Obtener las posiciones de inicio y fin del cable
        int filaInicial = cableInicial.getFilaInicio(); // Fila del inicio del cable
        int columnaInicial = cableInicial.getColumnaInicio(); // Columna del inicio del cable
        int filaFinal = cableInicial.getFilaFin(); // Fila del final del cable
        int columnaFinal = cableInicial.getColumnaFin(); // Columna del final del cable
    
        // Verificar conexiones en la posición inicial del cable
        cablesConectados.addAll(buscarConexionesEnPosicion(filaInicial, columnaInicial, tipoCable, matrizEnergia));
    
        // Verificar conexiones en la posición final del cable, si es diferente a la inicial
        if (filaInicial != filaFinal || columnaInicial != columnaFinal) {
            cablesConectados.addAll(buscarConexionesEnPosicion(filaFinal, columnaFinal, tipoCable, matrizEnergia));
        }
    
        return cablesConectados; // Devuelve la lista de cables conectados
    }
    
    // Método auxiliar para buscar conexiones en una posición específica (inicio o final)
    private List<Cable> buscarConexionesEnPosicion(int fila, int columna, String tipoCable, String[][] matrizEnergia) {
        List<Cable> cablesConectados = new ArrayList<>();

        // Determinar el sector según la fila y buscar en el bus o el protoboard
        if (fila >= 0 && fila <= 1) {
            // Sector superior del bus (filas 0-1)
            cablesConectados.addAll(buscarEnSectorBus(tipoCable, fila, fila, matrizEnergia));
        } else if (fila >= 12 && fila <= 13) {
            // Sector inferior del bus (filas 12-13)
            cablesConectados.addAll(buscarEnSectorBus(tipoCable, fila, fila, matrizEnergia));
        } else if (fila >= 2 && fila <= 6) {
            // Sector superior del protoboard (filas 2-6)
            cablesConectados.addAll(buscarEnSectorProtoboard(tipoCable, fila, fila, matrizEnergia));
        } else if (fila >= 7 && fila <= 11) {
            // Sector inferior del protoboard (filas 7-11)
            cablesConectados.addAll(buscarEnSectorProtoboard(tipoCable, fila, fila, matrizEnergia));
        }

        return cablesConectados;
    }

    // Método para buscar cables conectados en los buses (sector de energía)
    private List<Cable> buscarEnSectorBus(String tipoCable, int filaInicio, int filaFin, String[][] matrizEnergia) {
        List<Cable> cablesConectados = new ArrayList<>();
        int numColumns = matrizEnergia[0].length; // Número de columnas en la matriz de energía

        // Recorrer las filas y columnas del sector
        for (int fila = filaInicio; fila <= filaFin; fila++) {
            for (int col = 0; col < numColumns; col++) {
                // Comprobar si hay un cable en la matriz de energía
                if (matrizEnergia[fila][col].equals(tipoCable)) {
                    // Obtener el cable en esta posición
                    Cable cableConectado = gestorCables.obtenerCableEnPosicion(fila, col);
                    if (cableConectado != null) {
                        cablesConectados.add(cableConectado);
                    }
                }
            }
        }
        return cablesConectados;
    }

    // Método para buscar cables conectados en el protoboard
    private List<Cable> buscarEnSectorProtoboard(String tipoCable, int filaInicio, int filaFin, String[][] matrizEnergia) {
        List<Cable> cablesConectados = new ArrayList<>();
        int numColumns = matrizEnergia[0].length; // Número de columnas en la matriz de energía

        // Recorrer las filas y columnas del sector
        for (int fila = filaInicio; fila <= filaFin; fila++) {
            for (int col = 0; col < numColumns; col++) {
                // Comprobar si hay un cable en la matriz de energía
                if (matrizEnergia[fila][col].equals(tipoCable)) {
                    // Obtener el cable en esta posición
                    Cable cableConectado = gestorCables.obtenerCableEnPosicion(fila, col);
                    if (cableConectado != null) {
                        cablesConectados.add(cableConectado);
                    }
                }
            }
        }
        return cablesConectados;
    }

    // Método para repasar las conexiones encontradas (implementa la lógica que necesites)
    private void repasarConexiones(List<Cable> listaConexiones, String tipoCable) {
        for (Cable cable : listaConexiones) {
            // Verificar si se encontró un cable "Led"
            if (cable.getObjeto().getId().equals("Led")) {
                System.out.println("Se encontró un cable Led, deteniendo búsqueda.");
                detenerBusqueda(); // Detener la búsqueda al encontrar un "Led"
                break; // Salir del ciclo
            }
        }
    }
}
