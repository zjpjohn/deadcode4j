package de.is24.deadcode4j.analyzer;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import de.is24.deadcode4j.Analyzer;
import de.is24.deadcode4j.CodeContext;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static de.is24.deadcode4j.Utils.addToMappedSet;
import static java.lang.annotation.ElementType.*;
import static java.util.Arrays.asList;

/**
 * Analyzes class files: looks for
 * <code>org.hibernate.annotations.TypeDef</code>/<code>org.hibernate.annotations.TypeDefs</code> and
 * <code>org.hibernate.annotations.Type</code> annotations and sets up a dependency between the class annotated with
 * <code>@Type</code> and the associated <code>@TypeDef</code> annotated class
 * (i.e. <code>Type.type</code> &rarr; <code>TypeDef.name</code>).
 *
 * @since 1.4
 */
public class HibernateAnnotationsAnalyzer extends ByteCodeAnalyzer implements Analyzer {
    @Nonnull
    @SuppressWarnings("unchecked")
    private static Collection<Annotation> getAnnotations(@Nonnull CtClass clazz, @Nonnull String typeName, ElementType... elementTypes) {
        List<ElementType> types = asList(elementTypes);
        List<AttributeInfo> attributes = newArrayList();
        if (types.contains(ElementType.TYPE)) {
            attributes.addAll(clazz.getClassFile2().getAttributes());
        }
        if (types.contains(METHOD)) {
            for (CtMethod method : clazz.getDeclaredMethods()) {
                attributes.addAll(method.getMethodInfo2().getAttributes());
            }
        }
        if (types.contains(FIELD)) {
            for (CtField field : clazz.getDeclaredFields()) {
                attributes.addAll(field.getFieldInfo2().getAttributes());
            }
        }

        List<Annotation> annotations = newArrayList();
        for (AttributeInfo attribute : attributes) {
            if (AnnotationsAttribute.class.isInstance(attribute)) {
                for (Annotation annotation : AnnotationsAttribute.class.cast(attribute).getAnnotations()) {
                    if (typeName.equals(annotation.getTypeName()))
                        annotations.add(annotation);
                }
            }
        }

        return annotations;
    }

    private static String getStringFrom(Annotation annotation, String memberName) {
        MemberValue memberValue = annotation.getMemberValue(memberName);
        if (!StringMemberValue.class.isInstance(memberValue))
            throw new IllegalArgumentException("The member [" + memberName + "] is no StringMemberValue!");
        return StringMemberValue.class.cast(memberValue).getValue();
    }

    private static Iterable<Annotation> getAnnotationsFrom(Annotation annotation, String memberName) {
        MemberValue memberValue = annotation.getMemberValue(memberName);
        if (!ArrayMemberValue.class.isInstance(memberValue))
            throw new IllegalArgumentException("The member [" + memberName + "] is no ArrayMemberValue!");
        return Iterables.transform(asList(ArrayMemberValue.class.cast(memberValue).getValue()), new Function<MemberValue, Annotation>() {
            @Override
            public Annotation apply(@Nullable MemberValue memberValue) {
                return memberValue == null ? null : ((AnnotationMemberValue) memberValue).getValue();
            }
        });
    }

    private final Map<String, String> typeDefinitions = newHashMap();
    private final Map<String, Set<String>> typeUsages = newHashMap();
    private final Map<String, Set<String>> generatorDefinitions = newHashMap();

    @Override
    protected final void analyzeClass(@Nonnull CodeContext codeContext, @Nonnull CtClass clazz) {
        codeContext.addAnalyzedClass(clazz.getName());
        processTypeDefAnnotation(clazz);
        processTypeDefsAnnotation(clazz);
        processTypeAnnotations(clazz);
        processGenericGenerator(clazz);
        processGenericGenerators(clazz);
    }

    @Override
    public void finishAnalysis(@Nonnull CodeContext codeContext) {
        reportDependencies(codeContext);
    }

    private void processTypeDefAnnotation(@Nonnull CtClass clazz) {
        for (Annotation annotation : getAnnotations(clazz, "org.hibernate.annotations.TypeDef", TYPE)) {
            processTypeDefinition(clazz, annotation);
        }
    }

    private void processTypeDefinition(@Nonnull CtClass clazz, @Nonnull Annotation annotation) {
        String typeName = getStringFrom(annotation, "name");
        this.typeDefinitions.put(typeName, clazz.getName());
    }

    private void processTypeDefsAnnotation(@Nonnull CtClass clazz) {
        for (Annotation annotation : getAnnotations(clazz, "org.hibernate.annotations.TypeDefs", TYPE)) {
            for (Annotation childAnnotation : getAnnotationsFrom(annotation, "value")) {
                processTypeDefinition(clazz, childAnnotation);
            }
        }
    }

    private void processTypeAnnotations(@Nonnull CtClass clazz) {
        for (Annotation annotation : getAnnotations(clazz, "org.hibernate.annotations.Type", METHOD, FIELD)) {
            String typeName = getStringFrom(annotation, "type");
            addToMappedSet(this.typeUsages, typeName, clazz.getName());
        }
    }

    private void processGenericGenerator(CtClass clazz) {
        for (Annotation annotation : getAnnotations(clazz, "org.hibernate.annotations.GenericGenerator", TYPE, METHOD, FIELD)) {
            processGenericGenerator(clazz, annotation);
        }
    }

    private void processGenericGenerator(CtClass clazz, Annotation annotation) {
        String generatorStrategy = getStringFrom(annotation, "strategy");
        addToMappedSet(this.generatorDefinitions, clazz.getName(), generatorStrategy);
    }

    private void processGenericGenerators(CtClass clazz) {
        for (Annotation annotation : getAnnotations(clazz, "org.hibernate.annotations.GenericGenerators", TYPE)) {
            for (Annotation childAnnotation : getAnnotationsFrom(annotation, "value")) {
                processGenericGenerator(clazz, childAnnotation);
            }
        }
    }

    private void reportDependencies(@Nonnull CodeContext codeContext) {
        final Collection<String> analyzedClasses = codeContext.getAnalyzedCode().getAnalyzedClasses();

        for (Map.Entry<String, Set<String>> generatorDefinition : this.generatorDefinitions.entrySet()) {
            for (String generator : generatorDefinition.getValue()) {
                if (analyzedClasses.contains(generator))
                    codeContext.addDependencies(generatorDefinition.getKey(), generator);
            }
        }

        for (Map.Entry<String, Set<String>> typeUsage : this.typeUsages.entrySet()) {
            String typeName = typeUsage.getKey();
            String classDefiningType = this.typeDefinitions.get(typeName);

            String dependee = null;
            if (classDefiningType != null) {
                dependee = classDefiningType;
            } else if (analyzedClasses.contains(typeName)) {
                dependee = typeName;
            } // no matter what else, scope is outside of the analyzed project
            if (dependee != null) {
                for (String classUsingType : typeUsage.getValue()) {
                    codeContext.addDependencies(classUsingType, dependee);
                }
            }
        }
    }

}
