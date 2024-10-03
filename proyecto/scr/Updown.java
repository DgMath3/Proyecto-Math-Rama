import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Updown {

    private boolean bateriaEncendida = true; // Inicialmente encendida

    // Método para guardar cables en un archivo
    public void guardar(List<Cable> cables, String nombreArchivo) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(nombreArchivo))) {
            for (Cable cable : cables) {
                out.writeDouble(cable.StartX()); // Guardar startX
                out.writeDouble(cable.StartY()); // Guardar startY
                out.writeDouble(cable.EndX()); // Guardar endX
                out.writeDouble(cable.EndY()); // Guardar endY

                // Guardar el ID del objeto asociado
                String id = cable.getObjeto().getId();
                out.writeUTF(id); // Utilizamos writeUTF para guardar cadenas

                out.writeInt(cable.getFilaInicio()); // Guardar filaInicio
                out.writeInt(cable.getColumnaInicio()); // Guardar columnaInicio
            }
            System.out.println("Cables guardados en " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar los cables: " + e.getMessage());
        }
    }

    // Método para cargar cables desde un archivo
    public void cargar(GestorCables gestorCables, String nombreArchivo, Protoboard protoboard, Controlador controlador) {
        List<Cable> cables = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(new FileInputStream(nombreArchivo))) {
            while (true) {
                try {
                    double startX = in.readDouble(); // Leer startX
                    double startY = in.readDouble(); // Leer startY
                    double endX = in.readDouble(); // Leer endX
                    double endY = in.readDouble(); // Leer endY

                    // Leer el ID del objeto asociado
                    String id = in.readUTF(); // Cambiado a readUTF

                    int filaInicio = in.readInt(); // Leer filaInicio
                    int columnaInicio = in.readInt(); // Leer columnaInicio

                    // Crear un nuevo cable con los datos leídos
                    Objeto objeto = new Objeto(id);
                    gestorCables.setObjetoSeleccionado(objeto);
                    gestorCables.dibujarCable(startX, startY, endX, endY, filaInicio, columnaInicio);
                    if (id.equals("cablegen+")) {
                        protoboard.cambiarColor(filaInicio, columnaInicio, Color.BLUE,bateriaEncendida);
                        controlador.actualizarBuses(protoboard.getGridPane());
                        controlador.ActualizarProtoboard(protoboard.getGridPane());
                    } else if (id.equals("cablegen-")) {
                        protoboard.cambiarColor(filaInicio, columnaInicio, Color.RED, bateriaEncendida);
                        controlador.actualizarBuses(protoboard.getGridPane());
                        controlador.ActualizarProtoboard(protoboard.getGridPane());
                    }
                } catch (EOFException e) {
                    break; // Fin del archivo
                }
            }

            for (Cable cable : cables) {
                // Llama al método dibujarCable para cada cable cargado
                gestorCables.setObjetoSeleccionado(cable.getObjeto());
                boolean exito = gestorCables.dibujarCable(cable.getStartX(), cable.getStartY(),
                        cable.getEndX(), cable.getEndY(), cable.getFilaInicio(), cable.getColumnaInicio());
                if (!exito) {
                    System.err.println("Error al dibujar el cable: " + cable);
                }
            }
            System.out.println("Cables cargados desde " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al cargar los cables: " + e.getMessage());
        }
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
