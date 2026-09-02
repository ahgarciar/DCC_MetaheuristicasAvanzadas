import java.util.Comparator;

public class Individuo implements Comparable<Individuo> {

    public int[] solucion;
    public int vo;

    @Override
    public int compareTo(Individuo o) {
        return  o.vo - this.vo;
    }
}
