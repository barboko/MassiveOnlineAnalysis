package moa.classifiers.multiclassifiers;

import com.github.javacliparser.Options;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Prediction;
import moa.MOAObject;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Example;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.gui.AWTRenderer;
import moa.learners.Learner;
import moa.tasks.TaskMonitor;

/**
 * Created by bokov on 17/02/2016.
 */
public class AbstractMultiClassifier<T extends AbstractClassifier> implements Classifier {
    @Override
    public Classifier[] getSubClassifiers() {
        return new Classifier[0];
    }

    @Override
    public String getPurposeString() {
        return null;
    }

    @Override
    public Options getOptions() {
        return null;
    }

    @Override
    public void prepareForUse() {

    }

    @Override
    public void prepareForUse(TaskMonitor monitor, ObjectRepository repository) {

    }

    @Override
    public int measureByteSize() {
        return 0;
    }

    @Override
    public Classifier copy() {
        return null;
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {

    }

    @Override
    public String getCLICreationString(Class<?> expectedType) {
        return null;
    }

    @Override
    public boolean correctlyClassifies(Instance inst) {
        return false;
    }

    @Override
    public void trainOnInstance(Instance inst) {

    }

    @Override
    public double[] getVotesForInstance(Instance inst) {
        return new double[0];
    }

    @Override
    public Prediction getPredictionForInstance(Instance inst) {
        return null;
    }

    @Override
    public boolean isRandomizable() {
        return false;
    }

    @Override
    public void setRandomSeed(int s) {

    }

    @Override
    public boolean trainingHasStarted() {
        return false;
    }

    @Override
    public double trainingWeightSeenByModel() {
        return 0;
    }

    @Override
    public void resetLearning() {

    }

    @Override
    public void trainOnInstance(Example<Instance> example) {

    }

    @Override
    public double[] getVotesForInstance(Example<Instance> example) {
        return new double[0];
    }

    @Override
    public Measurement[] getModelMeasurements() {
        return new Measurement[0];
    }

    @Override
    public Learner[] getSublearners() {
        return new Learner[0];
    }

    @Override
    public MOAObject getModel() {
        return null;
    }

    @Override
    public void setModelContext(InstancesHeader ih) {

    }

    @Override
    public InstancesHeader getModelContext() {
        return null;
    }

    @Override
    public Prediction getPredictionForInstance(Example<Instance> testInst) {
        return null;
    }

    @Override
    public AWTRenderer getAWTRenderer() {
        return null;
    }
}
