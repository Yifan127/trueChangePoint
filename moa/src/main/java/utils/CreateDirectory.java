package utils;

import java.io.File;

public class CreateDirectory {
	private static String rootPath = Constants.rootPath; 
	public File file;
	
	public void createDir(String path){
		file = new File(rootPath + path);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
	}
	
	public String getFullPath(){
		return file.getAbsolutePath();
	}

}
