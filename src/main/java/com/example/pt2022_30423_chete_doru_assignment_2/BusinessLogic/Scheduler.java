package com.example.pt2022_30423_chete_doru_assignment_2.BusinessLogic;

import com.example.pt2022_30423_chete_doru_assignment_2.Model.Server;
import com.example.pt2022_30423_chete_doru_assignment_2.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Scheduler {
    private final ArrayList<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        servers = new ArrayList<>();
        this.maxTasksPerServer = maxTasksPerServer;
        setStrategy(SelectionPolicy.SHORTEST_TIME);
        for(int i = 0; i < maxNoServers; i ++) {
            Server server = new Server(i+1, maxTasksPerServer);
            servers.add(server);
            Thread t = new Thread(server);
            t.start();
        }
    }

    public void setStrategy(SelectionPolicy policy) {
        if(policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task t) {
        strategy.addTask(servers, t);
    }

    public List<Server> getServers() {
        return servers;
    }
}
