package application;
	
import java.io.File;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;




public class Main extends Application {
	private File currentDir;
	private FileTableView table ;
	private TextField currentDir_textField ;

	private String darkThemeStyle;
	private String defaultThemeStyle;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("qwerty");
			

			BorderPane  mframe= new BorderPane ();

			Scene scene = new Scene(mframe,600,400);
			
			darkThemeStyle=getClass().getResource("darkTheme.css").toExternalForm();
			defaultThemeStyle=getClass().getResource("defaultTheme.css").toExternalForm();

			scene.getStylesheets().add(darkThemeStyle);
			
			
			
			
			primaryStage.setScene(scene);
			
				
			table= new FileTableView(this);

		   
		    

		    SplitPane splitPane=new SplitPane();
		    
		    
		    
		    
		    FileTreeView fileTreeView = new FileTreeView(new Callback<File,File>(){
				@Override
				public File call(File param) {
					
					return openDirectory(param);
				}		    	
		    });
		   


		    splitPane.getItems().addAll(fileTreeView,table);
		  
		    SplitPane.setResizableWithParent(fileTreeView, false);
		    splitPane.setDividerPositions(0.3,1);

		    
		    //splitPane.getDividerPositions()[0]
		    
		   // mframe.setRight(table);
		    
		    currentDir_textField = new TextField();
		   
		    currentDir_textField.setOnKeyPressed(new EventHandler<KeyEvent>()
		    {
		        @Override
		        public void handle(KeyEvent ke)
		        {
		            if (ke.getCode().equals(KeyCode.ENTER))
		            {
		            	openDirectory(new File(currentDir_textField.getText())).getAbsolutePath();
		            	currentDir_textField.selectEnd();
		            }
		        }
		    });
		    

		    

		    HBox hbox_buttons=new HBox();
		    Button newFolderButton = new Button("N");
		    newFolderButton.setFocusTraversable(false);
		    newFolderButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent mouseEvent) {

	            	int row = table.getItems().size() ;

      	          	System.out.println(1);
	            	FileInfo newFile =FileInfo.blankFileInfo(currentDir);
      	          	System.out.println(2);
	            	//FileInfo newFile = new FileInfo(
	            	//		new File(currentDir.getPath()+File.separator+"New folder"),false);
      	           	          	
      	          	table.getItems().add(newFile);
      	          	System.out.println(3);
	    		    //table.requestFocus();
	    		   // table.
	            	 //table.getSelectionModel().clearAndSelect(arg0);
				    table.getSelectionModel().clearAndSelect( row, table.getColumns().get(1));
      	          	System.out.println(4);
			        // scroll to new row
			        table.scrollTo( newFile );
      	          	System.out.println(5);


			        
			        try {//
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			        Platform.runLater(new Runnable() {
		                @Override
		                public void run() {
		                	table.edit(row, table.getColumns().get(1));
		                }
		             });
	            }
	        });
		    
		    Button changeThemeButton = new Button("C");
		    changeThemeButton.setFocusTraversable(false);
		    changeThemeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent mouseEvent) {
	            	
	            	if(!scene.getStylesheets().contains(darkThemeStyle))
	            		scene.getStylesheets().setAll(darkThemeStyle);
	            	else
	            		scene.getStylesheets().setAll(defaultThemeStyle);
	            	
	            }
	        });

		    
		    
		    hbox_buttons.getChildren().addAll(newFolderButton,changeThemeButton,new Button("2"),new Button("3"));
		    VBox vbox_top=new VBox();
		    vbox_top.getChildren().addAll(hbox_buttons,currentDir_textField);
		    mframe.setCenter(splitPane);
		    mframe.setTop(vbox_top);


		    openDirectory(new File("G:\\tests"));
		    
		    
		    
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void openPreviousDirectory()
	{
		openDirectory(currentDir.getParentFile());
		
	}
	public File openDirectory(File path)
	{
		System.out.println("opendir-1");
		if(path==null||!path.isDirectory())
		{
			currentDir_textField.setText(currentDir.getAbsolutePath());
			return currentDir;
		}
		//ObservableList<FileInfo> oar=FXCollections.observableArrayList();
		ObservableList<FileInfo> oar=FXCollections.observableArrayList();
		
		System.out.println("opendir-2");
		currentDir_textField.setText(path.getAbsolutePath());
		currentDir=path;
		
		File parentDir=currentDir.getParentFile();
		if (parentDir!=null)
			oar.add(new FileInfo(parentDir, true));
		System.out.println("opendir-3");
		if(currentDir.isDirectory()&&currentDir.canRead())
		{
			if(currentDir.listFiles()!=null)
			{
				if(currentDir.listFiles().length==0)
					System.err.println("opendir-len=0");
				for (File f : currentDir.listFiles())
				{						
					oar.add(new FileInfo(f, false));
				}
			
			}
		}

		
		System.out.println("opendir-5");
		table.setItems(oar);
		System.out.println("ItemsSet");
		
		
		
		table.getColumns().get(0).setSortType(TableColumn.SortType.DESCENDING);
		table.getSortOrder().add(table.getColumns().get(0));


		return currentDir;
		
	}
	public static void main(String[] args) {
		launch(args);
	}
}
