package moa.classifiers.meta;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstanceInformation;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.learners.Learner;
import moa.options.ClassOption;
import moa.tasks.TaskMonitor;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author leonid rise (leorise) & bar bokovza (barboko)
 */
public class DelayedAttributesClassifier extends AbstractClassifier {

    @Override
    public String getPurposeString() {
        return "Delayed Attributes Classifier based on Bar & Leonid ISE 2016 Project";
    }

    //region Internal Classes
    private class WindowedClassifier {
        private Classifier _classifier;
        private int _delayValue;

    }
    //endregion


    //region Options
    /**
     * Type of classifier to use as a component classifier.
     */
    public ClassOption learnerOption = new ClassOption("learner", 'l', "Classifier to train.", Classifier.class, "trees.HoeffdingTree -l NB -e 1000 -g 100 -c 0.01");

    /**
     * Number of folds in candidate classifier cross-validation.
     */
    public IntOption numFoldsOption = new IntOption("numFolds", 'f', "Number of cross-validation folds for candidate classifier testing.", 10, 1, Integer.MAX_VALUE);


    //endregion

    //region Members

    protected Classifier[] ensemble;
    protected Classifier candidateClassifier;
    protected int processedInstance;
    protected int numFolds;
    protected int attributesDelayedCount;
    protected Instances currentInstance;


    //endregion

    //region Properties

    @Override
    public void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
        this.attributesDelayedCount= getNumDelyedClasffiers();
        this.numFolds = this.numFoldsOption.getValue();
        this.candidateClassifier = (Classifier) getPreparedClassOption(this.learnerOption);
        this.candidateClassifier.resetLearning();

        this.ensemble = new Classifier[attributesDelayedCount];
        for(int i=0 ; i<attributesDelayedCount ; i++)
        {
            ensemble[i] =candidateClassifier.copy();
        }


        super.prepareForUseImpl(monitor, repository);
    }

    @Override
    public void resetLearningImpl() {
        this.currentInstance = null;
        this.processedInstance = 0;
        //this.ensemble = new Classifier[0];
        this.ensemble = new Classifier[0];
        //NOTICE: This line creates the classifier
        this.candidateClassifier = (Classifier) getPreparedClassOption(this.learnerOption);
        this.candidateClassifier.resetLearning();
    }

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

    public int getNumDelyedClasffiers ()
    {
        InstancesHeader ih = this.getModelContext();
        InstanceInformation ii =  ih.getInstanceInformation();
        int[] delayArray = ii.attributesDelayTimes();

        Set<Integer> setUniqueNumbers = new LinkedHashSet<Integer>();
        for(int x : delayArray) {
            setUniqueNumbers.add(x);
        }

        return setUniqueNumbers.size();
    }


    //endregion



}
