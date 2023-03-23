package com.example.pt2022_30423_chete_doru_assignment_2.BusinessLogic;

import com.example.pt2022_30423_chete_doru_assignment_2.Model.Server;
import com.example.pt2022_30423_chete_doru_assignment_2.Model.Task;

import java.util.List;

public interface Strategy {
    public void addTask(List<Server> servers, Task t);
}
