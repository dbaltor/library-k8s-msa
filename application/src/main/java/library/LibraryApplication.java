package library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.RequiredArgsConstructor;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@RequiredArgsConstructor
public class LibraryApplication {
    public static final String UI_CONFIG_NAME = "uiConfig";

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
