package pairFinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import peakMatching.ExpIon;


/**
 * Main method for finding pairs potentially corresponding to crosslinked peptides from a set of
 * experimental data. 
 * @author Dan
 *
 */


public class FindPairsMain {

	private static final double MTAG_PAIR = 128.1314;
	private static final double DC4_PAIR = 112.1000;
	
	private static final double DC4_CL_DEADEND = 69.034;
	private static final double DC4_UNC_DEADEND = 181.134;
	private static final double MTAG_UNC_DEADEND = 197.1654;
	private static final double BS3_DEADEND = 157.09;
	
	private static final String TOPDIR = "C://Users//Dan//Desktop//PeptideAnalysis";
	private JFileChooser fc = null;

	private final IonPair[] thyPairs;
	
	private static final double TOLERANCEPPM = 10.0;		// error tolerance in PPM
	
	public FindPairsMain(){
		// Initialize file chooser for the experimental data
		fc = new JFileChooser();
		File defaultDir = new File(TOPDIR);
		fc.setCurrentDirectory(defaultDir);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setDialogTitle("Select the data file(s) to analyze");
        fc.setFileFilter(new FileNameExtensionFilter("CSV files only","csv"));
        fc.setFileSelectionMode(2);
        fc.setMultiSelectionEnabled(true);
        
        // Initialize the list of theoretical ion pairs
        IonPair DC4pair = new IonPair(DC4_PAIR,"DC4-pair");
        IonPair MTagpair = new IonPair(MTAG_PAIR,"Mtag-pair");
        IonPair DC4CDE = new IonPair(DC4_CL_DEADEND, "Mtag OR DC-4 cleaved dead-end and unxlinked peptide");
        IonPair DC4UDE = new IonPair(DC4_UNC_DEADEND, "DC-4 uncleaved dead-end and unxlinked peptide");
        IonPair MTagUDE = new IonPair(MTAG_UNC_DEADEND, "Mtag uncleaved dead-end and unxlinked peptide");
        IonPair BS3DE = new IonPair(BS3_DEADEND, "BS3 dead-end and unxlinked peptide");
        IonPair[] myPairs = {DC4pair,MTagpair,DC4CDE,DC4UDE,MTagUDE,BS3DE};
        thyPairs = myPairs;

	}
	
	public static void main(String[] args) {
		FindPairsMain finder = new FindPairsMain();
		JFrame mainframe = new JFrame();
		Object[] choices = {"1","2","3","4"};
				
		// Added support for multiple files with a file chooser
		if (finder.fc.showDialog(mainframe,"OK") == 0){
			File[] expfiles = finder.fc.getSelectedFiles();
			
			
			for (File expfile : expfiles){
				// Get the file name
				String outputName = expfile.getName();
				int endIndex = outputName.lastIndexOf(".");
				outputName = outputName.substring(0, endIndex);

				// Get the charge state by asking the user
				Object charge = JOptionPane.showInputDialog(null,"Choose the charge state of this data","Input",JOptionPane.INFORMATION_MESSAGE,null,choices,choices[0]);
				int mycharge = Integer.parseInt((String) charge);
				
				// Read in the exp data from file
				ArrayList<ExpIon> expData = finder.parseExpCSV(expfile.toString());
				
				// Find pairs of peptide peaks
				ArrayList<IonPair> pairs = finder.findPairs(expData,mycharge);
				
				// Print output of pair finding to file
				String finaloutname = TOPDIR + File.separator + outputName + "_pairlist.csv";
				printPairs(pairs,finaloutname);
				
			}
		}
		
		System.out.println("Done! Exiting.");
		System.exit(0);
		
	}
	/**
	 * Method to print an array of ion pairs to specified output file
	 * @param pairs
	 * @param outputDir
	 */
	private static void printPairs(ArrayList<IonPair> pairs, String outputDir){
		File outFile = new File(outputDir);
		String linesep = System.getProperty("line.separator");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			
			// Write file header
			String header = "m/z 1,m/z 2,charge,Ion 1 intensity,Ion 2 intensity, difference (Da), theoretical pair difference (Da), error (PPM), pair type";
			writer.write(header);
			writer.write(linesep);
			
			// Loop through the list of hits, printing each to a csv
			for (IonPair pair : pairs){
				writer.write(String.valueOf(pair.getMz1()) + "," + String.valueOf(pair.getMz2()) + "," + String.valueOf(pair.getCharge()) + "," + String.valueOf(pair.getIntensity1()) + "," + String.valueOf(pair.getIntensity2()) + "," + 
						 String.valueOf(pair.getMzdiff()) + "," + String.valueOf(pair.getThymzdiff()) + "," + String.valueOf(pair.getError()) 
								 + "," + pair.getPairtype());
				writer.write(linesep);
			}
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Linear search to find all types of pairs in the experimental dataset by comparing each ion in the set
	 * to each other ion and checking against a list of expected pairs. 
	 * @param expData
	 * @return
	 */
	private ArrayList<IonPair> findPairs(ArrayList<ExpIon> expData, int charge) {
		ArrayList<IonPair> pairs = new ArrayList<IonPair>();
		
		// Look for each pair type by searching each m/z against all other m/zs in the list
		for (ExpIon currentIon : expData){
			double currentMZ = currentIon.getMz();
			
			// Compute the difference between this ion and all others, and check against the list of pairs
			for (ExpIon searchIon : expData){
				double difference = (currentMZ - searchIon.getMz())/charge;
				// Search against all pair types and save hits
				for (IonPair thyPair : thyPairs){
					double thydiff = thyPair.getMzdiff()/charge;
					double error = Math.abs(difference - thydiff);
					double toleranceDa = TOLERANCEPPM / 1000000;
					// Check if the given pair is within the tolerance we've set and save it if so
					if (Math.abs(error) < toleranceDa){
						double errorPPM = error * 1000000;		// for printing in PPM
						IonPair hit = new IonPair(currentMZ,currentIon.getIntensity(),searchIon.getMz(),searchIon.getIntensity(),difference,thydiff,errorPPM,thyPair.getPairtype(),charge);
						pairs.add(hit);
					}
				}
			}
		}			
		return pairs;
	}

	/**
	 * Parses input experimental peak list to an Ion arraylist for searching. 
	 * Exp file format: m/z, intensity
	 * @param inputDir
	 * @return
	 */
	public ArrayList<ExpIon> parseExpCSV(String inputDir){
		ArrayList<ExpIon> expions = new ArrayList<ExpIon>();
		File expFile = new File(inputDir);
		
		// Read the input file
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(expFile));
			while ((line = reader.readLine()) != null){
				String[] splits = line.split(",");
				double mz = Double.parseDouble(splits[0]);
				double intensity = Double.parseDouble(splits[1]);
				
				ExpIon currentIon = new ExpIon(mz,intensity);
				expions.add(currentIon);
			}
			reader.close();
			
			} catch (FileNotFoundException ex){
				ex.printStackTrace();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		
		return expions;
	}
	
}
