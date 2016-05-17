package moa.utility;

import static java.lang.System.*;

public class DelayPrediction {
    //region Members
    private int trueClass;
    private int[] predictions;
    //endregion

    //region Properties & Methods
    public double getTrueClass() { return trueClass;}
    public int size() { return predictions == null ? 0 : predictions.length; }
    public boolean isTruePrediction(int index) {
        return !(index < 0 || index >= size()) && predictions[index] == trueClass;
    }
    public int getPrediction(int index) {
        if(index < 0 || index >= size())
            throw new IllegalArgumentException("index is not in the range [0,"+size()+")");
        return predictions[index];
    }
    public int[] getPredictions() {return predictions;}
    //endregion

    //region Constructors
    public DelayPrediction(int trueClass, int[] predictions) {
        this.trueClass = trueClass;
        this.predictions = deepCopy(predictions);
    }

    public static int[] deepCopy(int[] source) {
        if(source == null)
            return null;

        int[] result = new int[source.length];
        arraycopy(source, 0, result, 0, source.length);
        return result;
    }
    //endregion
}
