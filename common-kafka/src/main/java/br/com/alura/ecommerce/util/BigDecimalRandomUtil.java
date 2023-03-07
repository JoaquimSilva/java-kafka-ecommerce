package br.com.alura.ecommerce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalRandomUtil {
    public static BigDecimal random(int range) {
        BigDecimal max = new BigDecimal(range);
        BigDecimal randFromDouble = BigDecimal.valueOf(Math.random());
        BigDecimal actualRandomDec = randFromDouble.multiply(max);
        actualRandomDec = actualRandomDec.setScale(2, RoundingMode.HALF_UP);
        return actualRandomDec;
    }
}
