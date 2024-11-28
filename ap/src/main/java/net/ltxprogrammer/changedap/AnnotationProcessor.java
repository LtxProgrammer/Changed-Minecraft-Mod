package net.ltxprogrammer.changedap;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes(
        "net.ltxprogrammer.changed.extension.RequiredMods"
)
public class AnnotationProcessor extends AbstractProcessor {
    private static String trimString(String modId) {
        return modId.replace('"', ' ').trim();
    }

    private static String wrapString(String str) {
        return '"' + str + '"';
    }

    @SuppressWarnings("unchecked")
    private Set<String> findRequiredModIds(Element clazz) {
        return clazz.getAnnotationMirrors().stream().filter(mirror -> getSupportedAnnotationTypes().contains(mirror.getAnnotationType().toString()))
                .flatMap(mirror -> mirror.getElementValues().entrySet().stream())
                .filter(entry -> entry.getKey().getSimpleName().contentEquals("value"))
                .flatMap(entry -> ((List<Object>)entry.getValue().getValue()).stream())
                .map(Object::toString)
                .map(AnnotationProcessor::trimString)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Messager messager = processingEnv.getMessager();
        final Filer filer = processingEnv.getFiler();

        Map<String, Set<String>> modIds = new HashMap<>();
        for (TypeElement anno : annotations) {
            for (Element clazz : roundEnv.getElementsAnnotatedWith(anno)) {
                if (clazz.getAnnotationMirrors().stream().noneMatch(annotationMirror ->
                                "org.spongepowered.asm.mixin.Mixin".equals(annotationMirror.getAnnotationType().toString()))) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "class " + clazz + " is missing a @Mixin annotation");
                    continue;
                }

                final Set<String> required = findRequiredModIds(clazz);
                if (required.isEmpty()) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "class " + clazz + " is has empty @RequiredMods annotation");
                    continue;
                }

                modIds.computeIfAbsent(clazz.toString(), name -> new HashSet<>())
                                .addAll(required);
            }
        }

        if (modIds.isEmpty())
            return true;

        try {
            JavaFileObject fileObject = filer.createSourceFile("net.ltxprogrammer.changed.extension.MixinDependencies");
            try (Writer writer = fileObject.openWriter()) {
                writer.write("package net.ltxprogrammer.changed.extension;\n");
                writer.write("public abstract class MixinDependencies {\n");
                writer.write("    public static final com.google.common.collect.Multimap<String, String> MULTIMAP = com.google.common.collect.ImmutableMultimap.<String, String>builder()\n");
                for (var entry : modIds.entrySet()) {
                    if (entry.getValue().isEmpty())
                        continue;

                    writer.write("        .putAll(");
                    writer.write(wrapString(entry.getKey()));

                    for (var modId : entry.getValue()) {
                        writer.write(", ");
                        writer.write(wrapString(modId));
                    }
                    writer.write(")\n");
                }
                writer.write("        .build();\n");
                writer.write("}\n");
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.WARNING, "failed to generate mixin dependency class: " + e.getMessage());
        }

        return true;
    }
}