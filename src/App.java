import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
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
                int code = p.waitFor();
                if (code != 0) {
                    System.err.println("Subproceso finalizado con código: " + code);
                }
            }

            System.out.println("\n=== Todos los subprocesos han terminado ===");
            mostrarResultadosGlobales(carpetaSalidas);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error en ejecución de subprocesos: " + e.getMessage());
            // Si se interrumpe, restaurar el estado de interrupción
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Lee los archivos de salida y muestra resumen global
    private static void mostrarResultadosGlobales(File carpetaSalidas) {
        File[] resultados = carpetaSalidas.listFiles((dir, name) -> name.startsWith("salida_"));

        if (resultados == null || resultados.length == 0) {
            System.out.println("No hay archivos de salida para mostrar resultados.");
            return;
        }

        int totalPositivas = 0;
        int totalNegativas = 0;
        int totalNeutras = 0;
        int totalReseñas = 0;

        System.out.println("\n=== RESULTADOS POR PRODUCTO ===");

        for (File salida : resultados) {
            String nombreProducto = salida.getName().replaceFirst("^salida_", "").replaceFirst("\\.txt$", "");
            int pos = 0;
            int neg = 0;
            int neu = 0;
            int total = 0;

            List<String> lines;
            try {
                lines = Files.readAllLines(salida.toPath(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + salida.getName() + ": " + e.getMessage());
                continue;
            }

            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                total++;
                String low = line.toLowerCase();
                // Buscar indicadores simples de clasificación (ajustar según formato real de
                // salida)
                if (low.contains("posit")) {
                    pos++;
                } else if (low.contains("negat")) {
                    neg++;
                } else if (low.contains("neutr") || low.contains("neut")) {
                    neu++;
                }
            }

            totalPositivas += pos;
            totalNegativas += neg;
            totalNeutras += neu;
            totalReseñas += total;

            System.out.println("Producto: " + nombreProducto);
            System.out.println("  Positivas: " + pos);
            System.out.println("  Negativas: " + neg);
            System.out.println("  Neutras: " + neu);
            System.out.println("  Total reseñas leídas: " + total);
        }

        System.out.println("\n=== RESULTADO GLOBAL ===");
        System.out.println("Total positivas: " + totalPositivas);
        System.out.println("Total negativas: " + totalNegativas);
        System.out.println("Total neutras: " + totalNeutras);
        System.out.println("Total reseñas: " + totalReseñas);
    }
}
