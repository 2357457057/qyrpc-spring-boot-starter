package top.yqingyu.qyrpc.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import org.springframework.context.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;


import java.util.concurrent.atomic.AtomicInteger;


public class ConsumerBeanRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware, ApplicationContextAware {

    ApplicationContext context;
    ResourceLoader resourceLoader;
    Environment environment;
    static AtomicInteger integer = new AtomicInteger();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(metadata.getAnnotationAttributes(RpcScan.class.getName()));
        if (mapperScanAttrs != null) {
            registerBeanDefinitions(mapperScanAttrs, registry, getBeanName());
        }
    }

    public void registerBeanDefinitions(AnnotationAttributes mapperScanAttrs, BeanDefinitionRegistry registry, String name) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConsumerBeanConfigure.class);
        builder.addPropertyValue("scanPackage", mapperScanAttrs.get("path"));
        builder.addPropertyValue("consumerName", mapperScanAttrs.get("name"));
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }


    static class RepeatingRegistrar extends ConsumerBeanRegister {
        /**
         * {@inheritDoc}
         */
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            AnnotationAttributes mapperScansAttrs = AnnotationAttributes
                    .fromMap(importingClassMetadata.getAnnotationAttributes(RpcScans.class.getName()));
            if (mapperScansAttrs != null) {
                AnnotationAttributes[] annotations = mapperScansAttrs.getAnnotationArray("value");
                for (AnnotationAttributes annotation : annotations) {
                    registerBeanDefinitions(annotation, registry, getBeanName());
                }
            }
        }
    }

    String getBeanName() {
        return "QyRpcConsumerReg#" + integer.getAndIncrement();
    }
}