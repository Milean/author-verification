package authorverification.preprocessing;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import authorverification.Tools;
import authorverification.reader.BasicAlphabetReader;
import authorverification.reader.NoInterpunctionReader;

public class NGramReversedStopwordFilter {
	public static void main(String[] args) throws IOException{

		//TODO: remove next four lines for command line launch
		if(args == null || args.length == 0){
			args = new String[1];
//			args[0] = "corpus/training";
			args[0] = "corpus/filter_training";
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
		
		//saveMostCommonNGrams(corpus, N, amount)
//		saveMostCommonNGrams(corpus, 1,   41 );
//		saveMostCommonNGrams(corpus, 2,  700 );
//		saveMostCommonNGrams(corpus, 3, 3000 );
//		saveMostCommonNGrams(corpus, 4, 4000 );
//		saveMostCommonNGrams(corpus, 5, 5000 );
//		saveMostCommonNGrams(corpus, 6, 5000 );
		saveMostCommonNGrams(corpus, 1, 10000 );
		saveMostCommonNGrams(corpus, 2, 10000 );
		saveMostCommonNGrams(corpus, 3, 10000 );
		saveMostCommonNGrams(corpus, 4, 10000 );
		saveMostCommonNGrams(corpus, 5, 10000 );
		saveMostCommonNGrams(corpus, 6, 10000 );
	}
	
	/**
	 * Processes all files contained in the corpus given as an argument
	 * splits them up into n-grams and
	 * saves the most common n-grams to a file, for later use.
	 *   
	 * @param corpus The directory containing (other directories) with training texts.
	 * @param n The size of n-grams. e.g. n=3 would result in 3-grams.
	 * @param amount The amount of n-grams to save to file. e.g. 2500 would save the 2500 most common n-grams. It will include slightly more if n-grams are tied for 2500th most common.
	 * @throws IOException
	 */
	private static void saveMostCommonNGrams(File corpus, int n, int amount) throws IOException{
		
		File[] instances = corpus.listFiles();

		HashMap<String, Double> ngrams = new HashMap<String, Double>();
		
		System.out.print("Adding files...");
		for(File instance : instances){
			if(instance.isDirectory()){
				
				File[] authorfiles = instance.listFiles();
				
				for(File f : authorfiles){
					//System.out.println("Adding "+f.getAbsolutePath());
					System.out.print(".");
					
					//** Different configuration options for reader. 
					//** Use same configuration for creating filter and for running test
					BasicAlphabetReader reader = new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f)));
					//BasicAlphabetReader reader = new BasicAlphabetReader(new LowercaseReader(new FileReader(f)));
					//StaticNumberReader reader = new StaticNumberReader(new BasicAlphabetReader(new FileReader(f)));
					//NoInterpunctionReader reader = new NoInterpunctionReader(new FileReader(f));
					//LowercaseReader reader = new LowercaseReader(new FileReader(f));
					//BufferedReader reader = new BufferedReader(new FileReader(f));

					ngrams = Tools.addCharacterNGrams(reader, n, ngrams);
					reader.close();
				}
			}
		}
		System.out.println(" done.");

		int size = ngrams.size();
		
		System.out.println("\n"+n+"-gram profile of size "+size+": ");
//		System.out.println(Tools.statistics(ngrams, true, true));
//		System.out.println();
		
		HashMap<String, Double> mostCommon = Tools.keepHighestN(ngrams, amount, true);
		System.out.println("Most common list of size "+mostCommon.size()+"\n");
		mostCommon = Tools.normalizeNGrams(mostCommon);
		
		Tools.saveNGrams(new File("filter."+n+"gram"), mostCommon);
	}
}
