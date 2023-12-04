package com.rockysingh.CloudGatewat;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class CloudGatewatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudGatewatApplication.class, args);
	}



	/*
	* The userKeyResolver() method is a component of Spring Security's web security
	* framework that is used to resolve the user principal for an incoming HTTP request.
	* The user principal is an object that represents the authenticated user and is used by the framework
	* to enforce access control rules.
	* */

//	this stores keys in redis
	@Bean
	KeyResolver userKeySolver() {
		return exchange -> Mono.just("userKey");
	}


	/*
	* The Customizer<Resilience4JCircuitBreakerFactory> interface in Spring Cloud Circuit Breaker
	*  provides a mechanism to customize the configuration of Resilience4J circuit breakers. It allows
	* developers to override the default configuration settings or define additional configuration
	*  properties for specific circuit breakers.
	* */

//	this is the circuitbreaker bean for the api
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(
				id -> new Resilience4JConfigBuilder(id)
						.circuitBreakerConfig(
								CircuitBreakerConfig.ofDefaults()
						).build()
		);
	}

}
