package com.wee.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;


@Slf4j
@Service
public class PublishService {
    @Autowired
     Environment environment;

     SnsClient snsClient;
    static String EVENT_ARN = "aws_sns_event_arn";
    
    

    public PublishService() {

        snsClient = SnsClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }

    public void logEvents(String payload, String eventArn) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(payload)
                    .topicArn(eventArn)
                    .build();
            PublishResponse publish = snsClient.publish(request);
            log.info("Publish Message {}", publish);
        } catch (SnsException e) {
            log.error("Error Response {}", e);
        }
    }

}
