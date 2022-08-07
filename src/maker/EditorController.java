package maker;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class EditorController {

    @FXML private HBox hboxRoot;
    @FXML private ListView<Problem> lstProblems;
    
    @FXML private RowConstraints rowBallCount;
    @FXML private RowConstraints rowBallColour;
    @FXML private RowConstraints rowSolutionCount;
    @FXML private RowConstraints rowSolutionColour;
    @FXML private TextArea txtStatement;
    @FXML private TextField txtPotCount;
    @FXML private ChoiceBox<String> cbContainer;
    @FXML private ChoiceBox<String> cbBallType;
    @FXML private TextField txtBallCount;
    @FXML private Label lblBallCount;
    @FXML private TableView<BallColour> tblBallColour;
    @FXML private Label lblBallColour;
    @FXML private TableColumn<BallColour, String> tcolColour;
    @FXML private TableColumn<BallColour, Integer> tcolAmount;
    @FXML private TableView<KeyTerms> tblKeyTerms;
    @FXML private TableColumn<KeyTerms, String> tcolKeyTerm;
    @FXML private TableColumn<KeyTerms, Boolean> tcolRequired;
    @FXML private ChoiceBox<String> cbSolutionType;
    @FXML private Label lblSolutionPotCount;
    @FXML private TableView<SolutionPotCount> tblSolPotCount;
    @FXML private TableColumn<SolutionPotCount, Integer> tcolSolCntPot;
    @FXML private TableColumn<SolutionPotCount, Integer> tcolSolCntCount;
    @FXML private Label lblSolutionPotColour;
    @FXML private TableView<SolutionPotColour> tblSolPotColour;
    @FXML private TableColumn<SolutionPotColour, Integer> tcolSolColPot;
    @FXML private TableColumn<SolutionPotColour, Integer> tcolSolRedCount;
    @FXML private TableColumn<SolutionPotColour, Integer> tcolSolBlueCount;
    @FXML private TableColumn<SolutionPotColour, Integer> tcolSolGreenCount;
    @FXML private TableColumn<SolutionPotColour, Integer> tcolSolYellowCount;
    private ObservableList<Problem> problemListing;
    private Problem currentProblem = null;
    private ObservableList<String> containers;
    private ObservableList<String> ballTypes;
    private ObservableList<String> SolutionTypes;
    private ObservableList<KeyTerms> allKeyTerms;
    private ObservableList<SolutionPotCount> allSolutionPotCounts;
    private ObservableList<SolutionPotColour> allSolutionPotColours;

    @FXML void initialize() {
		problemListing = FXCollections.observableArrayList();
		lstProblems.setItems(problemListing);
		containers = FXCollections.observableArrayList("Tray", "Bag");
		cbContainer.setItems(containers);
		
		cbContainer.getSelectionModel().selectFirst();
		ballTypes = FXCollections.observableArrayList("BallCount", "BallColour");
		cbBallType.setItems(ballTypes);
		cbBallType.getSelectionModel().selectFirst();
		tcolColour.setCellValueFactory(new PropertyValueFactory<>("colour"));
		tcolAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
		allKeyTerms = FXCollections.observableArrayList(item -> 
        new Observable[] {item.filteredProperty()});
		filterKeyTerms(true);
		tcolKeyTerm.setCellValueFactory(new PropertyValueFactory<>("term"));
		tcolRequired.setCellValueFactory(new PropertyValueFactory<>("required"));
		tcolRequired.setCellFactory(col -> {
		    TableCell<KeyTerms, Boolean> cell = new TableCell<>();
		    cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    Node centreBox = createBooleanGraphic(newVal);
                    cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(centreBox));
                }
            });
		    return cell;
		});
		initKeyTerms();
		SolutionTypes = FXCollections.observableArrayList("PotCount", "PotColour");
		cbSolutionType.setItems(SolutionTypes);
		cbSolutionType.getSelectionModel().selectFirst();
		allSolutionPotCounts = FXCollections.observableArrayList();
		tblSolPotCount.setItems(allSolutionPotCounts);
		tcolSolCntPot.setCellValueFactory(new PropertyValueFactory<>("potId"));
		tcolSolCntCount.setCellValueFactory(new PropertyValueFactory<>("potCount"));
		allSolutionPotColours = FXCollections.observableArrayList();
		tblSolPotColour.setItems(allSolutionPotColours);
		tcolSolColPot.setCellValueFactory(new PropertyValueFactory<>("potId"));
		tcolSolRedCount.setCellValueFactory(new PropertyValueFactory<>("red"));
		tcolSolBlueCount.setCellValueFactory(new PropertyValueFactory<>("blue"));
		tcolSolGreenCount.setCellValueFactory(new PropertyValueFactory<>("green"));
		tcolSolYellowCount.setCellValueFactory(new PropertyValueFactory<>("yellow"));

		getAllProblemFiles();
		ProblemComparator pc = new ProblemComparator();
        FXCollections.sort(problemListing, pc);
        
        if (!problemListing.isEmpty()) {
	        Problem problem = lstProblems.getItems().get(0);
	        displayDetails(problem);
	        lstProblems.getSelectionModel().select(0);
        }
	}
	
    @FXML private void selectProblem(MouseEvent click) {
    	if (click.getClickCount() == 2) {
            Problem problem = lstProblems.getSelectionModel().getSelectedItem();
            displayDetails(problem);
         }
    }
    
    @FXML private void changeContainer(ActionEvent ev) {
        String selectedItem = cbContainer.getSelectionModel().getSelectedItem();
    }
    @FXML private void changeBallType(ActionEvent ev) {
    	String selectedItem = cbBallType.getSelectionModel().getSelectedItem();
    	if (selectedItem.equals("BallCount")) {
    		txtBallCount.setVisible(true);
    		txtBallCount.setMaxHeight(24);
    		lblBallCount.setVisible(true);
    		lblBallCount.setMaxHeight(24);
    		rowBallCount.setMinHeight(24);
    		rowBallCount.setMaxHeight(24);

    		tblBallColour.setVisible(false);
    		tblBallColour.setMaxHeight(0);
    		lblBallColour.setVisible(false);
    		lblBallColour.setMaxHeight(0);
    		rowBallColour.setMinHeight(0);
    		rowBallColour.setMaxHeight(0);
    	} else {
    		txtBallCount.setVisible(false);
    		txtBallCount.setMaxHeight(0);
    		lblBallCount.setVisible(false);
    		lblBallCount.setMaxHeight(0);
    		rowBallCount.setMinHeight(0);
    		rowBallCount.setMaxHeight(0);
    		
    		tblBallColour.setVisible(true);
    		tblBallColour.setMaxHeight(126);
    		lblBallColour.setVisible(true);
    		lblBallColour.setMaxHeight(24);
    		rowBallColour.setMinHeight(126);
    		rowBallColour.setMaxHeight(126);
    	}
    }
    @FXML private void changeSolutionType(ActionEvent ev) {
    	String selectedItem = cbSolutionType.getSelectionModel().getSelectedItem();
    	if (selectedItem.equals("PotCount")) {
    		tblSolPotCount.setVisible(true);
    		tblSolPotCount.setMaxHeight(102);
    		lblSolutionPotCount.setVisible(true);
    		lblSolutionPotCount.setMaxHeight(24);
    		rowSolutionCount.setMinHeight(102);
    		rowSolutionCount.setMaxHeight(102);

    		tblSolPotColour.setVisible(false);
    		tblSolPotColour.setMaxHeight(0);
    		lblSolutionPotColour.setVisible(false);
    		lblSolutionPotColour.setMaxHeight(0);
    		rowSolutionColour.setMinHeight(0);
    		rowSolutionColour.setMaxHeight(0);
    	} else {
    		tblSolPotCount.setVisible(false);
    		tblSolPotCount.setMaxHeight(0);
    		lblSolutionPotCount.setVisible(false);
    		lblSolutionPotCount.setMaxHeight(0);
    		rowSolutionCount.setMinHeight(0);
    		rowSolutionCount.setMaxHeight(0);
    		
    		tblSolPotColour.setVisible(true);
    		tblSolPotColour.setMaxHeight(126);
    		lblSolutionPotColour.setVisible(true);
    		lblSolutionPotColour.setMaxHeight(24);
    		rowSolutionColour.setMinHeight(126);
    		rowSolutionColour.setMaxHeight(126);
    	}
    }

    private void filterKeyTerms(boolean on) {
    	FilteredList<KeyTerms> filteredData = new FilteredList<>(allKeyTerms, t ->  t.isFiltered(on));
    	tblKeyTerms.setItems(filteredData);
    }
    
    private void initKeyTerms() {
		allKeyTerms.clear();
		allKeyTerms.add(new KeyTerms("pick()",false));
		allKeyTerms.add(new KeyTerms("pick(colour)",false));
		allKeyTerms.add(new KeyTerms("put(n)",false));
		allKeyTerms.add(new KeyTerms("look()",false));
		allKeyTerms.add(new KeyTerms("replace()",false));
		allKeyTerms.add(new KeyTerms("if",false));
		allKeyTerms.add(new KeyTerms("increment(n)",false));
		allKeyTerms.add(new KeyTerms("loop",false));
		allKeyTerms.add(new KeyTerms("loop until",false));
		allKeyTerms.add(new KeyTerms("loop while",false));
    }
    
    private void setAllKeyTermsOff() {
    	for (KeyTerms term: allKeyTerms) {
    		term.setRequired(false);
    	}
    }
    
    private Node createBooleanGraphic(boolean required){
        HBox graphicContainer = new HBox();
        graphicContainer.setAlignment(Pos.CENTER);
        Image img = null;
        try {
        	if (required) {
        		img = new Image(new FileInputStream("resources/green-checkmark.jpg"));
        	} else {
        		img = new Image(new FileInputStream("resources/red-cross.jpg"));
        	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(18);
        imageView.setPreserveRatio(true);
        graphicContainer.getChildren().add(imageView);
        return graphicContainer;
    }
    private void getAllProblemFiles() {
		File folder = new File("resources/");
		FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".json")
                		&&pathname.getName().toLowerCase().startsWith("problem");
            }
        };
		File[] listOfFiles = folder.listFiles(fileFilter);

		for (int i = 0; i < listOfFiles.length; i++) {
			JSONParser parser = new JSONParser();
	        try {
	        	Object obj = parser.parse(new FileReader("resources/"+listOfFiles[i].getName()));
	        	Problem problem = new Problem(listOfFiles[i].getName(), (JSONObject)obj);
	        	problemListing.add(problem);
	        } catch(Exception e) {
                e.printStackTrace();
	        }
		}
	}
    protected void resizeWidth(Number newWidth) {
    	display();
    }
    
    protected void resizeHeight(Number newHeight) {
    	display();
    }
    
    public void displayDetails(Problem problem) {
    	txtStatement.setText(problem.objectiveStatement);
		txtPotCount.setText(""+problem.potCount);
		if (problem.container == null) {
			cbContainer.getSelectionModel().selectFirst();
		} else {
			cbContainer.getSelectionModel().select(problem.container);
		}
		if (problem.ballDetails == null) {
			cbBallType.getSelectionModel().selectFirst();
		} else {
			cbBallType.getSelectionModel().select(problem.ballDetails);
		}
		txtBallCount.setText(""+problem.ballCount);
		if (problem.ballColours != null) {
			tblBallColour.getItems().clear();
			for (BallColour bc : problem.ballColours) {
		 		tblBallColour.getItems().add(bc);			
			}
		}
		setAllKeyTermsOff();
		for (KeyTerms term: allKeyTerms) {
			if (problem.keyTerms.contains(term.getTerm())) {
				term.setRequired(true);
			}
    	}
		filterKeyTerms(true);
		tblKeyTerms.refresh();
		if (problem.solutionType == null) {
			cbSolutionType.getSelectionModel().selectFirst();
		} else {
			cbSolutionType.getSelectionModel().select(problem.solutionType);
		}
		if (problem.solutionType.equals("PotCount")) {
			allSolutionPotCounts.clear();
			int potCount = 1;
			for (Object cnt: problem.solutionPotCount) {
				SolutionPotCount spc = new SolutionPotCount(potCount, ((Long)cnt).intValue());
				potCount++;
				allSolutionPotCounts.add(spc);
			}
		} else {
			allSolutionPotColours.clear();
			int potCount = 1;
			for (Object json: problem.solutionPotCount) {
				int red = 0;
				int blue = 0;
				int green = 0;
				int yellow = 0;
					JSONObject colour = (JSONObject)json;
					if (colour.containsKey("Red")) {
						red = ((Long)colour.get("Red")).intValue();
					}
					if (colour.containsKey("Blue")) {
						blue = ((Long)colour.get("Blue")).intValue();
					}
					if (colour.containsKey("Green")) {
						green = ((Long)colour.get("Green")).intValue();
					}
					if (colour.containsKey("Yellow")) {
						yellow = ((Long)colour.get("Yellow")).intValue();
					}
				SolutionPotColour spc = new SolutionPotColour(potCount, red, blue, green, yellow);
				potCount++;
				allSolutionPotColours.add(spc);
			}
		}
		
		
// 		tblBallColour.getItems().add(new BallColour("Blue", 4));
// 		tblBallColour.getItems().add(new BallColour("Green", 3));
// 		tblBallColour.getItems().add(new BallColour("Yellow", 2));
//    	 lblBallColour.setText(problem.ballColourMap);
//    	 lblKeyTerms.setText(problem.keyTerms);
//    	 lblSolutionType.setText(problem.solution);
//    	 lblSolutionPotCount.setText(""+problem.objectiveStatement);
//    	 lblSolutionPotColour.setText(problem.objectiveStatement);
    }
    
    public void display() {
    	if (currentProblem == null) {
    		PuzzleMaker.primaryStage.setTitle("Edit and View Puzzles");
    	} else {
    		PuzzleMaker.primaryStage.setTitle("Edit and View Puzzles - " + currentProblem );
    	}
    }

}
