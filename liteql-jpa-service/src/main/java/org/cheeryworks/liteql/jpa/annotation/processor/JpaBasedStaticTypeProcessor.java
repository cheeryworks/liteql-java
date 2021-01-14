package org.cheeryworks.liteql.jpa.annotation.processor;

import org.cheeryworks.liteql.schema.annotation.processor.AbstractStaticTypeProcessor;
import org.cheeryworks.liteql.schema.field.ClobField;
import org.cheeryworks.liteql.schema.field.Field;
import org.cheeryworks.liteql.schema.field.IdField;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({
        JpaBasedStaticTypeProcessor.SUPPORTED_ANNOTATION_JPA_ENTITY,
        JpaBasedStaticTypeProcessor.SUPPORTED_ANNOTATION_JPA_MAPPED_SUPERCLASS
})
public class JpaBasedStaticTypeProcessor extends AbstractStaticTypeProcessor {

    public static final String SUPPORTED_ANNOTATION_JPA_ENTITY = "javax.persistence.Entity";

    public static final String SUPPORTED_ANNOTATION_JPA_MAPPED_SUPERCLASS = "javax.persistence.MappedSuperclass";

    private static final String SUPPORTED_ANNOTATION_JPA_FIELD_ID = "javax.persistence.Id";

    private static final String SUPPORTED_ANNOTATION_JPA_FIELD_LOB = "javax.persistence.Lob";

    private static final String SUPPORTED_ANNOTATION_JPA_FIELD_TRANSIENT = "javax.persistence.Transient";

    private static final String JPA_ANNOTATION_BASE_PACKAGE = "javax.persistence";

    @Override
    protected void checkAnnotationCompatibility(TypeElement entityElement, Set<TypeElement> typeInterfaceElements) {
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

        typeInterfaceElements.forEach(entityInterfaceElement -> {
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

    @Override
    protected Class<? extends Field> getFieldType(Element fieldElement, Element methodElement) {
        if (fieldElement.asType().toString().equals(String.class.getName())) {
            if (containAnyAnnotations(fieldElement, SUPPORTED_ANNOTATION_JPA_FIELD_ID)) {
                return IdField.class;
            } else if (containAnyAnnotations(fieldElement, SUPPORTED_ANNOTATION_JPA_FIELD_LOB)) {
                return ClobField.class;
            }
        }

        return super.getFieldType(fieldElement, methodElement);
    }

    @Override
    protected boolean isFieldElement(Element element) {
        return super.isFieldElement(element)
                && !containAnyAnnotations(element, SUPPORTED_ANNOTATION_JPA_FIELD_TRANSIENT);
    }

}
