package shareit.validator;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class BeanValidator<T> 
{
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    /**
     * Validates an given Object that contains annotations of jakarta validation
     * @param value request 
     * @return
     */
    public Set<ConstraintViolation<T>> validate(T value)
    {
        return validator.validate(value);
    }
}
