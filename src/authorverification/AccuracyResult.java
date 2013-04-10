package authorverification;

public class AccuracyResult implements Comparable<Object>{
	private int n;
	private int profilesize;
	private boolean filteredByCommonNGrams;
	private int ngramFilterSize;
	private String language;
	private double accuracy;
	
	public AccuracyResult(){}
	
	public AccuracyResult(int n, int profilesize,
			boolean filteredByCommonNGrams, int ngramFilterSize,
			String language, double accuracy) {
		super();
		this.n = n;
		this.profilesize = profilesize;
		this.filteredByCommonNGrams = filteredByCommonNGrams;
		this.ngramFilterSize = ngramFilterSize;
		this.language = language;
		this.accuracy = accuracy;
	}

	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	public int getProfilesize() {
		return profilesize;
	}
	public void setProfilesize(int profilesize) {
		this.profilesize = profilesize;
	}
	public boolean isFilteredByCommonNGrams() {
		return filteredByCommonNGrams;
	}
	public void setFilteredByCommonNGrams(boolean filteredByCommonNGrams) {
		this.filteredByCommonNGrams = filteredByCommonNGrams;
	}
	public int getNgramFilterSize() {
		return ngramFilterSize;
	}
	public void setNgramFilterSize(int ngramFilterSize) {
		this.ngramFilterSize = ngramFilterSize;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public int compareTo(Object arg0) {
		if(arg0 instanceof AccuracyResult){
			AccuracyResult that = (AccuracyResult)arg0;
			if(this.n < that.getN())
				return -1;
			else if(this.n > that.getN())
				return 1;
			else{
				if(this.profilesize < that.getProfilesize())
					return -1;
				else if(this.profilesize > that.getProfilesize())
					return 1;
				else{
					return this.language.compareTo(that.getLanguage());
				}
			}
		}
		return 0;
	}
	
	@Override
	public String toString(){
		return ""+n+"\t"+profilesize+"\t"+language+"\t"+accuracy;
	}
	
	public String compactString(){
		return "\t"+n+"\t"+accuracy;
	}
	
	
}
