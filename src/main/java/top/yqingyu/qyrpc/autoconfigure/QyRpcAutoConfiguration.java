package top.yqingyu.qyrpc.autoconfigure;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.yqingyu.common.utils.UUIDUtil;
import top.yqingyu.qymsg.Dict;
import top.yqingyu.qymsg.netty.ConnectionConfig;
import top.yqingyu.rpc.consumer.Consumer;
import top.yqingyu.rpc.consumer.ConsumerHolderContext;
import top.yqingyu.rpc.consumer.MethodExecuteInterceptor;
import top.yqingyu.rpc.producer.Producer;
import top.yqingyu.rpc.producer.ServerExceptionHandler;


import java.util.Map;
import java.util.Set;

@Import({ConsumerBeanProxyFactory.class, ProducerBeanRegister.class})
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(QyRpcProperties.class)
public class QyRpcAutoConfiguration implements InitializingBean {
    public static final Logger logger = LoggerFactory.getLogger(QyRpcAutoConfiguration.class);
    @Resource
    private QyRpcProperties properties;
    @Resource
    ApplicationContext ctx;

    @Bean
    @ConditionalOnMissingBean
    public ServerExceptionHandler qyrpcExceptionHandler() {
        logger.info("qyrpc producer use inner ExceptionHandler");
        return new ServerExceptionHandler() {
        };
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ServerExceptionHandler.class)
    @ConditionalOnProperty(prefix = Constants.prefix, name = Constants.mode)
    public Producer qyrpcProducer(ApplicationContext ctx) throws Exception {
        ProducerConfig config = properties.getProducer();
        Producer build = Producer.Builder.newBuilder()
                .port(config.port)
                .bodyLengthMax(config.bodyLengthMax)
                .radix(config.radix)
                .threadName(config.threadName)
                .exceptionHandler(ctx.getBean(ServerExceptionHandler.class))
                .pool(config.pool)
                .serverName(config.serverName)
                .build();

        try {
            switch (properties.getMode()) {
                case PRODUCER, BOTH -> {
                    logger.info("Initialize autoConfigure qyrpc Producer");
                    build.start();
                    logger.info("Initialized qyrpc Producer");
                }
            }
        } catch (Throwable t) {
            build.shutdown();
            throw t;
        }

        return build;
    }

    @Bean
    @ConditionalOnBean(Producer.class)
    @ConditionalOnMissingBean
    public MethodExecuteInterceptor qyrpcMethodExecuteInterceptor() {
        logger.info("qyrpc consumer use inner MethodExecuteInterceptor");
        return new MethodExecuteInterceptor() {
        };
    }
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(MethodExecuteInterceptor.class)
    @ConditionalOnProperty(prefix = Constants.prefix, name = Constants.mode)
    public ConsumerHolderContext qyrpcConsumerHolderContext() throws Exception {
        ConsumerHolderContext holderCache = new ConsumerHolderContext();
        try {
            String uuid = UUIDUtil.randomUUID().toString2();
            switch (properties.getMode()) {
                case CONSUMER, BOTH -> {
                    logger.info("Initialize autoConfigure qyrpc Consumer");
                    Map<String, ConsumerConfig> consumerConfigMap = properties.getConsumer();
                    Set<String> consumerConfigKey = consumerConfigMap.keySet();
                    for (String serverName : consumerConfigKey) {
                        ConsumerConfig consumerConfig = consumerConfigMap.get(serverName);
                        for (String url : consumerConfig.url) {
                            String host;
                            int port;
                            if (!url.startsWith("qyrpc://")) {
                                throw new IllegalArgumentException("[{}]配置文件中存在错误的url:{}，请检查 正确url: e.g. qyrpc://host:port", serverName, url);
                            }
                            url = url.replaceFirst("qyrpc://", "").trim();
                            String[] split = url.split(":");
                            if (split.length != 2) {
                                throw new IllegalArgumentException("[{}]配置文件中存在错误的url:{}，请检查 正确url: e.g. qyrpc://host:port", serverName, url);
                            }
                            try {
                                host = split[0].trim();
                                port = Integer.parseInt(split[1].trim());
                            } catch (Exception e) {
                                throw new IllegalArgumentException("[{}]配置文件中存在错误的url:{}，请检查 正确url: e.g. qyrpc://host:port", serverName, url);
                            }
                            ConnectionConfig.Builder builder = new ConnectionConfig.Builder();
                            builder.name(serverName)
                                    .threadName(consumerConfig.threadName)
                                    .poolMax(consumerConfig.poolMax)
                                    .poolMin(consumerConfig.poolMin)
                                    .radix(consumerConfig.radix)
                                    .clearTime(consumerConfig.clearTime)
                                    .host(host)
                                    .port(port);
                            Consumer consumer = Consumer.create(builder.build(), holderCache);
                            if (consumerConfig.id.length() != Dict.CLIENT_ID_LENGTH) {
                                logger.warn("[{}]未配置客户端ID或配值的id不等于32位，将采用32位长的无符号UUID {}", serverName, uuid);
                                continue;
                            }
                            consumer.setId(uuid);
                        }
                        logger.info("Initialized qyrpc Consumer [{}]", serverName);
                    }
                    logger.info("Initialized all qyrpc Consumer");
                }
            }
            return holderCache;
        } catch (Throwable t) {
            holderCache.shutdown();
            throw t;
        }
    }

    @Override
    public void afterPropertiesSet() {
        ConsumerBeanProxyFactory bean = ctx.getBean(ConsumerBeanProxyFactory.class);
        bean.properties = properties;
    }
}
