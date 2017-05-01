package peakMatching;

/**
 * Hit candidates are possible hits - includes information from both exp and thy lists. Intended
 * to be returned and printed as the final output of the peakMatching program
 * @author Dan
 *
 */
public class HitCandidate {

	// Comparison parameters
	private double expMZ;
	private double expIntensity;
	private double thyMZ;
	private double error;
	
	// Information imported from thy list
	private int charge;
	private String sequence;
	private String type;
	private String xlinks;
	private String adducts;
	private double neutralmass;
	private String decoy;


	// Constructor includes all information
	public HitCandidate(double myexpmz, double myexpintensity, double mythymz, double myerror, int mycharge,
			String mysequence, String mytype, String myxlinks, String myadducts, double myNM, String mydecoy){
		
		this.setExpMZ(myexpmz);
		this.setExpIntensity(myexpintensity);
		this.setThyMZ(mythymz);
		this.setError(myerror);
		this.setCharge(mycharge);
		this.setSequence(mysequence);
		this.setType(mytype);
		this.setXlinks(myxlinks);
		this.setAdducts(myadducts);
		this.setNeutralmass(myNM);
		this.setDecoy(mydecoy);
		
	}

	
	public double getNeutralmass() {
		return neutralmass;
	}

	public void setNeutralmass(double neutralmass) {
		this.neutralmass = neutralmass;
	}
	
	public double getExpMZ() {
		return expMZ;
	}

	public void setExpMZ(double expMZ) {
		this.expMZ = expMZ;
	}

	public double getExpIntensity() {
		return expIntensity;
	}

	public void setExpIntensity(double expIntensity) {
		this.expIntensity = expIntensity;
	}

	public double getThyMZ() {
		return thyMZ;
	}

	public void setThyMZ(double thyMZ) {
		this.thyMZ = thyMZ;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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


	public String getDecoy() {
		return decoy;
	}


	public void setDecoy(String decoy) {
		this.decoy = decoy;
	}
	
	
}
