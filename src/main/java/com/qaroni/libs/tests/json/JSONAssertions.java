package com.qaroni.libs.tests.json;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ExtendWith({JSONAssertionParameterResolver.class})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JSONAssertions {
    String basePath() default "src/test/resources";
}
