# faq for web

- rbac

- jwt

- url编码
    - encodeURIComponent 会转义除了字母、数字、(、)、.、!、~、*、'、-和_之外的所有字符   
    ``` 
    对于application/x-www-form-urlencoded(POST)这种数据方式，空格需要被替换成 '+'，  
    所以通常使用 encodeURIComponent 的时候还会把 "%20" 替换为 "+"
    ```
    - encodeURI 会替换所有的字符，但不包括以下字符  
        - 保留字符	; , / ? : @ & = + $
        - 非转义的字符	字母 数字 - _ . ! ~ * ' ( )
        - 数字符号	#  
    encodeURI 自身无法产生能适用于HTTP GET 或 POST 请求的URI，例如对于 XMLHTTPRequests,  
    因为 "&", "+", 和 "=" 不会被编码，然而在 GET 和 POST 请求中它们是特殊字符。然而encodeURIComponent这个方法会对这些字符编码
        
- urlencode vs form
    - multipart/form-data  
    将表单的数据处理为一条消息，以标签为单元，用分隔符分开。  
    既可以上传键值对，也可以上传文件。当上传的字段是文件时，会有Content-Type来说明文件类型；content-disposition，用来说明字段的一些信息；  
    由于有boundary隔离，所以multipart/form-data既可以上传文件，也可以上传键值对，它采用了键值对的方式，所以可以上传多个文件。  
    ``` 
    POST http://www.example.com HTTP/1.1
    Content-Type:multipart/form-data; boundary=----WebKitFormBoundaryrGKCBY7qhFd3TrwA
    
    ------WebKitFormBoundaryrGKCBY7qhFd3TrwA
    Content-Disposition: form-data; name="text"
    
    title
    ------WebKitFormBoundaryrGKCBY7qhFd3TrwA
    Content-Disposition: form-data; name="file"; filename="chrome.png"
    Content-Type: image/png
    
    PNG ... content of chrome.png ...
    ------WebKitFormBoundaryrGKCBY7qhFd3TrwA--
    ```
    - application/x-www-from-urlencoded  
    将表单内的数据转换为键值对，比如name=java&age=23    
    不能上传文件
    ``` 
    POST http://www.example.com HTTP/1.1
    Content-Type: application/x-www-form-urlencoded;charset=utf-8
    
    title=test&sub%5B%5D=1&sub%5B%5D=2&sub%5B%5D=3
    ```

- ping原理  
ping是一个用来测试网络连接的程序。它使用ICMP协议，请求目的地给予应答，它可以用来测试网络连通性、网络时延等，通常用来作为可用性的检查。它走在网络层，因此ping通不通与端口无关。   
ping 使用的是ICMP协议，它发送icmp回送请求消息给目的主机。ICMP协议规定：目的主机必须返回ICMP回送应答消息给源主机。如果源主机在一定时间内收到应答，则认为主机可达。  
ICMP协议通过IP协议发送的，IP协议是一种无连接的，不可靠的数据包协议。在Unix/Linux，序列号从0开始计数，依次递增。而Windows　ping程序的ICMP序列号是没有规律。    
ICMP协议在实际传输中数据包：20字节IP首部 + 8字节ICMP首部+ 1472字节<数据大小>38字节   
ICMP报文格式:IP首部(20字节)+8位类型+8位代码+16位校验和+(不同的类型和代码，格式也有所不同)    
``` 
- 假定主机A的IP地址是192.168.1.1，主机B的IP地址是192.168.1.2，都在同一子网内，则当你在主机A上运行“Ping 192.168.1.2”后，都发生了些什么呢? 

首先，Ping命令会构建一个固定格式的ICMP请求数据包，然后由ICMP协议将这个数据包连同地址“192.168.1.2”一起交给IP层协议（和ICMP一样，
实际上是一组后台运行的进程），IP层协议将以地址“192.168.1.2”作为目的地址，本机IP地址作为源地址，加上一些其他的控制信息，构建一个IP数据包，
并在一个映射表中查找出IP地址192.168.1.2所对应的物理地址（也叫MAC地址，这是数据链路层协议构建数据链路层的传输单元——帧所必需的），一并交给数据链路层。
后者构建一个数据帧，目的地址是IP层传过来的物理地址，源地址则是本机的物理地址，还要附加上一些控制信息，依据以太网的介质访问规则，将它们传送出去。
其中映射表由ARP实现，ARP(Address Resolution Protocol)是地址解析协议,是一种将IP地址转化成物理地址的协议。
ARP具体说来就是将网络层（IP层，也就是相当于OSI的第三层）地址解析为数据连接层（MAC层，也就是相当于OSI的第二层）的MAC地址。

主机B收到这个数据帧后，先检查它的目的地址，并和本机的物理地址对比，如符合，则接收；否则丢弃。接收后检查该数据帧，将IP数据包从帧中提取出来，交给本机的IP层协议。  
同样，IP层检查后，将有用的信息提取后交给ICMP协议，后者处理后，马上构建一个ICMP应答包，发送给主机A，其过程和主机A发送ICMP请求包到主机B一模一样
```

`ping [-dfnqrRv][-c 发送次数][-i 间隔秒数][-I (大写i)网络界面][-l (小写L)前置载入][-p 范本样式] [-s 数据包大小][-t 存活数值][主机名或IP地址]`

