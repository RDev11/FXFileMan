package application;
	
import java.io.File;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
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
			primaryStage.setTitle("FXFileMan");


			BorderPane  mframe= new BorderPane ();

			Scene scene = new Scene(mframe,800,500);
			
			darkThemeStyle=getClass().getResource("darkTheme.css").toExternalForm();
			defaultThemeStyle=getClass().getResource("defaultTheme.css").toExternalForm();

			scene.getStylesheets().add(darkThemeStyle);

			primaryStage.setScene(scene);
			
				
			
			
			table = new FileTableView(this);	    
		    	
		    FileTreeView fileTreeView = new FileTreeView(new Callback<File,File>(){
				@Override
				public File call(File param) {					
					return openDirectory(param);
				}		    	
		    });
		   
		    SplitPane splitPane=new SplitPane();
		    splitPane.getItems().addAll(fileTreeView,table);
		  
		    SplitPane.setResizableWithParent(fileTreeView, false);
		    splitPane.setDividerPositions(0.3,1);
		    mframe.setCenter(splitPane);
		    
		    
		    
		    
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
		    

		    
		    
		    
		    

		    Button newFolderButton = new Button();
		    newFolderButton.setFocusTraversable(false);
		    newFolderButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent mouseEvent) {       	
      	          	table.createNewFile();
	            }
	        });
		    newFolderButton.setTooltip(new Tooltip("Создать папку"));
		    newFolderButton.setGraphic( new ImageView());
		    //newFolderButton.setId("img_newFolder");
		    newFolderButton.getGraphic().setId("img_newFolder");
		    
		    
		    
		    Button changeThemeButton = new Button();
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
		    changeThemeButton.setTooltip(new Tooltip("Изменить тему"));
		    changeThemeButton.setGraphic( new ImageView());
		    changeThemeButton.getGraphic().setId("img_changeTheme");
		    


		    
		    

		    HBox hbox_buttons=new HBox();
		    hbox_buttons.getChildren().addAll(newFolderButton,changeThemeButton);
		    VBox vbox_top=new VBox();
		    vbox_top.getChildren().addAll(hbox_buttons,currentDir_textField);
		    
		    
		    
  
		    
		    mframe.setTop(vbox_top);



		    for (File f:  File.listRoots())
		    {
		    	if(f.isDirectory())
		    	{
		    		openDirectory(f);
		    		break;
		    	}
		    }
		    
			primaryStage.show();
		} catch(Exception e) {
			System.err.println("in Main.start():");			
			e.printStackTrace();
		}
	}
	public void openPreviousDirectory()
	{
		openDirectory(currentDir.getParentFile());
		
	}
	public File openDirectory(File path)
	{

		if(path==null||!path.isDirectory())
		{
			currentDir_textField.setText(currentDir.getAbsolutePath());
			return currentDir;
		}

		ObservableList<FileInfo> oar = FXCollections.observableArrayList();
		

		currentDir_textField.setText(path.getAbsolutePath());
		currentDir=path;
		
		File parentDir=currentDir.getParentFile();
		if (parentDir!=null)
			oar.add(new FileInfo(parentDir, true));


		if(currentDir.isDirectory()&&currentDir.canRead())
		{
			if(currentDir.listFiles()!=null)
			{
				//if(currentDir.listFiles().length==0)
				//	System.err.println("opendir-len=0");
				for (File f : currentDir.listFiles())
				{						
					//if(!f.isHidden()||showHidden)
					oar.add(new FileInfo(f, false));
				}		
			}
		}
		
	
		table.setItems(oar);
		
		table.getColumns().get(0).setSortType(TableColumn.SortType.DESCENDING);
		table.getSortOrder().add(table.getColumns().get(0));			

		


		return currentDir;
		
	}
	public File getCurrentDir()
	{
		return currentDir;	
	}
	public static void main(String[] args) {
		launch(args);
	}
}
