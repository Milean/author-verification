package authorverification;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import authorverification.judgements.AccuracyResult;
import authorverification.judgements.AccuracyReview;
import authorverification.reader.BasicAlphabetReader;
import authorverification.reader.NoInterpunctionReader;

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
//			args[0] = "corpus/training";
			args[0] = "corpus/gutenberg_cases";
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
		
		ArrayList<AccuracyResult> accuracies = new ArrayList<AccuracyResult>();

		//baseNGrams(corpus, N, minProfileSize, maxProfileSize, increments)
		for(int n = 1; n<7; n++){
			accuracies.addAll(baseNGrams(corpus, n, 5, 500, 5));
		}
		
		Tools.printGroupedAccuracyForLanguage(accuracies, "EN");
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
	private static ArrayList<AccuracyResult> baseNGrams(File corpus, int n, int minProfileSize, int maxProfileSize, int increment) throws IOException{
		
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
					BasicAlphabetReader reader = new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f)));
					//BasicAlphabetReader reader = new BasicAlphabetReader(new LowercaseReader(new FileReader(f)));
					//StaticNumberReader reader = new StaticNumberReader(new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f))));
					//StaticNumberReader reader = new StaticNumberReader(new LowercaseReader(new FileReader(f)));
					//StaticNumberReader reader = new StaticNumberReader(new BasicAlphabetReader(new FileReader(f)));
					//NoInterpunctionReader reader = new NoInterpunctionReader(new FileReader(f));
					//LowercaseReader reader = new LowercaseReader(new FileReader(f));
					//BufferedReader reader = new BufferedReader(new FileReader(f));

					if(f.getName().startsWith("known")){
						knownAuthor = Tools.addCharacterNGrams(reader, n, knownAuthor);
					}
					else if(f.getName().startsWith("unknown")){
						unknown = Tools.addCharacterNGrams(reader, n, unknown);
					}
					else{
						//Error: should be no other files
						System.err.println("ERROR: Unexpected file: "+f.getAbsolutePath());
						reader.close();
						System.exit(1);
					}
					reader.close();
				}
				
				knownAuthorForInstances.put(name, knownAuthor);
				unknownForInstances.put(name, unknown);
				
			}
		}
		
		//printProfileStatistics(instances, knownAuthorForInstances, unknownForInstances);
		
		HashMap<String, Double> ngramFilter = null;
//		ngramFilter = Tools.loadNGrams(new File("filter."+n+"gram"));
		
		ArrayList<AccuracyResult> accuracies = new ArrayList<AccuracyResult>();
		for(int profileSize = minProfileSize; profileSize <= maxProfileSize; profileSize+=increment){
			accuracies.addAll(computeDistances(instances, knownAuthorForInstances, unknownForInstances, profileSize, n, ngramFilter));
		}
		
		Tools.printAccuracyForLanguage(accuracies, "EN");
		
		return accuracies;
	}

	private static ArrayList<AccuracyResult> computeDistances(
			File[] instances, 
			HashMap<String, HashMap<String, Double>> knownAuthorForInstances, 
			HashMap<String, HashMap<String, Double>> unknownForInstances,
			int profileSize,
			int n,
			HashMap<String, Double> ngramFilter ){
		
		HashMap<String, Double> distancesEnglish = new HashMap<String, Double>();
		HashMap<String, Double> distancesSpanish = new HashMap<String, Double>();
		HashMap<String, Double> distancesGreek = new HashMap<String, Double>();
		
		for(File instance : instances){
			if(instance.isDirectory()){
				String name = instance.getName();
				HashMap<String, Double> knownAuthor = knownAuthorForInstances.get(name);
				HashMap<String, Double> unknown = unknownForInstances.get(name);
				
				//Apply filters to ngram profiles.
				if(ngramFilter != null){
					knownAuthor = Tools.keepAllContaining(knownAuthor, ngramFilter.keySet());
//					unknown = Tools.keepAllContaining(unknown, ngramFilter.keySet());
				}

				knownAuthor = Tools.keepHighestN(knownAuthor, profileSize, true);
//				unknown = Tools.keepHighestN(unknown, profileSize, true);

				//Normalize the N-gram profiles to sum to 1
				knownAuthor = Tools.normalizeNGrams(knownAuthor);
				unknown = Tools.normalizeNGrams(unknown);
				
				
				double distance = Tools.distanceStamatatos2007(unknown, knownAuthor);

//				System.out.println(""+name+"\t"+distance);
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
		
		
		ArrayList<AccuracyResult> accuracies = new ArrayList<AccuracyResult>();
		
		double accEn = AccuracyReview.getAccuracy(getJudgements(distancesEnglish));
		double accSp = AccuracyReview.getAccuracy(getJudgements(distancesSpanish));
		double accGr = AccuracyReview.getAccuracy(getJudgements(distancesGreek));

		accuracies.add(new AccuracyResult(n, profileSize, ngramFilter != null, ngramFilter != null ? ngramFilter.size() : 0, "EN", accEn));
		accuracies.add(new AccuracyResult(n, profileSize, ngramFilter != null, ngramFilter != null ? ngramFilter.size() : 0, "SP", accSp));
		accuracies.add(new AccuracyResult(n, profileSize, ngramFilter != null, ngramFilter != null ? ngramFilter.size() : 0, "GR", accGr));
		
		//System.out.println(""+n+"\t"+profileSize+"\tEN\t"+accEn+"\tSP\t"+accSp+"\tGR\t"+accGr);
		
		return accuracies;
	}
	
	/**
	 * Evaluates a list of judgments per instance, returning for every instance a true/false judgment.
	 *  
	 * @param distances The list of judgments per instance. True means the author is considered the same for the known and unknown documents.
	 * @return A list of true/false judgments, mapped from the instance names.
	 */
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

	/**
	 * Prints statistics of (known and unknown) n-gram profiles for every instance.
	 * 
	 * @param instances The list of files (directories) containing instances.
	 * @param knownAuthorForInstances The list of known author ngram profiles, sorted by instance
	 * @param unknownForInstances The list of unknown categorization documents, sorted by instance
	 */
	public static void printProfileStatistics(File[] instances, 
			HashMap<String, HashMap<String, Double>> knownAuthorForInstances, 
			HashMap<String, HashMap<String, Double>> unknownForInstances ){
		
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
	}
	

	
	public static double compare(String[] authorDocs, String unknownDoc){
		return 0d;
	}
}
