# java web

## Servlet相关

- url-pattern    
    - `<url-pattern>/</url-pattern>`：会匹配到/springmvc这样的路径型url，不会匹配到模式为*.jsp这样的后缀型url    
    - `<url-pattern>/*</url-pattern>`：会匹配所有的url：路径型的和后缀型的url(包括/springmvc，.jsp，.js和*.html等)   
- servlet is just an implementation of HTTP request and response mechanism based on Java.

- cookie
- session

- servlet 3新增9个注解
    - @HandlesTypes
    - @HttpConstraint
    - @HttpMethodConstraint
    - @MultipartConfig
    - @ServletSecurity
    - @WebFilter
    - @WebInitParam
    - @WebListener
    - @WebServlet

- spring常用注解
    - @Component 所有受Spring管理的组件的通用注解
    - @Controller
    - @Service
    - @Repository 对应数据访问层bean


## java相关

- jdk/src.zip包含java se核心源码
- jdk/jre/lib/rt.jar包含java较完整的字节码，比src.zip多sun.开头的包
- jdk/lib 目录下包含多个jdk工具包， jdk/jre/lib包含jvm字符集和多个工具包，和前者完全不同
- Statement会使数据库频繁编译SQL，可能造成数据库缓冲区溢出。  
    - 采用Statement.addBatch(sql)方式实现批处理：
        - 优点：可以向数据库发送多条不同的SQL语句。
        - 缺点：SQL语句没有预编译，当向数据库发送多条语句相同，但仅参数不同的SQL语句时，需重复写上很多条SQL语句
- PreparedStatement可对SQL进行预编译，从而提高数据库的执行效率。并且PreperedStatement对于sql中的参数，允许使用占位符的形式进行替换，简化sql语句的编写。
    - 采用PreparedStatement.addBatch()实现批处理
        - 优点：发送的是预编译后的SQL语句，执行效率高。
        - 缺点：只能应用在SQL语句相同，但参数不同的批处理中。因此此种形式的批处理经常用于在同一个表中批量插入数据，或批量更新表的数据。
- 数据库连接池 DataSource
    - DBCP: tomcat
    - C3P0: hibernate
        - dbcp没有自动回收空闲连接的功能
        - c3p0有自动回收空闲连接功能
- JNDI(Java Naming and Directory Interface)，Java命名和目录接口，它对应于J2SE中的javax.naming包  
    - 可以把Java对象放在一个容器中（JNDI容器），并为容器中的java对象取一个名称，以后程序想获得Java对象，只需 通过名称检索即可。  
    其核心API为Context，它代表JNDI容器，其lookup方法为检索容器中对应名称的对象
    

      


## note

- Servlet接口共5个方法 init(), getServletConfig(), service(), getServletInfo(), destroy()

- 在编译和运行 Java 程序时，都会通过 ExtClassLoader类装载器去<JDK安装主目录>\jre\lib\ext 目录下的 Jar 包中搜索要加载的类，  
 所以，如果将包含Serlet API的jar文件复制到该目录，在编译Servlet程序时，就不必在CLASSPATH环境变量中增加包含Servlet API的jar文件。
