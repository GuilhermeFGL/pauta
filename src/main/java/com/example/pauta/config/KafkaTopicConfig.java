package com.example.pauta.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC_PAUTA_CLOSE = "clouse_pauta";
    public static final String TOPIC_PAUTA_RESULT = "clouse_pauta";
    public static final String GROUP_ID = "group";
    
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
    
    @Bean
    public NewTopic topicClosePauta() {
         return new NewTopic(KafkaTopicConfig.TOPIC_PAUTA_CLOSE, 1, (short) 1);
    }

    @Bean
    public NewTopic topicPautaResult() {
        return new NewTopic(KafkaTopicConfig.TOPIC_PAUTA_RESULT, 1, (short) 1);
    }
}