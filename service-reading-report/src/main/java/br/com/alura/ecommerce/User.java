package br.com.alura.ecommerce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class User {

    private final String uuid;

    public String getReportPath() {

        return "target/" + uuid + "-report.txt";
    }
}
