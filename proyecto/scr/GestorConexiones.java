import javafx.application.Platform;
import java.util.ArrayList;

public class GestorConexiones {

    private final GestorCables gestorCables;
    private Thread hiloConexiones;

    public GestorConexiones(GestorCables gestorCables) {
        this.gestorCables = gestorCables;
        iniciarBusquedaConexiones();
    }

    private void iniciarBusquedaConexiones() {
        hiloConexiones = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                for (Cable cable : gestorCables.obtenerCables()) {
                    if (cable.getObjeto().getId().equals("cablegen+")
                            || cable.getObjeto().getId().equals("cablegen-")) {
                        procesarConexion(cable);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        hiloConexiones.setDaemon(true); // Permite que el hilo se detenga al cerrar la aplicación
        hiloConexiones.start();
    }

    // Método para detener el hilo
    public void detener() {
        if (hiloConexiones != null && hiloConexiones.isAlive()) {
            hiloConexiones.interrupt();
        }
    }

    private void procesarConexion(Cable cableInicial) {
        ArrayList<Cable> conexion = new ArrayList<>();
        conexion.add(cableInicial);
        Cable anterior = cableInicial;

        int i = 0;
        while (i < gestorCables.obtenerCables().size()) {
            Cable siguiente = buscar(anterior, conexion);

            if (siguiente != null && !conexion.contains(siguiente)) {
                anterior = siguiente;
                conexion.add(siguiente);
            }

            if (siguiente != null && ((cableInicial.getObjeto().getId().equals("cablegen+")
                    && siguiente.getObjeto().getId().equals("cablegen-"))
                    || (cableInicial.getObjeto().getId().equals("cablegen-")
                            && siguiente.getObjeto().getId().equals("cablegen+")))) {
                actualizarConexion(conexion);
                break;
            }
            i++;
        }
    }

    private Cable buscar(Cable cable, ArrayList<Cable> Conexion) {
        for (Cable cable2 : gestorCables.obtenerCables()) {
            if ((encontrar(cable2.getFilaInicio(), cable2.getColumnaInicio(), cable) != null
                    || encontrarv2(cable2.getFilaInicio(), cable2.getColumnaInicio(), cable) != null)) {
                if (!Conexion.contains(cable2)) {
                    return cable2;
                }
            } else if ((encontrar(cable2.getFilaFin(), cable2.getColumnaFin(), cable) != null
                    || encontrarv2(cable2.getFilaFin(), cable2.getColumnaFin(), cable) != null)) {
                if (!Conexion.contains(cable2)) {
                    return cable2;
                }
            }
        }
        return null;
    }

    private Cable encontrar(int fila, int columna, Cable cable) {
        if (fila < 2 && cable.getFilaInicio() < 2) {
            if (fila == cable.getFilaInicio()) {
                return cable;
            }
        }
        if (fila > 11 && cable.getFilaInicio() > 11) {
            if (fila == cable.getFilaInicio()) {
                return cable;
            }
        }
        if (fila <= 7 && cable.getFilaInicio() <= 7) {
            if (columna == cable.getColumnaInicio()) {
                return cable;
            }
        }
        if (fila > 7 && cable.getFilaInicio() > 7) {
            if (columna == cable.getColumnaInicio()) {
                return cable;
            }
        }
        return null;
    }

    private Cable encontrarv2(int fila, int columna, Cable cable) {
        if (fila < 2 && cable.getFilaFin() < 2) {
            if (fila == cable.getFilaFin()) {
                return cable;
            }
        }
        if (fila > 11 && cable.getFilaFin() > 11) {
            if (fila == cable.getFilaFin()) {
                return cable;
            }
        }
        if (fila <= 7 && cable.getFilaFin() <= 7) {
            if (columna == cable.getColumnaFin()) {
                return cable;
            }
        }
        if (fila > 7 && cable.getFilaFin() > 7) {
            if (columna == cable.getColumnaFin()) {
                return cable;
            }
        }
        return null;
    }

    private void actualizarConexion(ArrayList<Cable> conexiones) {
        Platform.runLater(() -> {
            for (Cable cable : conexiones) {
                if (cable.getObjeto().getId().equals("Led") && gestorCables.getestado()) {
                    gestorCables.eliminarCable(cable, false);
                    gestorCables.redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(),
                            cable.getColumnaFin(), cable.getStartX(), cable.getStartY(), cable.getvalor(),
                            new Objeto("Led_on"));
                }
                if (cable.getObjeto().getId().equals("Led_on") && !gestorCables.getestado()) {
                    gestorCables.eliminarCable(cable, false);
                    gestorCables.redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(),
                            cable.getColumnaFin(), cable.getStartX(), cable.getStartY(), cable.getvalor(),
                            new Objeto("Led"));
                }
            }
        });
    }
}
