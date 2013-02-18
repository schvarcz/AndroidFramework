package com.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author schvarcz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CDBField
{

    String fieldName();

    DBTypes types() default DBTypes.Null;

    boolean notnull() default false;

    public enum DBTypes
    {
        Null,
        Int,
        Text,
        Float
    }
}
