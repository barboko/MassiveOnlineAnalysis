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
    private PrintStream stream;
    //endregion

    //region Properties
    public void setStream(PrintStream stream) { this.stream = stream;}
    //endregion

    //region Constructors
    public DelayedAttributesAccuracyEvaluation(List<Integer> delays, PredictionList predictions) {
        this.delays = DelayedAttributesEvaluation.convert(delays);
        this.predictions = predictions;
    }
    //endregion

    private Measurement[] generateMeasurements() {
        List<Measurement> m = new LinkedList<>();


        for(int idx = 0; idx < delays.length; idx++)
            m.add(new Measurement("Accuracy #" + delays[idx], predictions.accuracy(idx)));

        Measurement[] output = new Measurement[m.size()];
        return DelayedAttributesEvaluation.convert(m, output);
    }

    public void writeHeader() {
        if(stream == null)
            return;

        Measurement[] measurements = generateMeasurements();

        String result = "";
        boolean isFirst = true;

        for(Measurement m : measurements) {
            if(isFirst)
                isFirst = false;
            else
                result += ",";

            result += m.getName();
        }

        stream.println(result);
        stream.flush();
    }
    public void write() {
        if(stream == null || !predictions.isFull())
            return;

        Measurement[] measurements = generateMeasurements();

        String result = "";
        boolean isFirst = true;

        for(Measurement m : measurements) {
            if(isFirst)
                isFirst = false;
            else
                result += ",";

            result += m.getValue();
        }

        stream.println(result);
        stream.flush();
    }
}
