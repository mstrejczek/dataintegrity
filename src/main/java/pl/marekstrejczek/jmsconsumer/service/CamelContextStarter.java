package pl.marekstrejczek.jmsconsumer.service;

import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by maro on 2014-06-04.
 */
public class CamelContextStarter implements ApplicationContextAware {
    private ApplicationContext ac;

    public void start() throws Exception {
        SpringCamelContext springCamelContext = (SpringCamelContext)ac.getBean("camel1");
        springCamelContext.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
