//package marchmadness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;

/**
 *  MarchMadnessGUI
 *
 * this class contains the buttons the user interacts
 * with and controls the actions of other objects
 *
 * @author Grant Osborn
 */
public class MarchMadnessGUI extends Application {


    //all the gui ellements
    private BorderPane root;
    private ToolBar toolBar;
    private ToolBar btoolBar;
    private Button simulate;
    private Button login;
    private Button scoreBoardButton;
    private Button viewBracketButton;
		private Button teamStats; // @author: Arjun Bott
    private Button clearButton;
    private Button resetButton;
    private Button finalizeButton;



    //Button that will let the user navigate to a page that would display all the scores of the teams in the brackets -Justin Lamberson
    private Button teamScores;

    //Button that would allow the user to logout of the account that is in use -Justin Lamberson
    private Button logout;


    //allows you to navigate back to division selection screen
    private Button back;


    private  Bracket startingBracket;
    //reference to currently logged in bracket
    private Bracket selectedBracket;
    private Bracket simResultBracket;


		private Bracket userBracket; // @author: Arjun Bott
    private ArrayList<Bracket> playerBrackets;
    private HashMap<String, Bracket> playerMap;



    private ScoreBoardTable scoreBoard;
    private TableView table;
    private BracketPane bracketPane;
    private GridPane loginP;
    private TournamentInfo teamInfo;


    //Placeholder constructor used to throw an exception if the file
    //TournamentTeamTable uses is not found
    private TournamentTeamTable teamStatsBoard = new TournamentTeamTable();

    public MarchMadnessGUI() throws IOException {
    }


    @Override
    public void start(Stage primaryStage) {
        //try to load all the files, if there is an error display it
        try {
            teamInfo = new TournamentInfo();
            startingBracket = new Bracket(teamInfo.loadStartingBracket());
            simResultBracket = new Bracket(teamInfo.loadStartingBracket());
        } catch (IOException ex) {
            showError(new Exception("Can't find " + ex.getMessage(), ex), true);
        }
        //deserialize stored brackets
        playerBrackets = loadBrackets();

        playerMap = new HashMap<>();
        addAllToMap();



        //the main layout container
        root = new BorderPane();
        scoreBoard = new ScoreBoardTable();
        table = scoreBoard.start();
        loginP = createLogin();
        CreateToolBars();

        //display login screen
        login();

        setActions();
        root.setTop(toolBar);
        root.setBottom(btoolBar);
        Scene scene = new Scene(root);
        primaryStage.setMaximized(true);

        primaryStage.setTitle("March Madness Bracket Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }



    /**
     * simulates the tournament
     * simulation happens only once and
     * after the simulation no more users can login
     */
    private void simulate() {
        //cant login and restart prog after simulate

        login.setDisable(true);
        logout.setDisable(true);
        simulate.setDisable(true);

       scoreBoardButton.setDisable(false);
       viewBracketButton.setDisable(false);

       teamInfo.simulate(simResultBracket);
       for(Bracket b: playerBrackets){
           scoreBoard.addPlayer(b, b.scoreBracket(simResultBracket));
       }

       /**
        * Displays the winner amongst the players
        * @author Jovan Rodriguez
        */
        scoreBoard.showWinner();

        displayPane(table);
    }

    /**
     * Displays the login screen
     *
     */
    private void login(){
        login.setDisable(true);
        logout.setDisable(false);
        simulate.setDisable(true);
        scoreBoardButton.setDisable(true);
        viewBracketButton.setDisable(true);
        btoolBar.setDisable(true);
        displayPane(loginP);
    }

     /**
     * Displays the score board
     *
     */
    private void scoreBoard(){
        displayPane(table);
    }

     /**
      * Displays Simulated Bracket
      *
      */
	@SuppressWarnings("static-access")
	private void viewBracket(){
       selectedBracket = simResultBracket;
       bracketPane = new BracketPane(selectedBracket);
			 bracketPane.colorBracket(userBracket); // @author: Arjun Bott

       GridPane full = bracketPane.getFullPane();
       full.setDisable(true);
       /**
        * @author: Carlos Rodriguez
        * Adding the previous implemented full pane
        * to a new Pane that is enabled to show a
        * tooltip based on the winner of the simulation
        */
       Pane aPane = new Pane(full);

       	/**
       	 * Tries retrieving info of the tournament, gets
       	 * the winner and installs the tooltip to the
       	 * Team instance.
       	 *
       	 * Catches an exception of input output and shows
       	 * an alert.
       	 */
		try {
			TournamentInfo info = new TournamentInfo();
			Team winner = info.getTeam(selectedBracket.getBracket().get(0).toString());
			Tooltip aTT = new Tooltip();
		    aTT.setText("Championship Winner: " + winner.getName() +
		    		"\nThe '" + winner.getNickname() + "' get crowned after" +
		    		"\na long and disputed tournament. Congrats!");
		    Tooltip.install(aPane, aTT);
		    displayPane(new ScrollPane(aPane));
		} catch (IOException e) {
			Alert anAlert = new Alert(Alert.AlertType.INFORMATION);
			anAlert.getDialogPane().setContentText("Information was not found!");
		}

    }
		/**
			* @author: Arjun Bott
			* adding a button for a page that displays team stats
		 */
		private void viewTeamStats() {
			try {
				TournamentInfo ti = new TournamentInfo();
				GridPane pane = new GridPane();
				pane.setHgap(15.0d);
				pane.setVgap(15.0d);
				pane.add(new Text("School Name"), 0, 0);
				pane.add(new Text("Team Name"), 1, 0);
				pane.add(new Text("National Rank"), 2, 0);
				pane.add(new Text("Offensive PPG"), 3, 0);
				pane.add(new Text("Defensive PPG"), 4, 0);
				pane.add(new Text("Description"), 5, 0);
				pane.add(new Text(""), 0, 1);
				int i = 2;
				for (Team team : ti.getTeams().values()) {
					// node, column index, row index
					// pane.add(new Text(team.get), );
					pane.add(new Text(team.getName()), 0, i);
					pane.add(new Text(team.getNickname()), 1, i);
					pane.add(new Text(team.getRanking() + ""), 2, i);
					pane.add(new Text(team.getOffensePPG() + ""), 3, i);
					pane.add(new Text(team.getDefensePPG() + ""), 4, i);
					pane.add(new Text(team.getInfo()), 5, i);
					i++;
				}


				displayPane(new ScrollPane(pane));
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

    /**
     * allows user to choose bracket
     *
     */
   private void chooseBracket() {
        //login.setDisable(true);
        btoolBar.setDisable(false);
        bracketPane = new BracketPane(selectedBracket);
        displayPane(bracketPane);

    }
    /**
     * resets current selected sub tree
     * for final4 reset Ro2 and winner
     */
    //Updated clear function so that it would correctly work in clearing the FULL bracket -Justin Lamberson
    private void clear(){
        if(bracketPane.getDisplayedSubtree() == 7){
            selectedBracket = new Bracket(startingBracket);
            bracketPane = new BracketPane(selectedBracket);
            displayPane(bracketPane);
        } else {
            bracketPane.clear();
            bracketPane = new BracketPane(selectedBracket);
            displayPane(bracketPane);
        }

    }

    /**
     * resets entire bracket
     */
    private void reset() {
        if(confirmReset()) {
            //horrible hack to reset
            selectedBracket = new Bracket(startingBracket);
            bracketPane = new BracketPane(selectedBracket);
            displayPane(bracketPane);
        }
    }

    private void finalizeBracket(){
       if(bracketPane.isComplete()){
           btoolBar.setDisable(true);
           bracketPane.setDisable(true);
           simulate.setDisable(false);
           login.setDisable(false);
           logout.setDisable(true);
           //save the bracket along with account info
           seralizeBracket(selectedBracket);

       }else{
            infoAlert("You can only finalize a bracket once it has been completed.");
            //go back to bracket section selection screen
            // bracketPane=new BracketPane(selectedBracket);
            displayPane(bracketPane);

       }
       //bracketPane=new BracketPane(selectedBracket);
    }


    /**
     * displays element in the center of the screen
     *
     * @param p must use a subclass of Pane for layout.
     * to be properly center aligned in  the parent node
     */
    private void displayPane(Node p){
        root.setCenter(p);
        BorderPane.setAlignment(p,Pos.CENTER);
    }

    /**
     * Creates toolBar and buttons.
     * adds buttons to the toolbar and saves global references to them
     */
    //added the logout and teamScores buttons to the toolbars -Justin Lamberson
    private void CreateToolBars(){
        toolBar  = new ToolBar();
        btoolBar  = new ToolBar();

        login = new Button("Login");
        simulate = new Button("Simulate");
        scoreBoardButton = new Button("ScoreBoard");
        viewBracketButton = new Button("View Simulated Bracket");
				teamStats = new Button("Team Statistics"); // @author: Arjun Bott new button instantiated for team statistics
        clearButton = new Button("Clear");
        resetButton = new Button("Reset");
        finalizeButton = new Button("Finalize");

        teamScores = new Button("Team Scores");

        logout = new Button("Logout");

        toolBar.getItems().addAll(
                createSpacer(),
                login,
                simulate,
                scoreBoardButton,
                viewBracketButton,
                createSpacer()
        );
        btoolBar.getItems().addAll(
                createSpacer(),
                clearButton,
                resetButton,
                finalizeButton,
                back=new Button("Choose Division"),
                teamScores,
                logout,

                back = new Button("Choose Division"),
								teamStats, //@author: Arjun Bott adding new button to toolbar
                createSpacer()
        );
    }

    //method attached to the logout button that will log the user out -Justin Lamberson
    //Bug: user and password field not clearing upon use -Justin Lamberson
    private void userLogout(){
        seralizeBracket(selectedBracket);
        login();
    }

    private void displayTeamInfo(){
        displayPane(teamStatsBoard.loadTable());
    }

   /**
    * sets the actions for each button
    */
    private void setActions(){
        login.setOnAction(e->login());
        simulate.setOnAction(e->simulate());
        scoreBoardButton.setOnAction(e->scoreBoard());
        viewBracketButton.setOnAction(e->viewBracket());
				teamStats.setOnAction(e->viewTeamStats());// @author: Arjun Bott
        clearButton.setOnAction(e->clear());
        resetButton.setOnAction(e->reset());
        finalizeButton.setOnAction(e->finalizeBracket());
        back.setOnAction(e->{
            bracketPane=new BracketPane(selectedBracket);
            displayPane(bracketPane);
        });
        logout.setOnAction(e->userLogout());
        teamScores.setOnAction(e->displayTeamInfo());
    }

    /**
     * Creates a spacer for centering buttons in a ToolBar
     */
    private Pane createSpacer() {
        Pane spacer = new Pane();
        HBox.setHgrow(
                spacer,
                Priority.SOMETIMES
        );
        return spacer;
    }


    private GridPane createLogin() {


        /*
        LoginPane
        Sergio and Joao
         */

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(5, 5, 5, 5));

        Text welcomeMessage = new Text("March Madness Login");
        loginPane.add(welcomeMessage, 0, 0, 2, 1);

        Label userName = new Label("User Name: ");
        loginPane.add(userName, 0, 1);

        /**
         * Changing enterUser from a TextField to a ComboBox for adding the dropdown menu
         */
        ComboBox<String> enterUser = new ComboBox<String>();
        enterUser.setEditable(true);

        comboadd(enterUser); //adding all saved player names to enterUser

        loginPane.add(enterUser, 1, 1);

        Label password = new Label("Password: ");
        loginPane.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        loginPane.add(passwordField, 1, 2);

        Button signButton = new Button("Sign in");
        loginPane.add(signButton, 1, 4);
        signButton.setDefaultButton(true);//added by matt 5/7, lets you use sign in button by pressing enter

        Label message = new Label();
        loginPane.add(message, 1, 5);

        /**
         *code to greet the player with a welcome message that gives the user a brief description of the game.
         * @author Jovan Rodriguez
         */
        Alert.AlertType type = Alert.AlertType.INFORMATION;
        Alert alert = new Alert(type, "");

        alert.initModality(Modality.APPLICATION_MODAL);

        alert.getDialogPane().setContentText("This program will allow you to simulate March Madness, the series of " +
                "college basketball games. If you have not already created a profile, entering a name and a" +
                " password shall create a new account with that name and password. If you already have an account," +
                " you can enter your information to come back to your saved bracket. Once in the game, you will" +
                " be able to create your own bracket and then finalize it to simulate the series.\n\nPress OK to" +
                " continue");

        alert.getDialogPane().setHeaderText("Welcome!");

        alert.showAndWait(); //Displays an alert window for the user

        signButton.setOnAction(event -> {

            // the name user enter
            String name = enterUser.getValue();
            // the password user enter
            String playerPass = passwordField.getText();

            if (playerMap.get(name) != null) {
                //check password of user

                Bracket tmpBracket = this.playerMap.get(name);

                String password1 = tmpBracket.getPassword();

                if (Objects.equals(password1, playerPass)) {
                    // load bracket
                    selectedBracket = playerMap.get(name);
                    userBracket = playerMap.get(name); // @author: Arjun Bott
                    chooseBracket();
                }else{
                   infoAlert("The password you have entered is incorrect!");
                }

            } else {
                //check for empty fields
                if(!name.equals("")&&!playerPass.equals("")){
                    //create new bracket
                    Bracket tmpPlayerBracket = new Bracket(startingBracket, name);
                    playerBrackets.add(tmpPlayerBracket);
                    tmpPlayerBracket.setPassword(playerPass);

                    playerMap.put(name, tmpPlayerBracket);
                    selectedBracket = tmpPlayerBracket;
                    //alert user that an account has been created
                    infoAlert("No user with the Username \""  + name + "\" exists. A new account has been created.");
                    chooseBracket();
                }
            }
        });

        return loginPane;
    }

    /**
     * addAllToMap
     * adds all the brackets to the map for login
     */
    private void addAllToMap(){
        for(Bracket b:playerBrackets){
            playerMap.put(b.getPlayerName(), b);
        }
    }

    /**
     * The Exception handler
     * Displays a error message to the user
     * and if the error is bad enough closes the program
     * @param fatal true if the program should exit. false otherwise
     */
    private void showError(Exception e,boolean fatal){
        String msg = e.getMessage();
        if(fatal){
            msg = msg + " \n\nthe program will now close";
            //e.printStackTrace();
        }
        Alert alert = new Alert(AlertType.ERROR,msg);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(420);
        alert.setTitle("Error");
        alert.setHeaderText("something went wrong");
        alert.showAndWait();
        if (fatal) {
            System.exit(666);
        }
    }

    /**
     * alerts user to the result of their actions in the login pane
     * @param msg the message to be displayed to the user
     */
    private void infoAlert(String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Prompts the user to confirm that they want
     * to clear all predictions from their bracket
     * @return true if the yes button clicked, false otherwise
     */
    private boolean confirmReset(){
        Alert alert = new Alert(AlertType.CONFIRMATION,
                "Are you sure you want to reset the ENTIRE bracket?",
                ButtonType.YES,  ButtonType.CANCEL);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }


    /**
     * Tayon Watson 5/5
     * seralizedBracket
     * @param B The bracket the is going to be seralized
     */
    private void seralizeBracket(Bracket B){
        FileOutputStream outStream = null;
        ObjectOutputStream out = null;
    try
    {
      outStream = new FileOutputStream(B.getPlayerName()+".ser");
      out = new ObjectOutputStream(outStream);
      out.writeObject(B);
      out.close();
    }
    catch(IOException e)
    {
      // Grant osborn 5/6 hopefully this never happens
      showError(new Exception("Error saving bracket \n"+e.getMessage(),e),false);
    }
    }
    /**
     * Tayon Watson 5/5
     * deseralizedBracket
     * @param filename of the seralized bracket file
     * @return deserialized bracket
     */
    private Bracket deseralizeBracket(String filename){
        Bracket bracket = null;
        FileInputStream inStream = null;
        ObjectInputStream in = null;
    try
    {
        inStream = new FileInputStream(filename);
        in = new ObjectInputStream(inStream);
        bracket = (Bracket) in.readObject();
        in.close();
    }catch (IOException | ClassNotFoundException e) {
      // Grant osborn 5/6 hopefully this never happens either
      showError(new Exception("Error loading bracket \n" + e.getMessage(),e),false);
    }
    return bracket;
    }

      /**
     * Tayon Watson 5/5
     * deseralizedBracket
     * @return deserialized bracket
     */
    private ArrayList<Bracket> loadBrackets()
    {
        ArrayList<Bracket> list=new ArrayList<Bracket>();
        File dir = new File(".");
        for (final File fileEntry : dir.listFiles()){
            String fileName = fileEntry.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);

            if (extension.equals("ser")){
                list.add(deseralizeBracket(fileName));
            }
        }
        return list;
    }

    /**
     * code from loadBrackets(), repurposed for adding to enterUser combobox
     * @author Jovan Rodriguez
     */
    public void comboadd(ComboBox<String> box){
        ArrayList<Bracket> list=new ArrayList<Bracket>();
        File dir = new File(".");
        for (final File fileEntry : dir.listFiles()){
            String fileName = fileEntry.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1);

            if (extension.equals("ser")){
                box.getItems().add(fileName.substring(0, fileName.length()-4));
            }
        }
    }

}
