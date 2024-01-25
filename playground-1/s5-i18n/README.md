## Java 格式化

Java 国际化: java.util.ResourceBundle, ListResourceBundle, PropertyResourceBundle(默认编码 ISO-8859-1)
格式化: java.text.MessageFormat, java.util.Formatter, org.slf4j.helpers.MessageFormatter(性能更好)

## Spring 国际化

核心接口: org.springframework.context.MessageSource

主要实现: 
* org.springframework.context.support.ResourceBundleMessageSource
* org.springframework.context.support.ReloadableResourceBundleMessageSource