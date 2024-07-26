package dev.hemil.userservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BcryptPasswordEncoderConfig {
    /* Here the BCryptPasswordEncoder is not developed by us. If we visit its file
    by clicking on it, we can see that it is not annotated with any tag that means spring
    will not be able to load it in application context level. If that is not possible, then
    we cannot inject it like a normal dependency injection. So we created this configs package
    to include those kinds of files here which are not some utility, but are important with
    project perspective.
    By annotating it with @Bean, spring will make this class as singleton its object will
    be made available throughout the project, which we call it as application context, and
    the best part of making it is a configuration file is that spring identifies the
    files where this is used as dependency injection and as we have also annotated it as @Bean
    it holds the object, and can inject it.
    */
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
