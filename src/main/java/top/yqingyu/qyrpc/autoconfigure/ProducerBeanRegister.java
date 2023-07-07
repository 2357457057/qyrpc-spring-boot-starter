package top.yqingyu.qyrpc.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import top.yqingyu.rpc.annontation.QyRpcProducer;
import top.yqingyu.rpc.producer.Producer;


import java.util.LinkedList;


public class ProducerBeanRegister implements InstantiationAwareBeanPostProcessor {
    public static final Logger logger = LoggerFactory.getLogger(ProducerBeanRegister.class);
    private Producer qyrpcProducer;
    private final LinkedList<Object> BEAN_QUEUE = new LinkedList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("qyrpcProducer".equals(beanName)) {
            qyrpcProducer = (Producer) bean;
        }
        Class<?> aClass = bean.getClass();
        QyRpcProducer annotation = aClass.getAnnotation(QyRpcProducer.class);
        if (annotation != null) {
            BEAN_QUEUE.add(bean);
        }
        if (qyrpcProducer != null) {
            Object poll;
            do {
                poll = BEAN_QUEUE.poll();
                if (poll != null) {
                    try {
                        qyrpcProducer.register(poll);
                    } catch (ClassNotFoundException e) {
                        logger.error("qyrpc Producer 对象 {} 注册失败 请检查", poll, e);
                    }
                }
            } while (poll != null);
        }

        return InstantiationAwareBeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}