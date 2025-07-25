package dev.gagnon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class VendorServiceApplication {
    public static void main(String[] args) {SpringApplication.run(VendorServiceApplication.class, args);
    }

}


