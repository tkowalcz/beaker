package pl.tkowalcz;

import javax.lang.model.element.VariableElement;

public record FieldSpec(String name, String type, String type2) {

    public static FieldSpec fromElement(VariableElement variableElement) {
        String arrayType = variableElement.asType().toString();
        String type = arrayType
                .replaceAll("\\[", "")
                .replaceAll("]", "");
        String typeCapitalised = Character.toUpperCase(type.charAt(0)) + type.substring(1);

        return new FieldSpec(
                variableElement.getSimpleName().toString(),
                arrayType,
                typeCapitalised
        );
    }
}
