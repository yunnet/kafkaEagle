package org.smartloli.kafka.eagle.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.eagle.common.util.SystemConfigUtils;
import org.smartloli.kafka.eagle.common.util.TimeBasedCache;
import org.smartloli.kafka.eagle.core.ipc.KafkaOffsetGetter;
import org.smartloli.kafka.eagle.web.controller.aop.AccountInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author yunnet
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebMvcConfig.class);

    @Bean
    public KafkaOffsetRunTask kafkaOffsetRunTask() {
        LOG.info("going to create KafkaOffsetRunTask");
        KafkaOffsetGetter getter = new KafkaOffsetGetter();
        KafkaOffsetRunTask runTask = new KafkaOffsetRunTask();
        runTask.start();
        return runTask;
    }

    @Bean
    public TimeBasedCache timeBasedCache() {
        LOG.info("invoke TimeBasedCache::");
        TimeBasedCache timeBasedCache = new TimeBasedCache<String, String>(2048, 10);
        return timeBasedCache;
    }

    public class KafkaOffsetRunTask extends Thread {

        public void run() {
            String formatter = SystemConfigUtils.getProperty("kafka.eagle.offset.storage");
            if ("kafka".equals(formatter)) {
                try {
                    LOG.info("going to exec KafkaOffsetGetter.getInstance()");
                    KafkaOffsetGetter.getInstance();
                } catch (Exception ex) {
                    LOG.error("Initialize KafkaOffsetGetter thread has error,msg is :", ex);
                }
            }
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccountInterceptor());
        super.addInterceptors(registry);
    }
}