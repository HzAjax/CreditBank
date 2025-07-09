package ru.volodin.statement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ru.volodin.statement",
        "org.example.mylib"
})
public class StatementApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatementApplication.class, args);
    }
}
