package br.com.alura.ecommerce.util;

import java.util.UUID;

public class EmailGeneratorRanddomUtil {
    public static String generateRandomEmail() {
        return String.format("%s@%s", getUniqueId(), "myemail.com");
    }
    private static String getUniqueId() {
        return String.format("%s_%s", UUID.randomUUID().toString().substring(0, 5), System.currentTimeMillis() / 1000);
    }


}
