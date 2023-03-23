module com.example.pt2022_30423_chete_doru_assignment_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pt2022_30423_chete_doru_assignment_2 to javafx.fxml;
    exports com.example.pt2022_30423_chete_doru_assignment_2;
    exports com.example.pt2022_30423_chete_doru_assignment_2.BusinessLogic;
    opens com.example.pt2022_30423_chete_doru_assignment_2.BusinessLogic to javafx.fxml;
}