package lm;
import java.util.*;
import java.io.*;
import java.lang.Math.*;

public class NBModel {
    String trainingData;
    String testSentences;
    double lambda;
    HashMap<String, Double> phoebeProbs = new HashMap<>();
    HashMap<String, Double> michaelProbs = new HashMap<>();
    HashMap<String, Double> lukeProbs = new HashMap<>();
    HashSet<String> vocab = new HashSet<>();
    double smoothingFactor;
    double phoebeLabelCount;
    double michaelLabelCount;
    double lukeLabelCount;
    
    double phoebeLabel;
    double michaelLabel;
    double lukeLabel;

    /**
     *  paramaterized constructor 
     * 
     * @param trainingData String; the filename of our training data
     * @param testSentences String; the sentences that we will be running our classifier on
     * @param lambda double - our lambda value for smoothing
     */
    public NBModel(String trainingData, String testSentences, double lambda){
        this.trainingData = trainingData;
        this.testSentences = testSentences;
        this.lambda = lambda;

        train();
        test();
        
    }
    
    /**
     * train classification model - collects counts and calculates probabilites
     */
    private void train(){
        HashMap<String, Double> phoebeCounts = new HashMap<>();
        HashMap<String, Double> michaelCounts = new HashMap<>();
        HashMap<String, Double> lukeCounts = new HashMap<>();
        
        try(BufferedReader buffer = new BufferedReader(new FileReader(trainingData))){
            String line;
            while((line = buffer.readLine()) != null){
                String[] data = line.split("\\s");
                String label = data[0];
                boolean isPhoebe = label.equals("phoebe_buffay");
                boolean isMichael = label.equals("michael");

                if(isPhoebe){
                    phoebeLabelCount++;
                } else if(isMichael){
                    michaelLabelCount++;
                } else {
                    lukeLabelCount++;
                }


                for(int i = 1; i < data.length; i++){
                    vocab.add(data[i]);
                    if(isPhoebe){
                        phoebeCounts.put(data[i],phoebeCounts.getOrDefault(data[i], 0.0)+1);
                    } else if(isMichael) {
                        michaelCounts.put(data[i],michaelCounts.getOrDefault(data[i], 0.0)+1);
                    } else {
                        lukeCounts.put(data[i],lukeCounts.getOrDefault(data[i], 0.0)+1);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        double vocabSize = vocab.size();
        smoothingFactor = lambda*vocabSize;

        for(String key: phoebeCounts.keySet()){
            double numerator = phoebeCounts.get(key) + lambda;
            double denom = phoebeLabelCount + smoothingFactor;
            phoebeProbs.put(key,numerator/denom);
        }

        for(String key: michaelCounts.keySet()){
            double numerator = michaelCounts.get(key) + lambda;
            double denom = michaelLabelCount + smoothingFactor;
            michaelProbs.put(key,numerator/denom);
        }

        for(String key: lukeCounts.keySet()){
            double numerator = lukeCounts.get(key) + lambda;
            double denom = lukeLabelCount + smoothingFactor;
            lukeProbs.put(key,numerator/denom);
        }

        double totalLabelCount = phoebeLabelCount + michaelLabelCount + lukeLabelCount;
        phoebeLabel = phoebeLabelCount/totalLabelCount;
        michaelLabel = michaelLabelCount/totalLabelCount;
        lukeLabel = lukeLabelCount/totalLabelCount;
    }

    /**
     * find the accuracy of our model
     */
    private void test(){
        double phoebeLambda = lambda/(phoebeLabelCount + smoothingFactor);
        double michaelLambda = lambda/(michaelLabelCount + smoothingFactor);
        double lukeLambda = lambda/(lukeLabelCount + smoothingFactor);
        ArrayList<String> actualLabels = new ArrayList<>();
        ArrayList<String> predictedLabels = new ArrayList<>();

        try(BufferedReader buffer = new BufferedReader(new FileReader(testSentences));
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/predictions.txt"))){
            String line;
            while((line = buffer.readLine()) != null){
                String[] sentence = line.split("\\s");
                String label = sentence[0];
                actualLabels.add(label);

                double phoebeProb = 0;
                double michaelProb = 0;
                double lukeProb = 0;
                for(int i = 0; i < sentence.length; i++){
                    if(vocab.contains(sentence[i])){
                        phoebeProb += Math.log10(phoebeProbs.getOrDefault(sentence[i], phoebeLambda));
                        michaelProb += Math.log10(michaelProbs.getOrDefault(sentence[i], michaelLambda));
                        lukeProb += Math.log10(lukeProbs.getOrDefault(sentence[i], lukeLambda));
                    }
                }
                phoebeProb += Math.log10(phoebeLabel);
                michaelProb += Math.log10(michaelLabel);  
                lukeProb += Math.log10(lukeLabel);  

                double maxProb = Math.max(lukeProb, Math.max(phoebeProb, michaelProb));
                
                if(maxProb == phoebeProb){
                    predictedLabels.add("phoebe");
                    writer.write("phoebe\t" + phoebeProb + "\n");
                } else if(maxProb == michaelProb){
                    predictedLabels.add("michael");
                    writer.write("michael\t" + michaelProb + "\n");
                } else {
                    predictedLabels.add("luke");
                    writer.write("luke\t" + lukeProb + "\n");
                }
            
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        double matches = 0;
        for(int i = 0; i < actualLabels.size(); i++){
            if(actualLabels.get(i).equals(predictedLabels.get(i))){
                matches++;
            }
        }

        
        System.out.println(matches + "\t\t" +  actualLabels.size() + "\t" + lambda + "\t" + matches/actualLabels.size() );
    }

    public static void main(String[] args) {
        System.out.println("correct\tall sentences\tlambda\taccuracy");
        new NBModel("data/training.txt", "data/test.txt", 5);
        new NBModel("data/training.txt", "data/test.txt", 3);
        new NBModel("data/training.txt", "data/test.txt", 1.5);
        new NBModel("data/training.txt", "data/test.txt", 1);
        new NBModel("data/training.txt", "data/test.txt", 0.1);
        new NBModel("data/training.txt", "data/test.txt", 0.01);
        new NBModel("data/training.txt", "data/test.txt", 0.001);
        new NBModel("data/training.txt", "data/test.txt", 0.0001);
        new NBModel("data/training.txt", "data/test.txt", 0.00001);
    }
}