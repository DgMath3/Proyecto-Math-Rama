import javafx.scene.paint.Color;
import java.io.*;
import java.util.List;

public class Updown {
    private boolean bateriaEncendida = true;

    public void guardar(List<Cable> cables, List<Chip> chips, String nombreArchivo) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(nombreArchivo))) {
            // Guardar cables
            out.writeInt(cables.size()); // Guardar el número de cables
            for (Cable cable : cables) {
                out.writeDouble(cable.StartX());
                out.writeDouble(cable.StartY());
                String id = cable.getObjeto().getId();
                out.writeUTF(id);
                out.writeInt(cable.getFilaInicio());
                out.writeInt(cable.getColumnaInicio());
                out.writeInt(cable.getFilaFin());
                out.writeInt(cable.getColumnaFin());
                out.writeDouble(cable.getvalor());
            }

            // Guardar chips
            out.writeInt(chips.size()); // Guardar el número de chips
            for (Chip chip : chips) {
                out.writeInt(chip.getFilaInicio()); // Guardar la fila de inicio
                out.writeInt(chip.getColumnaInicio()); // Guardar la columna de inicio
            }

            System.out.println("Cables y chips guardados en " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    public void cargar(GestorCables gestorCables, String nombreArchivo, Protoboard protoboard, Controlador controlador) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(nombreArchivo))) {
            // Cargar cables
            int numCables = in.readInt(); // Leer el número de cables
            for (int i = 0; i < numCables; i++) {
                double startX = in.readDouble();
                double startY = in.readDouble();
                String id = in.readUTF();
                int filaInicio = in.readInt();
                int columnaInicio = in.readInt();
                int filaFin = in.readInt();
                int columnaFin = in.readInt();
                Double valor = in.readDouble();
                Objeto objeto = new Objeto(id);
                gestorCables.setObjetoSeleccionado(objeto);
                gestorCables.redibujar(filaInicio, columnaInicio, filaFin, columnaFin, startX, startY, valor);
                actualizarProtoboardConCable(id, filaInicio, columnaInicio, protoboard, controlador);
            }

            // Cargar chips
            int chipCount = in.readInt(); // Leer el número de chips
            for (int i = 0; i < chipCount; i++) {
                int filaInicio = in.readInt(); // Leer la fila de inicio
                int columnaInicio = in.readInt(); // Leer la columna de inicio
                gestorCables.colocarChip(filaInicio, columnaInicio); // Colocar el chip en el gestor
            }

            System.out.println("Cables y chips cargados desde " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al cargar: " + e.getMessage());
        }
    }

    private void actualizarProtoboardConCable(String id, int filaInicio, int columnaInicio, Protoboard protoboard, Controlador controlador) {
        if (id.equals("cablegen+")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.BLUE, bateriaEncendida);
        } else if (id.equals("cablegen-")) {
            protoboard.cambiarColor(filaInicio, columnaInicio, Color.RED, bateriaEncendida);
        }
        controlador.actualizarBuses(protoboard.getGridPane());
        controlador.ActualizarProtoboard(protoboard.getGridPane());
    }

    public void setBateriaEncendida(boolean estado) {
        this.bateriaEncendida = estado;
        System.out.println("Estado de la batería actualizado a: " + (estado ? "Encendida" : "Apagada"));
    }

    public boolean isBateriaEncendida() {
        return this.bateriaEncendida;
    }
}
