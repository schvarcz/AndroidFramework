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
public @interface DBRelation
{

    DBRelations relation() default DBRelations.BELONGS_TO;
    //Class <?> table();
    //Class <?> pivotTable() default null;

    public enum DBRelations
    {
        //MANY_MANY,
        HAS_MANY,
        BELONGS_TO
    }
}
