import java.io.*;
import java.util.List;

public class Updown {
    public void guardar(List<Cable> cables, List<Chip> chips, String nombreArchivo) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(nombreArchivo))) {
            // Guardar cables
            out.writeInt(cables.size());
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
            out.writeInt(chips.size()); 
            for (Chip chip : chips) {
                out.writeInt(chip.getFilaInicio());
                out.writeInt(chip.getColumnaInicio());
                out.writeUTF(chip.getId());
                out.writeInt(chip.getlargo());
            }

            System.out.println("Cables y chips guardados en " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    public void cargar(GestorCables gestorCables, String nombreArchivo, Protoboard protoboard, Controlador controlador) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(nombreArchivo))) {
            // Cargar cables
            int numCables = in.readInt();
            for (int i = 0; i < numCables; i++) {
                double startX = in.readDouble();
                double startY = in.readDouble();
                String id = in.readUTF();
                int filaInicio = in.readInt();
                int columnaInicio = in.readInt();
                int filaFin = in.readInt();
                int columnaFin = in.readInt();
                Double valor = in.readDouble();
                Objeto objeto = new Objeto(id, "x");
                gestorCables.redibujar(filaInicio, columnaInicio, filaFin, columnaFin, startX, startY, valor, objeto);
            }

            // Cargar chips
            int chipCount = in.readInt();
            for (int i = 0; i < chipCount; i++) {
                int filaInicio = in.readInt();
                int columnaInicio = in.readInt();
                String id = in.readUTF();
                int largo = in.readInt();
                gestorCables.colocarChip(filaInicio, columnaInicio, id, largo);
            }

            gestorCables.setEnergia();

            System.out.println("Cables y chips cargados desde " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al cargar: " + e.getMessage());
        }
    }
}
