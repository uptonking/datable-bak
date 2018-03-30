package player.data.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义Servlet的初始化参数注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebInitParamx {

    //参数名
    String name() default "";

    //参数的值
    String value() default "";

    String description() default "";

}
