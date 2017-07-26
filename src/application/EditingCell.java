package application;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class EditingCell extends TableCell<FileInfo, String> {
	
	private TextField textField;	
	
	private Timeline timeline;
	private static final Duration baseTime=Duration.millis(500);
	private static final Duration onF2Time=Duration.millis(495);
	private static final Duration onMouseClickTime=Duration.millis(0);
	
	
	Main mainref;
	
    public EditingCell(Main m) {
    	mainref=m;
    	//setEditable(false);
    	this.setEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if(isEmpty())return;
            	if(timeline!=null && timeline.getStatus()==Animation.Status.RUNNING) timeline.playFrom(onMouseClickTime);
            	if (event.getClickCount() > 1) {

                 	//EditingCell c = (EditingCell) event.getSource();
                 	//EditingCell c = EditingCell.this;
                 	
                 	//FileInfo fi = c.getTableView().getSelectionModel().getSelectedItem();
                 	FileInfo fi = (FileInfo) EditingCell.this.getTableRow().getItem();
                 	if(timeline!=null)timeline.stop();
                 	
                 	if(fi.getFtype().isFolder())
                 	{
                 		cancelEdit();
                 		mainref.openDirectory(fi.getFile());
                 	}
                 		
                 	
                     System.out.println("Cell text: (" + fi +") _ "+event.getClickCount());
                    // event.consume();
                     
                     //cancelEdit();
                 }  	            	
            }
        });

    }

    @Override
    public void startEdit() {
    	 System.out.println("startEdit ");
        if (!isEmpty()) {
        	if(getString().equals("..")||!isEditable())
             	return;
        	
        	if(timeline==null)
        		timeline = new Timeline(new KeyFrame(
	        			baseTime,
	        	        ae -> {
	        	        	super.startEdit();
	        	            	        	            
	        	            createTextField();
	        	            setText(null);
	        	            setGraphic(textField);
	        	            Platform.runLater( ()->{
	        	                textField.requestFocus();                  
	        	                textField.selectAll();
	        	                //textField.deselect();      
	        	            });
	        	        	
	        	        }
	        	    ));
        	
        	timeline.playFrom(onF2Time);//just let mouse_event_handler do it's job     	
           
        }
    }

    @Override
    public void cancelEdit() {

        FileInfo fi=null;
        try{
        	fi=getTableView().getItems().get(getTableRow().getIndex());
        }catch(Exception e)  {}

       // super.cancelEdit();

        System.out.println("cancelEdit ("+fi+") "+isEditing());
        if(isEditing())
        {
        	super.cancelEdit();
        	if(fi!=null&&fi.isNewFile())
        	{
        		getTableView().getItems().remove(fi);
        	}
        }
        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        cancelEdit();
        if(timeline!=null)timeline.stop();
        
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
                System.out.println("editing "+item);//why ?
            } else {
            	//System.out.println("update  "+item);//why ?
                setText(getString());
                setGraphic(null);
            }
        }
    }
    private void createTextField() {
        textField = new TextField(getString());
        textField.getStyleClass().add("FilenameEditTextField");
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
        
        textField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, 
                Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        commitEdit(textField.getText());
                        EditingCell.this.getTableView().requestFocus();
                    }
            }
        });
        textField.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode()==KeyCode.ENTER)
				{
					commitEdit(textField.getText());
					EditingCell.this.getTableView().requestFocus();
				}else if (event.getCode() == KeyCode.ESCAPE) {
	                cancelEdit();
				}
			}
		});
			
    }
    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
