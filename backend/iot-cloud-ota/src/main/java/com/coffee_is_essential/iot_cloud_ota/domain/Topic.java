package com.coffee_is_essential.iot_cloud_ota.domain;

import java.util.ArrayList;
import java.util.List;

public record Topic(
        String topic
) {
    public static List<Topic> transformToDeploymentTopics(List<Long> regionIds, List<Long> groupIds, List<Long> deviceIds) {
        List<Topic> topicList = new ArrayList<>();
        int maxLength = Math.max(Math.max(deviceIds.size(), groupIds.size()), regionIds.size());

        for (int i = 0; i < maxLength; i++) {
            String topicStr = String.format("v1/%s/%s/%s/firmware/download/request",
                    regionIds.isEmpty() ? "+" : regionIds.get(i),
                    groupIds.isEmpty() ? "+" : groupIds.get(i),
                    deviceIds.isEmpty() ? "+" : deviceIds.get(i)
            );

            topicList.add(new Topic(topicStr));
        }

        return topicList;
    }
}
