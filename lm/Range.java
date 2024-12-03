package lm;

/**
 * 
 * Class to represent a range with lowest and highest n-gram probability. 
 * Has word associated with the range of probability.
 * 
 */
public class Range {
    private double low;
    private double high;
    private String word;

    public Range(double low, double high, String word) {
        this.low = low;
        this.high = high;
        this.word = word;
    }

    public double getLow() {
        return this.low;
    }


    public double getHigh() {
        return this.high;
    }

    public String getWord() {
        return this.word;
    }

    public String toString(){
        return "(" + this.low + ", " + this.high + ", " + this.word + ")";
    }

    public String contains(double number){
        if(number >= high){
            return "higher";
        } else if (number < low) {
            return "lower";
        } else {
            return word;
        }
    }
}
