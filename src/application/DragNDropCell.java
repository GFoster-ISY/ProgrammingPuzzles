package application;

import application.keyword.CommandTerm;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragNDropCell extends ListCell<CommandTerm> {

	private static final String DEFAULT_BACKGROUND = "derive(-fx-base,80%)";
    private static final String HIGHLIGHTED_BACKGROUND = "derive(OrangeRed, 50%)";

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
				
				items.remove(draggedId);
				items.add(thisId, draggedObject);
				
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
            if (item.isRunning()) {
            	setStyle("-fx-control-inner-background: " + HIGHLIGHTED_BACKGROUND + ";");
            } else {
                setStyle("-fx-control-inner-background: " + DEFAULT_BACKGROUND + ";");
            }
        }
    }
	
} // end of DragNDropCell
