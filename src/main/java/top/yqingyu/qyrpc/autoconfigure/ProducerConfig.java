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

    /**
     * 服务名称
     */
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 线程名称
     */
    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /**
     * 分片消息容器清理时间
     */
    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = clearTime;
    }

    /**
     * 长过的消息将被拆分
     */
    public int getBodyLengthMax() {
        return bodyLengthMax;
    }

    public void setBodyLengthMax(int bodyLengthMax) {
        this.bodyLengthMax = bodyLengthMax;
    }

    /**
     * 传输进制，producer和consumer当采用相同的进制传输
     */
    public int getRadix() {
        return radix;
    }

    public void setRadix(int radix) {
        this.radix = radix;
    }

    /**
     * 服务端线程数
     */
    public int getPool() {
        return pool;
    }

    public void setPool(int pool) {
        this.pool = pool;
    }
}
