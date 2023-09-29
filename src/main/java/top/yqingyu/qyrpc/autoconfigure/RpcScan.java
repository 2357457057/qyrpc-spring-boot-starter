package top.yqingyu.qyrpc.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RpcScans.class)
@Import(ConsumerBeanRegister.class)
public @interface RpcScan {

    String name();

    String[] path();
}
