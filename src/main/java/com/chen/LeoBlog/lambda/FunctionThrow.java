package com.chen.LeoBlog.lambda;

@FunctionalInterface
public interface FunctionThrow<T> {


    T apply() throws Throwable;
}
