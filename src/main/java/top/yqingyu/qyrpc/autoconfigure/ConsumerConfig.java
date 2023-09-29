package top.yqingyu.qyrpc.autoconfigure;

public class ConsumerConfig {
    //
    String id = "";
    String threadName = "handle";
    String[] url = {"qyrpc://127.0.0.1:4729"};
    /**
     * 要远程调用的方法路径，
     * 假如你想使用自动注入的话。
     */
    String[] rpcScanPath;
    int poolMax = 2;
    int poolMin = 1;
    long clearTime = 1800000;
    int bodyLengthMax = 1400;
    int radix = 32;

    /**
     * 32位长的标识ID
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    /**
     * producer地址，同类地址的
     */
    public void setUrl(String[] url) {
        this.url = url;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String[] getUrl() {
        return url;
    }

    public int getPoolMax() {
        return poolMax;
    }

    public void setPoolMax(int poolMax) {
        this.poolMax = poolMax;
    }

    public int getPoolMin() {
        return poolMin;
    }

    public void setPoolMin(int poolMin) {
        this.poolMin = poolMin;
    }

    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = clearTime;
    }

    public int getBodyLengthMax() {
        return bodyLengthMax;
    }

    public void setBodyLengthMax(int bodyLengthMax) {
        this.bodyLengthMax = bodyLengthMax;
    }

    public int getRadix() {
        return radix;
    }

    public void setRadix(int radix) {
        this.radix = radix;
    }
}
