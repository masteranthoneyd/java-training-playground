package com.yangbingdong.acme.biz.api.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author yangbingdong1994@gmail.com
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PropertySourceMessageSourceIntegrationTest.class)
class PropertySourceMessageSourceIntegrationTest {

    @Bean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    public static MessageSource messageSource(ConfigurableEnvironment environment) {
        return new PropertySourceMessageSource(environment);
    }

    @Autowired
    MessageSource messageSource;

    @Test
    public void test() {
        String code = "my.name";
        Object[] args = new Object[0];
        assertEquals("张三", messageSource.getMessage(code,args, Locale.getDefault()));
        assertEquals("zhangsan", messageSource.getMessage(code,args, Locale.ENGLISH));
        assertEquals("zhang san", messageSource.getMessage(code,args, Locale.US));
        assertEquals("default message", messageSource.getMessage("not.exist.code", args, "default message", Locale.US));
    }

}
