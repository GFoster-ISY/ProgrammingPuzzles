package application;

import application.keyword.CommandTerm;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragNDropCell extends ListCell<CommandTerm> {

	private static final String DEFAULT_BACKGROUND = "derive(-fx-base,80%)";
    private static final String ERROR_BACKGROUND = "derive(OrangeRed, 50%)";
    private static final String RUNNING_BACKGROUND = "derive(DeepSkyBlue, 50%)";
    
	public DragNDropCell () {
		ListCell<CommandTerm> thisCell = this;
		
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
				if (thisId > draggedId) {
					CommandTerm aboveObject = items.get(thisId);
					if (aboveObject != null) {
						if (aboveObject.hasClosure()) {
							draggedObject.setIndentLevel(aboveObject.getIndentLevel()+1);
						} else {
							draggedObject.setIndentLevel(aboveObject.getIndentLevel());
						}
					} else {
						draggedObject.setIndentLevel(0);
					}
				} else {
					CommandTerm belowObject = items.get(thisId);
					if (belowObject != null) {
						if (belowObject.getClosesIndent()) {
							draggedObject.setIndentLevel(belowObject.getIndentLevel()+1);
						} else {
							draggedObject.setIndentLevel(belowObject.getIndentLevel());
						}
					} else {
						draggedObject.setIndentLevel(0);
					}
					
				}
				
				items.remove(draggedId);
				items.add(thisId, draggedObject);
				
				getListView().getSelectionModel().clearSelection();
			}
			event.setDropCompleted(success);
			event.consume();
		});

	} // end of constructor
	
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
            } else {
                setStyle("-fx-control-inner-background: " + DEFAULT_BACKGROUND + ";");
            }
        }
    }
	
} // end of DragNDropCell
