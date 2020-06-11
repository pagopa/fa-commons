package it.gov.pagopa.fa.common.model.validation;

import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class NotNullIfValidation implements ConstraintValidator<EnableNotNullIf, Object> {

    @Override
    public void initialize(EnableNotNullIf constraintAnnotation) {
    }

    @SneakyThrows
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext ctx) {

        final Class<?> clazz = bean.getClass();
        final Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(NotNullIfPropertyEqualTo.class)) {
                if (BeanUtils.getProperty(bean, field.getName()) == null) {
                    for (NotNullIfPropertyEqualTo annotation :
                            field.getAnnotationsByType(NotNullIfPropertyEqualTo.class)){
                        String dependencyField = annotation.property();
                        Object dependencyValue = BeanUtils.getProperty(bean, dependencyField);

                        if (dependencyValue != null && !"".equals(dependencyValue)) {
                            String annotEqualsToValue = annotation.value();

                            if (annotEqualsToValue != null && !"".equals(annotEqualsToValue)
                                    && dependencyValue.equals(annotEqualsToValue)) {
                                return false;
//                            throw new NotNullIfException(field.getName(), dependencyField, annotEqualsToValue);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
