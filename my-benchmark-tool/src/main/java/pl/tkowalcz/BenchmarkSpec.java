package pl.tkowalcz;

import javax.lang.model.element.VariableElement;
import java.util.List;

public record BenchmarkSpec(
        String methodName,
        List<? extends VariableElement> parameters,
        String[] argumentNames
) {
}
