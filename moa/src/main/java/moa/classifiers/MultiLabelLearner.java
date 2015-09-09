package moa.classifiers;

import com.yahoo.labs.samoa.instances.MultiLabelInstance;
import com.yahoo.labs.samoa.instances.Prediction;

public interface MultiLabelLearner extends Classifier {

    void trainOnInstanceImpl(MultiLabelInstance instance);

    Prediction getPredictionForInstance(MultiLabelInstance instance);

}
