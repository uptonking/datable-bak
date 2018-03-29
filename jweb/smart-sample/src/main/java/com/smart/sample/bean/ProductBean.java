package com.smart.sample.bean;

import com.smart.framework.base.BaseBean;
import com.smart.sample.entity.Product;
import com.smart.sample.entity.ProductType;

/**
 * 产品实体bean
 */
public class ProductBean extends BaseBean {

    private Product product;
    private ProductType productType;

    public ProductBean(Product product, ProductType productType) {
        this.product = product;
        this.productType = productType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
