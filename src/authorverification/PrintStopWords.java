package authorverification;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PrintStopWords {
	public static void main(String[] args) throws IOException{
		HashMap<String, Double> ngramFilter = null;
		ngramFilter = Tools.loadNGrams(new File("filter.3gram"));
		
		ngramFilter = Tools.keepHighestN(ngramFilter, 50, true);
		
		Set<String> ngramSet = ngramFilter.keySet();
		Object[] ngrams = ngramSet.toArray(); 
		Arrays.sort(ngrams);
		
		int i = 0;
		for(Object o : ngrams){
			String ngram = (String)o;
			System.out.print(ngram + "\t");
			
			i++;
			if(i>=10){
				i = 0;
				System.out.println();
			}
		}

	}

}
