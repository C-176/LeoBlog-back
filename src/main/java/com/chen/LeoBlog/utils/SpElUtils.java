package com.chen.LeoBlog.utils;

import com.chen.LeoBlog.base.UserDTOHolder;
import com.chen.LeoBlog.dto.UserDTO;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Description: spring el表达式解析
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-22
 */
public class SpElUtils {
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static Object parseSpEl(Method method, Object[] args, String spEl) {
        String[] params = Optional.ofNullable(parameterNameDiscoverer.getParameterNames(method)).orElse(new String[]{});//解析参数名
        EvaluationContext context = new StandardEvaluationContext();//el解析需要的上下文对象
        for (int i = 0; i < params.length; i++) {
            System.out.println("params[i]:" + params[i]);
            System.out.println("args[i]:" + args[i]);
            context.setVariable(params[i], args[i]);//所有参数都作为原材料扔进去
        }
        // 将threadLocal中的参数也放入上下文中
        UserDTO user = UserDTOHolder.get();
        context.setVariable("user", user);

        Expression expression = parser.parseExpression(spEl);
        return expression.getValue(context);
    }

    public static String getMethodKey(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
