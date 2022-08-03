package application;

import application.keyword.CommandTerm;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragNDropCommandTermCell extends ListCell<CommandTerm> {

	private static final String DEFAULT_BACKGROUND = "derive(-fx-base,80%)";
    private static final String HOVER_BACKGROUND = "derive(CornflowerBlue, 40%)";
    private static final String LINKED_BACKGROUND = "derive(CornflowerBlue, 80%)";
    private static final String ERROR_BACKGROUND = "derive(OrangeRed, 50%)";
    private static final String RUNNING_BACKGROUND = "derive(DeepSkyBlue, 50%)";
    
	public DragNDropCommandTermCell (PuzzleController pc) {
		ListCell<CommandTerm> thisCell = this;
		
		setOnMouseEntered(event -> {
			if (getItem() == null) { return;}
	        ObservableList<CommandTerm> items = getListView().getItems();
			CommandTerm ct = items.get(thisCell.getIndex());
			// It is important to return if hover is already true
			// Because of the call getListView().refresh() which will cause a feedback loop 
	        if (ct.isHover()) {return;}
	        // For every hover item set it to false, because sometimes the mouseExit command is missed
	        clearAllHoverItems(items);
			updateLinkedItems(ct.getRootTerm(), true);
			ct.setHover(true);
			event.consume();
			getListView().refresh();
	    });
	    
		setOnMouseExited(event -> {
			if (getItem() == null) { return;}
	        ObservableList<CommandTerm> items = getListView().getItems();
			CommandTerm ct = items.get(thisCell.getIndex());
	        if (!ct.isHover()) {return;}
	        clearAllHoverItems(items);
			updateLinkedItems(ct, false);
			event.consume();
			getListView().refresh();
	    });
	        
		setOnDragDetected (event -> {
			if (getItem() == null) { return;}

			ObservableList<CommandTerm> items = getListView().getItems();
			ClipboardContent content = new ClipboardContent();
			int draggedId = items.indexOf(getItem());
			content.putString(""+draggedId);
			Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
			dragboard.setContent(content);
			event.consume();
		});
		
		setOnDragOver (event ->{
			if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
		});
		
		setOnDragEntered (event ->{
			if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
				setOpacity(0.3);
			}
		});

		setOnDragExited (event ->{
			if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
				setOpacity(1);
			}
		});

		setOnDragDropped (event ->{
			if (getItem() == null) {
				return;
			}
			
			Dragboard dragboard = event.getDragboard();
			boolean success = false;
			
			if (dragboard.hasString()) {
				ObservableList<CommandTerm> items = getListView().getItems();
				int draggedId = Integer.parseInt(dragboard.getString());
				CommandTerm draggedObject = items.get(draggedId);
				int thisId = items.indexOf(getItem());
				
				items.remove(draggedId);
				items.add(thisId, draggedObject);
				getListView().getSelectionModel().clearSelection();
			}
			event.setDropCompleted(success);
			event.consume();
		});

	} // end of constructor
	
	private void clearAllHoverItems(ObservableList<CommandTerm> items) {
		for (CommandTerm item : items) {
			item.setHover(false);
			item.setHoverLinked(false);
		}
	}
	private void updateLinkedItems(CommandTerm root, boolean highlight) {
		root.setHoverLinked(highlight);
		for (CommandTerm term : root.getChildTerms()) {
			term.setHoverLinked(highlight);
		}
	}
	
	@Override
    protected void updateItem(CommandTerm item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText("");
            setStyle("-fx-control-inner-background: " + DEFAULT_BACKGROUND + ";");
        } else {
            setText(item.toString());
        	if (item.isInError()) {
            	setStyle("-fx-control-inner-background: " + ERROR_BACKGROUND + ";");
            } else if (item.isRunning()) {
            	setStyle("-fx-control-inner-background: " + RUNNING_BACKGROUND + ";");
            } else if (item.isHover()) {
            	setStyle("-fx-control-inner-background: " + HOVER_BACKGROUND + ";");
            } else if (item.isHoverLinked()) {
            	setStyle("-fx-control-inner-background: " + LINKED_BACKGROUND + ";");
            } else {
                setStyle("-fx-control-inner-background: " + DEFAULT_BACKGROUND + ";");
            }
        }
    }
	
} // end of DragNDropCell
