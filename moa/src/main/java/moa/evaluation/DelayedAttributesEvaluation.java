package moa.evaluation;

import moa.AbstractMOAObject;
import moa.core.Measurement;
import moa.utility.DelayPrediction;

import java.io.PrintStream;
import java.util.*;


public class DelayedAttributesEvaluation extends AbstractMOAObject {

    //region Members
    private int[] _delays;
    private int _count;
    private PrintStream stream = null;
    //endregion

    //region Properties
    public void setStream(PrintStream stream) {
        this.stream = stream;
    }
    //endregion

    //region Constructors
    public DelayedAttributesEvaluation(List<Integer> delays) {
        _delays = convert(delays);
        reset();
    }

    public static <T> T[] convert(List<T> source, T[] output) {
        if (source == null || output == null || source.size() != output.length)
            return null;

        int i = 0;

        for (T val : source) {
            output[i] = val;
            i++;
        }

        return output;
    }

    public static int[] convert(List<Integer> source) {
        if (source == null)
            return null;

        int[] result = new int[source.size()];
        int i = 0;

        for (int val : source) {
            result[i] = val;
            i++;
        }

        return result;
    }

    public void reset() {
        _count = 1;
    }
    //endregion

    //region Implementation
    @Override
    public void getDescription(StringBuilder sb, int indent) {
        sb.append("This evaluation process is using for multiple classifiers, based on different delay value.");
    }
    //endregion

    //region Methods
    public Measurement[] generateMeasurements(DelayPrediction prediction, DelayPrediction actual) {
        List<Measurement> results = new LinkedList<>();

        if (prediction == null)
            return null;

        results.add(new Measurement("Count", _count));
        results.add(new Measurement("Actual Prediction", actual.getPrediction(0)));
        for (int i = 0; i < prediction.size(); i++)
            results.add(new Measurement("Prediction #" + _delays[i], prediction.getPrediction(i)));

        results.add(new Measurement("Entropy", entropy(prediction.getPredictions())));
        results.add(new Measurement("First True Prediction", firstTrue(prediction.getPredictions(), prediction.getTrueClass())));
        results.add(new Measurement("Stability", stability(prediction.getPredictions())));
        results.add(new Measurement("Stability From First True",
                stabilityFirstTrue(prediction.getPredictions(), prediction.getTrueClass())));

        _count++;
        Measurement[] res = new Measurement[results.size()];
        return convert(results, res);
    }


    //region Measurements
    private static double entropy(int[] values) {
        if (values == null)
            return -1;
        if (values.length <= 1)
            return 0;

        Map<Double, Double> mapping = new HashMap<>();
        for (double p : values)
            mapping.put(p, 1 + (mapping.containsKey(p) ? mapping.get(p) : 0));

        double entropy = 0;
        int size = values.length;
        final double log2 = Math.log(2);

        if (mapping.size() > 1) {
            for (double key : mapping.keySet()) {
                double p = mapping.get(key) / size;
                entropy += -1 * p * (Math.log(p) / log2);
            }
        }

        return entropy;
    }

    private static double firstTrue(int[] values, double trueClass) {
        if (values == null || values.length == 0)
            return -1;

        for (int i = 0; i < values.length; i++)
            if (values[i] == trueClass)
                return i;

        return -1;
    }

    private static double stability(int[] values) {
        return stability(values, 0);
    }

    private static double stability(int[] values, int startIndex) {
        if (values == null || values.length <= 1 || startIndex >= values.length || startIndex < 0)
            return -1;

        double result = 0;
        for (int i = startIndex; i + 1 < values.length; i++)
            if (values[i] != values[i + 1])
                result++;

        return result;
    }

    private static double stabilityFirstTrue(int[] values, double trueClass) {
        int first = (int) firstTrue(values, trueClass);
        if (first == -1)
            return -1;
        return stability(values, first);
    }
    //endregion


    public void writeHeader() {
        if (stream == null)
            return;

        int[] tmp = new int[_delays.length], tmp2 = new int[1];
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = 0;
        tmp2[0] = 0;

        Measurement[] measurements = generateMeasurements(new DelayPrediction(0, tmp), new DelayPrediction(0, tmp2));

        boolean isNotFirst = false;
        for (Measurement m : measurements) {
            if (isNotFirst)
                stream.print(',');
            else
                isNotFirst = true;

            stream.print(m.getName());
        }

        stream.print('\n');
    }

    public Measurement[] write(DelayPrediction prediction, DelayPrediction actual) {
        Measurement[] measurements = generateMeasurements(prediction, actual);

        boolean isNotFirst = false;

        for (Measurement m : measurements) {
            if (isNotFirst)
                stream.print(',');
            else
                isNotFirst = true;

            stream.print(m.getValue());
        }

        stream.print('\n');

        return measurements;
    }

    //endregion
}
