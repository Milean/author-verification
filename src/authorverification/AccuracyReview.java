package authorverification;

import java.util.Arrays;

public class AccuracyReview {
	private static int judgementCount = 0;
	private static int correct = 0;
	
	private static String[] positives = {	
											"EN04","EN11","EN18","EN19","EN23","GR02","GR04","GR06",
											"GR08","GR10","GR12","GR14","GR16","GR18","GR20","SP02",
											"SP05","SP10"
										};

	private static String[] negatives = {
											"EN07","EN13","EN21","EN24","EN30","GR01","GR03","GR05",
											"GR07","GR09","GR11","GR13","GR15","GR17","GR19","SP03",
											"SP09"
										};
	
	public static void addJudgement(String instance, boolean judgement){
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
	
	public static int totalAmount(){
		return judgementCount;
	}
	
	public static int correctAmount(){
		return correct;
	}
	
	public static double accuracy(){
		return ((double)correct)/((double)judgementCount);
	}
	
	public static void reset(){
		correct = 0;
		judgementCount = 0;
	}
}
