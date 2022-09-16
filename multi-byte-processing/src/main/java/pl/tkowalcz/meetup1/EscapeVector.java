package pl.tkowalcz.meetup1;

import jdk.incubator.vector.*;

public class EscapeVector {

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

    public static void vectorEscape(byte[] input, byte[] output) {
        int inputIndex = 0;
        int outputIndex = 0;

        VectorSpecies<Byte> speciesPreferred = ByteVector.SPECIES_PREFERRED;
        ByteVector backslashes = ByteVector.fromArray(speciesPreferred, new byte[]{'\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\', '\\'}, 0);

        while (inputIndex < input.length) {
            ByteVector inputVector = ByteVector.fromArray(speciesPreferred, input, inputIndex);
            VectorMask<Byte> toBeEscaped = inputVector.eq(backslashes);
            VectorMask<Byte> toBeEscaped_Inverted = toBeEscaped.not();

            ByteVector expanded = inputVector.expand(toBeEscaped_Inverted);
            ByteVector out = expanded.blend('\\', toBeEscaped);

            out.intoArray(output, outputIndex);

            inputIndex += toBeEscaped_Inverted.trueCount();
            outputIndex += speciesPreferred.length();
        }
    }
}
