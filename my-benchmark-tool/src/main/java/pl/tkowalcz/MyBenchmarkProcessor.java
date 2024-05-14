package pl.tkowalcz;

import com.google.auto.service.AutoService;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("pl.tkowalcz.MyBenchmark")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class MyBenchmarkProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        velocityEngine.init();

        AtomicBoolean useGcProfiler = new AtomicBoolean();
        AtomicBoolean usePerfProfiler = new AtomicBoolean();

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            List<BenchmarkSpec> benchmarks = new ArrayList<>();
            for (Element annotatedElement : annotatedElements) {
                Element clazz = annotatedElement.getEnclosingElement();
                Name methodName = annotatedElement.getSimpleName();

                useGcProfiler.set(clazz.getAnnotation(GcProfiler.class) != null);
                usePerfProfiler.set(clazz.getAnnotation(PerfProfiler.class) != null);

                List<? extends VariableElement> parameters = ((ExecutableElement) annotatedElement).getParameters();
                String[] argumentNames = parameters.stream()
                        .map(VariableElement::getSimpleName)
                        .map(Objects::toString)
                        .toArray(String[]::new);

                BenchmarkSpec benchmark = new BenchmarkSpec(
                        clazz.getEnclosingElement().toString(),
                        clazz.toString(),
                        clazz.getSimpleName().toString(),
                        methodName.toString(),
                        methodName.toString(),
                        argumentNames);
                benchmarks.add(benchmark);
            }

            Map<String, List<BenchmarkSpec>> byClazz = benchmarks.stream().collect(Collectors.groupingBy(BenchmarkSpec::clazz));
            byClazz.forEach((clazz, methods) -> {
                Template benchmarkTemplate = velocityEngine.getTemplate("microbenchmark.vm");

                VelocityContext context = new VelocityContext();
                context.put("clazz", clazz);
                context.put("packageName", methods.get(0).packageName());
                context.put("simpleClazz", methods.get(0).simpleClazz() + "MyMicrobenchmark");
                context.put("benchmarks", methods);

                context.put("useGcProfiler", useGcProfiler.get());
                context.put("usePerfProfiler", usePerfProfiler.get());

                StringWriter writer = new StringWriter();
                benchmarkTemplate.merge(context, writer);

                try {
                    JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(clazz + "MyMicrobenchmark");
                    try (Writer writer1 = builderFile.openWriter()) {
                        writer1.append(writer.toString());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return true;
    }

    private void writeBuilderFile(String className, Map<String, String> setterMap) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName
                .substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler()
                .createSourceFile(builderClassName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public class ");
            out.print(builderSimpleClassName);
            out.println(" {");
            out.println();

            out.print("    private ");
            out.print(simpleClassName);
            out.print(" object = new ");
            out.print(simpleClassName);
            out.println("();");
            out.println();

            out.print("    public ");
            out.print(simpleClassName);
            out.println(" build() {");
            out.println("        return object;");
            out.println("    }");
            out.println();

            setterMap.entrySet().forEach(setter -> {
                String methodName = setter.getKey();
                String argumentType = setter.getValue();

                out.print("    public ");
                out.print(builderSimpleClassName);
                out.print(" ");
                out.print(methodName);

                out.print("(");

                out.print(argumentType);
                out.println(" value) {");
                out.print("        object.");
                out.print(methodName);
                out.println("(value);");
                out.println("        return this;");
                out.println("    }");
                out.println();
            });

            out.println("}");
        }
    }
}
