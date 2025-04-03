package com.murali.placify.config;

import com.murali.placify.util.AutowiringSpringBeanJobFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public JobFactory jobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler(JobFactory jobFactory) throws SchedulerException {
        Scheduler scheduler = org.quartz.impl.StdSchedulerFactory.getDefaultScheduler();
        scheduler.setJobFactory(jobFactory);
        scheduler.start();
        return scheduler;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
