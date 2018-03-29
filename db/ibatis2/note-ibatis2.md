# note-ibatis2

## 源码摘要

- ibatis2架构设计分3部分
    - common    
        - 通用工具类，包括io、xml、jdbc
    - sqlmap 
        - 目标是减少数据库操作的代码
        - 侧重数据源操作      
        - 通过使用xml文件将bean、map映射成jdbc的输入参数statement，再将jdbc处理结果映射成bean         
        - 依赖common
            
    - dao  
        - 目标是使数据访问层的操作原理应用程序的业务逻辑  
        - 侧重数据访问方式  
        - The iBATIS DAO framework is a very elementary IoC container and can be useful if you are not already using something like Spring or PicoContainer to manage dependencies. However, the framework is now deprecated and we suggest that you move to Spring. 
        - 不依赖common
    
      

#### dao

- 4部分
    - client 提供给外部的接口
    - builder 根据配置文件生成builder
    - impl 实现dao
    - transaction 事务管理  

- dao.xml配置  daoConfig是根节点
    - context子节点 配置数据库信息
        - transactionManager子节点 事务处理参数
            - property 参数
        - dao子节点 包含所有业务dao接口及实现类
    - properties 其他属性  


#### sqlmap

- 采用分层结构模式，基本上按照接口层、会话层、调度层、映射层和数据库操作层组成



