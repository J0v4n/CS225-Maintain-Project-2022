import javafx.collections.*;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


//class used to build the table for displaying all teams information -Justin Lamberson
public class TournamentTeamTable {
    ObservableList<Team> teams = FXCollections.observableArrayList();
    TableView<Team> table;

    public TournamentTeamTable() throws IOException {
        loadFromFile();

        //sets up the team name column
        TableColumn<Team, String> teamNameCol = new TableColumn<>("Team Name");
        teamNameCol.setMinWidth(200);
        teamNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        //sets up the team nickname column
        TableColumn<Team, String> teamNickNameCol = new TableColumn<>("Team Nickname");
        teamNickNameCol.setMinWidth(200);
        teamNickNameCol.setCellValueFactory(new PropertyValueFactory<>("nickname"));

        //sets up the ranking of the team column
        TableColumn<Team, Integer> teamRankCol = new TableColumn<>("Team rank");
        teamRankCol.setMinWidth(200);
        teamRankCol.setCellValueFactory(new PropertyValueFactory<>("ranking"));

        //sets up the team offensePPG column
        TableColumn<Team, Double> teamOffensePPGCol = new TableColumn<>("Offense PPG");
        teamOffensePPGCol.setMinWidth(200);
        teamOffensePPGCol.setCellValueFactory(new PropertyValueFactory<>("offensePPG"));

        //sets up the team defensePPG column
        TableColumn<Team, Double> teamDefensePPGCol = new TableColumn<>("Defense PPG");
        teamDefensePPGCol.setMinWidth(200);
        teamDefensePPGCol.setCellValueFactory(new PropertyValueFactory<>("defensePPG"));

        //sets up the information on the team column
        TableColumn<Team, String> teamDescription = new TableColumn<>("Team Description");
        teamDescription.setMinWidth(800);
        teamDescription.setCellValueFactory(new PropertyValueFactory<>("info"));

        table = new TableView<>();
        table.setItems(teams);
        table.getColumns().addAll(teamNameCol, teamNickNameCol, teamRankCol, teamOffensePPGCol, teamDefensePPGCol, teamDescription);

    }


    //method used to take all the information from the teaminfo.txt and convert
    //into an ObservableList, most of the method borrowed from
    //TournamentInfo.java- Justin Lamberson
    private void loadFromFile() throws IOException {

        String name;
        String nickname;
        String info;
        int ranking;
        double offensivePPG;
        double defensivePPG;


        try{
            InputStream u = getClass().getResourceAsStream("teamInfo.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(u));

            while((name = br.readLine()) != null){
                nickname = br.readLine();
                info = br.readLine();
                ranking = Integer.parseInt(br.readLine());
                offensivePPG = Double.parseDouble(br.readLine());
                defensivePPG = Double.parseDouble(br.readLine());

                Team newTeam = new Team(name, nickname, info, ranking, offensivePPG, defensivePPG); //creates team with info

                teams.add(newTeam);

                br.readLine();   //gets rid of empty line between team infos


            }

            br.close();

        }
        catch(IOException ioe) {
            throw ioe;
        }
    }

    public TableView loadTable(){
        return table;
    }

}
