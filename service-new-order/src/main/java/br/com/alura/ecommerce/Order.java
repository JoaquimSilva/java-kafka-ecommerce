package br.com.alura.ecommerce;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
@ToString
@AllArgsConstructor
public class Order {

    private final String orderId, email;
    private final BigDecimal amount;
}
