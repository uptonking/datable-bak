package com.smart.sample.service.impl;

import com.smart.framework.DataSet;
import com.smart.framework.annotation.Bean;
import com.smart.framework.base.BaseService;
import com.smart.framework.cache.Cache;
import com.smart.framework.cache.CacheManager;
import com.smart.framework.util.CastUtil;
import com.smart.framework.util.CollectionUtil;
import com.smart.sample.entity.Customer;
import com.smart.sample.service.CustomerService;
import java.util.List;
import java.util.Map;

/**
 * Customer服务缓存
 */
@Bean
public class CustomerServiceCacheImpl extends BaseService implements CustomerService {

    private String key = "customer";
    private Cache cache = CacheManager.getInstance().createCache(key);

    @Override
    public List<Customer> getCustomerList() {
        List<Customer> customerList = cache.getAll();
        if (CollectionUtil.isEmpty(customerList)) {
            customerList = DataSet.selectList(Customer.class, null, null);
            if (CollectionUtil.isNotEmpty(customerList)) {
                cache.putAll(key, customerList);
            }
        }
        return customerList;
    }

    @Override
    public boolean deleteCustomer(long id) {
        return DataSet.delete(Customer.class, "id = ?", id);
    }

    @Override
    public Customer getCustomer(long id) {
        Customer customer = cache.get(key + "-" + id);
        if (customer == null) {
            customer = DataSet.select(Customer.class, "id = ?", id);
            if (customer != null) {
                cache.put(key + "-" + id, customer);
            }
        }
        return customer;
    }

    @Override
    public boolean updateCustomer(long id, Map<String, Object> fieldMap) {
        boolean result = DataSet.update(Customer.class, fieldMap, "id = ?", id);
        if (result) {
            Customer customer = DataSet.select(Customer.class, "id = ?", id);
            if (customer != null) {
                cache.put(key + "-" + customer.getId(), customer);
            }
        }
        return result;
    }

    @Override
    public boolean createCustomer(Map<String, Object> fieldMap) {
        boolean result = DataSet.insert(Customer.class, fieldMap);
        if (result) {
//            long id = CastUtil.castLong(fieldMap.get("id"));
//            Customer customer = DataSet.select(Customer.class, "id = ?", id);
//            if (customer != null) {
//                cache.put(key + "-" + customer.getId(), customer);
//            }
        }
        return result;
    }
}
