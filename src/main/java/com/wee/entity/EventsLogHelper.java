package com.wee.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wee.service.PublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class EventsLogHelper {
    private  final ObjectMapper objectMapper;
    private final PublishService publishService;
    @Autowired
    Environment environment;
    static String EVENT_ARN = "aws_sns_event_arn";

    @Async("processExecutor")
    public void addAgentEvent(JSONObject metaData){
        HashMap<String,String> attributes = new HashMap<>();
        if(metaData.has("userId")) {
            attributes.put("userId", String.valueOf(metaData.get("userId")));
        }
        if(metaData.has("partnerId")) {
            attributes.put("partnerId", String.valueOf(metaData.get("partnerId")));
        }
        else{
            attributes.put("partnerId", null);
        }
        if(metaData.has("type")){
            attributes.put("type",metaData.getString("type"));
        }
        if(metaData.has("referenceId")) {
            attributes.put("referenceId", String.valueOf(metaData.get("referenceId")));
        }
        if(metaData.has("Url")) {
            attributes.put("Url", String.valueOf(metaData.get("Url")));
        }
        attributes.put("Browser", String.valueOf(metaData.get("Browser")));
        attributes.put("BrowserMajorVersion", String.valueOf(metaData.get("BrowserMajorVersion")));
        attributes.put("DeviceType", String.valueOf(metaData.get("DeviceType")));
        attributes.put("ipAddress", String.valueOf(metaData.get("ipAddress")));
        try {
            generateEventPayload(attributes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateEventPayload(HashMap<String, String> attributes) throws JsonProcessingException {
        attributes.put("distinct_id",String.valueOf(attributes.get("userId")));
        HashMap<String, Object> data = new HashMap<String, Object>();
        List<HashMap<String, Object>> actions = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> action = new HashMap<String, Object>();
        action.put("action","link_click_tracking");
        action.put("attributes", attributes);
        actions.add(action);
        data.put("actions", actions);
        data.put("type", "event");
        data.put("event_type", "agent_event");
        String payload = objectMapper.writeValueAsString(data);
        publishService.logEvents(payload,environment.getProperty(EVENT_ARN));
    }
}
