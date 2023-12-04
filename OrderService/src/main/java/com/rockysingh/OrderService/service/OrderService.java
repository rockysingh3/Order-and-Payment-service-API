package com.rockysingh.OrderService.service;

import com.rockysingh.OrderService.model.OrderRequest;
import com.rockysingh.OrderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
    OrderResponse getOrderDetails(long orderId);
}
