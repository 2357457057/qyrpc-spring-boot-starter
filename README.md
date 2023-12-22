# QyRpc

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
 class 1
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
class2
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
#### 参与贡献

1.  Fork 本仓库
2.  新建 feature_xxx 分支
3.  提交代码
4.  新建 Pull Request
