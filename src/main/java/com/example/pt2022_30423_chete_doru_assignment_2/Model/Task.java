package com.example.pt2022_30423_chete_doru_assignment_2.Model;

public class Task {
    private int ID;
    private final int arrivalTime;
    private final int serviceTime;
    private int waitTime;

    public Task(int ID, int serviceTime, int arrivalTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getID() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public int getWaitTime() {
        return waitTime;
    }
}
