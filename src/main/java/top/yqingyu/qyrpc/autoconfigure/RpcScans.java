package top.yqingyu.qyrpc.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ConsumerBeanRegister.RepeatingRegistrar.class})
public @interface RpcScans {
    RpcScan[] value();
}
