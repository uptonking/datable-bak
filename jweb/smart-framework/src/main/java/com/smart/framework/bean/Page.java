package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 页面基本类
 */
public class Page extends BaseBean {

    /**
     * 页面路径
     */
    private String path;
    /**
     * 相关数据
     */
    private Map<String, Object> data;

    public Page(String path) {
        this.path = path;
        data = new HashMap<String, Object>();
    }

    public Page data(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public boolean isRedirect() {
        return path.startsWith("/");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
