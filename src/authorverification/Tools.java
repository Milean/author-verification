package authorverification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * This class will contain commonly used tools for Information Retrieval 
 * purposes, that can be reused in other classes.
 * 
 * @author Michiel van Dam
 *
 */
public class Tools {

	/**
	 * Returns a map of character n-grams contained in this String, with a count how often each n-gram occurs.
	 * 
	 * @param input The string to split up into n-grams.
	 * @param n The value for n. For example, a value of 3 will give a list of 3-grams.
	 * @return A map of character n-grams, with a count how often each n-gram occurs in the given String.
	 */
	public static Map<String, Double> getCharacterNGrams(String input, int n){
		HashMap<String, Double> result = new HashMap<String, Double>();
		
		String ngram;
		
		for(int start = 0; start+n < input.length(); start++){
			ngram = input.substring(start, start+n);
			
			Double amount = result.get(ngram);
			if(amount == null){
				amount = 0d;
			}
			amount = amount+1;
			
			result.put(ngram, amount);
		}
		
		return result;
	}
	
	/**
	 * Returns a map of character n-grams contained in this reader, with a count how often each n-gram occurs.
	 * 
	 * @param input The string to split up into n-grams.
	 * @param n The value for n. For example, a value of 3 will give a list of 3-grams.
	 * @return A map of character n-grams, with a count how often each n-gram occurs in the given String.
	 */
	public static HashMap<String, Double> getCharacterNGrams(Reader reader, int n) throws IOException{
		HashMap<String, Double> ngrams = new HashMap<String, Double>();
		
		ngrams = addCharacterNGrams(reader, n, ngrams);
		
		return ngrams;
	}
	
	/**
	 * Returns a map of character n-grams contained in this reader combined with all n-grams already
	 * provided, with a count how often each n-gram occurs.
	 * 
	 * @param input The string to split up into n-grams.
	 * @param n The value for n. For example, a value of 3 will give a list of 3-grams.
	 * @param ngrams The HashMap containing existing n-grams.
	 * 
	 * @return A map of character n-grams, with a count how often each n-gram occurs in the given String.
	 */
	public static HashMap<String, Double> addCharacterNGrams(Reader reader, int n, HashMap<String, Double> ngrams) throws IOException{
		if(ngrams == null){
			ngrams = new HashMap<String, Double>();
		}
		
		NGramTokenizer ngramTok = new NGramTokenizer(reader, n, n);
		CharTermAttribute terms = ngramTok.addAttribute(CharTermAttribute.class);
		while(ngramTok.incrementToken()){
			String ngram = terms.toString();
			
			if(!ngrams.containsKey(ngram)){
				ngrams.put(ngram, 0d);
			}
			
			ngrams.put(ngram, ngrams.get(ngram)+1d);
		}
		
		return ngrams;

	}
	

	
	public static HashMap<String, Double> normalizeNGrams(Map<String, Double> ngrams){
		HashMap<String, Double> result = new HashMap<String, Double>();
		double amount = 0;
		Set<String> keys = ngrams.keySet();
		for(String key : keys){
			amount += ngrams.get(key);
		}
		
		for(String key : keys){
			double normValue = (double)(ngrams.get(key));
			normValue = normValue / amount;
			result.put(key, normValue);
		}
		return result;
	}
	
	/**
	 * Writes a list of N-grams to a file. 
	 * Each line contains the N-gram, a tab character, and the amount of occurances.
	 * 
	 * @param file The File that should be (over)written with the n-grams.
	 * @param ngrams The list of N-grams
	 * @return true iff writing was succesful, false otherwise
	 */
	public static boolean writeCharacterNGrams(File file, Map<String, Double> ngrams){
		try {
			PrintWriter printer = new PrintWriter(file);
			Set<String> keys = ngrams.keySet();
			for(String key : keys){
				printer.println(key+"\t"+ngrams.get(key).doubleValue());
			}
			printer.close();
			
			return true;
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Reads a list of N-grams from a file. 
	 * Each line should contain the N-gram, a tab character, and the amount of occurances.
	 * 
	 * @param file The File all N-grams should be read from.
	 * @return A Map containing all N-grams with the amount of occurrance, or null in case of a reading error.
	 */
	public static HashMap<String, Double> readCharacterNGrams(File file){
		try {
			HashMap<String, Double> ngrams = new HashMap<String, Double>();
			
			Scanner reader = new Scanner(file);
			reader.useDelimiter("(\\t|\\r\\n)");

			while(reader.hasNext()){
				String key = reader.next();
				double value = Double.parseDouble(reader.next());
				ngrams.put(key, value);
			}
			
			reader.close();
			
			return ngrams;
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns a new Map containing only the key-value pairs where the key is contained in the filter Set.
	 * 
	 * @param data The old Map that will be used as base data for the filtered Map.
	 * @param filter The list of keys that will be kept. 
	 * @return A new Map, containing only key-value pairs where the key is contained in the filter Set. 
	 */
	public static HashMap<String, Double> keepAllContaining(Map<String, Double> data, Set<String> filter){
		HashMap<String, Double> result = new HashMap<String, Double>();
		for(String s : filter){
			if(data.containsKey(s)){
				result.put(s,data.get(s));
			}
		}
		return result;
	}
	
	/**
	 * Returns a new Map containing only the key-value pairs where the key is not contained in the filter Set.
	 * 
	 * @param data The old Map that will be used as base data for the filtered Map.
	 * @param filter The list of keys that will be removed. 
	 * @return A new Map, containing only key-value pairs where the key is not contained in the filter Set. 
	 */
	public static HashMap<String, Double> filterAllContaining(Map<String, Double> data, Set<String> filter){
		HashMap<String, Double> result = new HashMap<String, Double>(data);
		for(String s : filter){
			if(result.containsKey(s)){
				result.remove(s);
			}
		}
		return result;
	}
	
	/**
	 * Calculates and returns the cosine similarity of two vectors expressed as key-value pairs.
	 * 
	 * A lower value means a lower similarity.
	 * 
	 * @param one The first vector.
	 * @param two The second vector.
	 * @return The cosine similarity of the two parameter vectors.
	 */
	public static double cosineSim(Map<String, Double> one, Map<String, Double> two){
		Set<String> keySetOne = one.keySet();
		Set<String> keySetTwo = two.keySet();
		
		double nominator = 0d;
		
		for(String keyOne : keySetOne){
			double oneValue = one.get(keyOne);
			if(two.containsKey(keyOne)){
				double twoValue = two.get(keyOne);
				nominator += oneValue*twoValue;
			}
		}
		
		double denominatorOne = 0d;
		for(String keyOne : keySetOne){
			double oneValue = one.get(keyOne);
			denominatorOne += oneValue*oneValue;
		}
		denominatorOne = Math.sqrt(denominatorOne);

		double denominatorTwo = 0d;
		for(String keyTwo : keySetTwo){
			double twoValue = two.get(keyTwo);
			denominatorTwo += twoValue*twoValue;
		}
		denominatorTwo = Math.sqrt(denominatorTwo);
		
		double denominator = denominatorOne*denominatorTwo;
		
		double cosineSim = nominator/denominator;
		
		return cosineSim;
	}
	
	/**
	 * Calculates the normalized distance function proposed by [Stamatatos 2009].
	 * "Intrinsic Plagiarism Detection Using Character n-gram Profiles"
	 * 
	 * A lower value means a higher similarity.
	 * 
	 * @param one Author profile A
	 * @param two Author profile B
	 * @return the distance between the profiles.
	 */
	public static double distanceStamatatos2007(Map<String, Double> one, Map<String, Double> two){
		double sum = 0d;
		
		Set<String> keys = one.keySet();
		for(String key : keys){
			double fa = one.get(key);
			double fb = 0;
			if(two.containsKey(key)){
				fb = two.get(key);
			}
			
			sum = sum + Math.pow(2 * (fa - fb) / (fa + fb), 2);
		}
		
		//normalize the result (as in Stamatatos 2007)
		double result = sum / (4d*(double)keys.size());
		
		return result;
	}
	
	/**
	 * Returns a String representation of a Map of Strings with corresponding Integers. 
	 * Every line first contains the Integer, then a tab character, and then the key 
	 * of the Map which has that Integer set.
	 * 
	 * @param input The Map for which a String representation is needed.
	 * @return A String representation of the input Map.
	 */
	public static String toString(Map<String, Double> input){
		String result = "";
		
		Set<String> keys = input.keySet(); 
		for(String ngram : keys){
			Double amount = input.get(ngram);
			result += amount+"\t"+ngram+"\n";
		}
		
		return result;
	}
	
}
