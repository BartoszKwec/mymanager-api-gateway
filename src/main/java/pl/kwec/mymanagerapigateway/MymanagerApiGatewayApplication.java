package pl.kwec.mymanagerapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MymanagerApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MymanagerApiGatewayApplication.class, args);
    }

}
