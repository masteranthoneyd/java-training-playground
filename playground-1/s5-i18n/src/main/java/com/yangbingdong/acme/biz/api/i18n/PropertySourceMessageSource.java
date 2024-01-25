package com.yangbingdong.acme.biz.api.i18n;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Properties;

/**
 * 基于 PropertySources 实现 {@link MessageSource}
 * <p>
 * <ol>
 *     <li>通过 ResourcePatternResolver 获取 Locale 与 Resource 映射关系</li>
 *     <li>解析 YAML Resource 变成 Spring PropertySource</li>
 *     <li>通过 Slf4j {@link MessageFormatter} 实现格式化</li>
 *     <li>通过 Locale 获取 PropertySource</li>
 * </ol>
 *
 * @author yangbingdong1994@gmail.com
 */
public class PropertySourceMessageSource implements MessageSource, SmartInitializingSingleton {

    public static final String PROPERTY_SOURCE_NAME_PREFIX = "Messages_";
    public static final String CODE_PREFIX = "messages.";

    private final MutablePropertySources propertySources;

    public PropertySourceMessageSource(ConfigurableEnvironment environment) {
        this.propertySources = environment.getPropertySources();
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        String messagePattern = getMessagePattern(code, locale);
        String message = format(messagePattern, args);
        return message == null ? defaultMessage : message;
    }

    private String getMessagePattern(String code, Locale locale) {
        String propertySourceName = buildPropertySourceName(locale);
        PropertySource<?> propertySource = propertySources.get(propertySourceName);

        String propertyName = CODE_PREFIX + code;
        return propertySource == null ? null : (String) propertySource.getProperty(propertyName);
    }

    private static String buildPropertySourceName(Locale locale) {
        return (PROPERTY_SOURCE_NAME_PREFIX + locale).toLowerCase();
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return getMessage(code, args, null, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        String message = null;
        String defaultMessage = resolvable.getDefaultMessage();
        for (String code : resolvable.getCodes()) {
            message = getMessage(code, resolvable.getArguments(), defaultMessage, locale);
            if (message != null) {
                break;
            }
        }
        return message;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources("classpath*:/META-INF/Messages*.yaml");
            String prefix = "/META-INF/Messages";
            String suffix = ".yaml";
            Locale locale = null;

            // 通过资源它们对应的 Locale
            for (Resource resource : resources) {
                URI uri = resource.getURI();
                String path = uri.getPath();
                String localeString = path.substring(path.indexOf(prefix) + prefix.length());
                if (localeString.startsWith(".")) { // Default Locale
                    locale = Locale.getDefault();
                } else if (localeString.startsWith("_")) {
                    localeString = localeString.substring(1, localeString.length() - suffix.length());
                    locale = new Locale(localeString);
                }
                // 构建 Locale 与 Resource 对应 YAML PropertySource
                PropertySource<?> propertySource = buildPropertySource(locale, resource);
                // 添加 Locale PropertySource 到 PropertySources 中
                propertySources.addLast(propertySource);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private PropertySource<?> buildPropertySource(Locale locale, Resource resource) {
        String propertySourceName = buildPropertySourceName(locale);
        return newPropertySource(propertySourceName, resource);
    }

    private PropertySource<?> newPropertySource(String propertySourceName, Resource resource) {

        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        // 关联 YAML Resource
        yamlPropertiesFactoryBean.setResources(resource);
        // 初始化
        yamlPropertiesFactoryBean.afterPropertiesSet();
        // 将 YAML 资源转化成 Properties
        Properties properties = yamlPropertiesFactoryBean.getObject();
        return new PropertiesPropertySource(propertySourceName, properties);
    }

    public static String format(String messagePattern, Object... args) {
        if (messagePattern == null) {
            return null;
        }
        return MessageFormatter.arrayFormat(messagePattern, args).getMessage();
    }
}
