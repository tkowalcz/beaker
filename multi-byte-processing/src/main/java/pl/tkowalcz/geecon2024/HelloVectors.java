package pl.tkowalcz.geecon2024;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;
import pl.tkowalcz.MyBenchmark;
import pl.tkowalcz.PerfAsmProfiler;

@PerfAsmProfiler
public class HelloVectors {

    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    @MyBenchmark
    public static float hello(float[] floats) {
        FloatVector vector = FloatVector.fromArray(SPECIES, floats, 0);

//        System.out.println("SPECIES = " + SPECIES);
//        System.out.println("vector = " + vector);

        return vector.lane(0);
    }

    public static void main(String[] args) {
        float[] floats = Microbenchmark.randomArrayOfFloat(1024);
        hello(floats);
    }
}
