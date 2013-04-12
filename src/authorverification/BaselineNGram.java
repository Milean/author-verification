package authorverification;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import authorverification.judgements.*;
import authorverification.reader.*;

public class BaselineNGram {
	
	public static final String[] READERS = {
//		  "BAR/NoIR"
//		, "BAR/LCR", 
		"SNR/BAR/NoIR" 
//		, "SNR/LCR", 
//		, "SNR/BAR",
//		, "SNR/NoIR" 
//		, "NoIR" 
//		, "LCR" 
//		, "BR"
		};

	private static String readerConfig = "No";
	private static String filterConfig = "No";
	private static String profileConfig = "No";
	private static String normalizeConfig = "No";
	private static String corpusConfig = "";
	public static PrintWriter resultOutput;
	
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
//			args[0] = "corpus/gutenberg_cases";
		}
		
		if(args.length == 0){
			System.out.println("Not enough parameters given. Expected: [Directory path]");
			System.exit(0);
		}
		
		corpusConfig = args[0];
		File corpus = new File(corpusConfig);
		if(!corpus.isDirectory()){
			System.out.println("First parameter is not a directory. Expected: [Directory path]");
			System.exit(0);
		}

		
		resultOutput = new PrintWriter(new FileWriter(new File("results_"+System.currentTimeMillis()+".log")));
		
		resultOutput.println("corpusConfig\t "+
				"N\t" +
				"ProfileSize\t" +
				"Language\t" +
				"readerConfig\t" +
				"filterConfig\t" +
				"profileConfig\t" +
				"normalizeConfig\t" +
				"JudDistInfo\t" +
				"accuracy\t" +
				"avgPos\t" +
				"avgNeg\t" +
				"percDist\t" +
				"oneMeasureSaysAll");
		
		
		for(String s : READERS){
			readerConfig = s;
			System.out.println("Starting test-run with reader "+readerConfig);

			ArrayList<AccuracyResult> accuracies = new ArrayList<AccuracyResult>();

			//baseNGrams(corpus, N, minProfileSize, maxProfileSize, increments)
			for(int n = 3; n<=4; n++){
				accuracies.addAll(baseNGrams(corpus, n, 20, 3000, 20));
				System.out.println("Done with "+n+"-grams.");
			}
			
			//Tools.printGroupedAccuracyForLanguage(accuracies, "EN");
		}
		
		resultOutput.flush();
		resultOutput.close();
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
					Reader reader = null;
					if(readerConfig.equals("BAR/NoIR")) reader = new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f)));
					if(readerConfig.equals("BAR/LCR")) reader = new BasicAlphabetReader(new LowercaseReader(new FileReader(f)));
					if(readerConfig.equals("SNR/BAR/NoIR")) reader = new StaticNumberReader(new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f))));
					if(readerConfig.equals("SNR/LCR")) reader = new StaticNumberReader(new LowercaseReader(new FileReader(f)));
					if(readerConfig.equals("SNR/BAR")) reader = new StaticNumberReader(new BasicAlphabetReader(new FileReader(f)));
					if(readerConfig.equals("SNR/NoIR")) reader = new StaticNumberReader(new NoInterpunctionReader(new FileReader(f))); 
					if(readerConfig.equals("NoIR")) reader = new NoInterpunctionReader(new FileReader(f));
					if(readerConfig.equals("LCR")) reader = new LowercaseReader(new FileReader(f));
					if(readerConfig.equals("BR")) reader = new BufferedReader(new FileReader(f));
					if(reader == null){
						System.err.println("ERROR: No Valid Reader: "+readerConfig+"!");
						System.exit(1);
					}

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
		//ngramFilter = Tools.loadNGrams(new File("filter."+n+"gram"));
		
		ArrayList<AccuracyResult> accuracies = new ArrayList<AccuracyResult>();
		for(int profileSize = minProfileSize; profileSize <= maxProfileSize; profileSize+=increment){
			accuracies.addAll(computeDistances(instances, knownAuthorForInstances, unknownForInstances, profileSize, n, ngramFilter));
		}
		
		//Tools.printAccuracyForLanguage(accuracies, "EN");
		
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
					
					filterConfig = "Known";
					knownAuthor = Tools.keepAllContaining(knownAuthor, ngramFilter.keySet());

//					filterConfig = "Unknown";
//					unknown = Tools.keepAllContaining(unknown, ngramFilter.keySet());
//
//					filterConfig = "Both"
//					knownAuthor = Tools.keepAllContaining(knownAuthor, ngramFilter.keySet());
//					unknown = Tools.keepAllContaining(unknown, ngramFilter.keySet());
				}

				profileConfig = "Author";
				knownAuthor = Tools.keepHighestN(knownAuthor, profileSize, true);

//				profileConfig = "Unknown";
//				unknown = Tools.keepHighestN(unknown, profileSize, true);
//
//				profileConfig = "Both";
//				knownAuthor = Tools.keepHighestN(knownAuthor, profileSize, true);
//				unknown = Tools.keepHighestN(unknown, profileSize, true);

				//Normalize the N-gram profiles to sum to 1
				
//				normalizeConfig = "Known";
//				knownAuthor = Tools.normalizeNGrams(knownAuthor);
//				
//				normalizeConfig = "Unknown";
//				unknown = Tools.normalizeNGrams(unknown);
//
				normalizeConfig = "Both";
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
					System.err.println("ERROR, unrecognized instance language: "+name);
					System.exit(1);
				}
			}
		}
		
		
		ArrayList<AccuracyResult> accuracies = new ArrayList<AccuracyResult>();

		
		ArrayList<JudgementDistance> judDistances = new ArrayList<JudgementDistance>();
				
		//** Expanded section for english. Does the same as this line.
		double accEn = AccuracyReview.getAccuracy(getJudgements(distancesEnglish));
		//**
//		HashMap<String, Boolean> judgements = getJudgements(distancesEnglish);
//		AccuracyReview accRevEnglish = new AccuracyReview();
//		for(String instance : judgements.keySet()){
//			boolean judgement = judgements.get(instance);
//			boolean correct = accRevEnglish.addJudgement(instance, judgements.get(instance));
//			boolean trueJudgement = correct ? judgement : !judgement;
//			JudgementDistance judDist = new JudgementDistance(distancesEnglish.get(instance), judgements.get(instance), trueJudgement, "EN");
//			judDistances.add(judDist);
//		}
//		double accEn = accRevEnglish.accuracy();
//		resultOutput.print(""+corpusConfig+"\t"+n+"\t"+profileSize+"\t"+"EN"+"\t"+readerConfig+"\t"+filterConfig+"\t"+profileConfig+"\t"+normalizeConfig+"\t");
//		Tools.printJudgementGroupDistances(judDistances);
		//** End of expanded section
		
		double accSp = AccuracyReview.getAccuracy(getJudgements(distancesSpanish));
		
		//** Expanded section for greek. Does the same as this line.
//		double accGr = AccuracyReview.getAccuracy(getJudgements(distancesGreek));
		//**
		HashMap<String, Boolean> judgements = getJudgements(distancesGreek);
		AccuracyReview accRevGreek = new AccuracyReview();
		for(String instance : judgements.keySet()){
			boolean judgement = judgements.get(instance);
			boolean correct = accRevGreek.addJudgement(instance, judgements.get(instance));
			boolean trueJudgement = correct ? judgement : !judgement;
			JudgementDistance judDist = new JudgementDistance(distancesGreek.get(instance), judgements.get(instance), trueJudgement, "EN");
			judDistances.add(judDist);
		}
		double accGr = accRevGreek.accuracy();
		resultOutput.print(""+corpusConfig+"\t"+n+"\t"+profileSize+"\t"+"GR"+"\t"+readerConfig+"\t"+filterConfig+"\t"+profileConfig+"\t"+normalizeConfig+"\t");
		Tools.printJudgementGroupDistances(judDistances);
		//** End of expanded section

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
