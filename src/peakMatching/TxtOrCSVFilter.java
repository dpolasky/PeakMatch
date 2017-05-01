package peakMatching;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

/**
 * Accepts text, csv, OR .isotopes (Dmitry tool) files
 * @author dpolasky
 *
 */
public class TxtOrCSVFilter extends FileFilter implements FilenameFilter {

	
	public boolean accept(File dir, String name) {
		// accept .csv and .txt
		boolean baccept = name.endsWith(".txt") || name.endsWith(".csv") || name.endsWith(".isotopes") || dir.isDirectory();
		return baccept;
	}

	public boolean accept(File dir) {
		String name = dir.toString();
		boolean baccept = name.endsWith(".txt") || name.endsWith(".csv") || name.endsWith(".isotopes") || dir.isDirectory();
		return baccept;
	}

	public String getDescription() {
		return "Accepts .txt, .csv, .isotope files";
	}

}
