package br.com.alura.ecommerce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
@Getter
@ToString
@AllArgsConstructor
public class Order {

    private final String orderId, email;
    private final BigDecimal amount;
}
