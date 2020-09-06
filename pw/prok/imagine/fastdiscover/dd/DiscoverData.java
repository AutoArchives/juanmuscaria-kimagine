package pw.prok.imagine.fastdiscover.dd;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DiscoverData {
    private Map<String, Set<String>> annotationToClass = new HashMap<>();
    private Map<String, Set<String>> classToAnnotation = new HashMap<>();

    public Map<String, Set<String>> getAnnotationToClass() {
        return annotationToClass;
    }

    public Map<String, Set<String>> getClassesToAnnotation() {
        return classToAnnotation;
    }

    public Set<String> getAnnotationsForClass(String className) {
        Set<String> annotations = classToAnnotation.get(className);
        return annotations != null ? annotations : ImmutableSet.<String>of();
    }

    public Set<String> getClassesForAnnotation(String annotation) {
        Set<String> classes = annotationToClass.get(annotation);
        return classes != null ? classes : ImmutableSet.<String>of();
    }

    public void putAnnotations(String className, Set<String> annotations) {
        classToAnnotation.put(className, annotations);
        for (String annotation : annotations) {
            Set<String> classes = annotationToClass.get(annotation);
            if (classes == null) {
                annotationToClass.put(annotation, classes = Sets.newHashSet(className));
            } else {
                classes.add(className);
            }
        }
    }
}
