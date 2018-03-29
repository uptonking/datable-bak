package com.smart.framework.proxy;

/**
 * 代理接口
 */
public interface Proxy {

    void doProxy(ProxyChain proxyChain) throws Exception;
}
