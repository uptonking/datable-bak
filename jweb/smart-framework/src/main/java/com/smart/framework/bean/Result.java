package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;

/**
 * 返回结果结构基本类
 */
public class Result extends BaseBean {

    // 成功标志
    private int error;
    // 错误代码
    private Object data;
    // 相关数据
    private boolean success;

    public Result(boolean success) {
        this.success = success;
    }

    public Result error(int error) {
        this.error = error;
        return this;
    }

    public Result data(Object data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
