package io.jzheaux.springsecurity.resolutions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* Now with our custom UserDetailsService in place, we can access domain-specific information in our controller method parameters.

For this last task, first, open up the CurrentUsername annotation.

Then, add the expression @CurrentSecurityContext(expression="authentication.name"):
 */
import org.springframework.security.core.annotation.CurrentSecurityContext;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@CurrentSecurityContext(expression="authentication.name")
public @interface CurrentUsername {
}
