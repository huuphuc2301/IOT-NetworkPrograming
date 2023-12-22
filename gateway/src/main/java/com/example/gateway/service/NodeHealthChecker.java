package com.example.gateway.service;

import com.example.gateway.db.entity.Node;
import com.example.gateway.db.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class NodeHealthChecker {
	private HashMap<String, Node> onlineNodes = new HashMap<>();

	@Autowired
	private NodeRepository nodeRepository;

	public void addNodeConnection(String nodeId) {
		onlineNodes.put(nodeId,new Node(nodeId,"Node " + nodeId,true));
	}
	@Scheduled(fixedRate = 10000)
	public void reloadNodeHealth() {
		List<Node> nodes = nodeRepository.findAll();
		for(Node node : nodes) {
			String nodeId = node.getId();
			if(onlineNodes.get(nodeId) == null) {
				node.setStatus(false);
				onlineNodes.put(nodeId, node);
			}
		}
		List<Node> saveNodes = new ArrayList<>(onlineNodes.values());
		onlineNodes.clear();
		nodeRepository.saveAll(saveNodes);
		System.out.println("Reloaded Node health");
	}
}
