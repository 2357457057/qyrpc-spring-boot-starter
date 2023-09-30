package top.yqingyu.qyrpc.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RpcScans.class)
@Import(ConsumerBeanRegister.class)
public @interface RpcScan {

    /**
     * 代表该路径下采用什么RPC,
     * 此名称需要与 配置文件中的名称对称
     * 例：
     * qyrpc:
     *   mode: consumer
     *   consumer:
     *     qyrpc1:
     *      url: qyrpc://127.0.0.1:4737
     * name 此时应为 qyrpc1
     */
    String name();

    /**
     * 包路径。
     */
    String[] path();
}
