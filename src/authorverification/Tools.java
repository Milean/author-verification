package authorverification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

/**
 * This class will contain commonly used tools for Information Retrieval 
 * purposes, that can be reused in other classes.
 * 
 * @author Michiel van Dam
 *
 */
public class Tools {

	public static char[] removeAccents(char[] text) {
		if(text == null) 
			return null;
		
		String normalized = Normalizer.normalize(java.nio.CharBuffer.wrap(text), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return normalized.toCharArray();
	}
	
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
		boolean FILTERDOUBLESPACES = false;
		
		if(ngrams == null){
			ngrams = new HashMap<String, Double>();
		}
		
		StringBuffer buffer = new StringBuffer(35000);
		
		int pos = 0;
		int token = reader.read();
		boolean space = false;
		while(pos < n-1){
			if(FILTERDOUBLESPACES){
				if(space && (char)token == ' '){
					token = reader.read();
					continue;
				}
				else if((char)token == ' '){
					space = true;
				}
				else{
					space = false;
				}
			}
			
			buffer.append((char)token);
			token = reader.read();
			pos++;
		}

		while(token != -1){
			if(FILTERDOUBLESPACES){
				if(space && (char)token == ' '){
					token = reader.read();
					continue;
				}
				else if((char)token == ' '){
					space = true;
				}
				else{
					space = false;
				}
			}
			
			buffer.append((char)token);
			token = reader.read();
			pos++;

			String ngram = buffer.substring(pos-n, pos);
			if(!ngrams.containsKey(ngram)){
				ngrams.put(ngram, 0d);
			}
			
			ngrams.put(ngram, ngrams.get(ngram)+1d);

			if(ngram.length() != n){
				System.out.println("ERROR: ngram of unequal size!");
			}
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
	 * Returns a new Map containing only the key-value pairs where the key is not contained in the filter Set.
	 * 
	 * @param data The old Map that will be used as base data for the filtered Map.
	 * @param n The amount of keys that will be kept.
	 * @param including Toggle to true to add all key-value pairs at the cut-off value as well, or false to 
	 * only include values higher than the cutoff value (prefers a smaller or a bigger profile)
	 *  
	 * @return A new Map, containing only key-value pairs where the key is not contained in the filter Set. 
	 */
	public static HashMap<String, Double> keepHighestN(Map<String, Double> data, int n, boolean including){
		HashMap<String, Double> result = new HashMap<String, Double>();
		
		ArrayList<Double> valueList = new ArrayList<Double>(data.values());
		Collections.sort(valueList);
		
		double cutoffValue = 0;
		if(valueList.size()>n){
			cutoffValue = valueList.get(valueList.size()-n);
		}
		
		for(String s : data.keySet()){
			if(including && data.get(s) >= cutoffValue){
				result.put(s, data.get(s));
			}
			else if((!including) && data.get(s) > cutoffValue){
				result.put(s, data.get(s));
			}
		}

		return result;
	}
	
	/**
	 * Gives statistics on the values in this N-Gram map.
	 * 
	 * @param data The old Map that will be used for statistics.
	 *  
	 * @return A String, containing several lines of information about these N-grams. 
	 */
	public static String statistics(Map<String, Double> data, boolean extensive, boolean cumulative){
		String result = "";
		
		ArrayList<Double> valueList = new ArrayList<Double>(data.values());
		Collections.sort(valueList);
		
		double currentAmount = 0d;
		if(valueList.size()>0){
			currentAmount = valueList.get(valueList.size()-1).doubleValue();
		}
		
		double highest = currentAmount;
		
		int count = 0;
		for(int i = valueList.size()-1; i>=0; i--){
			if(valueList.get(i).doubleValue() == currentAmount){
				count++;
			}
			else if(valueList.get(i).doubleValue() > currentAmount){
				System.out.println("ERROR (Tools.statistics): increasing value after sorting!");
			}
			else{
				if(extensive){
					result += ""+count+" n-grams that occur "+currentAmount+" times"+ (cumulative ? "or more.\n" : ".\n");
				}
				else{
					result += "\t"+count;
				}
				currentAmount = valueList.get(i).doubleValue();
				
				if(cumulative){
					count++;
				}
				else{
					count = 1;
				}
			}
		}

		if(extensive){
			result += ""+count+" n-grams that occur "+currentAmount+" times"+ (cumulative ? "or more.\n" : ".\n");
		}
		else{
			result += "\t"+count;
		}
		
		if(extensive){
			result += "The most frequent n-grams are: ";
			
			for(String s : data.keySet()){
				if(data.get(s) == highest){
					result += s+", ";
				}
			}
			
			result += "and the total amount of n-grams is "+data.size()+"\n";
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
	
	public static void saveNGrams(File to, HashMap<String, Double> ngrams) {
		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);
		String xml = xstream.toXML(ngrams);
		try {
			FileWriter fw = new FileWriter(to);
			fw.write(xml);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, Double> loadNGrams(File file) {
		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);
		HashMap<String, Double> ngrams = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ngrams = (HashMap<String, Double>) xstream.fromXML(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return ngrams;
	}
	
	
	public static void printAccuracyForLanguage(ArrayList<AccuracyResult> accuracies, String language){
		Collections.sort(accuracies);
		
		for(AccuracyResult ar : accuracies){
			if(ar.getLanguage().equals(language)){
				System.out.println(ar.toString());
			}
		}
	}

	public static void printGroupedAccuracyForLanguage(ArrayList<AccuracyResult> accuracies, String language){
		Collections.sort(accuracies);
		
		ArrayList<AccuracyResult> keys = new ArrayList<AccuracyResult>();
		ArrayList<AccuracyResult> correctLanguage = new ArrayList<AccuracyResult>();
		
		int n_temp = 0;
		for(AccuracyResult ar : accuracies){
			if(ar.getLanguage().equals(language)){
				if(n_temp == 0 || n_temp == ar.getN()){
					if(n_temp == 0){
						n_temp = ar.getN();
					}
					keys.add(ar);
				}
				correctLanguage.add(ar);
			}
		}
		
		for(AccuracyResult ar : keys){
			String res = ""+ar.getLanguage()+"\t"+ar.getProfilesize();
			
			for(AccuracyResult current : correctLanguage){
				if(current.getProfilesize() == ar.getProfilesize()){
					res = res + current.compactString();
				}
			}
			
			System.out.println(res);
		}
		
	}
}
