// Name: Grace Everts
// Assignment: 2B

package lm;
import java.util.*;
import java.io.*;


public class DiscountLMModel {
	private String filename;
    private double discount;
	private int tokenCount;
    private HashMap<String, Double> uniCount = null;
	private HashMap<String, HashMap<String, Double>> biCount = null;
	private HashMap<String, Double> uniProb = null;
	private HashMap<String, HashMap<String, Double>> biProb = null;
	private HashMap<String, Double> alpha = null;
	private HashMap<String, ArrayList<Range>> wordDistributions = new HashMap<>();
	

    public DiscountLMModel(String filename, double discount){
        this.discount = discount;
        this.filename = filename;
		this.tokenCount = 0;
		this.uniCount = new HashMap<String, Double>();
		this.biCount = new HashMap<String, HashMap<String, Double>>();
		this.uniProb = new HashMap<String, Double>();
		this.biProb = new HashMap<String, HashMap<String, Double>>();
		this.alpha = new HashMap<String, Double>();

		uniCount.put("<s>", 0.0);
		uniCount.put("</s>", 0.0);

		uniProb.put("<s>", 0.0);
		uniProb.put("</s>", 0.0);

		train();
    }

	/**
	 * will train the model
	 */
	private void train(){
		 getCount();
		 fillUnigramProb();
		 fillBigramProb();
		 fillAlpha();
	}

	public String generateParagraph(){
		Random r = new Random();
		int numSentences = r.nextInt(50);
		String paragraph = "";

		for(int i = 0; i < numSentences; i++){
			paragraph += generateSentence();
		}

		return paragraph;
	}

	public String generateSentence(){
		String currentWord = "<s>";
		Double curMaxProb = 0.0;
		Random r = new Random();
		String finalSentence = "";

		while(!currentWord.equals("</s>") && !currentWord.equals("no string")){
			double rangeStart = 0.0;
			HashMap<String, Double> wordDistribution = biProb.get(currentWord);
			ArrayList<Range> ranges = new ArrayList<>();
			for (String key:wordDistribution.keySet()){
				double curBiProb = wordDistribution.get(key);
				double rangeEnd = rangeStart + curBiProb;
				Range toAdd = new Range(rangeStart, rangeEnd, key);
				ranges.add(toAdd);
				rangeStart += curBiProb;
				curMaxProb = rangeEnd;
			}

			wordDistributions.put(currentWord, ranges);
			double randomNum = curMaxProb * r.nextDouble();
			currentWord = binarySearch(ranges, 0, ranges.size()-1, randomNum);
			if(!currentWord.equals("</s>") && !currentWord.equals("no string")){
				finalSentence += currentWord + " ";
			}
		}

		return finalSentence;
	}

	String binarySearch(ArrayList<Range> arr, int l, int r, double randNum) { 
        if (r >= l)
        { 
            int mid = l + (r - l) / 2; 
			
			String comparisonResult = arr.get(mid).contains(randNum);
			if(comparisonResult.equals("lower")){
				return binarySearch(arr, l, mid-1, randNum);
			 } else if(comparisonResult.equals("higher")) {
				return binarySearch(arr, mid+1, r, randNum);
			 } else {
				return comparisonResult;
			 } 
        } 
 
        // We reach here when element is not present 
        // in array 
        return "no string"; 
    } 

    /**
	 * Given a sentence, return the log of the probability of the sentence based on the LM.
	 * 
	 * @param sentWords the words in the sentence.  sentWords should NOT contain <s> or </s>.
	 * @return the log probability
	 */
	public double logProb(ArrayList<String> sentWords){
		sentWords.add(0,"<s>");
		sentWords.add("</s>");

		// sum of the logs of the individual bigram probabilities
		double logProb = 0;
		
		for(int i = 0; i < sentWords.size()-1; i++){
			String currentWord = sentWords.get(i);
			String nextWord = sentWords.get(i+1);

			if(!uniCount.containsKey(currentWord)){
				sentWords.set(i, "<UNK>");
				currentWord = sentWords.get(i);
			} else if (!uniCount.containsKey(nextWord)){
				sentWords.set(i+1, "<UNK>");
				nextWord = sentWords.get(i+1);
			}
			double bigramProb = getBigramProb(currentWord, nextWord);
			logProb += Math.log10(bigramProb);
		}

		return logProb;
    }

    /**
	 * Given a text file, calculate the perplexity of the text file, that is the negative average per word log
	 * probability
	 * 
	 * @param filename a text file.  The file will contain sentences WITHOUT <s> or </s>.
	 * @return the perplexity of the text in file based on the LM
	 */
	public double getPerplexity(String filename) {
		double perplexity = 0;
		double n = 0;
		
		// read filename line by line
		// FileReader, not StringReaader
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
			// for each line in training set
            while ((line = reader.readLine()) != null) {
				ArrayList<String> sentence = new ArrayList<String>(Arrays.asList(line.split("\\s")));
				perplexity += logProb(sentence);
				n += sentence.size() - 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		// right now, perplexity is the sum of all the logprobs for each bigram
		// calculate actual perplexity
		perplexity = Math.pow(10,-perplexity/n);

		return perplexity;
    }

    /**
	 * Returns p(second | first)
	 * 
	 * @param first
	 * @param second
	 * @return the probability of the second word given the first word (as a probability)
	 */
	public double getBigramProb(String first, String second){
		if (!this.biProb.get(first).containsKey(second)){
			return this.alpha.get(first)*this.uniProb.get(second);
		} else {
			return this.biProb.get(first).get(second);
		}
    }

	/**
	 * fills in model's unigram and bigram count HashMaps
	 */
	private void getCount(){
		// read string line by line
		try (BufferedReader reader = new BufferedReader(new FileReader(this.filename))) {
            String line;
			HashSet<String> wordOccuredOnce = new HashSet<String>();
			// for each line in training set
            while ((line = reader.readLine()) != null) {
				// address start line and end line tokens 
				incrementValue(uniCount, "<s>");
				incrementValue(uniCount, "</s>");
				tokenCount += 2;

				// split up line into words, put in array
				String sentence[] = line.split("\\s");

				// collect unigram counts from training set
                for(int i = 0; i<sentence.length; i++){
					// if first occurence of word
					if (!uniCount.containsKey(sentence[i]) && wordOccuredOnce.contains(sentence[i])) {
						uniCount.put(sentence[i], 1.0);
						tokenCount++;
						wordOccuredOnce.remove(sentence[i]);
					}
					else {
						incrementValue(uniCount, sentence[i]);
						tokenCount++;
					}
				}

				// collect bigram counts from training set
				for(int j = -1; j < sentence.length; j++){
					String currentWord = "";
					String nextWord = "";
					
					// currentWord/nextWord edge cases
					if (j == -1){
						currentWord = "<s>";
						nextWord = sentence[j+1];
					} else if (j == sentence.length-1){
						currentWord = sentence[j];
						nextWord = "</s>";
					} else {
						currentWord = sentence[j];
						nextWord = sentence[j+1];
					}
					
					// get/create inner hashmap for current word
					HashMap<String, Double> innerMap = null;
					if (biCount.containsKey(currentWord) == true){
						innerMap = biCount.get(currentWord);
					} else {
						innerMap = new HashMap<String, Double>();
						biCount.put(currentWord, innerMap);
					}

					// increment inner hashmap
					innerMap.put(nextWord, innerMap.getOrDefault(nextWord, 0.0) + 1);
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * fill out uniProb hashtable with unigram probabilities
	 */
	private void fillUnigramProb(){
		// iterate through uniCount hashmap
		for(Map.Entry<String, Double> entry : uniCount.entrySet()){
			uniProb.put(entry.getKey(), (entry.getValue()/tokenCount));
		}
	}

	private void fillBigramProb(){
		// iterate through outer hashmap (biCount)
		for(Map.Entry<String, HashMap<String, Double>> entry : biCount.entrySet()){
			// find sum of counts for bigrams beginning with word entry.key()
			double innerSum = 0;
			String firstWord = entry.getKey();
			HashMap <String, Double> innerMap = entry.getValue();
			HashMap <String, Double> newInnerMap = new HashMap<String, Double>();
			for(Double value : innerMap.values()){
				innerSum += value;
			}

			// give newInnerMap the correct bigram probabilities
			for(Map.Entry<String, Double> innerEntry : innerMap.entrySet()){
				newInnerMap.put(innerEntry.getKey(), (innerEntry.getValue()-discount)/innerSum);
			} 

			// add firstWord to biProb keys, with newInnerMap as the value
			biProb.put(firstWord, newInnerMap);
		}
	}

	/**
	 * get alpha values for each unigram
	 */
	private void fillAlpha(){
		for(Map.Entry<String, HashMap<String, Double>> entry : biCount.entrySet()){
			// two componenents needed to calculate alpha
			Double reservedMass;
			Double denominator = 0.0;

			// find sum of counts for bigrams beginning with word entry.key()
			// need this to calculate reserved mass
			double innerSum = 0;
			String firstWord = entry.getKey();
			HashMap <String, Double> innerMap = entry.getValue();

			// iterate through inner loop to get sum, and add up probabilities of second
			// word (innerEntry.getKey)
			for(Map.Entry<String, Double> innerEntry : innerMap.entrySet()){
				innerSum += innerEntry.getValue();
				denominator -= this.uniProb.get(innerEntry.getKey());
			}
			// calculate reserved mass
			reservedMass = (innerMap.size()*discount)/innerSum;
			
			// finish denominator calculation
			denominator += 1;

			this.alpha.put(firstWord, reservedMass/denominator);
		}
	}

	/**
	 *  static method to increment the value associated with a key 
	 * 
	 * @param map the map which we will update
	 * @param key the key that points to the value we will increment
	 */
    private static void incrementValue(HashMap<String, Double> map, String key) {
        // get the current value
		if (map.containsKey(key)){
			Double value = map.get(key);
			map.put(key, value+1);
		} else {
			map.put(key, 0.0);
		}
    }
	
	public static void main(String[] args) {
		ArrayList<String> characters = new ArrayList<>();
		characters.add("luke_skywalker");
		characters.add("michael");
		characters.add("phoebe_buffay");
		Random r = new Random();

		// try(BufferedWriter writer = new BufferedWriter(new FileWriter("data/randomDialogue.txt"))){
		// 	try(BufferedWriter charWriter = new BufferedWriter(new FileWriter("data/characters.txt"))){
		// 		for(int i = 0; i < 50; i++){
		// 			int randIndex = r.nextInt(3);
		// 			String curCharacter = characters.get(randIndex);
		// 			charWriter.write(i + ": " + curCharacter + "\n");
					
		// 			String filename = String.format("data/%s_dialogue.txt", curCharacter);
		// 			DiscountLMModel test = new DiscountLMModel(filename, 0.5);
		// 			writer.write(i + ": " + test.generateParagraph() + "\n");
					
		// 		}
		// 	}
		// 	catch(IOException f){
		// 		f.printStackTrace();
		// 	}
		// } catch(IOException e){
		// 	e.printStackTrace();
		// }

		try(BufferedWriter labelSentWriter = new BufferedWriter(new FileWriter("data/labelWithSentence.txt"))){
			for(int i = 0; i < 10000; i++){
				int randIndex = r.nextInt(3);
				String curCharacter = characters.get(randIndex);
				String filename = String.format("data/%s_dialogue.txt", curCharacter);
				DiscountLMModel test = new DiscountLMModel(filename, 0.5);
				labelSentWriter.write(curCharacter + "\t" + test.generateSentence() + "\n");
			}
			
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
