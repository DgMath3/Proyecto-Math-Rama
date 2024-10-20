import java.util.ArrayList;
import java.util.List;

public class GestorConexiones {

    private final GestorCables gestorCables;
    private final int[][] matrizConexiones; // Matriz de conexiones

    // Constructor
    public GestorConexiones(GestorCables gestorCables, int[][] matrizConexiones) {
        this.gestorCables = gestorCables;
        this.matrizConexiones = matrizConexiones;
    }

    // Método principal para buscar conexiones en una posición específica
    public List<Cable> buscarConexionesEnPosicion(int fila, int columna) {
        List<Cable> cablesConectados = new ArrayList<>();

        // Determinar el sector según la fila y buscar en el bus o el protoboard
        if (fila >= 0 && fila <= 1) {
            // Sector superior del bus (filas 0-1)
            cablesConectados.addAll(buscarEnSectorBus(fila, columna));
        } else if (fila >= 12 && fila <= 13) {
            // Sector inferior del bus (filas 12-13)
            cablesConectados.addAll(buscarEnSectorBus(fila, columna));
        } else if (fila >= 2 && fila <= 6) {
            // Sector superior del protoboard (filas 2-6)
            cablesConectados.addAll(buscarEnSectorProtoboard(fila, columna));
        } else if (fila >= 7 && fila <= 11) {
            // Sector inferior del protoboard (filas 7-11)
            cablesConectados.addAll(buscarEnSectorProtoboard(fila, columna));
        }

        return cablesConectados;
    }

    // Método para buscar cables conectados en los buses (sector de energía)
    private List<Cable> buscarEnSectorBus(int fila, int columna) {
        List<Cable> cablesConectados = new ArrayList<>();
        int numColumns = matrizConexiones[0].length; // Número de columnas en la matriz de conexiones

        // Recorrer las columnas del sector de bus
        for (int col = 0; col < numColumns; col++) {
            // Verificar si hay una conexión en la matriz de conexiones
            if (matrizConexiones[fila][col] != 0) {
                // Obtener el cable conectado en esta posición
                Cable cableConectado = gestorCables.obtenerCableEnPosicion(fila, col);
                if (cableConectado != null && !cablesConectados.contains(cableConectado)) {
                    cablesConectados.add(cableConectado);
                }
            }
        }

        return cablesConectados;
    }

    // Método para buscar cables conectados en el protoboard
    private List<Cable> buscarEnSectorProtoboard(int fila, int columna) {
        List<Cable> cablesConectados = new ArrayList<>();
        int numColumns = matrizConexiones[0].length; // Número de columnas en la matriz de conexiones

        // Recorrer las columnas del sector del protoboard
        for (int col = 0; col < numColumns; col++) {
            // Verificar si hay una conexión en la matriz de conexiones
            if (matrizConexiones[fila][col] != 0) {
                // Obtener el cable conectado en esta posición
                Cable cableConectado = gestorCables.obtenerCableEnPosicion(fila, col);
                if (cableConectado != null && !cablesConectados.contains(cableConectado)) {
                    cablesConectados.add(cableConectado);
                }
            }
        }

        return cablesConectados;
    }
}
