package com.smart.sample.test;

import com.smart.framework.annotation.Order;
import com.smart.framework.base.BaseTest;
import com.smart.framework.bean.Pager;
import com.smart.framework.helper.BeanHelper;
import com.smart.sample.bean.ProductBean;
import com.smart.sample.entity.Product;
import com.smart.sample.service.ProductService;
import com.smart.sample.service.impl.ProductServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProductServiceTest extends BaseTest {

    private ProductService productService = BeanHelper.getBean(ProductServiceImpl.class);

    @BeforeClass
    @AfterClass
    public static void init() {
        initSQL("sql/product.sql");
    }

    @Test
    @Order(1)
    public void getProductListTest() {
        List<Product> productList = productService.getProductList();
        Assert.assertNotNull(productList);
        Assert.assertEquals(productList.size(), 7);
    }

    @Test
    @Order(2)
    public void getProductTest() {
        long productId = 1;
        Product product = productService.getProduct(productId);
        Assert.assertNotNull(product);
    }

    @Test
    @Order(3)
    public void createProductTest() {
        Map<String, Object> productFieldMap = new HashMap<String, Object>();
        productFieldMap.put("productTypeId", 1);
        productFieldMap.put("productName", "1");
        productFieldMap.put("productCode", "1");
        productFieldMap.put("price", 1);
        productFieldMap.put("description", "1");
        boolean result = productService.createProduct(productFieldMap);
        Assert.assertTrue(result);
    }

    @Test
    @Order(4)
    public void updateProductTest() {
        long productId = 1;
        Map<String, Object> productFieldMap = new HashMap<String, Object>();
        productFieldMap.put("productName", "1");
        productFieldMap.put("productCode", "1");
        boolean result = productService.updateProduct(productId, productFieldMap);
        Assert.assertTrue(result);
    }

    @Test
    @Order(5)
    public void deleteProductTest() {
        long productId = 1;
        boolean result = productService.deleteProduct(productId);
        Assert.assertTrue(result);
    }

    @Test
    @Order(6)
    public void searchProductPagerTest() {
        int pageNumber = 1;
        int pageSize = 3;
        Map<String, String> formFieldMap = new HashMap<String, String>();
        Pager<ProductBean> productBeanPager = productService.searchProductPager(pageNumber, pageSize, formFieldMap);
        Assert.assertEquals(productBeanPager.getRecordList().size(), 3);
    }
}
