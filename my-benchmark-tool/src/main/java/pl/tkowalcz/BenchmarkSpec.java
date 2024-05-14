package pl.tkowalcz;

public record BenchmarkSpec(
        String packageName,
        String clazz,
        String simpleClazz,
        String name,
        String methodName,
        String[] argumentNames
) {
}
