import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {

    static class Ciudad {
        int id;
        String nombre;
        double x;
        double y;

        public Ciudad(int id, String nombre, double x, double y) {
            this.id = id;
            this.nombre = nombre;
            this.x = x;
            this.y = y;
        }
    }

    static class Solucion {
        List<Integer> ruta;
        double costo;

        public Solucion(List<Integer> ruta, double costo) {
            this.ruta = ruta;
            this.costo = costo;
        }
    }

    static Random random = new Random(); //pref. con semilla

    public static void main(String[] args) {

        // Creacion de ciudades
        List<Ciudad> ciudades = new ArrayList<>();

        ciudades.add(new Ciudad(0, "A", 0, 0));
        ciudades.add(new Ciudad(1, "B", 2, 1));
        ciudades.add(new Ciudad(2, "C", 4, 1));
        ciudades.add(new Ciudad(3, "D", 5, 4));
        ciudades.add(new Ciudad(4, "E", 2, 5));
        ciudades.add(new Ciudad(5, "F", 1, 3));

        // Ciudad desde la cual inicia y termina la ruta
        int origen = 0;

        int maxIteraciones = 100;

        // 0 = comportamiento muy voraz
        // 1 = comportamiento más aleatorio
        double alpha = 0.9;

        Solucion mejorSolucion = ejecutarGRASP(ciudades, origen, maxIteraciones, alpha);

        System.out.println("\n\nMEJOR SOLUCION ENCONTRADA");
        mostrarRuta(mejorSolucion.ruta, ciudades);

        System.out.printf("Costo total: %.4f%n", mejorSolucion.costo);
    }

    public static Solucion ejecutarGRASP(List<Ciudad> ciudades, int origen, int maxIteraciones, double alpha) {

        Solucion mejorGlobal = null;

        for (int iteracion = 1; iteracion <= maxIteraciones; iteracion++) {

            // Fase 1: Construcción voraz aleatoria
            List<Integer> rutaConstruida = construirGRASP(ciudades, origen, alpha);

            double costoConstruido = evaluarRuta(rutaConstruida, ciudades);

            // Fase 2: Búsqueda local
            List<Integer> rutaMejorada = busquedaLocal2Opt(rutaConstruida, ciudades);

            double costoMejorado = evaluarRuta(rutaMejorada, ciudades);

            System.out.println("\nIteracion " + iteracion);

            System.out.printf("Costo construido: %.4f%n", costoConstruido);

            System.out.printf("Costo mejorado:    %.4f%n", costoMejorado);

            if (mejorGlobal == null || costoMejorado < mejorGlobal.costo) {

                mejorGlobal = new Solucion(new ArrayList<>(rutaMejorada), costoMejorado);

                System.out.println(">>> Nueva mejor solucion encontrada");
            }
        }

        return mejorGlobal;
    }

    public static List<Integer> construirGRASP(List<Ciudad> ciudades, int origen, double alpha) {

        List<Integer> ruta = new ArrayList<>();

        // Lista de ciudades que todavía no se visitan
        List<Integer> noVisitadas = new ArrayList<>();

        for (int i = 0; i < ciudades.size(); i++) {
            if (i != origen) {
                noVisitadas.add(i);
            }
        }

        ruta.add(origen); // La ruta comienza en la ciudad origen

        int ciudadActual = origen;

        while (!noVisitadas.isEmpty()) {

            double costoMinimo = Double.MAX_VALUE;
            double costoMaximo = Double.MIN_VALUE;

            for (int candidato : noVisitadas) {

                double costo = distancia(ciudades.get(ciudadActual), ciudades.get(candidato));

                if (costo < costoMinimo) {
                    costoMinimo = costo;
                }

                if (costo > costoMaximo) {
                    costoMaximo = costo;
                }
            }

            // ---------------------------------------------
            // Calcular el umbral de la RCL
            //
            // limite =
            // minimo + alpha * (maximo - minimo)
            // ---------------------------------------------
            double limite = costoMinimo + alpha * (costoMaximo - costoMinimo);

            // Construccion de la lista RCL
            List<Integer> rcl = new ArrayList<>();

            for (int candidato : noVisitadas) {

                double costo = distancia(ciudades.get(ciudadActual), ciudades.get(candidato));

                if (costo <= limite) {
                    rcl.add(candidato);
                }
            }

            int indiceAleatorio = random.nextInt(rcl.size());

            int ciudadSeleccionada = rcl.get(indiceAleatorio);

            ruta.add(ciudadSeleccionada);

            noVisitadas.remove(Integer.valueOf(ciudadSeleccionada));

            ciudadActual = ciudadSeleccionada;
        }

        ruta.add(origen);

        return ruta;
    }

    public static List<Integer> busquedaLocal2Opt(List<Integer> rutaInicial, List<Ciudad> ciudades) {

        List<Integer> mejorRuta = new ArrayList<>(rutaInicial);

        double mejorCosto = evaluarRuta(mejorRuta, ciudades);

        boolean huboMejora = true;

        while (huboMejora) {

            huboMejora = false;

            // i comienza en 1 para no modificar el origen
            for (int i = 1;
                 i < mejorRuta.size() - 2;
                 i++) {

                for (int j = i + 1;
                     j < mejorRuta.size() - 1;
                     j++) {

                    List<Integer> vecino = swap(mejorRuta, i, j);

                    double costoVecino = evaluarRuta(vecino, ciudades);

                    if (costoVecino < mejorCosto) {

                        mejorRuta = vecino;
                        mejorCosto = costoVecino;

                        huboMejora = true;

                        // Corta para volver a comenzar
                        // desde la nueva solución
                        break;
                    }
                }

                if (huboMejora) {
                    break;
                }
            }
        }

        return mejorRuta;
    }

    public static List<Integer> swap(List<Integer> ruta, int inicio, int fin) {

        List<Integer> nuevaRuta = new ArrayList<>(ruta);

        while (inicio < fin) {

            Collections.swap(nuevaRuta, inicio, fin);

            inicio++;
            fin--;
        }

        return nuevaRuta;
    }

    public static double evaluarRuta(List<Integer> ruta, List<Ciudad> ciudades) {

        double costoTotal = 0.0;

        for (int i = 0; i < ruta.size() - 1; i++) {

            Ciudad ciudadA = ciudades.get(ruta.get(i));

            Ciudad ciudadB = ciudades.get(ruta.get(i + 1));

            costoTotal += distancia(ciudadA, ciudadB);
        }

        return costoTotal;
    }

    public static double distancia(Ciudad a, Ciudad b) {

        double diferenciaX = a.x - b.x;

        double diferenciaY = a.y - b.y;

        return Math.sqrt(diferenciaX * diferenciaX + diferenciaY * diferenciaY
        );
    }

    public static void mostrarRuta(List<Integer> ruta, List<Ciudad> ciudades) {

        System.out.print("Ruta: ");

        for (int i = 0; i < ruta.size(); i++) {

            int indiceCiudad = ruta.get(i);

            System.out.print(ciudades.get(indiceCiudad).nombre);

            if (i < ruta.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }
}