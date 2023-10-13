package top.yqingyu.qyrpc.autoconfigure;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import top.yqingyu.rpc.consumer.ConsumerHolderContext;


public class ConsumerProxyBeanFactory<T> implements FactoryBean<T> {

    Class<T> consumerType;
    private ApplicationContext applicationContext;
    private volatile ConsumerHolderContext consumerHolderContext = null;
    String name;

    public ConsumerProxyBeanFactory(Class<T> consumerType) {
        this.consumerType = consumerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public T getObject() throws Exception {
        if (consumerHolderContext == null) {
            consumerHolderContext = applicationContext.getBean(ConsumerHolderContext.class);
        }
        return consumerHolderContext.getProxy(name, consumerType);
    }

    @Override
    public Class<?> getObjectType() {
        return consumerType;
    }

    public Class<T> getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(Class<T> consumerType) {
        this.consumerType = consumerType;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
