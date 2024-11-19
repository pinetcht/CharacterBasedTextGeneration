package lm;

public class Range {
    private double low;
    private double high;
    private String word;

    public Range(double low, double high, String word) {
        this.low = low;
        this.high = high;
        this.word = word;
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
