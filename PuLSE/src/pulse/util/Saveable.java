package pulse.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import pulse.tasks.Identifier;
import pulse.tasks.SearchTask;

/**
 * A {@code Saveable} is any individual {@code PULsE} entity that can be saved using a {@code FileOutputStream}.
 *
 */

public interface Saveable extends Describable {
	
	/**
	 * Gets the default export extension for this {@code Saveable}.
	 * @return {@code .html}, if not stated otherwise by overriding this method.
	 */
	
	public default Extension getDefaultExportExtension() {
		return Extension.CSV;
	}
	
	public static Extension[] getAllSupportedExtensions() {
		return Extension.values();
	}
	
	
	/**
	 * Returns an array of supported extensions, which by default contains only the default extension.
	 * @return an array with {@code Extension} type objects. 
	 */
	
	public default Extension[] getSupportedExtensions() {
		return new Extension[] {getDefaultExportExtension()};
	}
	
	/**
	 * Saves the contents to {@code directory} without asking a confirmation from the user. 
	 * @param directory the directory where this {@code Saveable} needs to be saved.
	 */
	
	public default void save(File directory, Extension extension) {

		Extension supportedExtension = extension;
		
		if(!Arrays.stream(getSupportedExtensions()).anyMatch(extension::equals) )
			supportedExtension = getDefaultExportExtension(); //revert to default extension
			
		try {
				File newFile = new File(directory, describe() + "." + supportedExtension);
				newFile.createNewFile();
	            FileOutputStream fos = new FileOutputStream(newFile);
	            printData(fos, supportedExtension);
	            
		} catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
		}
		
	}
	
	/**
	 * Provides a {@code JFileChooser} for the user to select the export destiation for this {@code Saveable}.
	 * @param parentWindow the parent frame.
	 * @param fileTypeLabel the label describing the specific type of files that will be saved.
	 */
	
	public default void askToSave(JFrame parentWindow, String fileTypeLabel) {
		JFileChooser fileChooser = new JFileChooser();
		
		File workingDirectory = new File(System.getProperty("user.home"));
		fileChooser.setCurrentDirectory(workingDirectory);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setSelectedFile(new File(describe()));
		
		for(Extension s : getSupportedExtensions())
			fileChooser.addChoosableFileFilter(
					new FileNameExtensionFilter(fileTypeLabel + " (." + s + ")", 
					s.toString().toLowerCase()));
		
		fileChooser.setAcceptAllFileFilterUsed(false);
		
	    int returnVal = fileChooser.showSaveDialog(parentWindow);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	        try {
	            File file = fileChooser.getSelectedFile();
	            String path = file.getPath();	            
	            
	            FileNameExtensionFilter currentFilter = (FileNameExtensionFilter)fileChooser.getFileFilter();
	            String ext = currentFilter.getExtensions()[0];
	            
	            if(!path.contains(".")) 	            
	            	file = new File(path + "." + ext);	             
	            
	            FileOutputStream fos = new FileOutputStream(file);
	            
	            printData(fos, Extension.valueOf(ext.toUpperCase()));
	            
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	}
	
	public abstract void printData(FileOutputStream fos, Extension extension);
	
}