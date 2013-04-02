package authorverification;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class BaselineNGram {
	public static void main(String[] args) throws IOException{

//		StringReader test = new StringReader("De kat krabt de krullen van de trap");
//		NoFormatReader reader = new NoFormatReader(test);
//		char[] res = new char[1024];
//		int amount = reader.read(res);
//		System.out.println(""+amount+" characters read, in an array of size "+res.length);
//		
//		char[] second = Tools.removeAccents(res);
//		
//		System.exit(0);
		
		
		//TODO: remove next four lines for command line launch
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
		
		//baseNGrams(corpus, N, minProfileSize, maxProfileSize, increments)
		baseNGrams(corpus, 9, 10, 1000, 10);
	}
	

	/**
	 * Baseline N-gram implementation
	 * 
	 * Creates N-gram profiles for a known author, and compares this against an unknown document.
	 * Makes a decision whether or not the document belongs to the same author.
	 * 
	 * @param corpus The directory with all test instances
	 * @param n The fixed length of the n-grams that will be used
	 * @param profileSize The amount of most frequent N-grams that will be used for the profiles.
	 * 
	 * @throws IOException
	 */
	private static void baseNGrams(File corpus, int n, int minProfileSize, int maxProfileSize, int increment) throws IOException{
		
		File[] instances = corpus.listFiles();

		HashMap<String, HashMap<String, Double>> knownAuthorForInstances = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, HashMap<String, Double>> unknownForInstances = new HashMap<String, HashMap<String, Double>>();
		
		for(File instance : instances){
			if(instance.isDirectory()){
				
				String name = instance.getName();
				
				File[] authorfiles = instance.listFiles();
				
				HashMap<String, Double> knownAuthor = new HashMap<String, Double>();
				HashMap<String, Double> unknown = new HashMap<String, Double>();
				
				for(File f : authorfiles){
					if(f.getName().startsWith("known")){
						//BasicAlphabetReader reader = new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f)));
						BasicAlphabetReader reader = new BasicAlphabetReader(new LowercaseReader(new FileReader(f)));
						//StaticNumberReader reader = new StaticNumberReader(new BasicAlphabetReader(new FileReader(f)));
						//NoInterpunctionReader reader = new NoInterpunctionReader(new FileReader(f));
						//LowercaseReader reader = new LowercaseReader(new FileReader(f));
						//BufferedReader reader = new BufferedReader(new FileReader(f));
						knownAuthor = Tools.addCharacterNGrams(reader, n, knownAuthor);
						reader.close();
					}
					else if(f.getName().startsWith("unknown")){
						//BasicAlphabetReader reader = new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f)));
						BasicAlphabetReader reader = new BasicAlphabetReader(new LowercaseReader(new FileReader(f)));
						//StaticNumberReader reader = new StaticNumberReader(new BasicAlphabetReader(new FileReader(f)));
						//NoInterpunctionReader reader = new NoInterpunctionReader(new FileReader(f));
						//LowercaseReader reader = new LowercaseReader(new FileReader(f));
						//BufferedReader reader = new BufferedReader(new FileReader(f));
						unknown = Tools.addCharacterNGrams(reader, n, unknown);
						reader.close();
					}
					else{
						//Error: should be no other files
						System.err.println("ERROR: Unexpected file: "+f.getAbsolutePath());
						System.exit(1);
					}
				}
				
				knownAuthorForInstances.put(name, knownAuthor);
				unknownForInstances.put(name, unknown);
				
//				if(name.startsWith("GR01")){
//					System.out.println(knownAuthor.size()+" known Author "+n+"-grams in "+name+": "+"\n"+Tools.toString(knownAuthor));
//				}

			}
		}
				
		System.out.println("\nKNOWN AUTHOR: ");
		for(File instance : instances){
			if(instance.isDirectory()){
				String name = instance.getName();
				System.out.println(name+Tools.statistics(knownAuthorForInstances.get(name), false, true));
			}
		}
		System.out.println("\nUNKNOWN: ");
		for(File instance : instances){
			if(instance.isDirectory()){
				String name = instance.getName();
				System.out.println(name+Tools.statistics(unknownForInstances.get(name), false, true));
			}
		}
		
		for(int profileSize = minProfileSize; profileSize <= maxProfileSize; profileSize+=increment){

			HashMap<String, Double> distancesEnglish = new HashMap<String, Double>();
			HashMap<String, Double> distancesSpanish = new HashMap<String, Double>();
			HashMap<String, Double> distancesGreek = new HashMap<String, Double>();
			
			for(File instance : instances){
				if(instance.isDirectory()){
					
					String name = instance.getName();
					
					HashMap<String, Double> knownAuthor = knownAuthorForInstances.get(name);
					HashMap<String, Double> unknown = unknownForInstances.get(name);
					
						
					knownAuthor = Tools.keepHighestN(knownAuthor, profileSize, true);
					unknown = Tools.keepHighestN(unknown, profileSize, true);
	
	//				if(name.startsWith("SP")){
	//					System.out.println("Highest frequency "+n+"-grams in "+name+": \n"+Tools.toString(knownAuthor));
	//				}
	
					knownAuthor = Tools.normalizeNGrams(knownAuthor);
					unknown = Tools.normalizeNGrams(unknown);
					
					double distance = Tools.distanceStamatatos2007(unknown, knownAuthor);
	
	//				if(name.startsWith("TEST")){
	//					System.out.println("Distance: "+distance);
	//				}
					
					if(name.startsWith("EN")){
						distancesEnglish.put(name, distance);
					}
					else if(name.startsWith("SP")){
						distancesSpanish.put(name, distance);
					}
					else if(name.startsWith("GR")){
						distancesGreek.put(name, distance);
					}
					else if(name.startsWith("TEST")){
						//do nothing
					}
					else{
						System.out.println("ERROR, unrecognized instance language: "+name);
						System.exit(1);
					}
				}
			}
			
			double accEn = AccuracyReview.getAccuracy(getJudgements(distancesEnglish));
			double accSp = AccuracyReview.getAccuracy(getJudgements(distancesSpanish));
			double accGr = AccuracyReview.getAccuracy(getJudgements(distancesGreek));
	
			System.out.println(""+n+"\t"+profileSize+"\tEN\t"+accEn+"\tSP\t"+accSp+"\tGR\t"+accGr);
		}
	}
	
	private static HashMap<String, Boolean> getJudgements(HashMap<String, Double> distances){
		HashMap<String, Boolean> judgements = new HashMap<String, Boolean>();
		
		double average = 0d;
		
		for(String instance : distances.keySet()){
			average += distances.get(instance);
		}
		
		average = average / ((double)distances.size());

		for(String instance : distances.keySet()){
			if(distances.get(instance) < average){
				judgements.put(instance, true);
			}
			else{
				judgements.put(instance, false);
			}
		}		
		
		return judgements;
	}
	
	public static double compare(String[] authorDocs, String unknownDoc){
		return 0d;
	}
}
