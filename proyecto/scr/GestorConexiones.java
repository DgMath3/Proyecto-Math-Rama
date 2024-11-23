import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

public class GestorConexiones {

    private final GestorCables gestorCables;
    private Thread hiloConexiones;

    public GestorConexiones(GestorCables gestorCables) {
        this.gestorCables = gestorCables;
        iniciarBusquedaConexiones();
    }

    private void iniciarBusquedaConexiones() {
        hiloConexiones = new Thread(() -> {
            while (true) {
                List<Cable> cables = gestorCables.obtenerCables();

                for (Cable cable : cables) {
                    if ("cablegen+".equals(cable.getObjeto().getId())
                            || "cablegen-".equals(cable.getObjeto().getId())) {
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

        hiloConexiones.setDaemon(true);
        hiloConexiones.start();
    }

    // Método para detener el hilo
    public void detener() {
        if (hiloConexiones != null && hiloConexiones.isAlive()) {
            hiloConexiones.interrupt();
        }
    }

    private void procesarConexion(Cable cableInicial) {
        List<Cable> conexion = new ArrayList<>();
        conexion.add(cableInicial);
        Cable anterior = cableInicial;

        int i = 0;
        while (i < gestorCables.obtenerCables().size() * 2) {

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

    private Cable buscar(Cable cable, List<Cable> Conexion) {
        for (Cable cable2 : gestorCables.obtenerCables()) {
            if (cable != cable2) {
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
        }
        return null;
    }

    private Cable encontrar(int fila, int columna, Cable cable) {
        if (fila < 2 && cable.getFilaInicio() < 2) {
            if (fila == cable.getFilaInicio()) {
                return cable;
            }
        } else if (fila > 11 && cable.getFilaInicio() > 11) {
            if (fila == cable.getFilaInicio()) {
                return cable;
            }
        } else if (1 < fila && fila <= 7 && 1 < cable.getFilaInicio() && cable.getFilaInicio() <= 7) {
            if (columna == cable.getColumnaInicio()) {
                if (!"cablegen+".equals(cable.getObjeto().getId())
                && !"cablegen-".equals(cable.getObjeto().getId())){
                    return cable;
                }

            }
        } else if (fila < 12 && fila > 7 && cable.getFilaInicio() <12 && cable.getFilaInicio() > 7) {
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
        } else if (fila > 11 && cable.getFilaFin() > 11) {
            if (fila == cable.getFilaFin()) {
                return cable;
            }
        } else if (1 < fila && fila <= 7 && cable.getFilaFin() < 1 && cable.getFilaFin() <= 7) {
            if (columna == cable.getColumnaFin()) {
                return cable;
            }
        } else if ( fila < 12 && fila > 7 && cable.getFilaFin() < 12 && cable.getFilaFin() > 7) {
            if (columna == cable.getColumnaFin()) {
                return cable;
            }
        }
        return null;
    }

    private void actualizarConexion(List<Cable> conexiones) {
        Platform.runLater(() -> {
            for (Cable cable : conexiones) {
                if (conexiones.size() < 4) {
                    if (cable.getObjeto().getId().equals("Led") && gestorCables.getestado()) {
                        gestorCables.eliminarCable(cable, false);
                        gestorCables.redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(),
                                cable.getColumnaFin(), cable.getStartX(), cable.getStartY(), cable.getvalor(),
                                new Objeto("led_roto", "x"));
                    }
                } else if (cable.getObjeto().getId().equals("Led") && gestorCables.getestado()) {
                    gestorCables.eliminarCable(cable, false);
                    gestorCables.redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(),
                            cable.getColumnaFin(), cable.getStartX(), cable.getStartY(), cable.getvalor(),
                            new Objeto("Led_on", cable.getColorled()));
                } else if (cable.getObjeto().getId().equals("Led_on") && !gestorCables.getestado()) {
                    gestorCables.eliminarCable(cable, false);
                    gestorCables.redibujar(cable.getFilaInicio(), cable.getColumnaInicio(), cable.getFilaFin(),
                            cable.getColumnaFin(), cable.getStartX(), cable.getStartY(), cable.getvalor(),
                            new Objeto("Led", cable.getColorled()));
                }
            }
        });
    }
}
