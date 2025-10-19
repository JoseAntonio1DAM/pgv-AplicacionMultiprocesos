import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        File carpetaReseñas = new File("reseñas");
        File carpetaSalidas = new File("salidas");

        if (!carpetaSalidas.exists()) {
            carpetaSalidas.mkdir();
        }

        File[] archivos = carpetaReseñas.listFiles((dir, name) -> name.endsWith(".txt"));
        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron archivos de reseñas en la carpeta /reseñas");
            return;
        }

        List<Process> procesos = new ArrayList<>();

        System.out.println("=== Iniciando análisis multiproceso ===");

        try {
            for (File archivo : archivos) {
                String nombreProducto = archivo.getName().replace(".txt", "");
                File salida = new File(carpetaSalidas, "salida_" + nombreProducto + ".txt");

                // Construir el subproceso (Analizador)
                ProcessBuilder pb = new ProcessBuilder(
                        "java", "Analizador", archivo.getPath());
                pb.redirectOutput(salida);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                Process proceso = pb.start();
                procesos.add(proceso);

                System.out.println("Subproceso lanzado para: " + nombreProducto);
            }

            // Esperar a que terminen todos los procesos
            for (Process p : procesos) {
                p.waitFor();
            }

            System.out.println("\n=== Todos los subprocesos han terminado ===");
            mostrarResultadosGlobales(carpetaSalidas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lee los archivos de salida y muestra resumen global
    private static void mostrarResultadosGlobales(File carpetaSalidas) throws IOException {
        File[] resultados = carpetaSalidas.listFiles((dir, name) -> name.startsWith("salida_"));

        int totalPositivas = 0;
        int totalNegativas = 0;
        int totalNeutras = 0;
        int totalReseñas = 0;

        System.out.println("\n=== RESULTADOS POR PRODUCTO ===");
    }
}
