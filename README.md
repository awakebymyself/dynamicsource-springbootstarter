# dynamicsource-springbootstarter 基于springboot 的动态数据源配置器



**背景及痛点：项目中发现使用mybatis的时候配置多数据源都是通过多套配置(多套数据源已经sqlSessionFactory等等...)，不仅繁琐而且不易于维护，所有试想结合springboot加少许的约定实现一个动态数据源走天下的目的。一般来讲我们的数据源配置信息都是配置在xxx.properties上，目前公司是基于disconf进行配置托管， 此动态数据源便适用于上述的场景**


## 场景一：Disconf client 托管我们的数据源配置信息
应用程序在使用Disconf进去配置的时候都是通过配置文件的形式进行配置，如下：
```java
@Component
@DisconfFile(fileName = "db.properties")
public class DbConfig {

    private String masterDriver;
    private String masterUrl;
    private String masterUser;
    private String masterPassword;

    private String slaveDriver;
    private String slaveUrl;
    private String slaveUser;
    private String slavePassword;

    private String activityWriteDriver;
    private String activityWriteUrl;
    private String activityWriteUser;
    private String activityWritePassword;

    private String activityReadDriver;
    private String activityReadUrl;
    private String activityReadUser;
    private String activityReadPassword;

    @DisconfFileItem(name = "activityWriteDriver")
    public String getActivityWriteDriver() {
        return activityWriteDriver;
    }

    @DisconfFileItem(name = "activityWriteUrl")
    public String getActivityWriteUrl() {
        return activityWriteUrl;
    }

    @DisconfFileItem(name = "activityWriteUser")
    public String getActivityWriteUser() {
        return activityWriteUser;
    }

    @DisconfFileItem(name = "activityWritePassword")
    public String getActivityWritePassword() {
        return activityWritePassword;
    }

    @DisconfFileItem(name = "activityReadDriver")
    public String getActivityReadDriver() {
        return activityReadDriver;
    }

    @DisconfFileItem(name = "activityReadUrl")
    public String getActivityReadUrl() {
        return activityReadUrl;
    }

    @DisconfFileItem(name = "activityReadUser")
    public String getActivityReadUser() {
        return activityReadUser;
    }

    @DisconfFileItem(name = "activityReadPassword")
    public String getActivityReadPassword() {
        return activityReadPassword;
    }

    @DisconfFileItem(name = "slaveDriver")
    public String getSlaveDriver() {
        return slaveDriver;
    }

    @DisconfFileItem(name = "slaveUrl")
    public String getSlaveUrl() {
        return slaveUrl;
    }

    @DisconfFileItem(name = "slaveUser")
    public String getSlaveUser() {
        return slaveUser;
    }

    @DisconfFileItem(name = "slavePassword")
    public String getSlavePassword() {
        return slavePassword;
    }

    @DisconfFileItem(name = "masterDriver")
    public String getMasterDriver() {
        return masterDriver;
    }

    @DisconfFileItem(name = "masterUrl")
    public String getMasterUrl() {
        return masterUrl;
    }

    @DisconfFileItem(name = "masterUser")
    public String getMasterUser() {
        return masterUser;
    }

    @DisconfFileItem(name = "masterPassword")
    public String getMasterPassword() {
        return masterPassword;
    }
```

以上是当前应该程序背景， 使用此starter的方式：
首先配置启动自动配置选项，在`application.properties`中增加:
```java
// 要进行动态数据源配置的service 方法
dynamic.ds.pointCut= execution (* com.lzg.xxx.service..*.*(..))
// 启用自动配置
dynamic.ds.enable=true
```
在`@Configuration` 中增加`@Bean` 配置：

```java
    @Bean
    public DynamicDsRegister register() {
        return new DynamicDsRegister(xxx.class); //注意这边的xxx.class及为上面disconf托管的配置类！
    }
```

**这就是全部所需的配置了，快乐就完事了，同时关于disconf托管的配置类有几处默认的约定需要遵守，如下：**

在配置数据源的时候我们通常会配置`Driver` `Url` `User` `Password`， 这些为配置的保留字，作为字段名的后缀,首字母大写, for example `masterUrl` `slaveUser`

#####字段命名方式:

```java
xxxWrite(Url|Driver|Password) write为保留字, xxx即为写数据源名称

同理 读信息配置类似 xxxRead(Url|Driver|Password) write为保留字, xxx即为读数据源名称

如果应用只是简单的一个读写，则可以直接命名为 master(Url|...)为写, slave(Ur;|...) 为读

```

####下面讨论多数据源使用的场景
 1. 一写一读或者多读（同库）：
  ```java
  private String xxxWriteUrl, xxxWriteDriver... 写配置

  private String xxxReadUrl,xxxReadUser... 读配置1

  private String ***ReadUrl,***ReadUser... 读配置2
  ```
方法不添加注解的时候根据方法名前缀判断是否走读库(query|select|find|get)开头，走读库的时候使用轮询算法，顺序使用多读源


 2. 多写多读（不同库）:
 ```java
 private String xxxWriteUrl, xxxWriteDriver... 写配置1 对应数据源名称为xxxWrite
 private String ***WriteUrl, ***WriteDriver... 写配置2  对应数据源名称为***Write

 private String xxxReadUrl,xxxReadUser... 读配置1 对应数据源名称为xxxRead
 private String ***ReadUrl,***ReadUser... 读配置2 对应数据源名称为***Read
 ```

对于不同库多写多读的情况，无法区别使用哪一个数据源，所有需要在方法层面通过注解指定数据源名称：
```java
  @DynamicDS("xxxWrite") 指定需要使用的数据源名称
    public String hello() {
        TicketLogPo ticketLogPo = logPoMapper.selectByPrimaryKey(1);

        return ticketLogPo.toString();
    }

```




## 场景二：基于springboot 配置文件进行的数据源配置(TODO)