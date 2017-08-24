package application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.geometry.Pos;



public class FileTableView extends TableView<FileInfo> {
	static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
	private Main frameRef;
	
	@SuppressWarnings("unchecked")
	public FileTableView(Main frameRef)	{
		this.frameRef=frameRef;
		setEditable(true);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		
	
		
		/////////////////////////////////////////////////////////////////////////////////////////////fileType column
		
		Callback<TableColumn<FileInfo,TypeOfFile>,TableCell<FileInfo,TypeOfFile>> fileTypeCellFactory= //setup graphics for column
				new Callback<TableColumn<FileInfo,TypeOfFile>,TableCell<FileInfo,TypeOfFile>>(){       
      	     @Override
      	     public TableCell<FileInfo,TypeOfFile> call(TableColumn<FileInfo,TypeOfFile> param) {               
      	         TableCell<FileInfo,TypeOfFile> cell = new TableCell<FileInfo,TypeOfFile>(){
      	             ImageView imageview = new ImageView();
      	             @Override
      	             public void updateItem(TypeOfFile item, boolean empty) {                       
      	                 if(item!=null){        
      	                	                	
      	                	imageview.setImage(item.getIcon());
      	                	setGraphic(imageview);
      	                	 /*
      	                	 if(item.isFolder())
      	                	 {
      	                		if(item.isParentDir())
      	                			imageview.getStyleClass().setAll("img_folder_open");
      	                		else
      	                			imageview.getStyleClass().setAll("img_folder");
      	                	 }
      	                	 else
      	                		 imageview.getStyleClass().setAll("img_file");
      	                     setGraphic(imageview);*/
      	                 }else 
      	                	// imageview.getStyleClass().clear();
      	                	 imageview.setImage(null);
      	             }
      	         };         
      	         return cell;
      	     }
      };//end-fileTypeCellFactory
      
		TableColumn<FileInfo, TypeOfFile> typeCol = new TableColumn<FileInfo, TypeOfFile>("");
	    typeCol.setCellValueFactory(new PropertyValueFactory<FileInfo, TypeOfFile>("ftype"));
	    typeCol.setCellFactory(fileTypeCellFactory);
	    typeCol.setPrefWidth(22);
	    typeCol.setComparator((s1,s2)->{//set comparator for sorting so folders goes first and files goes last
	    	if(s1==null||s2==null)return 0;
	    	if(s1.isParentDir())return 1;
	    	if(s2.isParentDir())return -1;
	    	return s1.isFolder()==s2.isFolder()?0:(s1.isFolder()?1:-1);
	    });
	    
	    
	    /////////////////////////////////////////////////////////////////////////////////////////////filename column
	    Callback<TableColumn<FileInfo, String>, TableCell<FileInfo, String>> fileNameCellFactory =
	             new Callback<TableColumn<FileInfo, String>, TableCell<FileInfo, String>>() {				
	                 public TableCell<FileInfo, String> call(TableColumn<FileInfo, String> p) {
	                	 
	                    return new EditingCell(frameRef);
	                 }
	             };
	                
      
      TableColumn<FileInfo, String> fileNameCol = new TableColumn<FileInfo, String>("First Name");
      fileNameCol.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fname"));
      fileNameCol.setCellFactory(fileNameCellFactory);
		
      fileNameCol.setOnEditCommit(new EventHandler<CellEditEvent<FileInfo, String>>()
		{
			@Override
	        public void handle(CellEditEvent<FileInfo, String> t) {
				//System.out.println("--OnEditCommit--");
				if (t==null)System.err.println("--OnEditCommit-- event=null");
				FileInfo finfo=t.getRowValue();
				if (finfo==null)System.err.println("--OnEditCommit-- finfo=null");
				boolean success=finfo.setFname(t.getNewValue());
				
				
				//System.out.println("--OnEditCommit--"+t.getOldValue()+" => "+
				//		t.getNewValue()+"; result="+success);
				if(!success)
				{
					if(finfo.isNewFile())
					{
						t.getTableView().getItems().remove(finfo);
					}
					else
					{
				     t.getTableColumn().setVisible(false);//reset rendered value
				     t.getTableColumn().setVisible(true);
					}
				}
				
			}
		});
      fileNameCol.setEditable(true);
		addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent event) {
           	if(event.getCode()==KeyCode.ENTER)
           	{         		
           		FileInfo fi = getSelectionModel().getSelectedItem();
           		//FileInfo fi = (FileInfo) EditingCell.this.getTableRow().getItem();


                	if(fi.getFtype().isFolder())
                	{

                		frameRef.openDirectory(fi.getFile());
                	}
           		event.consume();//consume = don't start edit cell
           	}else if(event.getCode()==KeyCode.BACK_SPACE)
           	{
           		frameRef.openPreviousDirectory();
           	}
           }
       });

	    /////////////////////////////////////////////////////////////////////////////////////////////size Column
	    TableColumn<FileInfo, Long> sizeCol =  new TableColumn<FileInfo, Long>("Size");	
	    
	    sizeCol.setCellFactory((p)->{
	    	return new TableCell<FileInfo, Long>(){
	    		{
	    			setAlignment(Pos.CENTER_RIGHT);
	    		}
	    		@Override
	    		public void updateItem(Long item, boolean empty)
	    		{
	    			if(!empty&&item!=null)
	    			{
	    				this.setText(item+"!");
	    				
	    				long rest = item;
			    		int i;
			    		for(i=0;i<=4;i++)
			    		{
			    			if (rest>=10000)
			    			{
			    				rest/=1024;
			    			}else 
			    				break;
			    		}
			    		switch(i)
			    		{
			    		case 0:
			    			this.setText(rest+" B");
			    			break;
			    		case 1:
			    			this.setText(rest+" KB");			    			
			    			break;
			    		case 2:
			    			this.setText(rest+" MB");			    			
			    			break;
			    		case 3:
			    			this.setText(rest+" GB");			    			
			    			break;
			    		case 4:
			    			this.setText(rest+" TB");			    			
			    			break;
			    		}
			    				    		
	    			}else
	    			{
	    				this.setText(null);
	    			}    			
	    		}
	    	};
	    });

	    sizeCol.setCellValueFactory(
		    	(row)->{
			    	//SimpleIntegerProperty property=new SimpleIntegerProperty();		
			    	FileInfo info = row.getValue();
			    	if(!info.getFtype().isFolder())
			    	{
			    		return new ReadOnlyObjectWrapper<>(info.getFsize());		    		
			    	}		    				    	
			    	return null;
		    	}
		);

	    /////////////////////////////////////////////////////////////////////////////////////////////lastModified Column
	    TableColumn<FileInfo, Date> lastModifiedColumn =  new TableColumn<FileInfo, Date>("Modified");
	    lastModifiedColumn.setCellFactory((p)->{
	    	return new TableCell<FileInfo, Date>(){
	    		{
	    			setAlignment(Pos.CENTER_RIGHT);
	    		}
	    		@Override
	    		public void updateItem(Date item, boolean empty)
	    		{
	    			if(!empty)
	    				setText(dateFormat.format(item));
	    			else 
	    				setText(null);
	    		}
	    	};
	    });
	    /*
	    lastModifiedColumn.setCellValueFactory(
	    	(row)->{
		    	SimpleStringProperty property=new SimpleStringProperty();		    	
		        property.setValue(dateFormat.format(row.getValue().getFLastModefied()));
		    	return property;
	    	}
	    );*/

	    lastModifiedColumn.setCellValueFactory(
		    	(row)->{
			    	//SimpleIntegerProperty property=new SimpleIntegerProperty();		
			    	FileInfo info = row.getValue();
			    	//if(!info.getFtype().isFolder())
			    	//{
			    		return new ReadOnlyObjectWrapper<>(info.getFLastModefied());		    		
			    	//}		    				    	
			    	//return null;
		    	}
		);
	    
	    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    getColumns().addAll(typeCol, fileNameCol, lastModifiedColumn,sizeCol);

	    
		getSortOrder().addListener(new ListChangeListener<TableColumn<FileInfo, ?>>() {//folders always go first
	        @Override
	        public void onChanged(Change<? extends TableColumn<FileInfo, ?>> change) {
	        	ObservableList<TableColumn<FileInfo, ?>> sortOrder = FileTableView.this.getSortOrder();
	        	if(sortOrder.size()==0||FileTableView.this.getSortOrder().get(0)!=typeCol)
	        	{
	        		FileTableView.this.getSortOrder().add(0, typeCol);
	        	}
	        }
	    });

	    
	}
	public void createNewFile()
	{
		if(getEditingCell()!=null)
        		return;//otherwise exception raised
        	
    	FileInfo newFile =FileInfo.blankFileInfo(frameRef.getCurrentDir());	          	
        	getItems().add(newFile);

    	int row = getItems().size() -1;
	    //getSelectionModel().clearAndSelect( row, getColumns().get(1));//filename column   
        // scroll to new row
        scrollTo( newFile );

        
        try {//may fail
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        Platform.runLater(()->{
            edit(row, getColumns().get(1)); //filename column       
         });
	}
}
