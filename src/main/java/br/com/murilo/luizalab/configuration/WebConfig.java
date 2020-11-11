package br.com.murilo.luizalab.configuration;

import br.com.murilo.luizalab.converters.NoticeRequestToNoticeConverter;
import br.com.murilo.luizalab.converters.NoticeToNoticeResponseConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(new NoticeRequestToNoticeConverter());
        registry.addConverter(new NoticeToNoticeResponseConverter());
    }
}
