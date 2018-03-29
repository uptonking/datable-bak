package com.smart.sample.entity;

import com.smart.framework.base.BaseEntity;

/**
 * 数据库ProductType表实体类
 */
public class ProductType extends BaseEntity {

    private String productTypeName;

    private String productTypeCode;

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }
}
