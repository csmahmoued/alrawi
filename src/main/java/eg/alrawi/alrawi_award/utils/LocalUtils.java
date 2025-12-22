package eg.alrawi.alrawi_award.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LocalUtils {

    private final MessageSource messageSource;

    public  String getMessage(String messageCode) {
        return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
    }
}
