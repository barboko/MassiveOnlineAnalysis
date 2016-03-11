package moa.delayed;

import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;

/**
 * @author leonid rice (leorice) & bar bokovza (barboko)
 */
public class DelayedAttributesClassifier extends AbstractClassifier {
    //region Internal Classes
    private class WindowedClassifier {
        private Classifier _classifier;
        private int _delayValue;

    }
    //endregion

    //region Members
    //endregion

    //region Properties
    //endregion

    //region Constructors
    public DelayedAttributesClassifier() {
        //TODO: Complete Here
    }
    //endregion

    //region Implementation
    @Override
    public double[] getVotesForInstance(Instance inst) {
        return new double[0];
    }

    @Override
    public void resetLearningImpl() {

    }

    @Override
    public void trainOnInstanceImpl(Instance inst) {

    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        return new Measurement[0];
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {

    }

    @Override
    public boolean isRandomizable() {
        return false;
    }
    //endregion
}
