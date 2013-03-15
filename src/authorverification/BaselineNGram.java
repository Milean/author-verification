package authorverification;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class BaselineNGram {
	public static void main(String[] args) throws IOException{

		//TODO: remove this for command line launch;
		if(args == null || args.length == 0){
			args = new String[1];
			args[0] = "corpus/training";
		}
		
		if(args.length == 0){
			System.out.println("Not enough parameters given. Expected: [Directory path]");
			System.exit(0);
		}
		
		String path = args[0];
		File corpus = new File(path);
		if(!corpus.isDirectory()){
			System.out.println("First parameter is not a directory. Expected: [Directory path]");
			System.exit(0);
		}
		
		baseNGrams(corpus, 3);
	}
	

	/**
	 * Baseline N-gram implementation
	 * 
	 * Creates N-gram profiles for a known author, and compares this against an unknown document.
	 * Makes a decision whether or not the document belongs to the same author.
	 * 
	 * @param corpus The directory with all test instances
	 * @param n The fixed length of the n-grams that will be used
	 * @throws IOException
	 */
	private static void baseNGrams(File corpus, int n) throws IOException{
		
		File[] instances = corpus.listFiles();
		for(File instance : instances){
			if(instance.isDirectory()){
				
				File[] authorfiles = instance.listFiles();
				
				HashMap<String, Double> knownAuthor = new HashMap<String, Double>();
				HashMap<String, Double> unknown = new HashMap<String, Double>();
				
				for(File f : authorfiles){
					if(f.getName().startsWith("known")){
						BufferedReader reader = new BufferedReader(new FileReader(f));
						knownAuthor = Tools.addCharacterNGrams(reader, n, knownAuthor);
						reader.close();
					}
					else if(f.getName().startsWith("unknown")){
						BufferedReader reader = new BufferedReader(new FileReader(f));
						unknown = Tools.addCharacterNGrams(reader, n, unknown);
						reader.close();
					}
					else{
						//Error: should be no other files
						System.err.println("ERROR: Unexpected file: "+f.getAbsolutePath());
					}
				}
				
				knownAuthor = Tools.keepAllContaining(knownAuthor, unknown.keySet());
				unknown = Tools.keepAllContaining(unknown, knownAuthor.keySet());
				
				knownAuthor = Tools.normalizeNGrams(knownAuthor);
				unknown = Tools.normalizeNGrams(unknown);
				
				double distance = Tools.distanceStamatatos2007(knownAuthor, unknown);
				if(distance < 1.00){
					AccuracyReview.addJudgement(instance.getName(), true);
				}
				else{
					AccuracyReview.addJudgement(instance.getName(), false);
				}
				System.out.println(instance.getName()+" "+distance);
			}
		}
		
		System.out.println("Accuracy reached: "+AccuracyReview.accuracy());
	}
	
	public static double compare(String[] authorDocs, String unknownDoc){
		return 0d;
	}
}
