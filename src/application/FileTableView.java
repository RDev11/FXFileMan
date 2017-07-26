package application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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




public class FileTableView extends TableView<FileInfo> {
	static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	//private Main mainref;
	public FileTableView(Main mainref)	{
		//this.mainref=mainref;
		setEditable(true);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		
		Callback<TableColumn<FileInfo, String>, TableCell<FileInfo, String>> cellFactory =//cellFactory
	             new Callback<TableColumn<FileInfo, String>, TableCell<FileInfo, String>>() {
					
	                 public TableCell<FileInfo, String> call(TableColumn<FileInfo, String> p) {
	                	 
	                    return new EditingCell(mainref);
	                 }
	             };
	             
	    Callback<TableColumn<FileInfo,TypeOfFile>,TableCell<FileInfo,TypeOfFile>> cb= new Callback<TableColumn<FileInfo,TypeOfFile>,TableCell<FileInfo,TypeOfFile>>(){       
       	     @Override
       	     public TableCell<FileInfo,TypeOfFile> call(TableColumn<FileInfo,TypeOfFile> param) {               
       	         TableCell<FileInfo,TypeOfFile> cell = new TableCell<FileInfo,TypeOfFile>(){
       	             ImageView imageview = new ImageView();
       	             @Override
       	             public void updateItem(TypeOfFile item, boolean empty) {                       
       	                 if(item!=null){                           

       	                	 if(item.isFolder())
       	                	 {
       	                		if(item.isParentDir())
       	                			imageview.getStyleClass().setAll("img_folder_open");
       	                		else
       	                			imageview.getStyleClass().setAll("img_folder");
       	                	 }
       	                	 else
       	                		 imageview.getStyleClass().setAll("img_file");
       	                     //SETTING ALL THE GRAPHICS COMPONENT FOR CELL
       	                     setGraphic(imageview);
       	                 }else 
       	                	 imageview.getStyleClass().clear();
       	             }
       	         };
       	         //System.out.println(cell.getIndex());              
       	         return cell;
       	     }
       };//end-callback
       
       TableColumn<FileInfo, String> fileNameCol = new TableColumn<FileInfo, String>("First Name");
       fileNameCol.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fname"));
       fileNameCol.setCellFactory(cellFactory);
		
       fileNameCol.setOnEditCommit(new EventHandler<CellEditEvent<FileInfo, String>>()
		{
			@Override
	        public void handle(CellEditEvent<FileInfo, String> t) {
				System.out.println("--OnEditCommit--");
				if (t==null)System.err.println("--OnEditCommit-- event=null");
				FileInfo finfo=t.getRowValue();
				if (finfo==null)System.err.println("--OnEditCommit-- finfo=null");
				boolean success=finfo.setFname(t.getNewValue());
				
				
				System.out.println("--OnEditCommit--"+t.getOldValue()+" => "+
						t.getNewValue()+"; result="+success);
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

            		System.out.println("enter ("+"COLUMN"+")");
                 	//if(timeline!=null)timeline.stop();
                 	
                 	if(fi.getFtype().isFolder())
                 	{
                 		//cancelEdit();
                 		mainref.openDirectory(fi.getFile());
                 	}
            		event.consume();
            	}else if(event.getCode()==KeyCode.BACK_SPACE)
            	{
            		mainref.openPreviousDirectory();
            	}
            }
        });
		//fileNameCol.setComparator(value);
		//firstNameCol.setEditable(true);
		
		
		
		TableColumn<FileInfo, TypeOfFile> typeCol = new TableColumn<FileInfo, TypeOfFile>("");
	    typeCol.setCellValueFactory(new PropertyValueFactory<FileInfo, TypeOfFile>("ftype"));
	    typeCol.setCellFactory(cb);
	    typeCol.setPrefWidth(22);
	    typeCol.setComparator((s1,s2)->{
	    	if(s1==null||s2==null)return 0;
	    	if(s1.isParentDir())return 1;
	    	if(s2.isParentDir())return -1;
	    	return s1.isFolder()==s2.isFolder()?0:(s1.isFolder()?1:-1);
	    });
	    TableColumn<FileInfo, String> sizeCol =  new TableColumn<FileInfo, String>("Size");
	    sizeCol.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fsize"));

	    TableColumn<FileInfo, String> lastModifiedCol =  new TableColumn<FileInfo, String>("Date");
	    lastModifiedCol.setCellValueFactory(
	    	(row)->{
		    	SimpleStringProperty property=new SimpleStringProperty();		    	
		        property.setValue(dateFormat.format(row.getValue().getFLastModefied()));
		    	return property;
	    	}
	    );

	    
	    getColumns().addAll(typeCol, fileNameCol, sizeCol, lastModifiedCol);

		getSortOrder().addListener(new ListChangeListener<TableColumn<FileInfo, ?>>() {
	        @Override
	        public void onChanged(Change<? extends TableColumn<FileInfo, ?>> change) {
	        	ObservableList<TableColumn<FileInfo, ?>> sortOrder = FileTableView.this.getSortOrder();
	        	if(sortOrder.size()==0||FileTableView.this.getSortOrder().get(0)!=typeCol)
	        	{
	        		FileTableView.this.getSortOrder().add(0, typeCol);
	        	}
	        }
	    });
	    //typeCol.setSortType(TableColumn.SortType.DESCENDING);
		//getSortOrder().add(typeCol);
	    
	}
}
