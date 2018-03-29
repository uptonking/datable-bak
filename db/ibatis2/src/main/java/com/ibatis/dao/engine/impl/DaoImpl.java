/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibatis.dao.engine.impl;

import com.ibatis.dao.client.Dao;

/**
 * dao接口的临时中转站
 */
public class DaoImpl {

    private StandardDaoManager daoManager;
    private DaoContext daoContext;
    private Class daoInterface;
    private Class daoImplementation;
    private Dao daoInstance;
    //dao的实例
    private Dao proxy;

    public StandardDaoManager getDaoManager() {
        return daoManager;
    }

    public void setDaoManager(StandardDaoManager daoManager) {
        this.daoManager = daoManager;
    }

    public DaoContext getDaoContext() {
        return daoContext;
    }

    public void setDaoContext(DaoContext daoContext) {
        this.daoContext = daoContext;
    }

    public Class getDaoInterface() {
        return daoInterface;
    }

    public void setDaoInterface(Class daoInterface) {
        this.daoInterface = daoInterface;
    }

    public Class getDaoImplementation() {
        return daoImplementation;
    }

    public void setDaoImplementation(Class daoImplementation) {
        this.daoImplementation = daoImplementation;
    }

    public Dao getDaoInstance() {
        return daoInstance;
    }

    public void setDaoInstance(Dao daoInstance) {
        this.daoInstance = daoInstance;
    }

    public Dao getProxy() {
        return proxy;
    }

    /**
     * 创建DaoImpl的代理对象
     */
    public void initProxy() {
        proxy = DaoProxy.newInstance(this);
    }

}
