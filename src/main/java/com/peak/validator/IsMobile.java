package com.peak.validator;

import javax.validation.*;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Check the mobile phone number. The default is true
 */
@Documented
@Constraint(validatedBy = { IsMobileValidator.class})
@Target({METHOD,FIELD,ANNOTATION_TYPE,CONSTRUCTOR,PARAMETER,TYPE_USE})
@Retention(RUNTIME)
public @interface IsMobile {

    boolean required() default true;

    String message() default  "手机号格式错误";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
