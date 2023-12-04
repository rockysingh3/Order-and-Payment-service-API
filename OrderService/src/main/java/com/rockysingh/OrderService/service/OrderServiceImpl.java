package com.rockysingh.OrderService.service;

import com.netflix.discovery.converters.Auto;
import com.rockysingh.OrderService.entity.Order;
import com.rockysingh.OrderService.exception.CustomException;
import com.rockysingh.OrderService.external.client.PaymentService;
import com.rockysingh.OrderService.external.client.ProductService;
import com.rockysingh.OrderService.external.request.PaymentRequest;
import com.rockysingh.OrderService.model.OrderRequest;
import com.rockysingh.OrderService.model.OrderResponse;
import com.rockysingh.OrderService.model.PaymentResponse;
import com.rockysingh.OrderService.model.ProductResponse;
import com.rockysingh.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

//    using feign client
    @Autowired
    private ProductService productService;

//    using feign client
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public long placeOrder(OrderRequest orderRequest) {

        // order entity -> save the data with status order created
        // product service - block products (reduce the quantity)
        // Payment Service -> payments -> Success -> Complete, else
        // Cancelled

        log.info("Placing Order Request: {}", orderRequest);

//        able to do this cuz of Feign client
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with status created");

//        converting orderRequest model into Order Entity
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

//        saving it to the DB
        order = orderRepository.save(order);

        log.info("Calling payment service to complete the payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                        .orderId(order.getId())
                                .paymentMode(orderRequest.getPaymentMode())
                                        .amount(orderRequest.getTotalAmount())
                                                .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the Order Status");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occurred in payment. Changing order service " + e);
            orderStatus = "PAYMENT_FAILED";
        }


        order.setOrderStatus(orderStatus);

//        if the payment request is good update the order repo
        orderRepository.save(order);


        log.info("Order Places successfully with Order id: {}", order.getId());
        return order.getId();
    }


    @Override
    public OrderResponse getOrderDetails(long orderId) {

        log.info("Get Order details for Order Id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order id: " + orderId, "NOT_FOUND", 404));


        //        GET product DETAILS
//        using the rest template
        log.info("Invocking product service to fetch the product details" + order.getProductId());
        ProductResponse productResponse = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );

        log.info("Getting payment information form the payment Service");
        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        );

        OrderResponse.ProductDetails productDetails
                = OrderResponse.ProductDetails
                .builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .build();

        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();


//        once we got the product details we added to the orderresponse

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(orderId)
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();


//        GET PAYMENT DETAILS
        return orderResponse;
    }
}
