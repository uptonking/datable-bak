# note-js



- JSON.parse() 反序列化对象时，输入对象key和value都必须是双引号，不能是单引号
- JSON.stringify()序列化对象时，输出的key和value都是双引号
- JS 字符串替换操作有replace() 方法  
这个方法有些问题，就是只能替换目标字符串中第一个匹配的字符串  
如果要将目标字符串全部替换的话，java里可以用replaceAll  
但是JS 没有提供这样的方法。使用正则表达式可以达到replaceAll的效果：
`str.replace(/word/g,"Excel") ;`  

