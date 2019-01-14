- [dubbo内核-SPI](#dubbo内核-SPI)  
    - [1 SPI](#1-SPI)  
            - [1.1 简介](#11-简介)  
            - [1.2 设计目标](#12-设计目标)  
            - [1.3 约定](#13-约定)  
    - [2 JDK SPI扩展](#2-JDK SPI扩展)  
            - [2.1 JDK SPI示例](#21-JDKSPI示例)  
            - [2.2 JDK SPI原理](#22-JDKSPI原理)  
            - [2.3 JDK SPI ServiceLoader缺点](#22-JDKSPIServiceLoader缺点)  

# dubbo内核-SPI
## 1 SPI
### 1.1 简介  
SPI(Service Provider Interface):服务提供商接口，是JDK内置的一种服务发现机制。目前有不少框架用它来做服务的扩展发现。***简单来说，它就是一种动态替换服务实现者的机制。***  
所以，Dubbo如此被广泛采纳的其中一个重要原因就是基于SPI实现强大灵活的扩展机制，开发者可以自定义插件嵌入Dubbo，实现灵活的业务需求。  
```
有人会觉得这就是建立在面向接口编程下的一种为了使组件可扩展或动态变更实现的规范。
常见的类SPI的设计有JDBC、JNDI、JAXP等。  
很多开源框架的内部实现也采用了SPI。  
例如：JDBC的架构是由一套API组成，用于给Java应用提供访问不同数据库的能力，而数据库提供商的驱动软件各不相同。  
JDBC通过提供一套通用行为的API接口，底层可以由提供商自由实现，虽然JDBC的设计没有指明是SPI，但也和SPI的设计类似。  
```

### 1.2 设计目标  
面向对象的设计里，模块之间是基于接口编程，模块之间不应该对实现类进行硬编码。  
一旦代码里涉及具体的实现类，就违反了可插拔的原则，如果需要替换一种实现，就需要修改代码。  
为了实现在***模块装配***的时候，不在模块里面写死代码，这就需要一种服务发现机制。  
为某个接口寻找服务实现的机制。有点类似IOC的思想，就是***将装配的控制权转移到代码之外***。  

### 1.3 约定
SPI的具体约定如下:  
当服务提供者(provider)，提供了一个接口多种实现时，一般会在jar包的META-INF/services/目录下，创建该接口的同名文件。  
该文件里面的内容就是该服务提供接口的具体实现类的名称。  
而当外部加载这个模块的时候，就能通过jar包META-INF/services/里的配置文件得到具体的类名，并加载实例化，完成模块的装配。  

## 2 JDK SPI扩展  
JDK为SPI的实现提供工具类，即java.util.ServiceLoader。  
ServiceLoader中定义的SPI规范没有什么特别之处，只需要有一个提供者配置文件(provider-configuration file)。  
该文件需要在resource目录META-INF/services下，文件名就是服务接口的全限定名。
```
1. 文件内容是提供者Class的全限定名列表，显然提供者Class都应该实现服务接口；
2. 文件必须使用UTF-8编码。
```

### 2.1 JDK SPI示例
SPI服务接口:  
```java
public interface Command {

    /**
     * 执行命令
     */
    void execute();
}
```

实现类1:  
```java
public class StartupCommand implements Command {

    @Override
    public void execute() {
        System.out.println("startup...");
    }
}
```

实现类2:  
```java
public class ShutdownCommand implements Command {
    @Override
    public void execute() {
        System.out.println("shutdown...");
    }
}
```

入口类:  
```java
public class SpiMain {
    public static void main(String[] args) {
        ServiceLoader<Command> loader = ServiceLoader.load(Command.class);
        System.out.println(loader);

        for (Command command : loader) {
            command.execute();
        }
    }
}
```

/META-INF/services/com.bc.soa.spi.jdk.serviceloader.Command文件中配置:  
```
com.bc.soa.spi.jdk.serviceloader.impl.StartupCommand
com.bc.soa.spi.jdk.serviceloader.impl.ShutdownCommand
```

运行结果:  
```
java.util.ServiceLoader[com.bc.soa.spi.jdk.serviceloader.Command]
startup...
shutdown...
```

[示例代码](https://github.com/BooksCup/dubbo-analysis/tree/master/src/main/java/com/bc/soa/spi/jdk)  

### 2.2 JDK SPI原理  
配置文件为什么放在META-INF/services下面?  
ServiceLoader类(ServiceLoader.java)定义如下:  
```java
private static final String PREFIX = "META-INF/services/";
```

### 2.3 JDK SPI ServiceLoader缺点  
1.虽然ServiceLoader也算是使用的延迟加载，但是基本只能通过遍历全部获取，也就是接口的实现类全部加载并实例化一遍。  
如果你并不想使用某些实现类，它也被加载并且实例化了，这就造成了浪费。  
2.获取某个实现类的方式不够灵活，只能通过Iterator形式获取，不能根据某个参数来获取对应的实现类。  

