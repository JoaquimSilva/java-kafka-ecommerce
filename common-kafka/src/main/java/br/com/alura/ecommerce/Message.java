package br.com.alura.ecommerce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Message<T> {

    private final CorrelationId correlationId;
    private final T payload;

    public T getPayLoad() {
        return payload;
    }
}
