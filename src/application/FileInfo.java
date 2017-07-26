package application;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.Alert;



class TypeOfFile{
	
	private boolean isFolder;
	private boolean isParentDir;
	private String extension;
	
	TypeOfFile( boolean isFolder,
				 boolean isParentDir,
				 String extension)
	{
		this.isFolder=isFolder;
		this.isParentDir=isParentDir;
		this.extension=extension;
	}
	public boolean isFolder()	{return isFolder;};
	public boolean isParentDir(){return isParentDir;};
	public String getExtension()	{return extension;};

	
	
}
public class FileInfo {
	private static Pattern extension_pattern=Pattern.compile("\\.[^.]*?$");
	private File f;

	private String fname;
	private TypeOfFile ftype;
	private String fsize;
	private Date fLastModefied;
	private boolean isNewFile;
	
	
	FileInfo(File f, boolean isParentDir)
	{
		this.f=f;
		if(isParentDir)
			fname="..";
		else
			fname=f.getName();
		
		String extension="";
		//find out type/extension of file
		if(f.isDirectory())
		{
			
			fsize="";
		}
		else
		{			
			//Pattern p=Pattern.compile("\\.[^.]*?$");
			Matcher matcher=extension_pattern.matcher( f.getName());  
			if(matcher.find())
			{
				extension=matcher.group();
			}
			else 
			{
				extension="";
			}
			
			fsize=String.valueOf((int)Math.ceil(f.length()/1024.0))+" KB";
		}
		ftype=new TypeOfFile(f.isDirectory(),isParentDir,extension);
		fLastModefied=new Date(f.lastModified());
		//f.get
		
		
		System.out.println(f.toString()+" "+ f.isDirectory()+" "+f.isFile());
	}
	static FileInfo blankFileInfo(File path)
	{
		FileInfo fi=new FileInfo(new File(path+File.separator+"New folder"),false);
		fi.isNewFile=true;
		
		return fi;
	}
	
	public String getFname()
	{
		return fname;
	}
	public TypeOfFile getFtype()
	{
		return ftype;
	}
	public String getFsize()
	{
		return fsize;
	}
	public Date getFLastModefied()
	{
		return fLastModefied;
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
	 * 
	 * */
	public boolean setFname(String fname)
	{
		if(!isNewFile)
		{	//rename
			File new_f=new File(f.getParent()+File.separator+fname);
			boolean result=f.renameTo(new_f);
			if(result)
			{
				f=new_f;
				this.fname=fname;
			}
			return result;
		}else
		{	
			String absPath=f.getAbsolutePath();
			String filePath = 
					absPath.substring(0,absPath.lastIndexOf(File.separator));
			
			f= new File(filePath+File.separator+fname);
			boolean result = f.mkdir();
			if(result)
			{
				isNewFile=false;
			}else
			{
			 /*Alert alert = new Alert(Alert.AlertType.INFORMATION);

			    alert.setTitle("Information");
			    alert.setHeaderText(null);
			    alert.setContentText("Hello World!");

			    alert.show();*/
			}
			System.out.println("trying makeDir("+result+"): "+filePath+File.separator+fname);
			fname=f.getName();
			return result;
			//f.getAbsolutePath()
			
		}
	}
	
	@Override
	public String toString()
	{
		return "FileInfo: "+ f.toString();
	}
}
