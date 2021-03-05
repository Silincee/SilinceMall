package cn.silince.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: SilinceMall
 * @description: 自定义校验器
 * @author: Silince
 * @create: 2021-02-07 22:52
 **/
// 第一个泛型指定校验注解，第二个指定要校验什么类型的数据
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> set = new HashSet<>();

    /**
    * @description: 初始化方法
    */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        for (int value : values) {
            set.add(value);
        }
    }
    /**
    * @description:  判断是否校验成功
    * @param: [integer: 需要校验的值, constraintValidatorContext: 上下文环境信息]
    * @return: boolean
    * @author: Silince
    * @date: 2/7/21
    */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
