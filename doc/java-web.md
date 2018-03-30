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

## note

- Servlet接口共5个方法 init(), getServletConfig(), service(), getServletInfo(), destroy()

- 在编译和运行 Java 程序时，都会通过 ExtClassLoader类装载器去<JDK安装主目录>\jre\lib\ext 目录下的 Jar 包中搜索要加载的类，  
 所以，如果将包含Serlet API的jar文件复制到该目录，在编译Servlet程序时，就不必在CLASSPATH环境变量中增加包含Servlet API的jar文件。
