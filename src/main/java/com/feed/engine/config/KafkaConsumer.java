package com.feed.engine.config;//package com.profile.handlersvc.config;


import com.feed.engine.feed.RecommendationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    RecommendationService recommendationService;

    @KafkaListener(topics = "test", groupId = "group_id")
    public void consume(String message) throws IOException {
        log.info(String.format("#### -> Consumed message -> %s", message));
//        recommendationService.getRecommendationForUser(message);

    }
}