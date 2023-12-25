package com.example.gateway.controller;

import com.example.gateway.db.entity.Node;
import com.example.gateway.db.entity.Temperature;
import com.example.gateway.db.repository.NodeRepository;
import com.example.gateway.db.repository.TemperatureRepository;
import com.example.gateway.dto.DataPoint;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {
    private final NodeRepository nodeRepository;
    private final TemperatureRepository temperatureRepository;

    @GetMapping("")
    public String showAllNodes(Model model) {
        List<Node> nodes = nodeRepository.findAll();
        model.addAttribute("nodes", nodes);
        return "index";
    }

    @GetMapping("/node/{id}")
    public String showTemperatureForNode(Model model,
                                         @PathVariable("id") String nodeId) {
        Node node = nodeRepository.findById(nodeId).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "node_id không tồn tại"));
        List<Temperature> temperatures = temperatureRepository.findAllByNodeId(nodeId);
        model.addAttribute("node", node);

        var dataPoints = temperatures.stream()
            .map(temp -> new DataPoint(temp.getCreateTime().toEpochMilli(), temp.getValue()))
            .collect(
                Collectors.toList());
        model.addAttribute("dataPoints", dataPoints);
        return "chart";
    }
}
