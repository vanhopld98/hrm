package vn.com.humanresourcesmanagement.aop;

import vn.com.humanresourcesmanagement.common.enums.RoleEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Secured {

    RoleEnum roles();

}
