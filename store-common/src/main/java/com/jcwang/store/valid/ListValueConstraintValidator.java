package com.jcwang.store.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private Set<Integer> set = new HashSet<>();

    // 初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {

        int[] values = constraintAnnotation.values();

        for (int value : values) {
            set.add(value);
        }
    }

    /**
     * 判断校验是否成功
     * @param integer 需要提交的值
     * @param constraintValidatorContext 上下文环境信息
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {

        return set.contains(integer);
    }
}
