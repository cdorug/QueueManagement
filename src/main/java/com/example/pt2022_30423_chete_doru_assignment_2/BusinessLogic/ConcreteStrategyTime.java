package com.example.pt2022_30423_chete_doru_assignment_2.BusinessLogic;

import com.example.pt2022_30423_chete_doru_assignment_2.Model.Server;
import com.example.pt2022_30423_chete_doru_assignment_2.Model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t) {
        int smallestWaitTime = Integer.MAX_VALUE;
        Server selectedServer = null;
        for(Server server: servers) {
            int currentServerWaitingPeriod = server.getWaitingPeriod().intValue();
            if(currentServerWaitingPeriod < smallestWaitTime) {
                selectedServer = server;
                smallestWaitTime = currentServerWaitingPeriod;
            }
        }
        if(selectedServer != null) {
            selectedServer.addTask(t);
        }
    }
}
