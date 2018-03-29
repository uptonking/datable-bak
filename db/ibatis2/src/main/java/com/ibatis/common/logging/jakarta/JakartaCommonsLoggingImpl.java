package com.ibatis.common.logging.jakarta;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

// 这实际上是一个代理类，全部方法转化到org.apache.commons.logging.Log上进行处理
public class JakartaCommonsLoggingImpl implements com.ibatis.common.logging.Log {

  private Log log;

  public JakartaCommonsLoggingImpl(Class clazz) {
    log = LogFactory.getLog(clazz);
  }

  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  public void error(String s, Throwable e) {
    log.error(s,e);
  }

  public void error(String s) {
    log.error(s);
  }

  public void debug(String s) {
    log.debug(s);
  }

  public void warn(String s) {
    log.warn(s);
  }

}
