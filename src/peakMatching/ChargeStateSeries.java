package peakMatching;

import java.util.ArrayList;

public class ChargeStateSeries {

	
	private ArrayList<ExpIon> ions;
	private int charge;
	
	// Constructors with empty array lists
	public ChargeStateSeries(){
		this.setIons(new ArrayList<ExpIon>());
	}
	
	public ChargeStateSeries(int mycharge){
		this.setCharge(mycharge);
		this.setIons(new ArrayList<ExpIon>());
	}
	
	// Creates a new Charge State Series containing the passed ion as the first entry in the ions arraylist
	public ChargeStateSeries(ExpIon myIon){
		ArrayList<ExpIon> newIons = new ArrayList<ExpIon>();
		newIons.add(myIon);
		this.setIons(newIons);
	}

	
	public ArrayList<ExpIon> getIons() {
		return ions;
	}

	public void setIons(ArrayList<ExpIon> ions) {
		this.ions = ions;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}
}
