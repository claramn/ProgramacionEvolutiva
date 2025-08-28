package main.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Gramatica {
    /*
    <prog> ::= <func> | <term>
    <func> ::= SICOMIDA(<prog>,<prog>) | PROG2(<prog>,<prog>) | PROG3(<prog>,<prog>,<prog>)
    <term> ::= AVANZA | DERECHA | IZQUIERDA
     */
    private List<ReglaProduccion> reglas;
    private String axiomaInicial;
    private String[] TERMINALES = {"AVANZA", "DERECHA", "IZQUIERDA"};
    private String[] FUNCIONES = {"SICOMIDA (<prog>,<prog>)","PROG2 (<prog>,<prog>)","PROG3 (<prog>,<prog>,<prog>)"};
    static Map<String, List<String>> gramatica = Map.of(
            "<prog>", List.of("<func>", "<term>"),
            "<func>", List.of("SICOMIDA(<prog>,<prog>)", "PROG2(<prog>,<prog>)", "PROG3(<prog>,<prog>,<prog>)"),
            "<term>", List.of("AVANZA", "DERECHA", "IZQUIERDA")
    );



    //  para encontrar una regla por su no terminal
    public ReglaProduccion getRegla(String noTerminal) {
        return reglas.stream()
                .filter(r -> r.getNoTerminal().equals(noTerminal))
                .findFirst()
                .orElse(null);
    }



    public String mapear(Integer[] cromosoma, int maxProf, int[] tam, int WRAPS) {
        String programa = "<func>";
        int index = 0;
        int wraps = 0;
        tam[0] = 1;

        while (hayNoTerminales(programa) && wraps < WRAPS) {
            NoTerminalInfo info = encontrarPrimerNoTerminal(programa);
            if (info == null) break;

            List<String> producciones = gramatica.get(info.noTerminal);
            if (producciones == null) return "ERROR";

            if (index >= cromosoma.length) {
                wraps++;
                index = 0;
            }

            int codon = cromosoma[index++];
            int opcion = codon % producciones.size();
            String reemplazo = producciones.get(opcion);

            programa = programa.substring(0, info.inicio) + reemplazo + programa.substring(info.fin);
        }

        if (wraps >= WRAPS) return "ERROR";
        return forzarTerminales(programa); // asegura que sea ejecutable
    }



    private static String forzarTerminales(String s) {
        Random rand = new Random();
        String[] terminales = {"AVANZA", "IZQUIERDA", "DERECHA"};

        while (hayNoTerminales(s)) {
            // elegir un terminal aleatorio
            String terminalAleatorio = terminales[rand.nextInt(terminales.length)];

            // reemplazar el primer no terminal encontrado
            s = s.replaceFirst("<term>", terminalAleatorio)
                    .replaceFirst("<prog>", terminalAleatorio)
                    .replaceFirst("<func>", terminalAleatorio);
        }
        return s;
    }

    //verifica si hay simbolos no terminales en el string
    static boolean hayNoTerminales(String programa) {
        return programa.contains("<");
    }

    //encuentra el primer no terminal en el string
    static NoTerminalInfo encontrarPrimerNoTerminal(String noTerminal) {
        int ini = noTerminal.indexOf("<");
        if(ini == -1) return null;
        int fin = noTerminal.indexOf(">", ini) +1;
        String noTerminal2 = noTerminal.substring(ini, fin);
        return new NoTerminalInfo(noTerminal2,ini,fin);
    }

    // clase auxiliar para almacenar info del no terminal encontrado
    static class NoTerminalInfo {
        String noTerminal;
        int inicio, fin;

        NoTerminalInfo(String noTerminal, int inicio, int fin) {
            this.noTerminal = noTerminal;
            this.inicio = inicio;
            this.fin = fin;
        }
    }


}
