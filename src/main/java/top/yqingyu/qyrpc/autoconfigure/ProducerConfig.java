package top.yqingyu.qyrpc.autoconfigure;

public class ProducerConfig {
    int port = 4729;
    String serverName = "QyRpcProducer";
    String threadName = "handle";
    long clearTime = 1800000;
    int bodyLengthMax = 1400;
    int radix = 32;
    int pool = Runtime.getRuntime().availableProcessors() * 2;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
