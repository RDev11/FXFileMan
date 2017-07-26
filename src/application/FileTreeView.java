package application;

import java.io.File;
import java.util.concurrent.Semaphore;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * FileTreeView цуцуцу
 * 
 * */
public class FileTreeView extends TreeView<String> {
	Callback<File, File> openDirCallback;
	
	public FileTreeView(Callback<File, File> callback)	{
		Semaphore treeViewMutex=new Semaphore(1);
		openDirCallback=callback;
		TreeItem<String> rootItem = new TreeItem<String>("roots"){
			{
	    		ObservableList<TreeItem<String>> children=this.getChildren();
	    		for(File f :File.listRoots())
	    		{
	    			if(f.isDirectory())
	    			{
		    			TreeItem<String> rootF=new FileTreeItem(f, treeViewMutex, callback);
		    			children.add(rootF);
	    			}
	    		} 	
			}
	    };

	    
		setRoot(rootItem);
		setShowRoot(false);
		//SplitPane.setResizableWithParent(this, false);
		
		
		getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Object>() {

	        @Override
	        public void changed(ObservableValue observable, Object oldValue,
	                Object newValue) {
	        		
	        	FileTreeItem selectedItem = (FileTreeItem) newValue;
	        	openDirCallback.call(selectedItem.getFile());
	            System.out.println("Selected Text : " + selectedItem.getValue());
	            // do what ever you want 
	        }

	      });
	}
		
}


class FileTreeItem extends TreeItem<String>{
	private Semaphore treeViewMutex;
	private boolean isLeaf=false;
	private File file;
	private ImageView imageview;
	Callback<File, File> openDirCallback;
	//static ChangeListener listener;
	
	public FileTreeItem(File file, Semaphore treeViewMutex, Callback<File, File> openDirCallback){
		super(!file.getName().equals("")?file.getName():file.getPath());
		this.file=file;
		this.treeViewMutex=treeViewMutex;
		this.openDirCallback=openDirCallback;
		//this.setValue("");
	    imageview= new ImageView();
	    imageview.getStyleClass().setAll("img_folder");
	    this.setGraphic(imageview);
	    
	    
	  /* this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//if(event.getClickCount()==2)
				{
					System.out.println(" !!!!!!!! ");
					openDirCallback.call(FileTreeItem.this.file);
				}	
			}
		});*/
	    
	    
	    
	    expandedProperty().addListener(new ChangeListener<Boolean>() {//lazily load 
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	           
	            Thread loader = new Thread(()->{
	        
	            	if (getChildren().size()==0&& !isLeaf)
	            	{
	            		try {
		            	    imageview.getStyleClass().setAll("img_loading");
							Thread.sleep(500);
							FileTreeItem.this.treeViewMutex.acquire();
							
							
							File[] containedDirs=file.listFiles( (File f) -> f.isDirectory() );
		            		if(containedDirs!=null)
		            		{
			            		for (File f : containedDirs)
			            			FileTreeItem.this.getChildren().add(
			            					new FileTreeItem(f, treeViewMutex, FileTreeItem.this.openDirCallback));
			            		isLeaf=containedDirs.length==0;
		            		}else
		            			isLeaf=true;

		            		
		            		String tmp=FileTreeItem.this.getValue();
		            		FileTreeItem.this.setValue("");	//re-set value to 		
		            		FileTreeItem.this.setValue(tmp);   //update arrow component
		            		
		            		
						} catch (InterruptedException e) {		
							e.printStackTrace();
						} finally {
							FileTreeItem.this.treeViewMutex.release();
	            		}

	            	}
	            	imageview.getStyleClass().setAll(!isLeaf&&newValue?"img_folder_open":"img_folder");
	            		
	            });
	            loader.start();
	        }
	    });//end-of- add_Change_Listener

	}//end-of-constructor
	
	public File getFile() {
		return file;
	}

	@Override
	public boolean isLeaf()
	{
		return isLeaf;
	}
}