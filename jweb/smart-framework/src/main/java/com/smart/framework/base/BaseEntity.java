package com.smart.framework.base;

/**
 * 数据库实体公共抽象类
 */
public abstract class BaseEntity extends BaseBean {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
