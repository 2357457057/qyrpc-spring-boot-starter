package top.yqingyu.qyrpc.autoconfigure;

public enum Mode {
    /**
     * 仅提供服务
     * Service only
     */
    PRODUCER,
    /**
     * 仅为调用方
     * For the caller only
     */
    CONSUMER,
    /**
     * 默认 二者兼具
     * Default to both
     */
    BOTH
}
