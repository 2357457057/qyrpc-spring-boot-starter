package top.yqingyu.qyrpc.autoconfigure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.StringUtils;
import top.yqingyu.common.utils.ClazzUtil;
import top.yqingyu.rpc.consumer.ConsumerHolderContext;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

public class ConsumerBeanConfigure implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

    private String scanPackage;
    private String consumerName;
    private String beanName;
    private ConsumerHolderContext consumerHolderContext;

    private Class<?> mapperFactoryBeanClass = ConsumerProxyBeanFactory.class;
    private ApplicationContext applicationContext;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getMapperFactoryBeanClass() {
        return mapperFactoryBeanClass;
    }

    public void setMapperFactoryBeanClass(Class<?> mapperFactoryBeanClass) {
        this.mapperFactoryBeanClass = mapperFactoryBeanClass;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.scanPackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // left intentionally blank
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        ConsumerScanner scanner = new ConsumerScanner(registry);
        scanner.setConsumerName(consumerName);
        scanner.setConsumerHolderContext(consumerHolderContext);
        scanner.registerFilters();
        scanner.scan(StringUtils.tokenizeToStringArray(this.scanPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    private Environment getEnvironment() {
        return this.applicationContext.getEnvironment();
    }


    static class ConsumerScanner extends ClassPathBeanDefinitionScanner {
        private static final Logger logger = LoggerFactory.getLogger(ConsumerScanner.class);
        BeanDefinitionRegistry registry;
        String consumerName;
        ConsumerHolderContext consumerHolderContext;
        private final Class<?> mapperFactoryBeanClass = ConsumerProxyBeanFactory.class;

        public ConsumerScanner(BeanDefinitionRegistry registry) {
            super(registry);
            this.registry = registry;
        }

        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

            if (beanDefinitions.isEmpty()) {
                logger.warn("No QyRpcConsumer was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
            } else {
                processBeanDefinitions(beanDefinitions);
            }

            return beanDefinitions;
        }

        private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
            AbstractBeanDefinition definition;
            BeanDefinitionRegistry registry = getRegistry();
            for (BeanDefinitionHolder holder : beanDefinitions) {
                definition = (AbstractBeanDefinition) holder.getBeanDefinition();
                boolean scopedProxy = false;
                if (ScopedProxyFactoryBean.class.getName().equals(definition.getBeanClassName())) {
                    definition = (AbstractBeanDefinition) Optional
                            .ofNullable(((RootBeanDefinition) definition).getDecoratedDefinition())
                            .map(BeanDefinitionHolder::getBeanDefinition).orElseThrow(() -> new IllegalStateException(
                                    "The target bean definition of scoped proxy bean not found. Root bean definition[" + holder + "]"));
                    scopedProxy = true;
                }
                String beanClassName = definition.getBeanClassName();
                logger.debug("Creating ConsumerProxyBeanFactory with name '" + holder.getBeanName() + "' and '" + beanClassName
                        + "' mapperInterface");
                definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
                try {
                    definition.getPropertyValues().add("consumerType", Class.forName(beanClassName, true, ClazzUtil.getDefaultClassLoader()));
                } catch (ClassNotFoundException ignore) {
                }
                definition.getPropertyValues().add("consumerHolderContext", consumerHolderContext);
                definition.getPropertyValues().add("name", consumerName);

                definition.setBeanClass(this.mapperFactoryBeanClass);
                logger.debug("Enabling autowire by type for ConsumerProxyFactoryBean with name '" + holder.getBeanName() + "'.");
                definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                definition.setLazyInit(true);
                if (scopedProxy) {
                    continue;
                }

                if (!definition.isSingleton()) {
                    BeanDefinitionHolder proxyHolder = ScopedProxyUtils.createScopedProxy(holder, registry, true);
                    if (registry.containsBeanDefinition(proxyHolder.getBeanName())) {
                        registry.removeBeanDefinition(proxyHolder.getBeanName());
                    }
                    registry.registerBeanDefinition(proxyHolder.getBeanName(), proxyHolder.getBeanDefinition());
                }

            }
        }

        public void registerFilters() {
            addIncludeFilter(new AnnotationTypeFilter(Annotation.class));
            addIncludeFilter(new AssignableTypeFilter(Class.class) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
            addExcludeFilter((metadataReader, metadataReaderFactory) -> {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            });
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
        }

        public String getConsumerName() {
            return consumerName;
        }

        public void setConsumerName(String consumerName) {
            this.consumerName = consumerName;
        }

        public ConsumerHolderContext getConsumerHolderContext() {
            return consumerHolderContext;
        }

        public void setConsumerHolderContext(ConsumerHolderContext consumerHolderContext) {
            this.consumerHolderContext = consumerHolderContext;
        }
    }
}
