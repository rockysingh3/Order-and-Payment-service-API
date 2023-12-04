package com.dailycodebuffer.ProductService.exception;

import lombok.Data;

@Data
public class ProductServiceCustomException extends RuntimeException {

    private String errorCode;

    public ProductServiceCustomException(String message, String error) {
        super(message);
        this.errorCode = error;
    }
}
