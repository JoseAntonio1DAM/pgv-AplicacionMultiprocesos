package net.salesianos.procesos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Analizador {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Analizador <archivo_reseñas>");
            return;
        }

        File archivo = new File(args[0]);
        if (!archivo.exists()) {
            System.out.println("No se encuentra el archivo: " + archivo.getPath());
            return;
        }

        List<String> positivas = Arrays.asList("excelente", "bueno", "recomendado", "fantástico", "genial");
        List<String> negativas = Arrays.asList("malo", "defectuoso", "caro", "horrible", "pésimo");

        int pos = 0,

                neg = 0,
                neut = 0,
                total = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.toLowerCase();
                total++;
                if (contienePalabra(linea, positivas))
                    pos++;
                else if (contienePalabra(linea, negativas))
                    neg++;
                else
                    neut++;
            }

            System.out.println("Archivo: " + archivo.getName());
            System.out.println("Total: " + total);
            System.out.println("Positivas: " + pos);
            System.out.println("Negativas: " + neg);
            System.out.println("Neutras: " + neut);
            System.out.printf("Porcentaje positivo: %.2f%%\n", (pos * 100.0) / total);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean contienePalabra(String texto, List<String> palabras) {
        for (String p : palabras) {
            if (texto.contains(p))
                return true;
        }
        return false;
    }
}
