package pairFinder;

/**
 * Datatype class for handling crosslinker pair data in pair finder
 * @author Dan
 *
 */
public class IonPair {
	
	private double mz1;
	private double mz2;
	private double intensity1;
	private double intensity2;
	private double mzdiff;
	private double thymzdiff;
	private double error;
	private String pairtype;
	private int charge;
	
	// Constructor for the pairtypes themselves - contain only the theoretical difference and pairtype identifier
	public IonPair(double thymzdiff,String pairtype){
		this.setMzdiff(thymzdiff);
		this.setPairtype(pairtype);
	}
	
	// Constructor for matches - contains the experimental mzs and the pairtype
	public IonPair(double mymz1, double myintensity1, double mymz2, double myintensity2, double mymzdiff, double thymzdiff, double myerror, String mypairtype,int charge){
		this.setMz1(mymz1);
		this.setMz2(mymz2);
		this.setIntensity1(myintensity1);
		this.setIntensity2(myintensity2);
		this.setMzdiff(mymzdiff);
		this.setThymzdiff(thymzdiff);
		this.setError(myerror);
		this.setPairtype(mypairtype);
		this.setCharge(charge);
	}

	public double getMz1() {
		return mz1;
	}

	public void setMz1(double mz1) {
		this.mz1 = mz1;
	}

	public double getMz2() {
		return mz2;
	}

	public void setMz2(double mz2) {
		this.mz2 = mz2;
	}

	public double getMzdiff() {
		return mzdiff;
	}

	public void setMzdiff(double mzdiff) {
		this.mzdiff = mzdiff;
	}

	public String getPairtype() {
		return pairtype;
	}

	public void setPairtype(String pairtype) {
		this.pairtype = pairtype;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getThymzdiff() {
		return thymzdiff;
	}

	public void setThymzdiff(double thymzdiff) {
		this.thymzdiff = thymzdiff;
	}

	public double getIntensity1() {
		return intensity1;
	}

	public void setIntensity1(double intensity1) {
		this.intensity1 = intensity1;
	}

	public double getIntensity2() {
		return intensity2;
	}

	public void setIntensity2(double intensity2) {
		this.intensity2 = intensity2;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}
	
	
	
}
