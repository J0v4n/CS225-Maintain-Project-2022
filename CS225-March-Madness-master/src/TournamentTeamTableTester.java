import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;


//Tester used to verify that the table was working -Justin Lamberson
public class TournamentTeamTableTester extends Application {

    Stage window;

    TournamentTeamTable teamsInformation = new TournamentTeamTable();

    public TournamentTeamTableTester() throws IOException {
    }

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("Table Tester");

        VBox vBox = new VBox();
        vBox.getChildren().addAll(teamsInformation.loadTable());

        Scene scene = new Scene(vBox);
        window.setScene(scene);
        window.show();


    }



}
