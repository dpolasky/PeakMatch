package ionPrediction;

import java.util.ArrayList;

/**
 * Class to contain information for a single predicted ion
 * @author Dan
 *
 */
public class Ion {
	
	private double neutralMass;		
	private double ionMass;
	private String sequence;
	private String adducts;
	private String xlinks;
	private int charge;
	private String type;		// b or y type ion
	private String decoy;
	private ArrayList<String> targets;
	
	// Various constructors - initialize information for the ion only
	/**
	 * 
	 * @param mymass
	 * @param mysequence
	 * @param myadducts
	 * @param myxlinks
	 * @param mycharge
	 * @param type
	 */
	public Ion(double mymass, String mysequence, double ionmass, int mycharge, String type, String myxlinks, String myadducts, String mydecoy){
		this.setNeutralMass(mymass);
		this.setSequence(mysequence);
		this.setAdducts(myadducts);
		this.setXlinks(myxlinks);
		this.setCharge(mycharge);
		this.setType(type);
		this.setIonMass(ionmass);
		this.setDecoy(mydecoy);
	}
	
	/**
	 * 
	 * @param mymass
	 * @param mysequence
	 * @param ionmass
	 * @param mycharge
	 * @param type
	 */
//	public Ion(double mymass, String mysequence, double ionmass, int mycharge, String type, String mydecoy){
//		this.setNeutralMass(mymass);
//		this.setSequence(mysequence);
//		this.setIonMass(ionmass);
//		this.setCharge(mycharge);
//		this.setType(type);
//		this.setDecoy(mydecoy);
//	}
	
	/**
	 * 
	 * @param mymass
	 * @param mysequence
	 * @param mytype
	 */
//	public Ion(double mymass, String mysequence, String mytype, String mydecoy){
//		this.setNeutralMass(mymass);
//		this.setSequence(mysequence);
//		this.setType(mytype);
//		this.setDecoy(mydecoy);
//
//	}
	
	/**
	 * Special constructor for initializing adducts and xlinks as ions. Sequence is empty. 
	 * @param mymass
	 * @param mysequence
	 * @param xlinks
	 * @param myadducts
	 */
	public Ion(double mymass, String mysequence, String myxlinks, String myadducts, String mydecoy, ArrayList<String> myTargets){
		this.setNeutralMass(mymass);
		this.setAdducts(myadducts);
		this.setXlinks(myxlinks);
		this.setSequence(mysequence);
		this.setDecoy(mydecoy);
		this.setTargets(myTargets);
	}
	
	/**
	 * Special constructor for initializing a blank ion with just sequence information
	 * @param subString
	 */
	public Ion(String subString){
		this.sequence = subString;
		this.adducts = "";
		this.xlinks = "";
		
	}
	
	/**
	 * 
	 * @param mymass
	 * @param mysequence
	 * @param mytype
	 * @param adducts
	 * @param xlinks
	 */
	public Ion(double mymass, String mysequence, String mytype, String myadducts, String myxlinks, String mydecoy){
		this.setNeutralMass(mymass);
		this.setSequence(mysequence);
		this.setType(mytype);
		this.setAdducts(myadducts);
		this.setXlinks(myxlinks);
		this.setDecoy(mydecoy);

	}
	
	
	
	public Ion(){
		
	}
	

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getXlinks() {
		return xlinks;
	}

	public void setXlinks(String xlinks) {
		this.xlinks = xlinks;
	}

	public String getAdducts() {
		return adducts;
	}

	public void setAdducts(String adducts) {
		this.adducts = adducts;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public double getIonMass() {
		return ionMass;
	}

	public void setIonMass(double ionMass) {
		this.ionMass = ionMass;
	}

	public double getNeutralMass() {
		return neutralMass;
	}

	public void setNeutralMass(double neutralMass) {
		this.neutralMass = neutralMass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDecoy() {
		return decoy;
	}

	public void setDecoy(String decoy) {
		this.decoy = decoy;
	}

	public ArrayList<String> getTargets() {
		return targets;
	}

	public void setTargets(ArrayList<String> targets) {
		this.targets = targets;
	}

}
