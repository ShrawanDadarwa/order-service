package com.appsdeveloperblog.estore.OrdersService.Service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Cacheable("OrderDetails")
    public String getOrderDetails(String empId){
        // some long processing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return "Sample Book Name";
    }
}
