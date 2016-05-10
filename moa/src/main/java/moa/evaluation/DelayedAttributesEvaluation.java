package moa.evaluation;

import moa.AbstractMOAObject;
import moa.classifiers.Classifier;
import moa.core.Measurement;

import java.io.PrintStream;
import java.util.*;

/**
 * Created by bokov on 07/05/2016.
 */
public class DelayedAttributesEvaluation extends AbstractMOAObject {

    //region Members
    private Classifier[] _source;
    private int[] _delays;
    private int _count;
    private PrintStream stream = null;
    //endregion

    //region Properties
    public void setStream(PrintStream stream) { this.stream = stream;}
    //endregion

    //region Constructors
    public DelayedAttributesEvaluation(Classifier[] classifiers, List<Integer> delays) {
        if ((_source = classifiers) == null || classifiers.length == 0
                || delays == null || classifiers.length != delays.size())
            throw new NullPointerException("Classifier must be declared.");

        _delays = new int[delays.size()];

        int i = 0;
        for(int delay: delays)
        {
            _delays[i] = delay;
            i++;
        }

        reset();
    }

    public void reset() { _count = 1;}
    //endregion

    //region Implementation
    @Override
    public void getDescription(StringBuilder sb, int indent) {
        sb.append("This evaluation process is using for multiple classifiers, based on different delay value.");
    }
    //endregion

    //region Methods
    public Measurement[] generateMeasurements(int[] predictions, double trueClass) {
        List<Measurement> results = new LinkedList<>();

        if(predictions == null || predictions.length != _source.length)
            return null;

        results.add(new Measurement("ID", _count));
        for(int i = 0; i < predictions.length; i++)
            results.add(new Measurement("Prediction #"+_delays[i], predictions[i]));

        results.add(new Measurement("Entropy", entropy(predictions)));
        results.add(new Measurement("First True Prediction", firstTrue(predictions, trueClass)));
        results.add(new Measurement("Stability", stability(predictions)));
        results.add(new Measurement("Stability From First True",
                stabilityFirstTrue(predictions, trueClass)));

        _count++;

        // CONVERSION ONLY
        Measurement[] output = new Measurement[results.size()];
        int i = 0;
        for(Measurement m: results) {
            output[i] = m;
            i++;
        }

        return output;
    }


    //region Measurements
    private static double entropy(int[] values) {
        if(values == null)
            return -1;
        if(values.length <= 1)
            return 0;

        Map<Double, Double> mapping = new HashMap<>();
        for(double p: values)
            mapping.put(p, 1 + (mapping.containsKey(p) ? mapping.get(p) : 0));

        double entropy = 0;
        int size = values.length;
        double log2 = Math.log(2);

        if(mapping.size() > 1)
        {
            for(double key: mapping.keySet()) {
                double p = mapping.get(key) / size;
                entropy += -1 * p * (Math.log(p) / Math.log(2));
            }
        }

        return entropy;
    }
    private static double firstTrue(int[] values, double trueClass) {
        if(values == null || values.length == 0)
            return -1;

        for(int i = 0; i < values.length; i++)
            if(values[i] == trueClass)
                return i;

        return -1;
    }
    private static double stability(int[] values) {
        return stability(values, 0);
    }
    private static double stability(int[] values, int startIndex) {
        if(values == null || values.length <= 1 || startIndex >= values.length || startIndex < 0)
            return -1;

        double result = 0;
        for(int i = startIndex; i + 1 < values.length; i++)
            if(values[i] != values[i+1])
                result++;

        return result;
    }
    private static double stabilityFirstTrue(int[] values, double trueClass) {
        int first = (int)firstTrue(values, trueClass);
        if(first == -1)
            return -1;
        return stability(values, first);
    }
    //endregion


    public void writeHeader() {
        if(stream == null)
            return;

        Measurement[] temp = generateMeasurements(new int[] {0, 0, 0}, 0);

        String result = "";
        boolean isFirst = true;

        for(Measurement m : temp) {
            if(isFirst)
                isFirst = false;
            else
                result += ",";

            result += m.getName();
        }

        stream.println(result);
        stream.flush();
    }
    public Measurement[] write(int[] predictions, double trueClass) {
        Measurement[] temp = generateMeasurements(predictions, trueClass);

        String result = "";
        boolean isFirst = true;

        for(Measurement m : temp) {
            if(isFirst)
                isFirst = false;
            else
                result += ",";

            result += m.getValue();
        }

        if(stream != null) {
            stream.println(result);
            stream.flush();
        }

        return temp;
    }

    //endregion
}
