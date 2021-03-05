package cn.silince.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
* @description: 自定义校验注解
*/

@Documented
@Constraint(validatedBy = { ListValueConstraintValidator.class}) // 该注解使用的校验器(可以指定多累校验器，适配不同的校验数据)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE }) // 该注解可标注的位置
@Retention(RUNTIME) // 运行时可获取到该注解
public @interface ListValue {

    // 从配置文件中获取校验出错的错误信息
    String message() default "{cn.silince.common.valid.ListValue.message}";

    // 支持分组校验
    Class<?>[] groups() default { };

    // 可以自定义一些负载信息
    Class<? extends Payload>[] payload() default { };

    int[] values() default { };
}
