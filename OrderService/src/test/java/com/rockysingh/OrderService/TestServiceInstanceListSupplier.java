package com.rockysingh.OrderService;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

/*
*
* This is the instance of the service layer we will be using to test out our controller layer
*
* */

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {
    @Override
    public String getServiceId() {
        return null;
    }
    @Override
    public Flux<List<ServiceInstance>> get() {

        List<ServiceInstance> result = new ArrayList<>();

//        creating product service instance
        result.add(new DefaultServiceInstance(
                "PRODUCT-SERVICE",
                "PRODUCT-SERVICE",
                "localhost",
                8080,
                false
        ));

//        creating payment service instance
        result.add(new DefaultServiceInstance(
                "PAYMENT-SERVICE",
                "PAYMENT-SERVICE",
                "localhost",
                8080,
                false
        ));


        return Flux.just(result);
    }
}
