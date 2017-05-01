package peakMatching;

import java.io.*;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import ionPrediction.Ion;
import ionPrediction.PredictorMain;

/**
 * Finds matches between experimental peak list and a theoretical/predicted list within a 
 * given tolerance using a linear search. Prints output matches and accompanying information to
 * a csv file. 
 * 
 * v2.0: 2/26/16. Updated to accept mMass inputs rather than doing data preprocessing in this program,
 * and now includes an _stats file that lists descriptive statistics for each analyzed file.
 * v2.1: 4/19/16: Updated to include a/x, c/z, d/v/w ions and improved code for internals. Confirmed
 * predicted ion masses against protein prospector for all types except internals. General code structure
 * improvements and bug fixes. Also now handles DT filtered files as a group when DTfilter boolean is set
 * to true. 
 * 
 * To use: update the tolerancePPM parameter to the appropriate tolerance for your analysis and ensure
 * that the protein in PredictorMain.protein matches your protein of interest. 
 * 
 * @author Dan Polasky
 *
 */
public class PeakMatchMain {

	// Globals
	private Ion[] thyList;
	private ArrayList<ExpIon> expList;
	private static final int BRUKER = 2;
	private static final int MANUAL = 1;
	private static final int MMASS = 0;
	private static final int DMITRY = 3;
	
	private static final double tolerancePPM = 10.0;		// Tolerance for error on hit in parts per million
	private static final double tolOffset = 21.0;	// manual calibration offset (ppm) until better solution for calibrating with Dmitry data. Moves all peaks over this much
	private static int MODE = DMITRY;
	
	private static boolean DTfilter = true;
	private static boolean chargeFilter = true;
	//	private static final String OUTDIR = "C://Users//dpolasky//Desktop//DataAnalysis//_PeptideAnalysis//Outputs//DT filter//No losses";
	private static String OUTDIR = "C://Users//dpolasky//Desktop//DataAnalysis//_PeptideAnalysis//Outputs//DT filter";
	//	private static final String OUTDIR = "C://Users//Dan//Desktop//DataAnalysis//_PeptideAnalysis//Outputs";

	// Manual directories for now
//	private static String TOPDIR = "C://Users//dpolasky//Desktop//DataAnalysis//_PeptideAnalysis//ExperimentalData//mMass Exp Data Annotated";
	private static String TOPDIR = "C://Users//dpolasky//Desktop//DataAnalysis//_PeptideAnalysis//_Dmitry_peak_outputs";
	private static final String thyDir = "C://Users//dpolasky//Desktop//DataAnalysis//_PeptideAnalysis//Theoretical peptide Lists";

	private static String thyName;

	private JFileChooser fc = null;
	private JFileChooser thyfc = null;
	private static final int[] ONE_CHARGES = {1};
	private static final int[] TWO_CHARGES = {2,3,4,5};
	
	// ****NOTE: using "two_charges" as the default array for everything for big proteins since there's some mixing****
	private static final int[] THREE_CHARGES = {3,4,5};
	private static final int[] FOUR_CHARGES = {4,5};
	private static final int[] DEFAULT_CHARGES = {1,2,3,4,5,6,7};


	public PeakMatchMain(){
		// Check analysis mode and update fields accordingly
		if (MODE == BRUKER){
			DTfilter = false;
			chargeFilter = false;
			TOPDIR = "C://Users//dpolasky//Desktop//DataAnalysis//_PeptideAnalysis//Bruker-UCLA//Peaklists";
			OUTDIR = "C://Users//dpolasky//Desktop//DataAnalysis//_PeptideAnalysis//Bruker-UCLA//Outputs";
		} else if (MODE == MMASS){
			DTfilter = true;
			chargeFilter = true;
		} else if (MODE == DMITRY){
			DTfilter = false;
			chargeFilter = false;
			System.out.println("Dmitry mode, DT filt = " + DTfilter + " chargefilt = " + chargeFilter);
		}
		
		// Load experimental file(s) to use
		fc = new JFileChooser();
		File defaultDir = new File(TOPDIR);
		fc.setCurrentDirectory(defaultDir);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Select the data file(s) to analyze");
		fc.setFileFilter(new TxtOrCSVFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);
		
		// Parse theoretical peak list to generate sorted Ion array to search
		thyfc = new JFileChooser();
		File defaultthyDir = new File(thyDir);
		thyfc.setCurrentDirectory(defaultthyDir);
		thyfc.setAcceptAllFileFilterUsed(false);
		thyfc.setDialogTitle("Select the theoretical peptide lists to use");
		thyfc.setFileFilter(new FileNameExtensionFilter("CSV files only","csv"));
		thyfc.setFileSelectionMode(2);
		thyfc.setMultiSelectionEnabled(false);

		if (thyfc.showDialog(null, "OK") == 0){
			File theoryFile = thyfc.getSelectedFile();			
			thyList = parseThyCSV(theoryFile);
			thyName = theoryFile.getName();
		}

	}

	public PeakMatchMain(File expFile, Ion[] theoryList){
		// Import experimental peak list
		//		expList = parseExpCSV(expFile.toString());

		// MANUAL ANNOTATION VERSION - comment out above and deisotoping step
//				expList = parseExpCSVManualAnnotations(expFile.toString());

		if (MODE == BRUKER){
			// parse Bruker file
			expList = parseBrukerExp(expFile.toString());
		} else if (MODE == MMASS){
			// mMass data version
			expList = parseMMassExp(expFile.toString());
		} else if (MODE == DMITRY){
			expList = parseDmitryExp(expFile.toString());
		} else {
			// Import experimental peak list
			expList = parseExpCSV(expFile.toString());
		}

		// Parse theoretical peak list to generate sorted Ion array to search
		thyList = theoryList;
	}

	public static void main(String[] args) {

		if (MODE == MMASS){
			runDTMatcher();
		} else {
			runSingleMatcher();
		}

		System.out.println("Done!");
		System.exit(0);
	}

	/**
	 * Executes main method code for the non-DT filtering case (each exp file corresponds to 
	 * a complete analysis).
	 */
	private static void runSingleMatcher(){
		// Initialize constructor - imports peak lists and file chooser
		PeakMatchMain matcher = new PeakMatchMain();
		JFrame mainframe = new JFrame();
		String statsName = "";
		ArrayList<String> statsNames = new ArrayList<String>();
		ArrayList<Double> peakNums = new ArrayList<Double>();

		// Added support for multiple files with a file chooser
		if (matcher.fc.showDialog(mainframe,"OK") == 0){
			File[] expfiles = matcher.fc.getSelectedFiles();
			ArrayList<ArrayList<HitCandidate>> allFileOutputs = new ArrayList<ArrayList<HitCandidate>>();

			for (File expfile : expfiles){
				String outputName = expfile.getName();
				int endIndex = outputName.lastIndexOf(".");
				outputName = outputName.substring(0, endIndex);

				// Make new matcher instance for each expfile - maybe not the best program structure..
				PeakMatchMain newmatcher = new PeakMatchMain(expfile,matcher.thyList);

				ArrayList<ExpIon> currentExps = newmatcher.expList;
				double numPeaks = newmatcher.expList.size();
				peakNums.add(numPeaks);
				ArrayList<HitCandidate> allhits = new ArrayList<HitCandidate>();

				// Loop through the experimental data, matching charged ions to theory lists
				for (ExpIon chargedIon : currentExps){
					// Basic match within PPM tolerance
					allhits.addAll(newmatcher.linSearchMatch(newmatcher.thyList,chargedIon,chargedIon.getCharge()));
				}
				// Print hits to file
				String outname = OUTDIR + File.separator + outputName + "_" + String.valueOf((int) tolerancePPM) + "ppm_" + thyName + "_hits.csv";
				statsName = OUTDIR + File.separator + outputName + "_" + String.valueOf((int) tolerancePPM) + "ppm_" + thyName + "_stats.csv";
				statsNames.add(statsName);
				newmatcher.printHits(allhits,outname);
				allFileOutputs.add(allhits);
			}	
			// Print stats on final outputs
			matcher.printStats(allFileOutputs, statsNames, peakNums);
		}
	}
	/**
	 * Executes main method code for the DT filtering case (several exp files make up one analysis).
	 * Requires that only files for a single analysis be selected (might include filename parsing and
	 * advanced handling later...)
	 */
	private static void runDTMatcher(){
		// Initialize constructor - imports peak lists and file chooser
		PeakMatchMain matcher = new PeakMatchMain();
		JFrame mainframe = new JFrame();
		String statsName = "";
		ArrayList<String> statsNames = new ArrayList<String>();
		ArrayList<Double> peakNums = new ArrayList<Double>();
		ArrayList<HitCandidate> allhits = new ArrayList<HitCandidate>();
		String outname = "";

		// Added support for multiple files with a file chooser
		if (matcher.fc.showDialog(mainframe,"OK") == 0){
			File[] expfiles = matcher.fc.getSelectedFiles();
			ArrayList<ArrayList<HitCandidate>> allFileOutputs = new ArrayList<ArrayList<HitCandidate>>();

			for (File expfile : expfiles){
				String outputName = expfile.getName();
				int endIndex = outputName.lastIndexOf(".");
				outputName = outputName.substring(0, endIndex);

				// Make new matcher instance for each expfile - maybe not the best program structure..
				PeakMatchMain newmatcher = new PeakMatchMain(expfile,matcher.thyList);

				ArrayList<ExpIon> currentExps = newmatcher.expList;
				double numPeaks = newmatcher.expList.size();
				// Add up peaks instead of making multiple entries
				if (peakNums.size() > 0){
					double newNum = peakNums.get(0) + numPeaks;
					peakNums.remove(0);
					peakNums.add(newNum);
				} else {
					peakNums.add(numPeaks);
				}

				// Loop through the experimental data, matching charged ions to theory lists
				for (ExpIon chargedIon : currentExps){
					// Basic match within PPM tolerance
					allhits.addAll(newmatcher.linSearchMatch(newmatcher.thyList,chargedIon,chargedIon.getCharge()));
				}
				// Print hits to file
				outname = OUTDIR + File.separator + outputName + "_" + String.valueOf((int) tolerancePPM) + "ppm" +".csv";
				statsName = OUTDIR + File.separator + outputName + "_" + String.valueOf((int) tolerancePPM) + "ppm" + "_stats_" + thyName + ".csv";
				statsNames.add(statsName);
				//				newmatcher.printHits(allhits,outname);
			}	
			// Print stats on final outputs - only add hits to a single list, so there's only one stats file output. Uses first statsname only
			allFileOutputs.add(allhits);
			matcher.printStats(allFileOutputs, statsNames, peakNums);
			String totalOutName = outname.substring(0, outname.lastIndexOf(".")) + "_hits_" + thyName + ".csv";
			matcher.printHits(allhits, totalOutName);
		}
	}
	
	/*
	 * Manual annotation version
	 */
	public ArrayList<ExpIon> parseExpCSV(String inputDir){
		ArrayList<ExpIon> expions = new ArrayList<ExpIon>();
		ArrayList<ExpIon> unfilteredIons = new ArrayList<ExpIon>();
		File expFile = new File(inputDir);

		// Read the input file
		String line = null;
		double maxintensity = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(expFile));
			while ((line = reader.readLine()) != null){
				String[] splits = line.split(",");
				double mz = 0.0, intensity = 0.0;
				try{
					mz = Double.parseDouble(splits[0]);
					intensity = Double.parseDouble(splits[1]);
					unfilteredIons.add(new ExpIon(mz,intensity));
					if ( intensity > maxintensity){
						maxintensity = intensity;
					}
				} catch (ArrayIndexOutOfBoundsException ex){
					// Empty lines at the end of the csv will hit this, but it's not a problem. Just ignore empty lines.
					//					System.out.println("error on line " + counter + " nothing on line. Line ignored");
				}


			}
			reader.close();

		} catch (FileNotFoundException ex){
			ex.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}

		// Only read in this ion if it's intensity is > than cutoff filter to avoid low S/N "peaks"
		//		double SNcutoff = maxintensity/SNR_RATIO_CUTOFF;
		//		if (SNcutoff > INTENSITY_ABSOLUTE_CUTOFF){
		//			SNcutoff = INTENSITY_ABSOLUTE_CUTOFF;
		//		}
		//		System.out.println("Intensity cutoff for file " + inputDir + " is " + SNcutoff);
		for (ExpIon unfilteredIon : unfilteredIons){
			double intensity = unfilteredIon.getIntensity();
			double mz = unfilteredIon.getMz();
			//			if (intensity > SNcutoff){
			ExpIon currentIon = new ExpIon(mz,intensity);
			expions.add(currentIon);
			//			}
		}

		return expions;
	}

	/*
	 * Read in Bruker (DataAnalysis 4.4) tab-delimited peak list file. File is already fully preprocessed
	 */
	public ArrayList<ExpIon>parseBrukerExp(String inputDir){
		ArrayList<ExpIon> expions = new ArrayList<ExpIon>();
		File expFile = new File(inputDir);
		// Read the input file
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(expFile));
			// skip header line
			reader.readLine();
			double mz = 0.0, absintensity = 0.0;
			int charge = 0;
			
			// Read in the file information
			while ((line = reader.readLine()) != null){
				try{
					// Read the parameters
					String[] splits = line.split("	");		// tab delimited
					mz = Double.parseDouble(splits[0]);
					charge = Integer.parseInt(splits[1].substring(0, splits[1].length() - 1));	//Remove the "+" following the charge
					absintensity = Double.parseDouble(splits[2]);

					// create a new ion with these parameters and add it to the list
					expions.add(new ExpIon(mz,absintensity,charge,true));
				}
				catch (ArrayIndexOutOfBoundsException ex){
					// catches blank lines at end of file - simply skip (do nothing)
				}
			}
			reader.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		return expions;
	}
	
	/*
	 * Dmitry's output (v1, from Dmitry's build version) lists columns as follows: (tab delimited), 1 line header
	 * m/z	IM mono	abs mono	ab_mono_total	mz_top	im_top	ab_top_peak	ab_top_total	cluster_peak_count	idx_top	charge	mz_cluster_avg	ab_cluster_peak	ab_cluster_total
	 */
	public ArrayList<ExpIon> parseDmitryExp(String inputDir){
		ArrayList<ExpIon> expions = new ArrayList<ExpIon>();
		File expFile = new File(inputDir);
		// Read the input file
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(expFile));
			// skip header line
			reader.readLine();
			double mz = 0.0, absintensity = 0.0;
			int charge = 0;
			
			// Read in the file information
			while ((line = reader.readLine()) != null){
				try{
					// Read the parameters
					String[] splits = line.split("	");		// tab delimited
					mz = Double.parseDouble(splits[0]);
					charge = Integer.parseInt(splits[10]);
//					charge = Integer.parseInt(splits[1].substring(0, splits[1].length() - 1));	//Remove the "+" following the charge
					absintensity = Double.parseDouble(splits[2]);

					// create a new ion with these parameters and add it to the list
					expions.add(new ExpIon(mz,absintensity,charge,true));
				}
				catch (ArrayIndexOutOfBoundsException ex){
					// catches blank lines at end of file - simply skip (do nothing)
				} catch (NumberFormatException ex){
					// catch extra details at the end of the file - ignore
				}
			}
			reader.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		return expions;
	}
	
	/**
	 * Read in pre-deconvoluted peaklist from mMass output. mMass data includes (in order): m/z,
	 * absolute intensity, rel int, s/n ratio, charge, neutral mass, fwhm, resolution. 
	 * @param inputDir
	 * @return
	 */
	public ArrayList<ExpIon> parseMMassExp(String inputDir){
		ArrayList<ExpIon> expions = new ArrayList<ExpIon>();
		File expFile = new File(inputDir);

		// Read the input file
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(expFile));
			while ((line = reader.readLine()) != null){
				String[] splits = line.split(",");
				double mz = 0.0, absintensity = 0.0, relintensity = 0.0, sn = 0.0, neutral = 0.0;
				int charge = 0;
				try{
					mz = Double.parseDouble(splits[0]);
					absintensity = Double.parseDouble(splits[1]);
					relintensity = Double.parseDouble(splits[2]);
					// sometimes S/N column is empty
					try {
						sn = Double.parseDouble(splits[3]);
					} catch (NumberFormatException ex){
						sn = 0;
					}
					charge = Integer.parseInt(splits[4]);
					neutral = Double.parseDouble(splits[5]);

					// allows user to decide whether or not to use DT filtering to narrow peaklist
					if (DTfilter){
						// Get the charge from the filename (BASED ON NAMING CONVENTIONS - charge must be next to the first '+' in the name)
						String[] namesplits = inputDir.split("\\+");
						int expFileCharge = 0;		
						try{
							expFileCharge = Integer.parseInt(namesplits[1].substring(0, 1));
						} catch (NumberFormatException ex){
							try {
								expFileCharge = Integer.parseInt(namesplits[0].substring(namesplits[0].length() - 1));
							} catch (NumberFormatException ex2){
								System.out.println("Charge not parsed. Charge read was '" + namesplits[0].substring(namesplits.length - 1, namesplits.length) 
										+ "'," + "Make sure charge is listed as either +X or X+ in the filename");
								expFileCharge = -1;
							}
						} 
						// If charge filtering, get the charge associated with this file and determine if the peak follows the rules
						int[] allowedCharges = DEFAULT_CHARGES;
						if (chargeFilter){
							if (expFileCharge == 1){
								allowedCharges = ONE_CHARGES;
							// NOTE: USING 2,3,4,5 as allowed charges for everything above one for large proteins due to mixing
							} else if (expFileCharge == 2 || expFileCharge == 3 || expFileCharge == 4 || expFileCharge == 5){
//							else if (expFileCharge == 2){
								allowedCharges = TWO_CHARGES;
							} 
//							else if (expFileCharge == 3){
//								allowedCharges = THREE_CHARGES;
//							} else if (expFileCharge == 4){
//								allowedCharges = FOUR_CHARGES;
//							} 
//							else if (expFileCharge == -1){
//								// In case of parsing errors, just use default charge set
//								allowedCharges = DEFAULT_CHARGES;
//							}
							
						} else {
							allowedCharges = DEFAULT_CHARGES;
						}

						// compare charge to allowedCharges, include it in the final list if it matches
						for (int goodCharge : allowedCharges){
							if (charge == goodCharge){
								expions.add(new ExpIon(mz,absintensity,charge,true,relintensity,sn,neutral));
							}
						}
					} else {
						expions.add(new ExpIon(mz,absintensity,charge,true,relintensity,sn,neutral));
					}

				} catch (ArrayIndexOutOfBoundsException ex){
					// Empty lines at the end of the csv will hit this, but it's not a problem. Just ignore empty lines.
					//					System.out.println("error on line " + counter + " nothing on line. Line ignored");
				}
			}
			reader.close();
		} catch (FileNotFoundException ex){
			ex.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		return expions;
	}


	/**
	 * Parses input theoretical peak list to an Ion array for searching. Input MUST be sorted in ascending order of m/z
	 * Thy file format: m/z, charge, sequence, ion type (b/y), neutral mass, xlinks (string), adducts (string)
	 * @param inputDir
	 * @return
	 */
	public Ion[] parseThyCSV(File thyFile){
		ArrayList<Ion> thyList = new ArrayList<Ion>();

		// Read in the file to determine length of the array
		//		File thyFile = new File(inputDir);

		// Read the input file
		String line = null;
		try {

			BufferedReader reader = new BufferedReader(new FileReader(thyFile));
			while ((line = reader.readLine()) != null){
				String[] splits = line.split(",");

				// Get the Ion information from the file
				double mz = 0;
				try {
					mz = Double.parseDouble(splits[0]);
				} catch(NumberFormatException ex){
					// Skip the header (and any other lines without m/z information)
					continue;
				}
				int z = Integer.parseInt(splits[1]);
				String sequence = splits[2];
				String ionType = splits[3];
				double neutralmz = Double.parseDouble(splits[4]);
				// Catch "empty" information for xlinks and adducts. Empty information will mean the 
				// 		splits[] won't have an entry for it. 
				String xlinks;
				try {
					xlinks = splits[7];
					// added additional columns if extra xlink information - include
					for (int i = 8; i < splits.length; i++){
						try {
							xlinks = xlinks + "," + splits[i];
						} catch (ArrayIndexOutOfBoundsException ex){
							// no additional data to add, ignore
						}
					}

				} catch (ArrayIndexOutOfBoundsException ex){
					xlinks = "";
				}

				String adducts;
				try{
					adducts = splits[5];
				} catch (ArrayIndexOutOfBoundsException ex){
					adducts = "";			
				}
				String decoy;
				try{
					decoy = splits[6];
				} catch (ArrayIndexOutOfBoundsException ex){
					decoy = "";			
				}
				// Make a new Ion and put it in the list
				Ion currentIon = new Ion(neutralmz,sequence,mz,z,ionType,xlinks,adducts,decoy);
				thyList.add(currentIon);
			}
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Once the file has been completely read into the arrayList, switch to array for binary searching and return
		Ion[] exampleIons = new Ion[1];
		Ion[] thyArray = thyList.toArray(exampleIons);

		return thyArray;
	}


	/**
	 * Linear search method. Compares the ExpIon to a list of thy ions, records and returns hits
	 * @param thylist
	 * @param expions
	 * @return
	 */
	public ArrayList<HitCandidate> linSearchMatch(Ion[] thylist, ExpIon expion, int charge){
		ArrayList<HitCandidate> hits = new ArrayList<HitCandidate>();
		double error;

		for (Ion thyIon : thyList){
			// Compute the 'error' of this theoretical peak to the observed peak
			error = thyIon.getIonMass() - expion.getMz();
			error = error/thyIon.getIonMass();
			// DMITRY mode: manual cal by shifting all peaks by tolOffset ppm
			if (MODE == DMITRY){
				error += (tolOffset / 1000000);
			}

			// If the absolute error is less than the tolerance we've set, a hit has occurred
			double toleranceDa = tolerancePPM / 1000000;
			if (Math.abs(error) < toleranceDa){

				// Confirm we have the right charge state, ignore anything else
				if (charge == thyIon.getCharge()){
					double errorPPM = error * 1000000;		// for printing in PPM
					HitCandidate hit = new HitCandidate(expion.getMz(),expion.getIntensity(),thyIon.getIonMass(),
							errorPPM,thyIon.getCharge(),thyIon.getSequence(),thyIon.getType(),thyIon.getXlinks(),thyIon.getAdducts(),thyIon.getNeutralMass(),thyIon.getDecoy());
					hits.add(hit);
				}
			}
		}
		return hits;
	}

	/**
	 * Prints hit list specified to file specified
	 * @param allhits
	 * @param outputDir
	 */
	public void printHits(ArrayList<HitCandidate> allhits, String outputDir){
		File outFile = new File(outputDir);
		String linesep = System.getProperty("line.separator");

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

			// Write file header
			String header = "ExpMZ,Exp Intensity,ThyMZ,ThyNeutralMass,Error (PPM),Thy Charge,Thy Sequence,Ion Type,Thy adducts,DECOY?,Thy xlinks";
			writer.write(header);
			writer.write(linesep);

			// Loop through the list of hits, printing each to a csv
			double lastHitNeutralMass = 0;
			double lastHitError = 0;
			for (HitCandidate hit : allhits){
				// Don't print duplicate hits (same ion, just different positioning of xlinks)
				if (Math.floor(hit.getNeutralmass() * 100000000) == lastHitNeutralMass && Math.floor(hit.getError() * 1000000) == lastHitError){
					//don't print this hit, as it is a duplicate
				} else {
					writer.write(String.valueOf(hit.getExpMZ()) + "," + String.valueOf(hit.getExpIntensity()) + "," +
							String.valueOf(hit.getThyMZ()) + "," + String.valueOf(hit.getNeutralmass()) + "," + String.valueOf(hit.getError()) + "," + String.valueOf(hit.getCharge()) 
							+ "," + hit.getSequence() + "," + hit.getType() + "," + hit.getAdducts() + "," + hit.getDecoy() + ","  + hit.getXlinks());
					writer.write(linesep);
				}
				// Truncate these values at large decimal places to avoid floating point problems with matching
				lastHitNeutralMass = Math.floor(hit.getNeutralmass() * 100000000);
				lastHitError = Math.floor(hit.getError() * 1000000);
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Major method to compute and print descriptive statistics for each analysis conducted. Loops through
	 * the output files in allOutputs and prints a statistical summary in .csv form to the corresponding
	 * directories in statsDirs. Evaluates FDR, modification numbers and types, and sequence coverage. 
	 * Depends on PredictorMain.protein for determining sequence coverage - make sure that's the same protein
	 * being evaluated. 
	 * Could definitely modularize (e.g. computing, then printing), but would involve passing lots of information
	 * so I've just left it as is for now. 
	 * @param allOutputs
	 * @param statsDirs
	 */
	public void printStats(ArrayList<ArrayList<HitCandidate>> allOutputs, ArrayList<String> statsDirs, ArrayList<Double> peakNums){
		int fileCounter = 0;
		String linesep = System.getProperty("line.separator");
		// Get the current protein - depends on the predictor being updated to the right one. 
		String protein = PredictorMain.protein;
		int proteinLength = protein.length();

		try {
			// Edit - making one stats file per output
			//			File totalOutFile = new File(statsDirs.get(0));
			//			BufferedWriter writer = new BufferedWriter(new FileWriter(totalOutFile));

			// Loop through each output file, computing stats and printing to file
			for (ArrayList<HitCandidate> allhits : allOutputs){
				String statsDir = statsDirs.get(fileCounter);
				double totalPeaks = peakNums.get(fileCounter);
				fileCounter++;
				File outFile = new File(statsDir);
				BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

				// Compute desired statistics about the hits
				int decoys = 0;
				double totalHits = allhits.size();

				// Numbers of mods - add each hit with a given number of mods to a list for computing stats
				ArrayList<HitCandidate> unmods = new ArrayList<HitCandidate>(),onemods = new ArrayList<HitCandidate>(),twomods = new ArrayList<HitCandidate>(),threemods = new ArrayList<HitCandidate>(),moremods = new ArrayList<HitCandidate>();

				// Mod Types - record the number of times a given mod is found
				ArrayList<String> mods = new ArrayList<String>();
				double[] modfreqs = new double[10],modints = new double[10];

				// Coverage array that increments each residue once whenever it is covered
				int[] coverages = new int[proteinLength];
				int[] cleavages = new int[proteinLength];
				int bCoverage = 0, yCoverage = 0;

				double totalInt = 0, unmodInt = 0, onemodInt = 0, twomodInt = 0, threemodInt = 0, moremodInt = 0;

				// Loop through the data and count the numbers of each type of information we're interested in
				for (HitCandidate hit : allhits){
					// Check if this is a decoy. If so, do NOT include it's characteristics in any other stats
					if (hit.getDecoy().matches("DECOY")){
						// do NOT compute stats for decoys
						decoys++;	

					} else {
						// This is not a decoy, so compute all other stats for it
						String hitmods = hit.getXlinks();
						totalInt = totalInt + hit.getExpIntensity();
						// Split in case of multiple modifications - get all of them 
						String[] modsplits = hitmods.split(",");
						int numMods = modsplits.length;

						// *************** Number of mods
						if (numMods < 1){
							// Unmodified
							unmods.add(hit);
							unmodInt = unmodInt + hit.getExpIntensity();
						} else if (numMods == 1){
							if (modsplits[0].matches("")){
								// Actually not a hit, but written this way for nicer printing in the print hits method
								unmods.add(hit);
								unmodInt = unmodInt + hit.getExpIntensity();
							} else {
								onemods.add(hit);	
								onemodInt = onemodInt + hit.getExpIntensity();
							}
						} else if (numMods == 2){
							twomods.add(hit);
							twomodInt = twomodInt + hit.getExpIntensity();
						} else if (numMods == 3){
							threemods.add(hit);
							threemodInt = threemodInt + hit.getExpIntensity();
						} else {
							// numMods > 3
							moremods.add(hit);
							moremodInt = moremodInt + hit.getExpIntensity();
						}

						// ***************** Mod types computations
						for (String mod : modsplits){
							if (! mods.contains(mod)){
								// If we haven't seen this one yet, add it to the list
								mods.add(mod);
								// compute freq/intensity
								int index = mods.indexOf(mod);
								double currentInt = modfreqs[index];
								modfreqs[index] = currentInt + 1;
								modints[index] = modints[index] + hit.getExpIntensity();
							} else {
								// Find the index where this mod is stored, and increment the corresponding index in the double arraylist
								int index = mods.indexOf(mod);
								double currentInt = modfreqs[index];
								modfreqs[index] = currentInt + 1;
								modints[index] = modints[index] + hit.getExpIntensity();
							}
						}

						// ****************** Sequence coverage
						String fullType = hit.getType();
						// The first character of the full type is it's actual type (b, y, etc), and the remainder is its length
						// added catch for 2-character types
						String type;
						int length;
						try {
							type = fullType.substring(0,1);
							length = Integer.parseInt(fullType.substring(1));
						} catch (NumberFormatException ex){
							type = fullType.substring(0,2);
							length = Integer.parseInt(fullType.substring(2));
						}
						// Get the ranges covered by this type
						if (type.matches("b")){
							// Get max length covered from N-terminus and increment all AAs covered
							if (length > bCoverage){
								bCoverage = length;
							}
							for (int i = 0; i < length; i++){
								coverages[i] = coverages[i] + 1;
							}
							cleavages[length] = cleavages[length] + 1;

						} else if (type.matches("y")){
							// Get max length covered from C-terminus and increment all AAs covered
							if (length > yCoverage){
								yCoverage = length;
							}
							for (int i = proteinLength - 1; i >= (proteinLength - length); i--){
								coverages[i] = coverages[i] + 1;
							}
							cleavages[proteinLength - length] = cleavages[proteinLength - length] + 1;
						} else {

							// internal ion - might add handling later

						}											


					} // end: not decoys	
				} // end types collection 

				// Now compute final stats as %s
				double realHits = totalHits - decoys;
				double hitRatio = realHits/totalPeaks * 100;
				double decoyRatio = decoys/totalHits * 100;

				double unmodRatio = unmods.size()/realHits * 100;
				double onemodRatio = onemods.size()/realHits * 100;
				double twomodRatio = twomods.size()/realHits * 100;
				double threemodRatio = threemods.size()/realHits * 100;
				double moremodRatio = moremods.size()/realHits * 100;

				// convert to relative intensity now that the total hit intensity is computed
				for (int m = 0; m < modints.length; m++){
					modints[m] = modints[m]/totalInt;
				}

				// Compute coverage
				double finalCoverage = bCoverage + yCoverage;
				double coverageRatio = 0;
				if (finalCoverage > proteinLength){
					// Every residue is covered
					coverageRatio = 100;
					finalCoverage = proteinLength;
				} else {
					coverageRatio = finalCoverage / proteinLength * 100;
				}
				// Compute cleavage site stats
				double numSitesCleaved = 0;
				for (int site : cleavages){
					if (site > 0){
						numSitesCleaved++;
					}
				}
				double percentCleaved = numSitesCleaved/proteinLength * 100;


				// Print to output

				// File Header
				String[] filesplits = statsDir.split("/");
				String filename = filesplits[filesplits.length - 1];
				writer.write("***************" + linesep);
				writer.write("File: ," + filename);
				writer.write(linesep);

				// Hit stats
				writer.write("Exp peaks (total):," + String.valueOf(totalPeaks) + linesep);
				writer.write("Non-decoy Hits:," + String.valueOf(realHits) + linesep);
				writer.write("Matched %:," + String.format("%.1f", hitRatio) + linesep);

				// FDR
				writer.write("Tolerance (ppm):," + String.format("%.1f", tolerancePPM) + linesep);
				writer.write("FDR (%): ," + String.format("%.1f", decoyRatio) + linesep + linesep);

				// Modification number data
				String modHeaders = "Number of Mods:,Unmodified,One Mod,Two Mods,Three Mods,>3 mods";
				writer.write(modHeaders);
				writer.write(linesep);		
				String modData = String.format("%s,%.1f,%.1f,%.1f,%.1f,%.1f","% of total:",unmodRatio,onemodRatio,twomodRatio,threemodRatio,moremodRatio);
				String modCts = String.format("%s,%s,%s,%s,%s,%s,","Frequency:",unmods.size(),onemods.size(),twomods.size(),threemods.size(),moremods.size());
				writer.write(modCts + linesep + modData + linesep);
				writer.write("Rel Intensity," + String.format("%.1f,%.1f,%.1f,%.1f,%.1f",unmodInt/totalInt*100,onemodInt/totalInt*100,twomodInt/totalInt*100,threemodInt/totalInt*100,moremodInt/totalInt*100));
				writer.write(linesep + linesep);

				// Modification type data
				int i = 0;
				String modTypes = "";
				String modInts = "";
				String modRatios = "";
				String modRelInts = "";
				for (String mod : mods){
					if (mod.matches("")){
						modTypes = modTypes + "unmodified,";
					} else {
						modTypes = modTypes + mod + ",";
					}
					modInts = modInts + String.valueOf(modfreqs[i]) + ",";
					modRatios = modRatios + String.format("%.1f", modfreqs[i]/realHits * 100) + ",";
					modRelInts = modRelInts + String.format("%.1f",modints[i] * 100) + ",";
					i++;
				}
				writer.write("Modification types:," + modTypes + linesep + "Frequency:," + modInts + linesep);
				writer.write("% of total:," + modRatios + linesep);
				writer.write("Rel Int:," + modRelInts);
				writer.write(linesep + linesep);

				// Sequence coverage summary
				writer.write("Sequence Coverage:," + finalCoverage + ",of," + proteinLength + linesep);
				writer.write("Coverage %:," + String.format("%.1f",coverageRatio) + linesep);
				writer.write("# Sites Cleaved:," + numSitesCleaved + linesep);
				writer.write("% Sites Cleaved:," + String.format("%.1f",percentCleaved) + linesep);
				// cleavage site map
				writer.write("Coverage map:," + linesep);
				writer.write("b ion:,");
				for (int l = 1; l < proteinLength + 1; l++) writer.write(String.valueOf(l) + ",");
				writer.write(linesep);
				writer.write("y ion:,");
				for (int l = proteinLength; l > 0; l--) writer.write(String.valueOf(l) + ",");
				writer.write(linesep);
				writer.write("cleavages at site:,");
				for (int j = 0; j < proteinLength; j++){
					writer.write(String.valueOf(cleavages[j]) + ",");
				}
				// coverage map
				writer.write(linesep);
				writer.write("amino acid:,");
				for (int j = 0; j < proteinLength; j++){
					writer.write(protein.substring(j, j+1) + ",");
				}
				writer.write(linesep);
				writer.write("Residue hits:,");
				for (int resCoverage : coverages){
					writer.write(String.valueOf(resCoverage) + ",");
				}
				writer.write(linesep + linesep);

				writer.flush();
				writer.close();
			}

		} catch (IOException ex){
			ex.printStackTrace();
		}
	}

}
