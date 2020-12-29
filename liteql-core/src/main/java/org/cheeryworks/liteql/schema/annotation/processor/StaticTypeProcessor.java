package org.cheeryworks.liteql.schema.annotation.processor;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.schema.annotation.LiteQLReferenceField;
import org.cheeryworks.liteql.schema.annotation.LiteQLStaticType;
import org.cheeryworks.liteql.schema.field.BlobField;
import org.cheeryworks.liteql.schema.field.BooleanField;
import org.cheeryworks.liteql.schema.field.ClobField;
import org.cheeryworks.liteql.schema.field.DecimalField;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;
import org.cheeryworks.liteql.schema.field.IntegerField;
import org.cheeryworks.liteql.schema.field.LongField;
import org.cheeryworks.liteql.schema.field.ReferenceField;
import org.cheeryworks.liteql.schema.field.StringField;
import org.cheeryworks.liteql.schema.field.TimestampField;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({
        StaticTypeProcessor.SUPPORTED_JPA_ANNOTATION_ENTITY,
        StaticTypeProcessor.SUPPORTED_JPA_ANNOTATION_MAPPED_SUPERCLASS,
        StaticTypeProcessor.SUPPORTED_ANNOTATION_LITE_QL_TYPE,
        StaticTypeProcessor.SUPPORTED_ANNOTATION_LITE_QL_MAPPED_TYPE
})
public class StaticTypeProcessor extends AbstractProcessor {

    public static final String SUPPORTED_ANNOTATION_LITE_QL_TYPE
            = "org.cheeryworks.liteql.schema.annotation.LiteQLType";

    public static final String SUPPORTED_ANNOTATION_LITE_QL_MAPPED_TYPE
            = "org.cheeryworks.liteql.schema.annotation.LiteQLMappedType";

    public static final String SUPPORTED_JPA_ANNOTATION_ENTITY = "javax.persistence.Entity";

    public static final String SUPPORTED_JPA_ANNOTATION_MAPPED_SUPERCLASS = "javax.persistence.MappedSuperclass";

    private static final String SUPPORTED_JPA_SUB_ANNOTATION_ID = "javax.persistence.Id";

    private static final String SUPPORTED_JPA_SUB_ANNOTATION_LOB = "javax.persistence.Lob";

    private static final String SUPPORTED_JPA_SUB_ANNOTATION_TRANSIENT = "javax.persistence.Transient";

    private static final String JPA_ANNOTATION_BASE_PACKAGE = "javax.persistence";

    private static final String STATIC_TYPE_NAME_SUFFIX = "___";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotationElement -> {
            roundEnv.getElementsAnnotatedWith(annotationElement).forEach(element -> {
                if (!ElementKind.CLASS.equals(element.getKind())) {
                    throw new IllegalStateException(element.toString() + " is not a class");
                }

                TypeElement typeElement = (TypeElement) element;

                processTypeElement(typeElement);
            });
        });

        return false;
    }

    private void processTypeElement(TypeElement entityElement) {
        Set<TypeElement> entityInterfaceElements = entityElement.getInterfaces()
                .stream()
                .map(typeMirror -> (TypeElement) ((DeclaredType) typeMirror).asElement())
                .collect(Collectors.toSet());

        checkJpaAnnotationCompatibility(entityElement, entityInterfaceElements);

        checkLiteQLAnnotationCompatibility(entityElement);

        Map<String, Element> liteQLReferenceFieldMethodElements
                = getLiteQLReferenceFieldMethodElements(entityElement, entityInterfaceElements);

        Map<String, Class<? extends Field>> entityFieldDefinitions = new HashMap<>();

        entityElement.getEnclosedElements()
                .stream()
                .filter(element -> isFieldElement(element))
                .forEach(fieldElement -> {
                    String fieldNameFromFieldElement = fieldElement.getSimpleName().toString();

                    Element methodElement = liteQLReferenceFieldMethodElements.get(fieldNameFromFieldElement);

                    entityFieldDefinitions.put(
                            getFieldName(entityElement, fieldNameFromFieldElement, fieldElement, methodElement),
                            getFieldType(fieldElement, methodElement));
                });

        writeStaticType(entityElement, entityFieldDefinitions);
    }

    private void checkJpaAnnotationCompatibility(TypeElement entityElement, Set<TypeElement> entityInterfaceElements) {
        entityElement.getEnclosedElements()
                .stream()
                .filter(element -> ElementKind.METHOD.equals(element.getKind()))
                .forEach(element -> {
                    element.getAnnotationMirrors()
                            .forEach(annotationMirror -> {
                                if (annotationMirror.getAnnotationType().toString()
                                        .startsWith(JPA_ANNOTATION_BASE_PACKAGE)) {
                                    throw new IllegalStateException("JPA annotation on method unsupported");
                                }
                            });
                });

        entityInterfaceElements.forEach(entityInterfaceElement -> {
            entityInterfaceElement.getEnclosedElements()
                    .stream()
                    .filter(element -> ElementKind.METHOD.equals(element.getKind()))
                    .forEach(element -> {
                        element.getAnnotationMirrors()
                                .forEach(annotationMirror -> {
                                    if (annotationMirror.getAnnotationType().toString()
                                            .startsWith(JPA_ANNOTATION_BASE_PACKAGE)) {
                                        throw new IllegalStateException("JPA annotation on method unsupported");
                                    }
                                });
                    });
        });
    }

    private void checkLiteQLAnnotationCompatibility(TypeElement entityElement) {
        entityElement.getEnclosedElements()
                .stream()
                .filter(element -> ElementKind.METHOD.equals(element.getKind()))
                .forEach(element -> {
                    element.getAnnotationMirrors()
                            .forEach(annotationMirror -> {
                                if (annotationMirror.getAnnotationType().toString()
                                        .equals(LiteQLReferenceField.class.getName())) {
                                    throw new IllegalStateException(
                                            "LiteQLReferenceField annotation on method"
                                                    + " in class or abstract class unsupported");
                                }
                            });
                });
    }

    private Map<String, Element> getLiteQLReferenceFieldMethodElements(
            TypeElement entityElement, Set<TypeElement> entityInterfaceElements) {
        Map<String, Element> liteqlReferenceFieldMethodElements = new HashMap<>();

        entityInterfaceElements.forEach(entityInterfaceElement -> {
            entityInterfaceElement.getEnclosedElements()
                    .stream()
                    .filter(element -> isLiteQLReferenceFieldMethodElement(element))
                    .forEach(liteQLReferenceFieldMethodElement -> {
                        String fieldNameFromMethodElement
                                = getFieldNameFromMethodElement(liteQLReferenceFieldMethodElement);

                        if (liteqlReferenceFieldMethodElements.containsKey(fieldNameFromMethodElement)) {
                            throw new IllegalStateException(
                                    "Duplicated LiteQLReferenceField declaration for property "
                                            + fieldNameFromMethodElement
                                            + " on entity " + entityElement.getQualifiedName());
                        }

                        liteqlReferenceFieldMethodElements.put(
                                fieldNameFromMethodElement, liteQLReferenceFieldMethodElement);
                    });
        });

        return liteqlReferenceFieldMethodElements;
    }

    private boolean isFieldElement(Element element) {
        return ElementKind.FIELD.equals(element.getKind())
                && !element.getModifiers().contains(Modifier.STATIC)
                && !element.getModifiers().contains(Modifier.TRANSIENT)
                && !containAnyAnnotations(element, SUPPORTED_JPA_SUB_ANNOTATION_TRANSIENT);
    }

    private String getFieldNameFromMethodElement(Element methodElement) {
        if (ElementKind.FIELD.equals(methodElement.getKind())) {
            return methodElement.getSimpleName().toString();
        } else if (ElementKind.METHOD.equals(methodElement.getKind())) {
            String methodName = methodElement.getSimpleName().toString();

            if (methodName.startsWith("get")) {
                return StringUtils.uncapitalize(methodName.substring(3));
            } else if (methodName.startsWith("is")) {
                return StringUtils.uncapitalize(methodName.substring(2));
            }
        }

        throw new IllegalStateException("Unsupported element " + methodElement.toString());
    }

    private boolean isLiteQLReferenceFieldMethodElement(Element element) {
        return ElementKind.METHOD.equals(element.getKind()) && containAnyAnnotations(
                element,
                LiteQLReferenceField.class.getName());
    }

    private String getFieldName(
            TypeElement entityElement, String fieldNameFromFieldElement, Element fieldElement, Element methodElement) {
        LiteQLReferenceField liteQLReferenceFieldOnField
                = fieldElement.getAnnotation(LiteQLReferenceField.class);

        LiteQLReferenceField liteQLReferenceFieldOnMethod = null;

        if (methodElement != null) {
            liteQLReferenceFieldOnMethod = methodElement.getAnnotation(LiteQLReferenceField.class);
        }

        if (liteQLReferenceFieldOnField != null && liteQLReferenceFieldOnMethod != null) {
            throw new IllegalStateException(
                    "Duplicated LiteQLReferenceField definition for field " + fieldNameFromFieldElement
                            + " on entity " + entityElement.getQualifiedName());
        } else if (liteQLReferenceFieldOnField != null) {
            return liteQLReferenceFieldOnField.name();
        } else if (liteQLReferenceFieldOnMethod != null) {
            return liteQLReferenceFieldOnMethod.name();
        }

        return fieldNameFromFieldElement;
    }

    private Class<? extends Field> getFieldType(Element fieldElement, Element methodElement) {
        String elementTypeName = fieldElement.asType().toString();

        if (elementTypeName.equals(String.class.getName())) {
            if (containAnyAnnotations(fieldElement, SUPPORTED_JPA_SUB_ANNOTATION_ID)) {
                return IdField.class;
            } else if (containAnyAnnotations(fieldElement, SUPPORTED_JPA_SUB_ANNOTATION_LOB)) {
                return ClobField.class;
            } else if (
                    containAnyAnnotations(fieldElement, LiteQLReferenceField.class.getName())
                            || (methodElement != null
                            && containAnyAnnotations(methodElement, LiteQLReferenceField.class.getName()))) {
                return ReferenceField.class;
            } else {
                return StringField.class;
            }
        } else if (
                elementTypeName.equalsIgnoreCase(Long.class.getSimpleName())
                        || elementTypeName.equalsIgnoreCase(Long.class.getName())) {
            return LongField.class;
        } else if (
                elementTypeName.equalsIgnoreCase(int.class.getSimpleName())
                        || elementTypeName.equalsIgnoreCase(Integer.class.getName())) {
            return IntegerField.class;
        } else if (elementTypeName.equalsIgnoreCase(Boolean.class.getSimpleName())) {
            return BooleanField.class;
        } else if (elementTypeName.equalsIgnoreCase(BigDecimal.class.getName())) {
            return DecimalField.class;
        } else if (elementTypeName.equalsIgnoreCase(Date.class.getName())) {
            return TimestampField.class;
        } else if (elementTypeName.equalsIgnoreCase(byte[].class.getSimpleName())) {
            return BlobField.class;
        }

        throw new IllegalStateException(elementTypeName + " not supported");
    }

    private void writeStaticType(
            TypeElement entityElement, Map<String, Class<? extends Field>> entityFieldDefinitions) {
        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(
                    entityElement.getQualifiedName() + STATIC_TYPE_NAME_SUFFIX);

            OutputStream os = javaFileObject.openOutputStream();
            PrintWriter pw = new PrintWriter(os);


            String packageName
                    = processingEnv.getElementUtils().getPackageOf(entityElement).getQualifiedName().toString();

            String staticTypeName = entityElement.getSimpleName() + STATIC_TYPE_NAME_SUFFIX;

            Set<String> imports = new HashSet<>();

            imports.add(LiteQLStaticType.class.getName());

            StringBuilder staticTypeBuilder = new StringBuilder();

            staticTypeBuilder
                    .append("@")
                    .append(LiteQLStaticType.class.getSimpleName())
                    .append("(")
                    .append(entityElement.getSimpleName())
                    .append(".class")
                    .append(")\n");

            staticTypeBuilder.append("public ");

            if (entityElement.getModifiers().contains(Modifier.ABSTRACT)) {
                staticTypeBuilder.append("abstract ");
            } else {
                staticTypeBuilder.append("final ");
            }

            staticTypeBuilder.append("class ").append(staticTypeName);

            if (!entityElement.getSuperclass().toString().equals(Object.class.getName())) {
                staticTypeBuilder
                        .append(" extends ")
                        .append(entityElement.getSuperclass().toString())
                        .append(STATIC_TYPE_NAME_SUFFIX);
            }

            staticTypeBuilder.append(" {\n\n");

            entityFieldDefinitions.forEach((fieldName, fieldType) -> {
                imports.add(fieldType.getName());

                staticTypeBuilder
                        .append("    public static volatile ")
                        .append(fieldType.getSimpleName())
                        .append(" ").append(fieldName).append(";\n");
            });

            staticTypeBuilder.append("\n}");

            if (StringUtils.isNotBlank(packageName)) {
                pw.println("package " + packageName + ";");
                pw.println();
            }

            for (String imported : imports) {
                pw.println("import " + imported + ";");
            }

            pw.println();

            pw.println(staticTypeBuilder.toString());

            pw.flush();
            pw.close();
        } catch (FilerException filerEx) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR, "Problem with Filer: " + filerEx.getMessage()
            );
        } catch (IOException ioEx) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Problem opening file to write MetaModel for "
                            + entityElement.getQualifiedName() + ioEx.getMessage()
            );
        }
    }

    private boolean containAnyAnnotations(Element element, String... annotationClassNames) {
        Set<String> annotationClassNameSet = Arrays.stream(annotationClassNames).collect(Collectors.toSet());
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationClassNameSet.contains(annotationMirror.getAnnotationType().toString())) {
                return true;
            }
        }

        return false;
    }

}
