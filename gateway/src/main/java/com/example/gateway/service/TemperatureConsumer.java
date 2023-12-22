package com.example.gateway.service;

import com.example.gateway.db.entity.Node;
import com.example.gateway.db.entity.Temperature;
import com.example.gateway.db.repository.NodeRepository;
import com.example.gateway.db.repository.TemperatureRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemperatureConsumer {
    private final TemperatureRepository temperatureRepository;
    private final NodeRepository nodeRepository;

    public void consumeTemperatureMessage(String messageContent) {
        String[] contents = messageContent.split(" ");
        String nodeId = contents[0];
        Integer temperatureValue = Integer.valueOf(contents[1]);
        Instant createTime = Instant.parse(contents[2]);

        Node node = nodeRepository.findById(nodeId)
            .orElse(new Node(nodeId, "node " + nodeId, true));
        Temperature temperature = new Temperature();
        temperature.setNode(node);
        temperature.setValue(temperatureValue);
        temperature.setCreateTime(createTime);
        temperatureRepository.saveAndFlush(temperature);
    }
}
