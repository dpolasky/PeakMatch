package peakMatching;

/**
 * Class for experimental peaks - analogous to the "Ion" class in IonPredictor, but with different information.
 * Separated to keep them from being confused. 
 * @author Dan
 *
 */
public class ExpIon {

	private double mz;
	private double intensity;
	private int charge;
	private boolean isMonoisotopic;
	
	// If from mMass, may include additional info
	private double relInt;
	private double sn;
	private double neutralmass;
	
	public ExpIon(double myMZ, double myIntensity){
		this.setMz(myMZ);
		this.setIntensity(myIntensity);
	}
	
	public ExpIon(double myMZ, double myIntensity, int myCharge, boolean myMono){
		this.setCharge(myCharge);
		this.setIntensity(myIntensity);
		this.setMonoisotopic(myMono);
		this.setMz(myMZ);
	}

	// mMass data constructor
	public ExpIon(double myMZ, double myabsint, int myCharge, boolean myMono, double myrelint, double mySN, double myNeutral){
		this.setCharge(myCharge);
		this.setIntensity(myabsint);
		this.setMonoisotopic(myMono);
		this.setMz(myMZ);
		this.setNeutralmass(myNeutral);
		this.setSn(mySN);
		this.setRelInt(myrelint);
		
	}
	
	public double getMz() {
		return mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public boolean isMonoisotopic() {
		return isMonoisotopic;
	}

	public void setMonoisotopic(boolean isMonoisotopic) {
		this.isMonoisotopic = isMonoisotopic;
	}

	public double getRelInt() {
		return relInt;
	}

	public void setRelInt(double relInt) {
		this.relInt = relInt;
	}

	public double getSn() {
		return sn;
	}

	public void setSn(double sn) {
		this.sn = sn;
	}

	public double getNeutralmass() {
		return neutralmass;
	}

	public void setNeutralmass(double neutralmass) {
		this.neutralmass = neutralmass;
	}
	
}
