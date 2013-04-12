package authorverification.judgements;

import java.util.Arrays;
import java.util.HashMap;

public class AccuracyReview {
	private int judgementCount;
	private int correct;
	
	private final String[] positives = {	
											"EN004","EN011","EN018","EN019","EN023",
											"EN100","EN101","EN102","EN103","EN104","EN105","EN106","EN107","EN108","EN109",
											"EN110","EN111","EN112","EN113","EN114","EN115","EN116","EN117","EN118","EN119",
											"EN120","EN121","EN122","EN123","EN124","EN125","EN126","EN127","EN128","EN129",
											
											"GR02","GR04","GR06","GR08","GR10","GR12","GR14","GR16","GR18","GR20",
											
											"SP02","SP05","SP10"
										};

	private final String[] negatives = {
											"EN007","EN013","EN021","EN024","EN030",
											"EN150","EN151","EN152","EN153","EN154","EN155","EN156","EN157","EN158","EN159",
											"EN160","EN161","EN162","EN163","EN164","EN165","EN166","EN167","EN168","EN169",
											"EN170","EN171","EN172","EN173","EN174","EN175","EN176","EN177","EN178","EN179",
											"EN180","EN181","EN182","EN183","EN184","EN185","EN186","EN187","EN188","EN189",
											
											"GR01","GR03","GR05","GR07","GR09","GR11","GR13","GR15","GR17","GR19",
											
											"SP03","SP09"
										};
	
	public AccuracyReview(){
		judgementCount = 0;
		correct = 0;
	}
	
	public void addJudgement(String instance, boolean judgement){
		if(Arrays.binarySearch(positives, instance) >= 0){
			judgementCount++;
			if(judgement){
				correct++;
			}
		}
		else if(Arrays.binarySearch(negatives, instance) >= 0){
			judgementCount++;
			if(!judgement){
				correct++;
			}
		}
		else{
			//Unknown instance. This AccuracyReview can't judge it.
			System.err.println("Unknown author verification instance: "+instance);
		}
	}
	
	public int totalAmount(){
		return judgementCount;
	}
	
	public int correctAmount(){
		return correct;
	}
	
	public double accuracy(){
		return ((double)correct)/((double)judgementCount);
	}
	
	public void reset(){
		correct = 0;
		judgementCount = 0;
	}
	
	public static double getAccuracy(HashMap<String, Boolean> judgements){
		AccuracyReview temp = new AccuracyReview();
		for(String instance : judgements.keySet()){
			temp.addJudgement(instance, judgements.get(instance));
		}
		double accuracy = temp.accuracy();
		temp = null;
		return accuracy;
	}
}
