package top.yqingyu.qyrpc.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import top.yqingyu.common.exception.QyRuntimeException;
import top.yqingyu.common.utils.StringUtil;
import top.yqingyu.rpc.annontation.QyRpcConsumer;
import top.yqingyu.rpc.consumer.ConsumerHolderContext;


import java.lang.reflect.Field;
import java.util.LinkedList;


public class ConsumerBeanProxyFactory implements InstantiationAwareBeanPostProcessor {
    public static final Logger logger = LoggerFactory.getLogger(ConsumerBeanProxyFactory.class);
    private final LinkedList<FieldHolder> BEAN_QUEUE = new LinkedList<>();
    ConsumerHolderContext holderCache;
    QyRpcProperties properties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("qyrpcConsumerHolderContext".equals(beanName)) {
            holderCache = (ConsumerHolderContext) bean;
        }
        Class<?> aClass = bean.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            QyRpcConsumer annotation = field.getAnnotation(QyRpcConsumer.class);
            if (annotation != null) {
                FieldHolder fieldHolder = new FieldHolder();
                fieldHolder.bean = bean;
                fieldHolder.field = field;
                if (field.trySetAccessible()) {
                    field.setAccessible(true);
                }
                fieldHolder.proxyClass = field.getType();
                fieldHolder.name = annotation.value();
                BEAN_QUEUE.add(fieldHolder);
            }
        }
        if (holderCache != null) {
            FieldHolder fieldHolder;
            do {
                fieldHolder = BEAN_QUEUE.poll();
                if (fieldHolder != null) {
                    try {
                        String name = fieldHolder.name;
                        if (StringUtil.isEmpty(name))
                            name = properties.getMain();
                        Object proxy = holderCache.getProxy(name, fieldHolder.proxyClass);
                        fieldHolder.field.set(fieldHolder.bean, proxy);
                    } catch (Exception e) {
                        logger.error("自动注入异常 {}", fieldHolder, e);
                        throw new QyRuntimeException(e, "自动注入异常 {}", fieldHolder);
                    }
                }
            } while (fieldHolder != null);
        }

        return InstantiationAwareBeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private static class FieldHolder {
        Field field;
        Object bean;
        Class<?> proxyClass;
        String name;

        @Override
        public String toString() {
            return StringUtil.fillBrace("对象 {} 的 类型为{}的{}字段 注入失败 consumer名称 {}", bean, field.getName(), proxyClass.getName(), name);
        }
    }
}