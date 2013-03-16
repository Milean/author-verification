package authorverification;

import java.util.Arrays;
import java.util.HashMap;

public class AccuracyReview {
	private int judgementCount;
	private int correct;
	
	private final String[] positives = {	
											"EN04","EN11","EN18","EN19","EN23","GR02","GR04","GR06",
											"GR08","GR10","GR12","GR14","GR16","GR18","GR20","SP02",
											"SP05","SP10"
										};

	private final String[] negatives = {
											"EN07","EN13","EN21","EN24","EN30","GR01","GR03","GR05",
											"GR07","GR09","GR11","GR13","GR15","GR17","GR19","SP03",
											"SP09"
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
