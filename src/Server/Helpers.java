package Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;


public class Helpers {
	public static final String rootDIR = "./www/"; 
	
	public static String buildFilePathString(String filename){
		return rootDIR + filename;
	}
	
	public static boolean validateFile(String filename) throws IOException, AccessDeniedException{
		String path = buildFilePathString(filename);
		if (path.contains("/..")){
			throw new IOException();
		}
		
		File reqFile = new File(path);
		if (reqFile.exists()){
			if (!reqFile.canRead()){
				throw new AccessDeniedException(filename);
			}
		} else {
			throw new IOException("Does not exist");
		}
		return true;
	}
}
