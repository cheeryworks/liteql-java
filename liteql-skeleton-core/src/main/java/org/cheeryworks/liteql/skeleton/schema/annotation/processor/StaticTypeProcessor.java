package org.cheeryworks.liteql.skeleton.schema.annotation.processor;

import org.cheeryworks.liteql.skeleton.schema.annotation.LiteQLReferenceField;
import org.cheeryworks.liteql.skeleton.schema.field.ClobField;
import org.cheeryworks.liteql.skeleton.schema.field.Field;
import org.cheeryworks.liteql.skeleton.schema.field.IdField;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({
        StaticTypeProcessor.SUPPORTED_ANNOTATION_LITE_QL_TYPE,
        StaticTypeProcessor.SUPPORTED_ANNOTATION_LITE_QL_MAPPED_TYPE
})
public class StaticTypeProcessor extends AbstractStaticTypeProcessor {

    public static final String SUPPORTED_ANNOTATION_LITE_QL_TYPE
            = "org.cheeryworks.liteql.schema.annotation.LiteQLDomainType";

    public static final String SUPPORTED_ANNOTATION_LITE_QL_MAPPED_TYPE
            = "org.cheeryworks.liteql.schema.annotation.LiteQLMappedType";

    public static final String SUPPORTED_ANNOTATION_LITE_QL_FIELD_ID
            = "org.cheeryworks.liteql.schema.annotation.LiteQLIdField";

    public static final String SUPPORTED_ANNOTATION_LITE_QL_FIELD_CLOB
            = "org.cheeryworks.liteql.schema.annotation.LiteQLClobField";

    @Override
    protected void checkAnnotationCompatibility(TypeElement typeElement, Set<TypeElement> typeInterfaceElements) {
        typeElement.getEnclosedElements()
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

    @Override
    protected Class<? extends Field> getFieldType(Element fieldElement, Element methodElement) {
        String fieldTypeName = fieldElement.asType().toString();

        if (fieldTypeName.equals(String.class.getName())) {
            if (containAnyAnnotations(fieldElement, SUPPORTED_ANNOTATION_LITE_QL_FIELD_ID)) {
                return IdField.class;
            } else if (containAnyAnnotations(fieldElement, SUPPORTED_ANNOTATION_LITE_QL_FIELD_CLOB)) {
                return ClobField.class;
            }
        }

        return super.getFieldType(fieldElement, methodElement);
    }

}
