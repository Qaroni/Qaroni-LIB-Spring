package com.qaroni.libs.tests.wiremock;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({WiremockExtension.class})
@Inherited
@Documented
public @interface Wiremock {
    String[] stubs();
    String basePath() default "src/test/resources/";
    int port() default 8080;
    boolean verbose() default false;
}
