package moa.tasks;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.classifiers.Classifier;
import moa.core.Example;
import moa.core.ObjectRepository;
import moa.evaluation.LearningEvaluation;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.learners.Learner;
import moa.options.ClassOption;
import moa.streams.ExampleStream;
import moa.streams.filters.DuplicateFilter;
import moa.streams.filters.SelectAttributesFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

@SuppressWarnings("ALL")
public class EvaluateModelWithDelayedAttributes extends MainTask {
    //region Options

    public ClassOption modelOption = new ClassOption("model", 'm',
            "Learner to evaluate.", Learner.class, "LearnModel");

    public ClassOption streamOption = new ClassOption("stream", 's',
            "Stream to evaluate on.", ExampleStream.class,
            "generators.RandomTreeGenerator");

    public ClassOption evaluatorOption = new ClassOption("evaluator", 'e',
            "Classification performance evaluation method.",
            LearningPerformanceEvaluator.class,
            "BasicClassificationPerformanceEvaluator");

    public IntOption maxInstancesOption = new IntOption("maxInstances", 'i',
            "Maximum number of instances to test.", 1000000, 0,
            Integer.MAX_VALUE);

    public FileOption outputPredictionFileOption = new FileOption("outputPredictionFile", 'o',
            "File to append output predictions to.", null, "pred", true);
    //endregion

    //region Properties - For comport
    private ExampleStream<Example<Instance>> stream;
    private InstancesHeader header;
    private SelectAttributesFilter[] selectors;
    private Classifier[] classifiers;
    private LearningPerformanceEvaluator[] evaluators;
    private Map<Integer, Set<Integer>> attributes;
    private boolean[] isOutput;
    //endregion

    //region Construction Methods

    private void _initialize() {
        stream = (ExampleStream<Example<Instance>>)getPreparedClassOption(this.streamOption);
        header = stream.getHeader();

        _createMap();
        _createMapping();
        _createFilters();
        _createClassifiers();
        _createEvaluators();
    }

    private void _createMap() {
        Set<String> output = new HashSet<String>();

        int size = header.numOutputAttributes();
        for(int  i = 0; i < size; i++) {
            Attribute attribute = header.outputAttribute(i);
            output.add(attribute.name());
        }

        size = header.numAttributes();
        isOutput = new boolean[size];

        for(int i = 0; i < size; i++) {
            Attribute attribute = header.attribute(i);
            isOutput[i] = output.contains(attribute.name());
        }
    }

    private void _createMapping() {
        //TODO: Check "Test -> Train" for getting the output attribute

        int size = header.numAttributes();
        attributes = new HashMap<>();

        for (int i = 0; i < size; i++) {
            if(isOutput[i])
                continue;

            Attribute a = header.attribute(i);
            int delay = a.getDelayTime();
            if (delay >= 0) {
                Set<Integer> lst;
                if (attributes.containsKey(delay)) lst = attributes.get(delay);
                else attributes.put(delay, (lst = new HashSet<Integer>()));
                lst.add(i);
            }
        }
    }
    private void _createFilters() {
        // Build the filter that duplicates the instances - tested
        DuplicateFilter copier = new DuplicateFilter(attributes.size());
        copier.setInputStream(stream);

        // Create the vector of SelectAttributesFilter
        selectors = new SelectAttributesFilter[attributes.size()];

        // Create the output string
        List<Integer> output = new LinkedList<>();
        for(int i = 0; i < isOutput.length; i++)
            if(isOutput[i])
                output.add(i+1);

        String outputStr = makeStr(output);

        List<Integer> inputIndexes = new LinkedList<>();
        List<Integer> keys = new LinkedList<>(attributes.keySet());
        Collections.sort(keys);

        // Load the vector with filters
        for (Integer delay : keys) {
            for(Integer idx : attributes.get(delay))
                inputIndexes.add(idx+1);

            Collections.sort(inputIndexes);
            String inputStr = makeStr(inputIndexes);

            SelectAttributesFilter filter = new SelectAttributesFilter();
            filter.setInputStream(copier);
            filter.inputStringOption.setValue(inputStr);
            filter.outputStringOption.setValue(outputStr);
            filter.prepareForUse();
            selectors[delay] = filter;
        }
    }
    private void _createClassifiers() {
        Classifier c = (Classifier) getPreparedClassOption(this.modelOption);
        classifiers = new Classifier[selectors.length];

        for (int i = 0; i < classifiers.length; i++) {
            classifiers[i] = c.copy();
        }
    }
    private void _createEvaluators() {
        evaluators = new LearningPerformanceEvaluator[selectors.length];
        LearningPerformanceEvaluator evaluator = (LearningPerformanceEvaluator) getPreparedClassOption(this.evaluatorOption);

        for(int i = 0; i < evaluators.length; i++)
            evaluators[i] = (LearningPerformanceEvaluator<Example>) evaluator.copy();
    }

    public static String makeStr(Collection<Integer> obj) {
        String s = "";
        boolean isFirst = true;

        for (Integer i : obj) {
            s += (isFirst ? "" : ",") + i;
            isFirst = false;
        }

        return s;
    }
    //endregion

    //region Overrides
    @Override
    protected Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
        _initialize();
        int maxInstances = this.maxInstancesOption.getValue();


        long instancesProcessed = 0;
        monitor.setCurrentActivity("Evaluating model...", -1.0);

        //File for output predictions
        File outputPredictionFile = this.outputPredictionFileOption.getFile();
        PrintStream outputPredictionResultStream = null;
        if (outputPredictionFile != null) {
            try {
                if (outputPredictionFile.exists()) {
                    outputPredictionResultStream = new PrintStream(
                            new FileOutputStream(outputPredictionFile, true), true);
                } else {
                    outputPredictionResultStream = new PrintStream(
                            new FileOutputStream(outputPredictionFile), true);
                }
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Unable to open prediction result file: " + outputPredictionFile, ex);
            }
        }
        while (stream.hasMoreInstances()
                && ((maxInstances < 0) || (instancesProcessed < maxInstances))) {


            for(int i = 0; i < classifiers.length; i++) {
                Example<Instance> example = selectors[i].nextInstance();
                Instance instance = example.getData();
                double trueClass = instance.classValue();

                if(instancesProcessed > 10000)
                    System.out.println("Hello");
                double[] prediction = classifiers[i].getPredictionForInstance(instance).getVotes();
                //classifiers[i].getVotesForInstance(instance);

                if (outputPredictionFile != null) outputPredictionResultStream.println(prediction[0] + "," + trueClass);
                evaluators[i].addResult(example, prediction);
            }
            instancesProcessed++;
            if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
                if (monitor.taskShouldAbort()) {
                    return null;
                }
                long estimatedRemainingInstances = stream.estimatedRemainingInstances();
                if (maxInstances > 0) {
                    long maxRemaining = maxInstances - instancesProcessed;
                    if ((estimatedRemainingInstances < 0)
                            || (maxRemaining < estimatedRemainingInstances)) {
                        estimatedRemainingInstances = maxRemaining;
                    }
                }
                monitor.setCurrentActivityFractionComplete(estimatedRemainingInstances < 0 ? -1.0
                        : (double) instancesProcessed
                        / (double) (instancesProcessed + estimatedRemainingInstances));
                if (monitor.resultPreviewRequested()) {
                    for(int i = 0; i < classifiers.length; i++)
                        monitor.setLatestResultPreview(new LearningEvaluation(
                            evaluators[i], classifiers[i]));
                }
            }
        }
        if (outputPredictionResultStream != null) {
            outputPredictionResultStream.close();
        }

        LearningEvaluation[] result = new LearningEvaluation[classifiers.length];

        for(int i = 0; i < classifiers.length; i++)
        {
            result[i] = new LearningEvaluation(evaluators[i], classifiers[i]);
        }
        return result;
    }

    @Override
    public Class<?> getTaskResultType() {
        return LearningEvaluation[].class;
    }
    //endregion
}
