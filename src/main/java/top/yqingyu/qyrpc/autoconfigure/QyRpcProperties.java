package top.yqingyu.qyrpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;
import java.util.Map;


@ConfigurationProperties(prefix = Constants.prefix)
public class QyRpcProperties implements Serializable {
    private Mode mode = Mode.BOTH;
    /**
     * 默认值为master
     * 当{@link top.yqingyu.rpc.annontation.QyRpcConsumer}
     * 中的value为空时采用此配置的RPC服务
     */
    private String main = "master";
    private int radix = 32;
    private Map<String, ConsumerConfig> consumer;
    @NestedConfigurationProperty
    private ProducerConfig producer;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getRadix() {
        return radix;
    }

    public void setRadix(int radix) {
        this.radix = radix;
    }

    public Map<String, ConsumerConfig> getConsumer() {
        return consumer;
    }

    public void setConsumer(Map<String, ConsumerConfig> consumer) {
        this.consumer = consumer;
    }

    public ProducerConfig getProducer() {
        return producer;
    }

    public void setProducer(ProducerConfig producer) {
        this.producer = producer;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }
}
