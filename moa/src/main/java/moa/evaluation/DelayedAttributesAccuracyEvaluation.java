package moa.evaluation;

import moa.AbstractMOAObject;
import moa.utility.PredictionList;

import java.io.PrintStream;

/**
 * Created by bokov on 17/05/2016.
 */
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
    private int count;
    private PrintStream stream;
    //endregion

    //region Properties
    public void setStream(PrintStream stream) { this.stream = stream;}
    //endregion

    //region Constructors
    public DelayedAttributesAccuracyEvaluation(int[] delays, PredictionList predictions) {
        this.delays = delays;
        this.predictions = predictions;
        this.count = 1;
    }
    //endregion



    //region Methods
    //endregion
}
