package top.yqingyu.qyrpc.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import top.yqingyu.rpc.annontation.QyRpcProducer;
import top.yqingyu.rpc.producer.Producer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;

public class ProducerBeanRegister implements InstantiationAwareBeanPostProcessor {
    public static final Logger logger = LoggerFactory.getLogger(ProducerBeanRegister.class);
    private Producer qyrpcProducer;
    final LinkedList<Object> BEAN_QUEUE = new LinkedList<>();
    ApplicationContext ctx;
    Class<?> mybatisMapperFactoryBeanClass;

    public ProducerBeanRegister(ApplicationContext ctx) {
        this.ctx = ctx;
        try {
            mybatisMapperFactoryBeanClass = Class.forName(Constants.MapperFactoryBean);
        } catch (Throwable ignore) {
        }
    }

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
        if (mybatisMapperFactoryBeanClass != null && Constants.MapperFactoryBean.equals(bean.getClass().getName())) {
            try {
                //代理工厂创建的Mybatis的代理类
                Method getObject = bean.getClass().getMethod(Constants.MapperFactoryBeanInvokeMethod);
                Object invoke = getObject.invoke(bean);
                BEAN_QUEUE.add(invoke);
            } catch (Exception ignore) {
            }
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
        return bean;
    }
}