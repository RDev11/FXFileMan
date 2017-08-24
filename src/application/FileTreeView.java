package application;


import java.io.File;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * FileTreeView
 * */
public class FileTreeView extends TreeView<String> {
	Callback<File, File> openDirCallback;
	
	public FileTreeView(Callback<File, File> callback)	{

		openDirCallback=callback;
		TreeItem<String> rootItem = new TreeItem<String>("roots"){
			{
	    		ObservableList<TreeItem<String>> children=this.getChildren();
	    		for(File f :File.listRoots())
	    		{
	    			//if(f.isDirectory())
	    			{
		    			TreeItem<String> rootF=new FileTreeItem(f, callback);
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
	        public void changed(ObservableValue<?> observable, Object oldValue,
	                Object newValue) {
	        		
	        	FileTreeItem selectedItem = (FileTreeItem) newValue;
	        	if(selectedItem==null)
	        		return;
	        	openDirCallback.call(selectedItem.getFile());
	            //System.out.println("Selected Text : " + selectedItem.getValue());
	            // do what ever you want 
	        }

	      });
		
	}
		
}

/**
 * see FileTreeView
 *
 */
class FileTreeItem extends TreeItem<String>{
	private boolean isLeaf=false;
	private File file;
	private ImageView imageview;
	private Image fileIcon;
	
	Callback<File, File> openDirCallback;
	//static ChangeListener listener;
	
	public FileTreeItem(File file, Callback<File, File> openDirCallback){
		super(!file.getName().equals("")?file.getName():file.getPath());
		this.file=file;
		this.openDirCallback=openDirCallback;
		//this.setValue("");
	    imageview= new ImageView();

	    
	    fileIcon=FileInfo.getFileIcon(file);		
      	imageview.setImage(fileIcon);
      		
	    this.setGraphic(imageview);
	    
	    

	    
	    
	    
	    expandedProperty().addListener(new ChangeListener<Boolean>() {//lazily load 
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	           
	            Thread loader = new Thread(()->{
	        
	            	if (getChildren().size()==0&& !isLeaf)
	            	{
	            		//synchronized(FileTreeItem.this.treeViewMutex)
	            			
		            	    imageview.getStyleClass().add("img_loading");
							//FileTreeItem.this.treeViewMutex.acquire();
		            	    try {
							Thread.sleep(2000);
		            	    } catch (InterruptedException e) {	//
								e.printStackTrace();
							} 
		            	    Platform.runLater(()->{
								File[] containedDirs=file.listFiles( (File f) -> f.isDirectory() );
			            		if(containedDirs!=null)
			            		{
				            		for (File f : containedDirs)
				            		{
				            			
				            			FileTreeItem.this.getChildren().add(
				            					new FileTreeItem(f, FileTreeItem.this.openDirCallback));
				            		}
				            		isLeaf=containedDirs.length==0;
			            		}else
			            			isLeaf=true;
	
			            		
			            		String tmp=FileTreeItem.this.getValue();
			            		FileTreeItem.this.setValue("");		//re-set value to 		
			            		FileTreeItem.this.setValue(tmp);   	//update arrow component
			            		

			            		imageview.getStyleClass().remove("img_loading");
			            		imageview.setImage(fileIcon);
		            	    });
		            		


	            	}	                    		
            		//imageview.getStyleClass().setAll(!isLeaf&&newValue?"img_folder_open":null);     		
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
		return !file.isDirectory()||isLeaf;
	}
}