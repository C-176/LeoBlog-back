package com.chen.LeoBlog.utils;

import cn.hutool.core.util.ObjectUtil;
import com.chen.LeoBlog.enums.ErrorEnum;
import com.chen.LeoBlog.exception.BusinessErrorEnum;
import com.chen.LeoBlog.exception.BusinessException;
import com.chen.LeoBlog.exception.CommonErrorEnum;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.text.MessageFormat;
import java.util.*;


public class AssertUtil {

    /**
     * 校验到失败就结束
     */
    private static Validator failFastValidator =
            Validation.byProvider(HibernateValidator.class)
                    .configure()
                    .failFast(true)
                    .buildValidatorFactory().getValidator();

    /**
     * 全部校验
     */
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 注解验证参数(校验到失败就结束)
     *
     * @param obj
     */
    public static <T> void fastFailValidate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = failFastValidator.validate(obj);
        if (constraintViolations.size() > 0) {
            throwException(CommonErrorEnum.PARAM_VALID, constraintViolations.iterator().next().getMessage());
        }
    }

    /**
     * 注解验证参数(全部校验,抛出异常)
     *
     * @param obj
     */
    public static <T> void allCheckValidateThrow(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (constraintViolations.size() > 0) {
            StringBuilder errorMsg = new StringBuilder();
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                //拼接异常信息
                errorMsg.append(violation.getPropertyPath().toString()).append(":").append(violation.getMessage()).append(",");
            }
            //去掉最后一个逗号
            throwException(CommonErrorEnum.PARAM_VALID, errorMsg.toString().substring(0, errorMsg.length() - 1));
        }
    }


    /**
     * 注解验证参数(全部校验,返回异常信息集合)
     *
     * @param obj
     */
    public static <T> Map<String, String> allCheckValidate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (constraintViolations.size() > 0) {
            Map<String, String> errorMessages = new HashMap<>();
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errorMessages;
        }
        return Collections.emptyMap();
    }

    /**
     * 保证表达式为true，否则抛出异常
     *
     * @param expression 表达式
     * @param msg        异常信息
     */
    public static void isTrue(boolean expression, String msg) {
        if (!expression) throwException(msg);
    }

    public static void isTrue(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (!expression) throwException(errorEnum, args);
    }

    /**
     * 保证表达式为false，否则抛出异常
     *
     * @param expression 表达式
     * @param msg
     */
    public static void isFalse(boolean expression, String msg) {
        if (expression) throwException(msg);
    }

    public static void isFalse(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (expression) throwException(errorEnum, args);
    }

    /**
     * 保证对象不为空，否则抛出异常
     *
     * @param obj 校验对象
     * @param msg 异常信息
     */
    public static void isNotEmpty(Object obj, String msg) {
        if (isEmpty(obj)) throwException(msg);
    }

    public static void isNotEmpty(Object obj, ErrorEnum errorEnum, Object... args) {
        if (isEmpty(obj)) throwException(errorEnum, args);
    }

    /**
     * 保证对象为空，否则抛出异常
     *
     * @param obj 校验对象
     * @param msg 异常信息
     */
    public static void isEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) throwException(msg);
    }

    /**
     * 保证对象相等，否则抛出异常
     *
     * @param o1
     * @param o2
     * @param msg
     */
    public static void equal(Object o1, Object o2, String msg) {
        if (!ObjectUtil.equal(o1, o2)) throwException(msg);
    }

    /**
     * 保证对象不相等，否则抛出异常
     *
     * @param o1
     * @param o2
     * @param msg
     */
    public static void notEqual(Object o1, Object o2, String msg) {
        if (ObjectUtil.equal(o1, o2)) throwException(msg);
    }

    private static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    private static void throwException(String msg) {
        throwException(null, msg);
    }

    private static void throwException(ErrorEnum errorEnum, Object... arg) {
        if (Objects.isNull(errorEnum)) errorEnum = BusinessErrorEnum.BUSINESS_ERROR;
        throw new BusinessException(errorEnum.getErrorCode(), MessageFormat.format(errorEnum.getErrorMsg(), arg));
    }


}

