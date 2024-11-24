package com.juansecu.restart.shared.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(NumericIdGeneratorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NumericIdGenerator {}
