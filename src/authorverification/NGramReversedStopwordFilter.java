package authorverification;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

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
		
		//baseNGrams(corpus, N, minProfileSize, maxProfileSize, increments)
		saveMostCommonNGrams(corpus, 6, 2500);
	}
	
	private static void saveMostCommonNGrams(File corpus, int n, int amount) throws IOException{
		
		File[] instances = corpus.listFiles();

		HashMap<String, Double> ngrams = new HashMap<String, Double>();
		
		for(File instance : instances){
			if(instance.isDirectory()){
				
				File[] authorfiles = instance.listFiles();
				
				for(File f : authorfiles){
					System.out.println("Adding "+f.getAbsolutePath());
					BasicAlphabetReader reader = new BasicAlphabetReader(new NoInterpunctionReader(new FileReader(f)));
					
					//** Different configuration options for reader. 
					//** Use same configuration for creating filter and for running test
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

		int size = ngrams.size();
		
		System.out.println("\n"+n+"-gram profile of size "+size+": ");
		System.out.println(Tools.statistics(ngrams, true, true));
		System.out.println();
		
		HashMap<String, Double> mostCommon = Tools.keepHighestN(ngrams, amount, true);
		mostCommon = Tools.normalizeNGrams(mostCommon);
		
		Tools.saveNGrams(new File("filter."+n+"gram"), mostCommon);
	}
	


}
