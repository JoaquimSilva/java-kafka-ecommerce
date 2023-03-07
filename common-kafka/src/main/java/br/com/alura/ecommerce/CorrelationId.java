package br.com.alura.ecommerce;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;
@Getter
@ToString
public class CorrelationId {

    private final String id;

    public CorrelationId(String title) {
        id = title + "(" + UUID.randomUUID() + ")";
    }

    public CorrelationId continueWith(String title) {
        return new CorrelationId(id + " - " + title);
    }
}
