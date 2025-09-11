package dev.gagnon.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudConfig {
    @Value(value="${cloud.api.name}")
    private String cloudName;
    @Value(value="${cloud.api.key}")
    private String cloudApiKey;
    @Value(value="${cloud.api.secret}")
    private String cloudApiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map map = ObjectUtils.asMap("cloud_name", this.cloudName, "api_key", this.cloudApiKey, "api_secret", this.cloudApiSecret);
        return new Cloudinary(map);
    }
}

