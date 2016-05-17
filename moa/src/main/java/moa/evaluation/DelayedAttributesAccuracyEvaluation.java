package moa.evaluation;

import moa.AbstractMOAObject;
import moa.core.Measurement;
import moa.utility.PredictionList;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class DelayedAttributesAccuracyEvaluation
 extends AbstractMOAObject
{
    //region Implementation
    @Override
    public void getDescription(StringBuilder sb, int indent) {

    }
    //endregion

    //region Members
    private int[] delays;
    private PredictionList predictions;
    private PredictionList actualPredictions;
    private PrintStream stream;
    private int count;
    //endregion

    //region Properties
    public void setStream(PrintStream stream) { this.stream = stream;}
    //endregion

    //region Constructors
    public DelayedAttributesAccuracyEvaluation(List<Integer> delays, PredictionList predictions, PredictionList actualPredictions) {
        this.delays = DelayedAttributesEvaluation.convert(delays);
        this.predictions = predictions;
        this.actualPredictions = actualPredictions;
        this.count = 0;
    }
    //endregion

    private Measurement[] generateMeasurements() {
        List<Measurement> m = new LinkedList<>();

        m.add(new Measurement("Count", count));
        count++;

        m.add(new Measurement("Actual Accuracy", actualPredictions.accuracy(0)));

        for(int idx = 0; idx < delays.length; idx++)
            m.add(new Measurement("Accuracy #" + delays[idx], predictions.accuracy(idx)));

        Measurement[] output = new Measurement[m.size()];
        return DelayedAttributesEvaluation.convert(m, output);
    }

    public void writeHeader() {
        if(stream == null)
            return;

        Measurement[] measurements = generateMeasurements();

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
    public void write() {
        if(stream == null)
            return;

        Measurement[] measurements = generateMeasurements();

        boolean isNotFirst = false;

        for (Measurement m : measurements) {
            if (isNotFirst)
                stream.print(',');
            else
                isNotFirst = true;

            stream.print(m.getValue());
        }

        stream.print('\n');
    }
}
