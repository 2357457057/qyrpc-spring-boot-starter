package top.yqingyu.qyrpc.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import top.yqingyu.common.exception.QyRuntimeException;
import top.yqingyu.common.utils.StringUtil;
import top.yqingyu.rpc.annontation.QyRpcConsumer;
import top.yqingyu.rpc.consumer.HolderCache;


import java.lang.reflect.Field;
import java.util.LinkedList;


public class ConsumerBeanProxyFactory implements InstantiationAwareBeanPostProcessor {
    public static final Logger logger = LoggerFactory.getLogger(ConsumerBeanProxyFactory.class);
    private final LinkedList<FieldHolder> BEAN_QUEUE = new LinkedList<>();
    HolderCache holderCache;
    QyRpcProperties properties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("qyrpcConsumerHolderCache".equals(beanName)) {
            holderCache = (HolderCache) bean;
        }
        if (bean instanceof QyRpcProperties) {
            properties = (QyRpcProperties) bean;
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
                String value = annotation.value();
                if (StringUtil.isEmpty(value))
                    value = properties.getMain();
                fieldHolder.name = value;
                BEAN_QUEUE.add(fieldHolder);
            }
        }
        if (holderCache != null) {
            FieldHolder fieldHolder;
            do {
                fieldHolder = BEAN_QUEUE.poll();
                if (fieldHolder != null) {
                    try {
                        Object proxy = holderCache.getProxy(fieldHolder.name, fieldHolder.proxyClass);
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