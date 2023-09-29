package top.yqingyu.qyrpc.autoconfigure;

import org.springframework.beans.factory.FactoryBean;
import top.yqingyu.rpc.consumer.ConsumerHolderContext;


public class ConsumerProxyBeanFactory<T> implements FactoryBean<T> {

    Class<T> consumerType;
    ConsumerHolderContext consumerHolderContext;
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

    public ConsumerHolderContext getConsumerHolderContext() {
        return consumerHolderContext;
    }

    public void setConsumerHolderContext(ConsumerHolderContext consumerHolderContext) {
        this.consumerHolderContext = consumerHolderContext;
    }
}
