package org.cheeryworks.liteql.skeleton.schema.annotation.processor;

import org.apache.commons.lang3.StringUtils;
import org.cheeryworks.liteql.skeleton.schema.annotation.field.LiteQLReferenceField;
import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLStaticType;
import org.cheeryworks.liteql.skeleton.schema.field.BlobField;
import org.cheeryworks.liteql.skeleton.schema.field.BooleanField;
import org.cheeryworks.liteql.skeleton.schema.field.DecimalField;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.IntegerField;
import org.cheeryworks.liteql.skeleton.schema.field.LongField;
import org.cheeryworks.liteql.skeleton.schema.field.ReferenceField;
import org.cheeryworks.liteql.skeleton.schema.field.StringField;
import org.cheeryworks.liteql.skeleton.schema.field.TimestampField;
import org.cheeryworks.liteql.skeleton.util.LiteQL;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
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

public abstract class AbstractStaticTypeProcessor extends AbstractProcessor {

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

    private void processTypeElement(TypeElement typeElement) {
        Set<TypeElement> typeInterfaceElements = typeElement.getInterfaces()
                .stream()
                .map(typeMirror -> (TypeElement) ((DeclaredType) typeMirror).asElement())
                .collect(Collectors.toSet());

        checkAnnotationCompatibility(typeElement, typeInterfaceElements);

        writeStaticType(typeElement, typeInterfaceElements);
    }

    protected void checkAnnotationCompatibility(TypeElement typeElement, Set<TypeElement> typeInterfaceElements) {

    }

    private void writeStaticType(TypeElement typeElement, Set<TypeElement> typeInterfaceElements) {
        try {
            Map<String, Class<? extends Field>> typeFieldDefinitions = new HashMap<>();

            Map<String, Element> liteQLReferenceFieldMethodElements
                    = getLiteQLReferenceFieldMethodElements(typeElement, typeInterfaceElements);

            typeElement.getEnclosedElements()
                    .stream()
                    .filter(element -> isFieldElement(element))
                    .forEach(fieldElement -> {
                        String fieldNameFromFieldElement = getFieldNameFromElement(fieldElement);

                        Element methodElement = liteQLReferenceFieldMethodElements.get(fieldNameFromFieldElement);

                        typeFieldDefinitions.put(
                                getFieldName(typeElement, fieldNameFromFieldElement, fieldElement, methodElement),
                                getFieldType(fieldElement, methodElement));
                    });

            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(
                    typeElement.getQualifiedName() + STATIC_TYPE_NAME_SUFFIX);

            OutputStream os = javaFileObject.openOutputStream();
            PrintWriter pw = new PrintWriter(os);


            String packageName
                    = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();

            String staticTypeName = typeElement.getSimpleName() + STATIC_TYPE_NAME_SUFFIX;

            Set<String> imports = new HashSet<>();

            imports.add(LiteQLStaticType.class.getName());

            StringBuilder staticTypeBuilder = new StringBuilder();

            staticTypeBuilder
                    .append("@")
                    .append(LiteQLStaticType.class.getSimpleName())
                    .append("(")
                    .append(typeElement.getSimpleName())
                    .append(".class")
                    .append(")\n");

            staticTypeBuilder.append("public ");

            if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                staticTypeBuilder.append("abstract ");
            } else {
                staticTypeBuilder.append("final ");
            }

            staticTypeBuilder.append("class ").append(staticTypeName);

            if (!typeElement.getSuperclass().toString().equals(Object.class.getName())) {
                staticTypeBuilder
                        .append(" extends ")
                        .append(typeElement.getSuperclass().toString())
                        .append(STATIC_TYPE_NAME_SUFFIX);
            }

            staticTypeBuilder.append(" {\n\n");

            typeFieldDefinitions.forEach((fieldName, fieldType) -> {
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
                            + typeElement.getQualifiedName() + ioEx.getMessage()
            );
        }
    }

    private Map<String, Element> getLiteQLReferenceFieldMethodElements(
            TypeElement typeElement, Set<TypeElement> typeInterfaceElements) {
        Map<String, Element> liteqlReferenceFieldMethodElements = new HashMap<>();

        typeInterfaceElements.forEach(typeInterfaceElement -> {
            typeInterfaceElement.getEnclosedElements()
                    .stream()
                    .filter(element -> isLiteQLReferenceFieldMethodElement(element))
                    .forEach(liteQLReferenceFieldMethodElement -> {
                        String fieldNameFromMethodElement
                                = getFieldNameFromElement(liteQLReferenceFieldMethodElement);

                        if (liteqlReferenceFieldMethodElements.containsKey(fieldNameFromMethodElement)) {
                            throw new IllegalStateException(
                                    "Duplicated LiteQLReferenceField declaration for property "
                                            + fieldNameFromMethodElement
                                            + " on type " + typeElement.getQualifiedName());
                        }

                        liteqlReferenceFieldMethodElements.put(
                                fieldNameFromMethodElement, liteQLReferenceFieldMethodElement);
                    });
        });

        return liteqlReferenceFieldMethodElements;
    }

    protected boolean isFieldElement(Element element) {
        return ElementKind.FIELD.equals(element.getKind())
                && !element.getModifiers().contains(Modifier.STATIC)
                && !element.getModifiers().contains(Modifier.TRANSIENT);
    }

    private String getFieldNameFromElement(Element element) {
        if (ElementKind.FIELD.equals(element.getKind())) {
            return element.getSimpleName().toString();
        } else if (ElementKind.METHOD.equals(element.getKind())) {
            String methodName = element.getSimpleName().toString();

            return LiteQL.ClassUtils.findFieldNameForMethod(methodName);
        }

        throw new IllegalStateException("Unsupported element " + element.toString());
    }

    private boolean isLiteQLReferenceFieldMethodElement(Element element) {
        return ElementKind.METHOD.equals(element.getKind()) && containAnyAnnotations(
                element,
                LiteQLReferenceField.class.getName());
    }

    private String getFieldName(
            TypeElement typeElement, String fieldNameFromFieldElement, Element fieldElement, Element methodElement) {
        LiteQLReferenceField liteQLReferenceFieldOnField
                = fieldElement.getAnnotation(LiteQLReferenceField.class);

        LiteQLReferenceField liteQLReferenceFieldOnMethod = null;

        if (methodElement != null) {
            liteQLReferenceFieldOnMethod = methodElement.getAnnotation(LiteQLReferenceField.class);
        }

        if (liteQLReferenceFieldOnField != null && liteQLReferenceFieldOnMethod != null) {
            throw new IllegalStateException(
                    "Duplicated LiteQLReferenceField definition for field " + fieldNameFromFieldElement
                            + " on type " + typeElement.getQualifiedName());
        } else if (liteQLReferenceFieldOnField != null) {
            return liteQLReferenceFieldOnField.name();
        } else if (liteQLReferenceFieldOnMethod != null) {
            return liteQLReferenceFieldOnMethod.name();
        }

        return fieldNameFromFieldElement;
    }

    protected Class<? extends Field> getFieldType(Element fieldElement, Element methodElement) {
        String fieldTypeName = fieldElement.asType().toString();

        if (fieldTypeName.equals(String.class.getName())) {
            if (containAnyAnnotations(fieldElement, LiteQLReferenceField.class.getName())
                    || (methodElement != null
                    && containAnyAnnotations(methodElement, LiteQLReferenceField.class.getName()))) {
                return ReferenceField.class;
            } else {
                return StringField.class;
            }
        } else if (
                fieldTypeName.equalsIgnoreCase(Long.class.getSimpleName())
                        || fieldTypeName.equalsIgnoreCase(Long.class.getName())) {
            return LongField.class;
        } else if (
                fieldTypeName.equalsIgnoreCase(int.class.getSimpleName())
                        || fieldTypeName.equalsIgnoreCase(Integer.class.getName())) {
            return IntegerField.class;
        } else if (fieldTypeName.equalsIgnoreCase(Boolean.class.getSimpleName())) {
            return BooleanField.class;
        } else if (fieldTypeName.equalsIgnoreCase(BigDecimal.class.getName())) {
            return DecimalField.class;
        } else if (fieldTypeName.equalsIgnoreCase(Date.class.getName())) {
            return TimestampField.class;
        } else if (fieldTypeName.equalsIgnoreCase(byte[].class.getSimpleName())) {
            return BlobField.class;
        }

        throw new IllegalStateException(fieldTypeName + " not supported");
    }

    protected static boolean containAnyAnnotations(Element element, String... annotationClassNames) {
        Set<String> annotationClassNameSet = Arrays.stream(annotationClassNames).collect(Collectors.toSet());
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationClassNameSet.contains(annotationMirror.getAnnotationType().toString())) {
                return true;
            }
        }

        return false;
    }

}
