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
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.util.converter.IntegerStringConverter;

public class EditorController {

    @FXML private HBox hboxRoot;
    @FXML private ListView<Problem> lstProblems;
    
    @FXML private Button btnEdit;
    @FXML private Button btnClone;
    @FXML private Button btnNew;
    @FXML private Button btnInsert;
    @FXML private Button btnAppend;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnUp;
    @FXML private Button btnDown;
    
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
    @FXML private TableView<KeyTerm> tblKeyTerms;
    @FXML private TableColumn<KeyTerm, String> tcolKeyTerm;
    @FXML private TableColumn<KeyTerm, Boolean> tcolRequired;
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
    
    private boolean readOnlyMode;
    private ObservableList<Problem> problemListing;
    private Problem currentProblem = null;
    private ObservableList<String> containers;
    private ObservableList<String> ballTypes;
    private ObservableList<String> SolutionTypes;
    private ObservableList<BallColour> allBallColours;
    private ObservableList<KeyTerm> allKeyTerms;
    private ObservableList<SolutionPotCount> allSolutionPotCounts;
    private ObservableList<SolutionPotColour> allSolutionPotColours;

    @FXML void initialize() {
    	readOnlyMode = true;
    	
		problemListing = FXCollections.observableArrayList();
		lstProblems.setItems(problemListing);
		containers = FXCollections.observableArrayList("Tray", "Bag");
		cbContainer.setItems(containers);
		
		cbContainer.getSelectionModel().selectFirst();
		ballTypes = FXCollections.observableArrayList("BallCount", "BallColour");
		cbBallType.setItems(ballTypes);
		cbBallType.getSelectionModel().selectFirst();
		allBallColours = FXCollections.observableArrayList(item -> 
        new Observable[] {item.filteredProperty()});
		initBallColours();
		filterBallColours(true);
		tcolColour.setCellValueFactory(new PropertyValueFactory<>("colour"));
		tcolAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
		tcolAmount.setCellFactory(TextFieldTableCell.<BallColour, Integer>forTableColumn(new IntegerStringConverter()));
		tcolAmount.setOnEditCommit(
			    new EventHandler<CellEditEvent<BallColour, Integer>>() {
			        @Override
			        public void handle(CellEditEvent<BallColour, Integer> t) {
			            ((BallColour) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setAmount(t.getNewValue());
			        }
			    }
			);
		allKeyTerms = FXCollections.observableArrayList(item -> 
        new Observable[] {item.filteredProperty()});
		filterKeyTerms(true);
		tcolKeyTerm.setCellValueFactory(new PropertyValueFactory<>("term"));
		tcolRequired.setCellFactory(col -> {
		    TableCell<KeyTerm, Boolean> cell = new TableCell<>();
		    cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    Node centreBox = createBooleanGraphic(newVal);
                    cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(centreBox));
                }
            });
		    return cell;
		});
		tcolRequired.setCellValueFactory(cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().isRequired()));
		tcolRequired.setOnEditCommit(
			    new EventHandler<CellEditEvent<KeyTerm, Boolean>>() {
			        @Override
			        public void handle(CellEditEvent<KeyTerm, Boolean> t) {
			            ((KeyTerm) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setRequired(t.getNewValue());
			        }
			    }
			);
		initKeyTerms();
		SolutionTypes = FXCollections.observableArrayList("PotCount", "PotColour");
		cbSolutionType.setItems(SolutionTypes);
		cbSolutionType.getSelectionModel().selectFirst();
		allSolutionPotCounts = FXCollections.observableArrayList();
		tblSolPotCount.setItems(allSolutionPotCounts);
		tcolSolCntPot.setCellValueFactory(new PropertyValueFactory<>("potId"));
		tcolSolCntCount.setCellValueFactory(new PropertyValueFactory<>("potCount"));
		tcolSolCntCount.setCellFactory(TextFieldTableCell.<SolutionPotCount, Integer>forTableColumn(new IntegerStringConverter()));
		tcolSolCntCount.setOnEditCommit(
		    new EventHandler<CellEditEvent<SolutionPotCount, Integer>>() {
		        @Override
		        public void handle(CellEditEvent<SolutionPotCount, Integer> t) {
		            ((SolutionPotCount) t.getTableView().getItems().get(
		                t.getTablePosition().getRow())
		                ).setPotCount(t.getNewValue());
		        }
		    }
		);

		allSolutionPotColours = FXCollections.observableArrayList();
		tblSolPotColour.setItems(allSolutionPotColours);
		tcolSolColPot.setCellValueFactory(new PropertyValueFactory<>("potId"));
		tcolSolRedCount.setCellValueFactory(new PropertyValueFactory<>("red"));
		tcolSolRedCount.setCellFactory(TextFieldTableCell.<SolutionPotColour, Integer>forTableColumn(new IntegerStringConverter()));
		tcolSolRedCount.setOnEditCommit(
			    new EventHandler<CellEditEvent<SolutionPotColour, Integer>>() {
			        @Override
			        public void handle(CellEditEvent<SolutionPotColour, Integer> t) {
			            ((SolutionPotColour) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setRed(t.getNewValue());
			        }
			    }
			);
		tcolSolBlueCount.setCellValueFactory(new PropertyValueFactory<>("blue"));
		tcolSolBlueCount.setCellFactory(TextFieldTableCell.<SolutionPotColour, Integer>forTableColumn(new IntegerStringConverter()));
		tcolSolBlueCount.setOnEditCommit(
			    new EventHandler<CellEditEvent<SolutionPotColour, Integer>>() {
			        @Override
			        public void handle(CellEditEvent<SolutionPotColour, Integer> t) {
			            ((SolutionPotColour) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setBlue(t.getNewValue());
			        }
			    }
			);
		tcolSolGreenCount.setCellValueFactory(new PropertyValueFactory<>("green"));
		tcolSolGreenCount.setCellFactory(TextFieldTableCell.<SolutionPotColour, Integer>forTableColumn(new IntegerStringConverter()));
		tcolSolGreenCount.setOnEditCommit(
			    new EventHandler<CellEditEvent<SolutionPotColour, Integer>>() {
			        @Override
			        public void handle(CellEditEvent<SolutionPotColour, Integer> t) {
			            ((SolutionPotColour) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setGreen(t.getNewValue());
			        }
			    }
			);
		tcolSolYellowCount.setCellValueFactory(new PropertyValueFactory<>("yellow"));
		tcolSolYellowCount.setCellFactory(TextFieldTableCell.<SolutionPotColour, Integer>forTableColumn(new IntegerStringConverter()));
		tcolSolYellowCount.setOnEditCommit(
			    new EventHandler<CellEditEvent<SolutionPotColour, Integer>>() {
			        @Override
			        public void handle(CellEditEvent<SolutionPotColour, Integer> t) {
			            ((SolutionPotColour) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setYellow(t.getNewValue());
			        }
			    }
			);

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
    	if (click.getClickCount() >= 1) {
    		if (readOnlyMode) {
	            Problem problem = lstProblems.getSelectionModel().getSelectedItem();
	            displayDetails(problem);
    		}
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

    @FXML private void onEdit(ActionEvent event){
    	displayButtons(true, true);
    	readOnlyMode = false;
    	displayEditControls();
    }

    @FXML private void onClone(ActionEvent event){
    	displayButtons(true, false);
    	readOnlyMode = false;
    	System.out.println(event);
    }

    @FXML private void onNew(ActionEvent event){
    	displayButtons(true, false);
    	readOnlyMode = false;
    	System.out.println(event);
    }

    @FXML private void onUp(ActionEvent event){
    	System.out.println(event);
    }

    @FXML private void onDown(ActionEvent event){
    	System.out.println(event);
    }

    @FXML private void onInsert(ActionEvent event){
    	displayButtons(false, false);
    	readOnlyMode = true;
    	System.out.println(event);
    }

    @FXML private void onAppend(ActionEvent event){
    	displayButtons(false, false);
    	readOnlyMode = true;
    	System.out.println(event);
    }

    @FXML private void onSave(ActionEvent event){
    	displayButtons(false, false);
    	readOnlyMode = true;
    	System.out.println(event);
    }

    @FXML private void onCancel(ActionEvent event){
    	displayButtons(false, false);
    	readOnlyMode = true;
    	displayEditControls();
    }

    private void displayButtons(boolean hide, boolean edit) {
    	btnEdit.setVisible(!hide);
    	btnClone.setVisible(!hide);
    	btnNew.setVisible(!hide);
    	btnUp.setVisible(!hide);
    	btnDown.setVisible(!hide);
    	btnCancel.setVisible(hide);
    	
    	if (hide) {
    		btnEdit.setMinWidth(0);
    		btnEdit.setMaxWidth(0);
    		btnClone.setMinWidth(0);
    		btnClone.setMaxWidth(0);
    		btnNew.setMinWidth(0);
    		btnNew.setMaxWidth(0);
    		btnUp.setMinWidth(0);
    		btnUp.setMaxWidth(0);
    		btnDown.setMinWidth(0);
    		btnDown.setMaxWidth(0);
    		btnCancel.setMinWidth(60);
    		if (edit) {
    	    	btnSave.setVisible(hide);
    	    	btnInsert.setVisible(!hide);
    	    	btnAppend.setVisible(!hide);
	    		btnSave.setMinWidth(60);
	    		btnInsert.setMinWidth(0);
	    		btnInsert.setMaxWidth(0);
	    		btnAppend.setMinWidth(0);
	    		btnAppend.setMaxWidth(0);
    		} else {
    	    	btnSave.setVisible(!hide);
    	    	btnInsert.setVisible(hide);
    	    	btnAppend.setVisible(hide);
        		btnSave.setMinWidth(0);
        		btnSave.setMaxWidth(0);
	    		btnInsert.setMinWidth(60);
	    		btnAppend.setMinWidth(60);
    		}
    	} else {
	    	btnSave.setVisible(hide);
	    	btnCancel.setVisible(hide);
	    	btnInsert.setVisible(hide);
	    	btnAppend.setVisible(hide);
    		btnSave.setMinWidth(0);
    		btnSave.setMaxWidth(0);
    		btnCancel.setMinWidth(0);
    		btnCancel.setMaxWidth(0);    			
    		btnInsert.setMinWidth(0);
    		btnInsert.setMaxWidth(0);
    		btnAppend.setMinWidth(0);
    		btnAppend.setMaxWidth(0);

    		btnEdit.setMinWidth(45);
    		btnClone.setMinWidth(50);
    		btnNew.setMinWidth(45);
    		btnUp.setMinWidth(30);
    		btnDown.setMinWidth(30);
    		btnSave.setMinWidth(0);
    		btnSave.setMaxWidth(0);
    		btnCancel.setMinWidth(0);
    		btnCancel.setMaxWidth(0);
    	}
    }
    
    private void filterBallColours(boolean on) {
    	FilteredList<BallColour> filteredData = new FilteredList<>(allBallColours, t ->  t.isFiltered(on));
    	tblBallColour.setItems(filteredData);    	
    }
    
    private void filterKeyTerms(boolean on) {
    	FilteredList<KeyTerm> filteredData = new FilteredList<>(allKeyTerms, t ->  t.isFiltered(on));
    	tblKeyTerms.setItems(filteredData);
    }
    
    private void initBallColours() {
    	allBallColours.clear();
    	allBallColours.add(new BallColour("Red", 0));
    	allBallColours.add(new BallColour("Blue", 0));
    	allBallColours.add(new BallColour("Green", 0));
    	allBallColours.add(new BallColour("Yellow", 0));
    }
    
    private void initKeyTerms() {
		allKeyTerms.clear();
		allKeyTerms.add(new KeyTerm("pick()",false));
		allKeyTerms.add(new KeyTerm("pick(colour)",false));
		allKeyTerms.add(new KeyTerm("put(n)",false));
		allKeyTerms.add(new KeyTerm("look()",false));
		allKeyTerms.add(new KeyTerm("replace()",false));
		allKeyTerms.add(new KeyTerm("if",false));
		allKeyTerms.add(new KeyTerm("increment(n)",false));
		allKeyTerms.add(new KeyTerm("loop",false));
		allKeyTerms.add(new KeyTerm("loop until",false));
		allKeyTerms.add(new KeyTerm("loop while",false));
    }
    
    private void setAllBallColoursOff() {
    	for (BallColour bc: allBallColours) {
    		bc.setAmount(0);
    	}
    }
    private void setAllKeyTermsOff() {
    	for (KeyTerm term: allKeyTerms) {
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
    	currentProblem = problem;
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
		setAllBallColoursOff();
		if (problem.ballColours != null) {
			for (BallColour bc: allBallColours) {
				for (BallColour co: problem.ballColours) {
					if (bc.getColour().equals(co.getColour())) {
						bc.setAmount(co.getAmount());
						break;
					}
				}
	    	}
		}
		filterBallColours(true);
		setAllKeyTermsOff();
		for (KeyTerm term: allKeyTerms) {
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
		display();
    } // end of method displayDetails
    
    private void displayEditControls() {
    	txtStatement.setEditable(!readOnlyMode);
		txtPotCount.setEditable(!readOnlyMode);
		cbContainer.setDisable(readOnlyMode);
		cbBallType.setDisable(readOnlyMode);
		txtBallCount.setEditable(!readOnlyMode);
		tblBallColour.setEditable(!readOnlyMode);
		tcolAmount.setEditable(!readOnlyMode);
		tblKeyTerms.setEditable(!readOnlyMode);
		tblKeyTerms.refresh();
		tcolRequired.setEditable(!readOnlyMode);
		tblSolPotCount.setEditable(!readOnlyMode);
		tblSolPotColour.setEditable(!readOnlyMode);
		filterBallColours(readOnlyMode);
		filterKeyTerms(readOnlyMode);
		if (readOnlyMode) {
			tcolRequired.setCellFactory(col -> {
			    TableCell<KeyTerm, Boolean> cell = new TableCell<>();
			    cell.itemProperty().addListener((obs, old, newVal) -> {
	                if (newVal != null) {
	                    Node centreBox = createBooleanGraphic(newVal);
	                    cell.graphicProperty().bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(centreBox));
	                }
	            });
			    return cell;
			});
		} else {
			tcolRequired.setCellFactory( col -> {
			    CheckBox checkBox = new CheckBox();
			    TableCell<KeyTerm, Boolean> cell = new TableCell<KeyTerm, Boolean>() {

			        @Override
			        protected void updateItem(Boolean item, boolean empty) {

			            super.updateItem(item, empty);
			            if (empty || item == null) 
			                setGraphic(null);
			            else {
			                setGraphic(checkBox);
			                checkBox.setSelected(item);
			            }
			        }
			    };

			    checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			    	((KeyTerm) cell.getTableRow().getItem()).setRequired(!checkBox.isSelected());
			    });

			    checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			        if(event.getCode() == KeyCode.SPACE) {
			        	((KeyTerm) cell.getTableRow().getItem()).setRequired(!checkBox.isSelected());
			        }
			    });

			    cell.setAlignment(Pos.CENTER);
			    cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			    return cell;
			});
		}
    } // end method displayEditControls
    
//    private void displayBallColour() {
//    	tblBallColour.getItems().clear();
//    	filter(readOnlyMode);
//		if (currentProblem.ballColours != null) {
//			tblBallColour.getItems().clear();
//			for (BallColour bc : currentProblem.ballColours) {
//		 		tblBallColour.getItems().add(bc);			
//			}
//		}
//    } // end of displayBallColour
    
    public void display() {
    	if (currentProblem == null) {
    		PuzzleMaker.primaryStage.setTitle("Edit and View Puzzles");
    	} else {
    		PuzzleMaker.primaryStage.setTitle("Edit and View Puzzles - " + currentProblem );
    	}
    }

}
