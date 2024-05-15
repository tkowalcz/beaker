package pl.tkowalcz;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public record ClassSpec(String className,
                        String packageName,
                        boolean useGcProfiler,
                        boolean usePerfProfiler,
                        boolean usePerfAsmProfiler,
                        List<BenchmarkSpec> benchmarkMethods) {

    public static ClassSpec fromElement(Element clazz) {
        boolean useGcProfiler = clazz.getAnnotation(GcProfiler.class) != null;
        boolean usePerfProfiler = clazz.getAnnotation(PerfProfiler.class) != null;
        boolean usePerfAsmProfiler = clazz.getAnnotation(PerfAsmProfiler.class) != null;

        return new ClassSpec(
                clazz.getSimpleName().toString(),
                clazz.getEnclosingElement().toString(),
                useGcProfiler,
                usePerfProfiler,
                usePerfAsmProfiler,
                new ArrayList<>());
    }
}
