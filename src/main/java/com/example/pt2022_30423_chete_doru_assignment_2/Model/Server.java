package com.example.pt2022_30423_chete_doru_assignment_2.Model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private final int QueueID;
    private final BlockingQueue<Task> tasks;
    private final AtomicInteger waitingPeriod;
    private double averageWaitTime;
    private double averageServiceTime;

    public Server(int ID, int maxTasksPerServer) {
        QueueID = ID;
        tasks = new ArrayBlockingQueue<>(maxTasksPerServer);
        waitingPeriod = new AtomicInteger(0);
        averageWaitTime = 0;
        averageServiceTime = 0;
    }

    public void addTask(Task newTask) {
        newTask.setWaitTime(newTask.getServiceTime() + waitingPeriod.intValue());
        this.tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public int getCurrentNumberOfTasks() {
        return tasks.size();
    }

    public boolean hasNoClients() {
        if(tasks.size() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getQueueID() {
        return QueueID;
    }

    public double getAverageWaitTime() {
        return averageWaitTime;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public String printClientsOnServer() {
        String clients = "";
        for(Task client: tasks) {
            clients = clients.concat("(" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ");
        }
        return clients;
    }

    @Override
    public void run() {
        int clientsServed = 0;
        int overallWaitTime = 0;
        int overallServiceTime = 0;
        while(true) {
            try {
                if (!tasks.isEmpty()) {
                    Task task = tasks.peek();
                    clientsServed++;
                    overallWaitTime += (task.getWaitTime() - task.getServiceTime());
                    overallServiceTime += task.getWaitTime();
                    averageWaitTime = (double) overallWaitTime / clientsServed;
                    averageServiceTime = (double) overallServiceTime / clientsServed;
                    for(int i = 0; i < task.getServiceTime(); i ++) {
                        waitingPeriod.getAndDecrement();
                        Thread.sleep(1000);
                    }
                    tasks.remove();
                }
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
