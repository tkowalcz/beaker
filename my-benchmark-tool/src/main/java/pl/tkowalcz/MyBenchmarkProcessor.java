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
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("pl.tkowalcz.MyBenchmark")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class MyBenchmarkProcessor extends AbstractProcessor {

    private final VelocityEngine velocityEngine;

    public MyBenchmarkProcessor() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ClassSpec> classes = new HashMap<>();

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotatedElement : annotatedElements) {
                Element clazz = annotatedElement.getEnclosingElement();

                ClassSpec classSpec = classes.computeIfAbsent(clazz.toString(), __ -> ClassSpec.fromElement(clazz));

                Name methodName = annotatedElement.getSimpleName();
                List<? extends VariableElement> parameters = ((ExecutableElement) annotatedElement).getParameters();

                String[] argumentNames = parameters.stream()
                        .map(VariableElement::getSimpleName)
                        .map(Objects::toString)
                        .toArray(String[]::new);

                BenchmarkSpec benchmarkSpec = new BenchmarkSpec(methodName.toString(), parameters, argumentNames);
                classSpec.benchmarkMethods().add(benchmarkSpec);
            }
        }

        classes.values().forEach(this::doClass);
        return true;
    }

    private void doClass(ClassSpec classSpec) {
        List<FieldSpec> fields = classSpec.benchmarkMethods()
                .stream()
                .flatMap(benchmarkSpec -> benchmarkSpec.parameters().stream())
                .map(FieldSpec::fromElement)
                .distinct()
                .toList();

        Template benchmarkTemplate = velocityEngine.getTemplate("microbenchmark.vm");

        VelocityContext context = new VelocityContext();
        context.put("clazz", classSpec.className());
        context.put("packageName", classSpec.packageName());
        context.put("benchmarkClass", classSpec.className() + "MyMicrobenchmark");
        context.put("benchmarks", classSpec.benchmarkMethods());
        context.put("fields", fields);

        context.put("useGcProfiler", classSpec.useGcProfiler());
        context.put("usePerfProfiler", classSpec.usePerfProfiler());
        context.put("usePerfAsmProfiler", classSpec.usePerfAsmProfiler());

        StringWriter writer = new StringWriter();
        benchmarkTemplate.merge(context, writer);

        try {
            JavaFileObject builderFile = processingEnv
                    .getFiler()
                    .createSourceFile(classSpec.packageName() + "." + classSpec.className() + "MyMicrobenchmark");
            try (Writer writer1 = builderFile.openWriter()) {
                writer1.append(writer.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

