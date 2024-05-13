package pl.tkowalcz.geecon2024.done;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class CosineSimilarity {

    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public float cosineSimilarity(float[] a, float[] b) {
        float numerator = 0.0f;
        float denominatorA = 0.0f;
        float denominatorB = 0.0f;

        for (int i = 0; i < a.length; i++) {
            numerator += a[i] * b[i];

            denominatorA += a[i] * a[i];
            denominatorB += b[i] * b[i];
        }

        return (float) (numerator / (Math.sqrt(denominatorA * denominatorB)));
    }

    public float cosineSimilarityWithVectors(float[] a, float[] b) {
        FloatVector numerator = FloatVector.zero(SPECIES);
        FloatVector denominatorA = FloatVector.zero(SPECIES);
        FloatVector denominatorB = FloatVector.zero(SPECIES);

        int loopBound = SPECIES.loopBound(a.length);

        int i = 0;
        for (; i < loopBound; i += SPECIES.length()) {
            FloatVector vA = FloatVector.fromArray(SPECIES, a, i);
            FloatVector vB = FloatVector.fromArray(SPECIES, b, i);

            // fma = fused multiply add
            numerator = vA.fma(vB, numerator);
            denominatorA = vA.fma(vA, denominatorA);
            denominatorB = vB.fma(vB, denominatorB);
        }

        float scalarNumerator = numerator.reduceLanes(VectorOperators.ADD);
        float scalarDenominatorA = denominatorA.reduceLanes(VectorOperators.ADD);
        float scalarDenominatorB = denominatorB.reduceLanes(VectorOperators.ADD);

        for (; i < a.length; i++) {
            scalarNumerator += a[i] * b[i];

            scalarDenominatorA += a[i] * a[i];
            scalarDenominatorB += b[i] * b[i];
        }

        return (float) (scalarNumerator / (Math.sqrt(scalarDenominatorA * scalarDenominatorB)));
    }
}
