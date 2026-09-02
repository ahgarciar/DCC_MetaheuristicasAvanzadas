import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {
    static Random rnd;

    public static void main(String[] args) {
        rnd = new Random();
        int tam_poblacion = 100;
        int tam_cromosoma = 1500;

        ArrayList<Individuo> poblacion = new ArrayList<>(); //Lista de soluciones
        //Generacion solución...
        for (int i = 0; i < tam_poblacion; i++) {
            poblacion.add(creaSolucion(tam_cromosoma));
        }

        System.out.println("Población Inicial:");
        ImprimeSolucion(poblacion);

        int tot_eval_fo = 5000;
        int eval_actual = 0;
        ///
        do{

            //seleccion natural
            Collections.sort(poblacion);
            double suma = 0; //acumulado de la proba
            for (Individuo indv: poblacion){
                suma += indv.vo;
            }

            int[] indicesPadres = new int[2]; //Para los dos padres que se ocupan

            for (int i = 0; i < 2; i++) {

                double u = rnd.nextDouble();
                int indice = 0;
                while (u>0 && indice < poblacion.size()) {
                    u -= poblacion.get(indice).vo/suma;

                    indice++;
                }

                indicesPadres[i] = indice-1;
            }

            //cruza
            int k = rnd.nextInt(tam_cromosoma); //punto de cruza
            Individuo padre1 = poblacion.get(indicesPadres[0]);
            Individuo padre2 = poblacion.get(indicesPadres[1]);
            Individuo hijo1 = new Individuo();
            Individuo hijo2 = new Individuo();
            //Genera el espacio de memoria para las soluciones
            hijo1.solucion = new int[tam_cromosoma]; //
            hijo2.solucion = new int[tam_cromosoma];
            //copia la parte inicial de la solución
            int i;
            for (i = 0; i < k; i++) {
                hijo1.solucion[i] = padre1.solucion[i];
                hijo2.solucion[i] = padre2.solucion[i];
            }
            //copia la parte final de la solución
            for (;i < tam_cromosoma;i++) {
                hijo1.solucion[i] = padre2.solucion[i];
                hijo2.solucion[i] = padre1.solucion[i];
            }

            //System.out.println();
            //mutacion
            Individuo[] hijos = new Individuo[2];
            hijos[0] = hijo1;
            hijos[1] = hijo2;
            double probabilidad_muta_gen = 0.1; //1/tam_cromosoma;
            for (int h = 0; h < 2; h++) {
                for (i = 0; i < tam_cromosoma; i++) {
                    double u = rnd.nextDouble();
                    if (u<probabilidad_muta_gen){
                        hijos[h].solucion[i] = hijos[h].solucion[i] == 1 ? 0: 1;
                    }
                }
            }

            //Evaluar funcion objetivo de los hijos
            for (int j = 0; j < 2; j++) {
                int vo = evaluaFO(hijos[j].solucion);
                hijos[j].vo = vo;
            }

            poblacion.add(hijos[0]);
            poblacion.add(hijos[1]);

            Collections.sort(poblacion);

            poblacion.removeLast();
            poblacion.removeLast();

            // ------

            eval_actual++;
        }
        while(eval_actual<tot_eval_fo); //criterios de paro

        System.out.println("\n\nMejor población generada: ");
        ImprimeSolucion(poblacion);

    }

    public static Individuo creaSolucion(int n){
        int[] solucion = new int[n];

        for (int i = 0; i < n; i++) {
            solucion[i] = rnd.nextInt(2);
        }
        int vo = evaluaFO(solucion);
        Individuo indv = new Individuo();
        indv.solucion = solucion;
        indv.vo = vo;
        return indv;
    }

    public static int evaluaFO(int[] solucion){
        int vo = 0;
        for (int i = 0; i < solucion.length; i++) {
            vo += solucion[i];
        }
        return vo;
    }

    public static void ImprimeSolucion(ArrayList<Individuo> pob){
        for (Individuo individuo : pob){ //por cada inviduo
            int[] solucion = individuo.solucion;
            for (int i = 0; i < solucion.length; i++) { //recorre cada gen del individuo
                System.out.print(solucion[i] + " \t"); //imprime al gen
            }
            System.out.print( " VO : " + individuo.vo +" \t");
            System.out.println();
        }

    }

}