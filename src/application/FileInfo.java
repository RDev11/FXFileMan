package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;


import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;


/**
 * содержит иконку файла
 * и порядок сортировки Folders first
 * see FileInfo*/
class TypeOfFile{
	private File f;
	private boolean isFolder;
	private boolean isParentDir;
	
//	private String extension;
	private Image icon=null;
	
	TypeOfFile(	File f, boolean isParentDir)	{
		this.f=f;
		this.isFolder=f.isDirectory();
		this.isParentDir=isParentDir;
		
		if(isParentDir)
			icon=new Image(getClass().getResource("back.png").toExternalForm());
		//this.extension=extension;
	}
	public boolean isFolder()	{return isFolder;};
	public boolean isParentDir(){return isParentDir;};
	//public String getExtension()	{return extension;};
	public Image getIcon()	{
		if (icon==null)
				icon=FileInfo.getFileIcon(f);
		return icon;
	}

	
	
}
/**
 * 
 * класс FileInfo служит оболочкой для File, используется с FileTableView 
 *
 */
public class FileInfo {
	//private static Pattern extension_pattern=Pattern.compile("\\.[^.]*?$");
	private File f;

	private TypeOfFile ftype;

	private boolean isNewFile;
	private boolean isParentDir;
	
	
	FileInfo(File f, boolean isParentDir)
	{
		this.f=f;
		this.isParentDir=isParentDir;
		
		//if(isParentDir)
		//	fname="..";
		//else
		//	fname=f.getName();
		
		//String extension="";
	
		ftype = new TypeOfFile(f,isParentDir);
		
		//System.out.println(f.toString()+" "+ f.isDirectory()+" "+f.isFile());
	}
	/**
	 * создает FileInfo для 
	 * @param path
	 * @return
	 */
	static FileInfo blankFileInfo(File path)
	{
		FileInfo fi=new FileInfo(new File(path+File.separator+"New folder"),false);
		fi.isNewFile=true;
		
		return fi;
	}
	
	public String getFname()
	{
		if(isParentDir)
			return "..";
		else
			return f.getName();
	}
	public TypeOfFile getFtype()
	{
		return ftype;
	}
	public long getFsize()
	{
		return f.length();
	}
	public Date getFLastModefied()
	{
		return new Date(f.lastModified());
	}
	
	
	
	
	public boolean isNewFile()
	{
		return isNewFile;
	}
	
	
	

	
	public File getFile()
	{
		return f;
	}

/**
 * rename existing file or create new file 
 * @param newName
 * @return is succeed
 */
	public boolean setFname(String newName)
	{
		if(!isNewFile)
		{	//rename
			File new_f=new File(f.getParent()+File.separator+newName);
			if(f.renameTo(new_f))
			{
				f=new_f;	
				return true;
			}else
				return false;
		}else
		{	
			String absPath=f.getAbsolutePath();
			String filePath = 
					absPath.substring(0,absPath.lastIndexOf(File.separator));
			
			f= new File(filePath+File.separator+newName);
			boolean result = f.mkdir();
			if(result)
			{
				isNewFile=false;
			}
			//System.out.println("trying makeDir("+result+"): "+filePath+File.separator+newName);
			
			return result;
			//f.getAbsolutePath()
			
		}
	}
	/**
	 * get system icon by file
	 * @param file
	 * @return Image containing system icon
	 */
	public static Image getFileIcon(File file)
	{
		
		ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
		if (icon==null)return null;
      	WritableImage fximg = new WritableImage(16,16); 	       

      	SwingFXUtils.toFXImage((BufferedImage) icon.getImage(), fximg) ;
		
      	return fximg;
	}
	@Override
	public String toString()
	{
		return "FileInfo: "+ f.toString();
	}
}
