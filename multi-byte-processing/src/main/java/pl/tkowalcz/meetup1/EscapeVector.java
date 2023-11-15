package pl.tkowalcz.meetup1;

import jdk.incubator.vector.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EscapeVector {
    private static final ByteVector BACKSLASHES;
    private static final VectorShuffle<Byte> SHUFFLE;
    private static final VectorMask<Byte> MASK;

    static {
        byte[] bytes = new byte[ByteVector.SPECIES_512.length()];
        Arrays.fill(bytes, (byte) '\\');

        BACKSLASHES = ByteVector.fromArray(
                ByteVector.SPECIES_512,
                bytes,
                0
        );

//        int[] indices = IntStream.range(1, ByteVector.SPECIES_512.length() + 1).toArray();
//        indices[indices.length - 1] = ByteVector.SPECIES_512.length() - 1;
//
//        SHUFFLE = VectorShuffle.fromArray(
//                ByteVector.SPECIES_512,
//                indices,
//                0
//        );

        SHUFFLE = VectorShuffle.iota(
                ByteVector.SPECIES_512,
                0,
                1,
                true
        );

        boolean[] mask = ArrayUtils.toPrimitive(Stream.iterate(true, i -> !i).limit(ByteVector.SPECIES_512.length()).toArray(Boolean[]::new));
        MASK = VectorMask.fromArray(
                ByteVector.SPECIES_512,
                mask,
                0
        );
    }

    public static void escape(byte[] input, byte[] output) {
        int inputIndex = 0;
        int outputIndex = 0;

        while (inputIndex < input.length) {
            if (input[inputIndex] == '\\') {
                output[outputIndex] = '\\';
                outputIndex++;
            }

            output[outputIndex] = input[inputIndex];

            inputIndex++;
            outputIndex++;
        }
    }

    public static void vectorEscapE_AVX512_VBMI2(byte[] input, byte[] output) {
        int inputIndex = 0;
        int outputIndex = 0;

        while (inputIndex + ByteVector.SPECIES_PREFERRED.length() < input.length) {
//            ByteVector.SPECIES_PREFERRED.indexInRange(inputIndex, input.length);
            ByteVector inputVector = ByteVector.fromArray(ByteVector.SPECIES_PREFERRED, input, inputIndex);
            VectorMask<Byte> toBeEscaped = inputVector.eq(BACKSLASHES);
            VectorMask<Byte> toBeEscaped_Inverted = toBeEscaped.not();

            ByteVector expanded = inputVector.expand(toBeEscaped_Inverted);
            ByteVector out = expanded.blend('\\', toBeEscaped);

            out.intoArray(output, outputIndex);

            inputIndex += toBeEscaped_Inverted.trueCount();
            outputIndex += ByteVector.SPECIES_PREFERRED.length();
        }
    }

    public static void vectorEscape(byte[] input, byte[] output) {
        int inputIndex = 0;
        int outputIndex = 0;

        while (inputIndex + ByteVector.SPECIES_256.length() < input.length) {
            ByteVector byteVector = ByteVector
                    .fromArray(ByteVector.SPECIES_256, input, inputIndex)
                    .convertShape(VectorOperators.B2S, ShortVector.SPECIES_512, 0)
                    .lanewise(VectorOperators.REVERSE_BYTES)
                    .reinterpretAsBytes();

            VectorMask<Byte> toBeEscaped = byteVector.eq(BACKSLASHES).or(MASK);
            ByteVector blend = byteVector.slice(1).blend(BACKSLASHES, toBeEscaped);

            blend.intoArray(output, outputIndex, toBeEscaped);

            inputIndex += toBeEscaped.trueCount();
            outputIndex += ByteVector.SPECIES_PREFERRED.length();
        }
    }
}
