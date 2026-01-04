package eg.alrawi.alrawi_award.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {


    private final LocaleChangeInterceptor localeChangeInterceptor;
    private final LocalValidatorFactoryBean validator;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Override
    public Validator getValidator() {
        return validator;
    }
}