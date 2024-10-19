import javafx.scene.paint.Color;

import java.io.*;
import java.util.List;

public class Updown {

    private boolean bateriaEncendida = true;

    public void guardar(List<Cable> cables, String nombreArchivo) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(nombreArchivo))) {
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
            System.out.println("Cables guardados en " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar los cables: " + e.getMessage());
        }
    }

    // Método para cargar cables desde un archivo
    public void cargar(GestorCables gestorCables, String nombreArchivo, Protoboard protoboard, Controlador controlador) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(nombreArchivo))) {
            while (true) {
                try {
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
                    gestorCables.redibujar(filaInicio, columnaInicio,filaFin,columnaFin,startX,startY,valor);
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
                    break;
                }
            }
            System.out.println("Cables cargados desde " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al cargar los cables: " + e.getMessage());
        }
    }
    public void setBateriaEncendida(boolean estado) {
        this.bateriaEncendida = estado;
        System.out.println("Estado de la batería actualizado a: " + (estado ? "Encendida" : "Apagada"));
    }
    public boolean isBateriaEncendida() {
        return this.bateriaEncendida;
    }
}
