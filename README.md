# Touch 方法&属性 映射工具(0.5 版本)

标签 ： 开源

---
> 线上后门与接口调试: 
原先需要测试一个接口(如Dubbo、DAO), 或为线上留后门, 需要写大量的Web层(Api、Servlet或Controller)代码并在浏览器触发, 进而调用到实际想要执行的方法或想查看的属性, 而有了Touch后, 你可以专注于业务功能的实现, 而不需要再书写这些跟业务无关的触发代码.


## 版本历史

---
### 0.0 版本
- Touch的第一个发布版本
    - 接口映射最核心功能
    - 基于Web.xml的配置: 类扫描Touch
    - `@Touch`的`touchPattern`属性提供Touch别名
    - 第一版`com.vdian.touch.converter.Converter`
    - `@TouchArg`注解获取参数泛型类型
    参考: [0.0.8版本文档](https://github.com/feiqing/Touch/wiki/Touch-0.0.8-%E7%89%88%E6%9C%AC%E6%96%87%E6%A1%A3)

---
### 0.1版本
- 增加Switcher接口, ~~并提供VitaminSwitcher默认实现(Vdian内部版本),~~ 增加线上环境安全校验
- Converter 添加@TouchArg参数
- 增加Converter与Switcher的默认绑定
- fix 空QueryString bug
- fix 重载函数定位不准 bug

---
### 0.2版本
- 增加包扫描功能, 精简web.xml内关于Touch的配置
- 增加ZookeeperSwitcher实现, 提供基于ZK集群的Touch安全控制
- 增加`lists.do` 接口提供获取touch context内容
- fix fastjson循环引用 bug

---
### 0.3版本
- 0.3.0
删除`@TouchArg`注解, 动态获取泛型参数类型
- 0.3.1 版本: 提高响应速度的同时降低内存占用
    - init时缓存方法参数名、参数类型、参数泛型类型, 加速参数映射速度
    - 使用WeakHashMap替换HashMap存储TouchContext, 占用的内存随GC而释放, 再次调用时重新init.
- 3.2 版本
    更新`Converter<T>`接口, 添加`Type[] actualTypes`参数代表泛型真实类型, 非泛型参数为`null`;

---
### 0.4版本
- 0.4.0
    - 将`@Touch`从annotation包内移出, 直接放在`com.vdian.touch`包下
    - 弃用web.xml配置, 使用Servlet 3.0 api实现`OnTouchServlet`自动注册(默认拦截`/touch/*`目录URL).
    - 将配置集中放入touch.xml(classpath下)中, 并新增touch.xsd约束(已经放如touch.jar包内), 书写touch.xml可实现代码提示.
    - 更新`TouchSwitcher`接口, 添加`init(Map<String, String> config)`方法, 创建`TouchSwitcher`实例时调用(且只调用一次), 将在touch.xml `<switch> <config .../> <switch>`标签内容传入`init()`.
    - 更新`ZookeeperSwitcher`实现, 支持自定义指定ZK集群.
    - fix Bean被AOP代理后找不到的Bug(但目前还不能支持Bean被JDK的同时对Bean Alias的情况).
    - fix jaxp-dom读取注释的bug
- 0.4.1
    - 使用GuavaCache替换WeakHashMap, 使key在不活跃1小时后失效, 解决频繁访问&频繁GC的问题.
    - fix json字符串无法反序列化为String的问题
    - fix 应用占用lists.do(打印所有touchPattern)的问题

---
### 0.5 版本
- 0.5.0
    - 去掉`loadOnStartup`, Touch不再随应用启动而初始化, 改为第一次访问时初始化;
    - 将`@Touch`移植到`filed`上, 支持成员变量映射;
    - fix 同名`touchPattern` bug;
- 0.5.1
    - 获取SpringBean时添加BeanInstance缓存, 加速获取Bean速度;
    - fix 单包内找不到`@Touch`注解而抛异常的错误, 改为所有的包如果都找不到才抛出`TouchException`通知.

> 接下来到2017年中旬不会再有大版本的更新, 而致力于使Touch更加稳定, 提供更高性能, 适用更多项目类型.

---
## 入门

### 1. pom
```
    <dependency>
        <groupId>com.vdian.touch</groupId>
        <artifactId>touch</artifactId>
        <version>0.5.1-SNAPSHOT</version>
    </dependency>
```

### 2. touch.xml(maven项目需要放在resource目录下)
```
<?xml version="1.0" encoding="utf-8"?>
<touch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.vdian.net/touch"
       xsi:schemaLocation="http://www.vdian.net/touch
       http://www.vdian.net/touch.xsd">

    <packages>
        <!-- package最少有一个(可以多个), 否则touch.xsd验证不通过, 且启动时会抛异常 -->
        <package>com.vdian.demo</package>
    </packages>

    <converters>
        <!-- 默认已经注册了下面三个Converter, 如果没有自定义的Converter可以去掉这整个 converters 的配置 -->
        <converter>com.vdian.touch.converter.CalendarConverter</converter>
        <converter>com.vdian.touch.converter.DateConverter</converter>
        <converter>com.vdian.touch.converter.SetConverter</converter>
    </converters>

    <switchers>
        <!-- Vdian内部版本默认已经注册了Vitamin的Switcher(开源版本没有), 如果没有自定义的Switcher(像下面的ZookeeperSwitcher), 则可以去掉这整个 switchers 的配置  
        <switcher class="com.vdian.touch.switcher.VitaminSwitcher"/>
-->
        <!-- 如果需要使用ZookeeperSwitcher, 需要在项目pom中添加curator-recipes的依赖, 该依赖默认在Touch中是optional的 -->
        <switcher class="com.vdian.touch.switcher.ZookeeperSwitcher">
            <config key="zookeeper" value="10.1.101.60:2181"/>
            <config key="touchPath" value="/touch/touch-switcher"/>
            <config key="touchKey" value="touch_open"/>
        </switcher>
    </switchers>

</touch>
```
> 注意: touch.xml只需放在项目classpath下即可,  Touch会自动去到classpath目录下扫描该文件, 不必被Spring托管.

### 3. @Touch
为想要touch的方法打上`@Touch`注解, 马上她就属于你啦:
![](https://si.geilicdn.com/daily_hz_img_0076000001586b7e1fc20a016558_2096_424_unadjust.png)

### 4. 浏览器输入
```
http://localhost:8080/touch/
	constumObject?                  // 方法名
	{
	  "user": {                     // 方法参数名
	    "name": "feiqing",          // 方法参数值
	    "age": 18
	  },
	  "date": "1992-03-20 01:01:01",
	  "users": [{
	    "name": "feiqing",
	    "age": 18
	  },
	  {
	    "name": "feiqing2",
	    "age": 98
	  }]
	}
```
这样, 你就可以愉快的touch一把:
![](https://si.geilicdn.com/daily_hz_img_0075000001586b52f5980a016558_1406_59_unadjust.png)

> 注:
- 从0.4版本开始弃用web.xml配置, 启用Servlet 3.0 api, OnTouchServlet自动注册到Servlet容器内.
- 从0.2版本开始Touch不再支持on_touch_class配置, 全面换成包扫描packages .
- 使用Touch的Servlet自动注册需要在项目中启用Servlet 3.0(web.xml头更新成如下即可, 否则还是需要像以前一样手动注册, 见注释), 且保证项目内Servlet-api已达到3.0及以上版本(Touch的pom已经引入, 但要防止被其他配置冲掉).


```
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.0"
         xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
    http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         metadata-complete="false">

    <!-- Servlet 2.0 配置
    <servlet>
        <servlet-name>OnTouchServlet</servlet-name>
        <servlet-class>com.vdian.touch.server.OnTouchServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>OnTouchServlet</servlet-name>
        <url-pattern>/touch/*</url-pattern>
    </servlet-mapping>
    -->
</web-app>
```



---
## 进阶
### 1. 同名方法
`@Touch`注解内提供了`touchPattern`参数用于区分重载方法, 但要注意`touchPattern`要和浏览器内的URI(如前面的`constumObject`)对应起来.

---
### 2. Converter与参数映射
- 参数映射
我们默认使用***json***与方法参数一一映射, 外层json的key为方法参数名. 

- 自定义Converter
`Converter`大部分情况下是不用配的, Touch默认为我们绑定了很多类型:
    - String -> 八种Java基础及包装类型
    - String -> String
    - String -> Date("yyyy-MM-DD HH:MM:SS")
    - String -> List
    - String -> Set
    - String -> Map<String, Object>
    - String -> JavaBean
    - String -> Calender
除非在一些特殊情况下, 如**String -> Calender**, 需要我们手动开发一个`Converter`并配置到touch.xml中(详见: com.vdian.touch.converter.CalendarConverter).

---

### 3. Switcher与安全性
Touch开放了`TouchSwitcher`接口来提高touch接口的安全性:
```
public interface TouchSwitcher {
    /**
     * init when new TouchSwitcher instance.
     *
     * @param config in touch.xml <switcher><config ... /> ...</switcher>
     */
    void init(Map<String, String> config);

    boolean isSwitchOn(String touchPattern, String queryString);
}

```
如果没有配置任何Switcher的话, ~~Touch会默认配置一个基于Vitamin的Touch开关规则: 日常、测试环境默认打开, 预发、线上环境默认关闭.~~ 如果想实现自己的安全过滤规则, 可参考~~`VitaminSwitcher`或~~`ZookeeperSwitcher`(基于Zookeeper).

---

## 属性映射(0.5 版本新功能)
```
@Component
public class TouchTestCaseImpl implements TouchTestCase {

    @Touch
    private String p1;

    @Touch
    private Date p2;

    @Touch
    private Map<String, Object> p3;

    @Touch
    private Set<String> p4;

    @Touch
    private int p5;

    @Touch
    private char p6;

    @Touch
    private User user;

    @Touch
    private List<User> users;
    
    // ...
}
```
在浏览器访问:
```
http://localhost:8080/touch/p1?string
http://localhost:8080/touch/p2?2012-01-05 12:30:30
http://localhost:8080/touch/p3?{"key1":"value1", "key2", 2}
http://localhost:8080/touch/p4?["set1", "set2"]
http://localhost:8080/touch/p5?88
http://localhost:8080/touch/p6?A
http://localhost:8080/touch/user?{"name":"jifang", "age":8}
http://localhost:8080/touch/users?[{"name":"jifang", "age":8},{"name":"jifang2", "age":18}]
```
即可映射&修改&获取属性值.

---
## 未来规划
### 1. 方法参数名
Touch当前版本获取方法参数名是从JVM的MethodArea用javassist获取, 其性能很难估计, 如果将来我们需要提升性能, 可以添加类似MyBatis的`@Param`注解, 但这种方式会增加用户使用的负担(毕竟需要多添加一个注解...).

---
### 2. 方法参数映射功能增强
在有了`@Param`注解后, 就可以可以参考Spring MVC的`@RequestParam`:
![](https://si.geilicdn.com/daily_hz_img_0077000001586b7eed1c0a016558_1386_213_unadjust.png)
作出更强大的功能(如参数默认值、参数别名、类型校验等).

---
### 3. RPC(如Dubbo)转HTTP
我们的目标是将Touch做到完善、简洁且高性能, 这样就可以用作一个通用的RPC转HTTP的工具, 我们只需编写业务代码, 把想要开放http调用的方法前添加`@Touch`注解, 不需再使用Web层代码对Service做一层包装.

---

## 参考 
- Javassist获取字节码代码主要参考一下两篇文章:
    - [用 Javassist 获取方法参数名](http://lzxz1234.github.io/java/2014/07/25/Get-Method-Parameter-Names-With-Javassist.html)
    - [Java动态编程初探——Javassist](http://www.cnblogs.com/hucn/p/3636912.html)
- 动态获取泛型参数可以参考
    - [Java 反射 (by 翡青)](http://blog.csdn.net/zjf280441589/article/details/50453776)

---
- *by* **攻城师@翡青**
    - Email: feiqing.zjf@gmail.com
    - 博客: [攻城师-翡青](http://blog.csdn.net/zjf280441589) - http://blog.csdn.net/zjf280441589
    - 微博: [攻城师-翡青](http://weibo.com/u/3319050953) - http://weibo.com/u/3319050953