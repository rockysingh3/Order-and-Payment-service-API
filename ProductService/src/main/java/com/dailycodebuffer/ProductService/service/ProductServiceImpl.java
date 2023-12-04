package com.dailycodebuffer.ProductService.service;

import com.dailycodebuffer.ProductService.entity.Product;
import com.dailycodebuffer.ProductService.exception.ProductServiceCustomException;
import com.dailycodebuffer.ProductService.model.ProductRequest;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import com.dailycodebuffer.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl  implements ProductService {



    // saves a product to the DB
    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {

        log.info("Adding Product...");

//        builder method is part of lombok lib, its like inititing a new object
        Product product =
                Product.builder()
                        .productName(productRequest.getName())
                        .quantity(productRequest.getQuantity())
                        .price(productRequest.getPrice())
                        .build();

        productRepository.save(product);

        log.info("Product Created");


//        returns the product id
        return product.getProductId();
    }


    @Override
    public ProductResponse getProductById(long productId) {

        log.info("Get the product for productId: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product with this ID not found", "PRODUCT_NOT_FOUND"));

        // convert the product we got from the DB into productResponse object
        // this is another way to create a product without using Builder()
        // here we are using BeanUtils
        ProductResponse productResponse = new ProductResponse();

//        convert product into productResponse
        BeanUtils.copyProperties(product, productResponse);


        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {}", quantity, productId);

//        find the product by the given ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException(
                        "Product with given Id not found",
                        "PRODUCT_NOT_FOUND"
                ));

//        handle the edge case
        if(product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                    "Product does not have sufficient Quantity",
                    "INSUFFICIENT_QUANTITY"
            );
        }


//        update the quantity
        product.setQuantity(product.getQuantity() - quantity);

//        update the DB
        productRepository.save(product);


        log.info("Product Quantity updated Successfully");
    }
}
