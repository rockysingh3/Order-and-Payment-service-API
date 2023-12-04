package com.rockysingh.PaymentService.service;

import com.rockysingh.PaymentService.model.PaymentRequest;
import com.rockysingh.PaymentService.model.PaymentResponse;

public interface PaymentService {


    long doPayment(PaymentRequest paymentRequest);
    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
