package com.example.pt2022_30423_chete_doru_assignment_2.BusinessLogic;

import com.example.pt2022_30423_chete_doru_assignment_2.Model.Server;
import com.example.pt2022_30423_chete_doru_assignment_2.Model.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server selectedServer = null;
        int leastNumberOfClients = Integer.MAX_VALUE;
        for(Server server: servers) {
            int currentNumberOfClients = server.getCurrentNumberOfTasks();
            if(currentNumberOfClients < leastNumberOfClients) {
                leastNumberOfClients = currentNumberOfClients;
                selectedServer = server;
            }
        }
        if(selectedServer != null) {
            selectedServer.addTask(t);
        }
    }
}
