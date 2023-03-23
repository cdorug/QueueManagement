package com.example.pt2022_30423_chete_doru_assignment_2.BusinessLogic;

import com.example.pt2022_30423_chete_doru_assignment_2.Model.Server;
import com.example.pt2022_30423_chete_doru_assignment_2.Model.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationManager implements Runnable {

    FileWriter logOfEvents;

    @FXML private Label log_text;
    @FXML private TextField no_of_clients;
    @FXML private TextField no_of_queues;
    @FXML private TextField min_arrival_time;
    @FXML private TextField max_arrival_time;
    @FXML private TextField min_service_time;
    @FXML private TextField max_service_time;
    @FXML private TextField simulation_time;

    private Scheduler scheduler;
    private ArrayList<Task> clients;
    private SelectionPolicy selectionPolicy;

    private int numberOfClients;
    private int numberOfQueues;
    private int minimumServiceTime;
    private int maximumServiceTime;
    private int minimumArrivalTime;
    private int maximumArrivalTime;
    private int simulationInterval;

    public SimulationManager() throws FileNotFoundException {}


    private void generateNRandomTasks() {
        Random randomGenerator = new Random();
        int randomServiceTime, randomArrivalTime;
        for(int i = 0; i < numberOfClients; i++) {
            randomServiceTime = randomGenerator.nextInt(maximumServiceTime - minimumServiceTime + 1) + minimumServiceTime;
            randomArrivalTime = randomGenerator.nextInt(maximumArrivalTime - minimumArrivalTime + 1) + minimumArrivalTime;
            Task task = new Task(i+1, randomServiceTime, randomArrivalTime);
            clients.add(task);
        }
        Collections.sort(clients, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
            }
        });
        appendLogWithoutThread("Generated clients: \n");
        for(Task client: clients) {
            appendLogWithoutThread("Client ID: " + client.getID() + " Service time: " + client.getServiceTime() + " Arrival time: " + client.getArrivalTime() + "\n");
        }
    }

    private int getSimulationParameters() throws IOException {
        log_text.setText(""); // clear the event log
        logOfEvents = new FileWriter("log.txt"); // create log text file
        try {
            numberOfClients = Integer.parseInt(no_of_clients.getText());
            numberOfQueues = Integer.parseInt(no_of_queues.getText());
            minimumServiceTime = Integer.parseInt(min_service_time.getText());
            maximumServiceTime = Integer.parseInt(max_service_time.getText());
            minimumArrivalTime = Integer.parseInt(min_arrival_time.getText());
            maximumArrivalTime = Integer.parseInt(max_arrival_time.getText());
            simulationInterval = Integer.parseInt(simulation_time.getText());
        } catch(NumberFormatException exception) {
            log_text.setText("Error! Invalid input data.");
            return -1;
        }
        // now check if inputs are positive and valid, i.e. min can't be bigger than max
        if(!((numberOfClients >= 0) && (numberOfQueues >= 0) && (minimumServiceTime >= 0) && (maximumServiceTime >= 0) &&
                (minimumArrivalTime >= 0) && (maximumArrivalTime >= 0) && (simulationInterval > 0))) {
            log_text.setText("Error! Input data must be positive integers.");
            return -1;
        }
        if(!((minimumArrivalTime < maximumArrivalTime)
            && (minimumServiceTime  < maximumServiceTime))) {
            log_text.setText("Error! Minimum cannot be greater than maximum.");
            return -1;
        }
        this.clients = new ArrayList<>();
        return 1;
    }

    @FXML
    private void startSimulation() throws IOException {
        int status = getSimulationParameters();
        if(status == 1) {
            scheduler = new Scheduler(numberOfQueues, numberOfClients);
            generateNRandomTasks();
            appendLogWithoutThread("Simulation has started\n");
            logOfEvents.write("Simulation has started\n");
            Thread t = new Thread(this);
            t.start();
        }
    }

    private void appendLogWithoutThread(String text) {
        log_text.setText(log_text.getText() + text);
    }

    private void appendLog(String text) {
        Platform.runLater(() -> log_text.setText(log_text.getText() + text));
    }

    private void writeLog(String text) {
        Platform.runLater(() -> log_text.setText(text));
    }

    @Override
    public void run() {
        try {
            Thread.sleep(4000); // sleep for a few seconds to be able to see the generated clients in the UI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int currentTime = 0;
        int maxClientsServedAtOneMoment = Integer.MIN_VALUE;
        int peakHour = 0;
        while(currentTime <= simulationInterval) {
            ListIterator<Task> iterator = clients.listIterator();
            while(iterator.hasNext()) {
                Task currentClient = iterator.next();
                if(currentClient.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(currentClient);
                    iterator.remove();
                }
            }
            List<Server> servers = scheduler.getServers();
            // update UI frame
            int finalCurrentTime = currentTime;
            try {
                writeLog("Time: " + finalCurrentTime + "\n" + "Waiting clients:");
                logOfEvents.write("Time: " + finalCurrentTime + "\n");
                logOfEvents.append("Waiting clients: ");
                for (Task client : clients) {
                    appendLog("(" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ");
                    logOfEvents.write("(" + client.getID() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ");
                }
                appendLog("\n");
                logOfEvents.write("\n");
                int numberOfClientsCurrentlyBeingServed = 0;
                for (Server server : servers) {
                    appendLog("Queue " + server.getQueueID() + ": ");
                    logOfEvents.write("Queue " + server.getQueueID() + ": ");
                    if (server.hasNoClients()) {
                        appendLog("closed\n");
                        logOfEvents.write("closed\n");
                    } else {
                        appendLog(server.printClientsOnServer());
                        appendLog("\n");
                        logOfEvents.write(server.printClientsOnServer() + "\n");
                        numberOfClientsCurrentlyBeingServed += server.getCurrentNumberOfTasks();
                    }
                }
                if(numberOfClientsCurrentlyBeingServed > maxClientsServedAtOneMoment) {
                    maxClientsServedAtOneMoment = numberOfClientsCurrentlyBeingServed;
                    peakHour = currentTime;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> appendLog("\n"));
            currentTime ++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            List<Server> servers = scheduler.getServers();
            double totalAverageWaitTime = 0;
            double totalAverageServiceTime = 0; // from moment getting into queue to leaving
            for(Server server: servers) {
                totalAverageWaitTime += server.getAverageWaitTime();
                totalAverageServiceTime += server.getAverageServiceTime();
            }
            totalAverageWaitTime = totalAverageWaitTime / numberOfQueues;
            totalAverageServiceTime = totalAverageServiceTime / numberOfQueues;
            writeLog("Results are: \n");
            appendLog("Peak hour: " + peakHour + "\n");
            appendLog("Average waiting time: " + totalAverageWaitTime + "\n");
            appendLog("Average service time: " + totalAverageServiceTime + "\n");
            logOfEvents.write("Peak hour: " + peakHour + "\n");
            logOfEvents.write("Average waiting time: " + totalAverageWaitTime + "\n");
            logOfEvents.write("Average service time: " + totalAverageServiceTime + "\n");
            logOfEvents.close();
        } catch (IOException e) {
            System.out.println("IO exception");
        }
        try {
            Thread.sleep(5000); // sleep for 5 secs for viewing the results
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}