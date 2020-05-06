package it.polito.tdp.extflightdelays.model;

public class CoppiaAeroporti {

	private Airport aeroportoP;
	private Airport aeroportoA;
	private double media;
	private int n;
	
	public CoppiaAeroporti(Airport aeroportoP, Airport aeroportoA, double media) {
		super();
		this.aeroportoP = aeroportoP;
		this.aeroportoA = aeroportoA;
		this.media = media;
		n=1;
	}

	public Airport getAeroportoP() {
		return aeroportoP;
	}

	public void setAeroportoP(Airport aeroportoP) {
		this.aeroportoP = aeroportoP;
	}

	public Airport getAeroportoA() {
		return aeroportoA;
	}

	public void setAeroportoA(Airport aeroportoA) {
		this.aeroportoA = aeroportoA;
	}

	public double getMedia() {
		return media;
	}

	public void setMedia(double media) {
		this.media = media;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aeroportoA == null) ? 0 : aeroportoA.hashCode());
		result = prime * result + ((aeroportoP == null) ? 0 : aeroportoP.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoppiaAeroporti other = (CoppiaAeroporti) obj;
		if (aeroportoA == null) {
			if (other.aeroportoA != null)
				return false;
		} else if (!aeroportoA.equals(other.aeroportoA))
			return false;
		if (aeroportoP == null) {
			if (other.aeroportoP != null)
				return false;
		} else if (!aeroportoP.equals(other.aeroportoP))
			return false;
		return true;
	}

	public void aggiornaMedia(double mediaPassata) {
		n++;
		this.media = (this.media+mediaPassata)/n;
		
	}

	@Override
	public String toString() {
		return "CoppiaAeroporti " + aeroportoP + " " + aeroportoA + "  " + media + "\n";
	}
	
	
	
	
}
