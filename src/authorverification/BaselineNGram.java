package authorverification;


import java.io.BufferedReader;
import java.io.File;
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
		

		//TODO: make instances for every test
		//TODO: array of knowns
		//TODO: file of unknown
		//TODO: author profile based on knowns
		//TODO: profile unknwon
		//TODO: comparison unknown known
		//TODO: judgement authorship
		
		
		String source = "corpus/test_own/michiel_swis_excerpt.txt";
		FileReader fr = new FileReader(source);
		BufferedReader br = new BufferedReader(fr);

		HashMap<String, Double> ngrams = Tools.getCharacterNGrams(br, 3);
		System.out.println(Tools.toString(ngrams));
		
	}
	
	public static double compare(String[] authorDocs, String unknownDoc){
		return 0d;
	}
}
