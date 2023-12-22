# qyrpc-spring-boot-starter


#### 介绍
基于QyMsg 传输协议构建的轻量Rpc框架


#### 安装教程

1.  在mvn官网 搜索  qyrpc
2.  引入最新版本的 mvn坐标 到你的Spring-Boot项目中
```xml
<!-- https://mvnrepository.com/artifact/top.yqingyu/qyrpc-spring-boot-starter -->
<dependency>
    <groupId>top.yqingyu</groupId>
    <artifactId>qyrpc-spring-boot-starter</artifactId>
    <version>1.9.7</version>
</dependency>
```

#### 使用说明
##### 首先要创建你的服务提供方
 1、引入依赖包后，在你的application.yml中添加如下
 ```yml
 qyrpc:
  mode: producer  #仅服务提供
  main: qyrpc
  producer:
    port: 4737 #对外提供的端口
    body-length-max: 10240 #RPC消息最大传输长度
 ```
 2、在你需要提供调用的类上添加注解 @QyRpcProducer


-  class 1
 ```java
import top.yqingyu.qyws.modules.wx.po.WxUser;

import java.util.Optional;

/**
 * @author Yangy
 * @description 针对表【TF_F_WX_USER】的数据库操作Service
 * @createDate 2023-05-20 21:48:39
 */
public interface IWxUserService {

    Optional<WxUser> findById(String id);
    void saveOrUpdate(WxUser wxUser);
}
```
- class2
```java

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.yqingyu.qyws.modules.wx.repository.WxUserRepository;
import top.yqingyu.qyws.modules.wx.po.WxUser;
import top.yqingyu.qyws.modules.wx.service.IWxUserService;
import top.yqingyu.rpc.annontation.QyRpcProducer;

import java.util.Optional;

/**
 * @author Yangy
 * @description 针对表【TF_F_WX_USER】的数据库操作Service实现
 * @createDate 2023-05-20 21:48:39
 */
@Service
@AllArgsConstructor
@QyRpcProducer
public class WxUserServiceImpl implements IWxUserService {
    WxUserRepository wxUserRepository;


    @Override
    public Optional<WxUser> findById(String id) {
        return wxUserRepository.findById(id);
    }

    @Override
    public void saveOrUpdate(WxUser wxUser) {
        wxUserRepository.saveAndFlush(wxUser);
    }

}
```
3、启动你的服务端

##### 其次创建你的服务消费端
1、在你的消费端application.yml中添加如下
```yml
qyrpc:
  mode: consumer
  #默认服务
  main: qyws-service
  consumer:
    qyws-service:
      id: a71224de6d5c4cd0a07b92fcfc4cbe44
      # 多个ip会进行同类型集群校验
      url: [ "qyrpc://127.0.0.1:4737" ]
      pool-max: 2
      pool-min: 2
```
2、在你的启动类上添加注解 @RpcScans或@RpcScan
```java
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.yqingyu.qyrpc.autoconfigure.RpcScan;
import top.yqingyu.qyrpc.autoconfigure.RpcScans;

@RpcScans({
        @RpcScan(name = "qyws-service", path = "top.yqingyu.qyws.*.*.service") /*
        配置配置文件中name为 qyws-service 的服务映射到路径表达式中top.yqingyu.qyws.*.*.service所有带有@QyRpcProducer 的类到你项目的IOC容器中。 
        @QyRpcProducerProperties(waitTime = 10000,retryTimes = 3)*/ 
})
@SpringBootApplication
public class QywsController_WX {
    public static String SERVER_NAME;
    public static void main(String[] args) {
        SpringApplication.run(QywsController_WX.class, args);
    }
}
```
3、在你所需服务的类上添加注解
```java
package top.yqingyu.qyws.modules.wx.service;

import top.yqingyu.qyws.modules.wx.po.WxUser;
import top.yqingyu.rpc.annontation.QyRpcProducer;
import top.yqingyu.rpc.annontation.QyRpcProducerProperties;

import java.util.Optional;

@QyRpcProducer
@QyRpcProducerProperties(waitTime = 10000, retryTimes = 3)
public interface IWxUserService {

    Optional<WxUser> findById(String id);

    void saveOrUpdate(WxUser wxUser);

}
```
4.在你需要调用这个接口的Spring compoment 中使用Sring的方式引用你IOC容器中的对象
```java
@Compoment
public class  AAAAAA {

@Autowired
IWxUserService iWxUserService

@Scheduled
public void aaaa(){
    System.out.println(iWxUserService.findById("111"));
} 

}
```



#### 参与贡献

1.  Fork 本仓库
2.  新建 feature_xxx 分支
3.  提交代码
4.  新建 Pull Request
