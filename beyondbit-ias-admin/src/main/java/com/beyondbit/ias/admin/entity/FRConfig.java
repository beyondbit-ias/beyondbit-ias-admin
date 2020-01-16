package com.beyondbit.ias.admin.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "frconfig")
@PropertySource(value = "classpath:ias.yml")
public class FRConfig {
    @Value("${MainHost}")
    String MainHost;

    @Value("${Response_Type}")
    String ResponseType;

    @Value("${Scope}")
    String Scope;

    @Value("${Client_ID}")
    String ClientID;

    @Value("${Client_Secret}")
    String ClientSecret;
}

