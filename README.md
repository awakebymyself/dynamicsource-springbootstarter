# dynamicsource-springbootstarter 基于springboot 的动态数据源配置器



**背景及痛点：项目中发现使用mybatis的时候配置多数据源都是通过多套配置(多套数据源配置多个sqlSessionFactory,sqlSessionTemplate等等...)，不仅繁琐而且不易于维护，所有试想结合springboot加少许的约定实现一个动态数据源走天下的目的。一般来讲我们的数据源配置信息都是配置在xxx.properties上，目前公司是基于disconf进行配置托管， 此动态数据源便适用于上述的场景**

**特点：目前市面上的自动配置一般都只支持一读一写或者多读一写，难免还会引发重复配置的问题, 此项目支持多读多写的数据源自动配置！**
 

## 场景一：Disconf client 托管我们的数据源配置信息
应用程序在使用Disconf进去配置的时候都是通过配置文件的形式进行配置，如下：
```java
@Component
@DisconfFile(fileName = "db.properties")
public class DbConfig {

    private String masterWriteDriver;
    private String masterWriteUrl;
    private String masterWriteUser;
    private String masterWritePassword;

    private String slaveReadDriver;
    private String slaveReadUrl;
    private String slaveReadUser;
    private String slaveReadPassword;

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

    @DisconfFileItem(name = "slaveReadDriver")
    public String getSlaveReadDriver() {
        return slaveReadDriver;
    }

    @DisconfFileItem(name = "slaveReadUrl")
    public String getSlaveReadUrl() {
        return slaveReadUrl;
    }

    @DisconfFileItem(name = "slaveReadUser")
    public String getSlaveReadUser() {
        return slaveReadUser;
    }

    @DisconfFileItem(name = "slaveReadPassword")
    public String getSlaveReadPassword() {
        return slaveReadPassword;
    }

    @DisconfFileItem(name = "masterWriteDriver")
    public String getMasterWriteDriver() {
        return masterWriteDriver;
    }

    @DisconfFileItem(name = "masterWriteUrl")
    public String getMasterWriteUrl() {
        return masterWriteUrl;
    }

    @DisconfFileItem(name = "masterWriteUser")
    public String getMasterWriteUser() {
        return masterWriteUser;
    }

    @DisconfFileItem(name = "masterWritePassword")
    public String getMasterWritePassword() {
        return masterWritePassword;
    }
```

以上是当前应该程序背景， 使用此starter的方式：
首先配置启动自动配置选项，在`application.properties`中增加:
```java
// 要进行动态数据源配置的service 方法, 拦截数据库操作
dynamic.ds.pointCut= execution (* com.lzg.xxx.service..*.*(..))
// 启用自动配置
dynamic.ds.enable=true
```
peiz配置shujuy配置数据源zhuce配置数据源注册配置数据源注册测配置数据源注册测类`DynamicDsRegister`
`JAVA配置:`
```java
    @Bean
    public DynamicDsRegister register() {
        return new DynamicDsRegister(xxx.class); //注意这边的xxx.class及为上面disconf托管的配置类！
    }
```
`Xml 配置:`

```xml

<bean id = "dunamicDsRegister" class = "com.lzg.dynamicsource.regist.DynamicDsRegister">
    <property name = "dbClass" value = "xxx.xxx.xx"/>
</bean>
```

**这就是全部所需的配置了，快乐就完事了，同时关于disconf托管的配置类有几处默认的约定需要遵守，如下：**

在配置数据源的时候我们通常会配置`Driver` `Url` `User` `Password`， 这些为配置的保留字，作为字段名的后缀,首字母大写,  如果这个数据源
是写数据源，那么字段命名为`xxxWriteUrl`, 同理如果为读数据源，那么命名为`xxxReadUrl`. 在应该初始化完成的时候，对应的写数据源名称为
`xxxWrite`, 读数据源名称为`xxxRead`

#####对于上面的DbConfig类:

```java
我们的写数据源有：masterWrite 和 activityWrite 两个

读数据源有：slaveRead  和 activityRead 两个

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

对于不同库多写多读的情况，无法区别使用哪一个数据源, 需要组合注解一起使用
可以指定程序默认使用的读写库, 在类上面添加注解:
```java

@DefaultDataSource(write = "xxxWrite", read = "xxxRead")
public class DbConfig {...}
```
这样如果我们没有在方法层面指定数据源，那么就会使用默认的数据源。

指定数据源所需要的是在方法层面通过注解指定数据源名称：
```java
  @DynamicDS("xxxWrite") 指定需要使用的数据源名称
    public String hello() {
        TicketLogPo ticketLogPo = logPoMapper.selectByPrimaryKey(1);

        return ticketLogPo.toString();
    }

```




## 场景二：基于springboot 配置文件进行的数据源配置

**许多应该的配置并不是基于Disconf去托管数据库配置文件的，那么可以使用这种配置文件的方式去启动。**


**配置方式支持类路径添加或者从外部文件系统加载**

`内部添加：`
 在`resources`目录下面添加`dynamic.properties`, 名称约定为这个， 配置数据源如下：

 ```java
aWriteUrl=aWriteUrl
aWriteUser=aUser
aWritePassword=aPassword
aWriteDriver=aDriver

aReadUrl=abc
aReadUser=aReadUser
aReadPassword=aReadPassword
aReadDriver=aReadDriver

bWriteUrl=bWriteUrl
bWriteUser=bWriteUser
bWritePassword=bWritePassword
bWriteDriver=bWriteDriver

bReadUrl=bReadUrl
bReadUser=bReadUser
bReadPassword=bReadReadPassword
bReadDriver=bReadDriver

bSecondReadUrl=bSecondReadUrl
bSecondReadUser=bSecondReadUser
bSecondReadPassword=bSecondReadPassword
bSecondReadDriver=bSecondReadDriver

 ```

读数据源有：`aRead`, `bRead`, `bSecondRead`
写数据源有：`aWrite`, `bWrite`


`从外部添加：`
可以在程序启动的时候知道外部文件的路径`System.setProperty("dynamic.path", "xxxpath")` 或者jar运行是设置`-Ddynamic.path=xxxpth`即可



**具体使用多数据源的场景**
 请参考上面Disconf场景说明，唯一一点区别就是在设置默认读写数据源的时候我们没有配置类可以添加注解了，那么我们需要在
 `springboot` 的默认的配置文件`application.properties`中添加配置:

 ```xml
 dynamic.ds.defaultWrite=xxxWrite

 dynamic.ds.defaultWrite=xxxRead

 ```


###综上两个不同的场景只是在配置数据源上会区别，其他需要的配置几乎一致， 简单易用可维护。


