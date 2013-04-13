package authorverification.judgements;

public class JudgementDistance{
	private double distance;
	private boolean judgement;
	private boolean trueJudgement;
	private String language;
	
	public JudgementDistance(){}
	
	public JudgementDistance(double distance, boolean judgement,
			boolean correct, String language) {
		super();
		this.distance = distance;
		this.judgement = judgement;
		this.trueJudgement = correct;
		this.language = language;
	}
	
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public boolean getJudgement() {
		return judgement;
	}
	public void setJudgement(boolean judgement) {
		this.judgement = judgement;
	}
	public boolean getTrueJudgement() {
		return trueJudgement;
	}
	public void setTrueJudgement(boolean correct) {
		this.trueJudgement = correct;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}
