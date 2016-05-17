package moa.tasks;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.utility.DelayPrediction;
import moa.classifiers.Classifier;
import moa.core.*;
import moa.evaluation.*;
import moa.options.ClassOption;
import moa.streams.ExampleStream;
import moa.streams.filters.DuplicateFilter;
import moa.streams.filters.MultiLabelStreamFilter;
import moa.streams.filters.SelectAttributesFilter;
import moa.utility.PredictionList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

@SuppressWarnings("ALL")
public class EvaluatePrequentialWithDelayedAttributes extends MainTask {

    //region Overrides
    @Override
    public String getPurposeString() {
        return "Evaluates a classifier on a stream by testing then training with each example in sequence.";
    }

    @Override
    public Class<?> getTaskResultType() {
        return LearningCurve[].class;
    }

    private static final long serialVersionUID = 1L;
    //endregion

    //region Options
    @SuppressWarnings("WeakerAccess")
    public ClassOption classifierOption = new ClassOption("classifier", 'c',
            "Classifier (learner) to train.", Classifier.class, "moa.classifiers.bayes.NaiveBayes");

    public ClassOption streamOption = new ClassOption("stream", 's',
            "Stream to learn from.", ExampleStream.class,
            "generators.RandomTreeGenerator");

    public ClassOption evaluatorOption = new ClassOption("evaluator", 'e',
            "Classification performance evaluation method.",
            LearningPerformanceEvaluator.class,
            "WindowClassificationPerformanceEvaluator");

    public IntOption instanceLimitOption = new IntOption("instanceLimit", 'i',
            "Maximum number of instances to test/train on  (-1 = no limit).",
            100000000, -1, Integer.MAX_VALUE);

    public IntOption timeLimitOption = new IntOption("timeLimit", 't',
            "Maximum number of seconds to test/train for (-1 = no limit).", -1,
            -1, Integer.MAX_VALUE);

    public IntOption sampleFrequencyOption = new IntOption("sampleFrequency",
            'f',
            "How many instances between samples of the learning performance.",
            100000, 0, Integer.MAX_VALUE);

    public IntOption memCheckFrequencyOption = new IntOption(
            "memCheckFrequency", 'q',
            "How many instances between memory bound checks.", 100000, 0,
            Integer.MAX_VALUE);

    //public FileOption dumpFileOption = new FileOption("dumpFile", 'd',
    //        "File to append intermediate csv results to.", null, "csv", true);

    public FileOption outputPredictionFileOption = new FileOption("outputPredictionFile", 'p', "File to append output predictions to.", null, "csv", true);

    public FileOption outputAccuracyFileOption = new FileOption(
      "outputAccuracy", 'z', "File to appent output accuracies to.", null, "csv", true);

    //New for prequential method DEPRECATED
    public IntOption widthOption = new IntOption("width",
            'w', "Size of Window", 1000);

    public FloatOption alphaOption = new FloatOption("alpha",
            'a', "Fading factor or exponential smoothing factor", .01);
    //End New for prequential methods

    //endregion

    //region Properties - For comport
    /// FOR CLASSIFICATION
    private ExampleStream<Example<Instance>> stream;
    private InstancesHeader header;
    private MultiLabelStreamFilter[] selectors;
    private Classifier[] classifiers;
    private double[] lastClassification;
    /// FOR EVALUATION
    private LearningPerformanceEvaluator[] evaluators;
    private LearningCurve[] curves;
    private DelayedAttributesEvaluation superEvaluator;
    private DelayedAttributesAccuracyEvaluation accuracyEvaluation;
    /// OTHER
    private Map<Integer, Set<Integer>> attributes;
    private List<Integer> delays;
    private boolean[] isOutput;
    private PredictionList predictions;
    //endregion

    //region Methods
    private void _initialize() {
        //noinspection unchecked
        stream = (ExampleStream<Example<Instance>>) getPreparedClassOption(this.streamOption);
        header = stream.getHeader();
        predictions = new PredictionList(widthOption.getValue());

        _createMap();
        _createMapping();
        _createFilters();
        _createClassifiers();
        _createEvaluators();
        _createCurves();

        superEvaluator = new DelayedAttributesEvaluation(delays);
        accuracyEvaluation = new DelayedAttributesAccuracyEvaluation(delays, predictions);
        lastClassification = new double[attributes.size()];
    }

    private void _createMap() {
        Set<String> output = new HashSet<>();

        int size = header.numOutputAttributes();
        for (int i = 0; i < size; i++) {
            Attribute attribute = header.outputAttribute(i);
            output.add(attribute.name());
        }

        size = header.numAttributes();
        isOutput = new boolean[size];

        for (int i = 0; i < size; i++) {
            Attribute attribute = header.attribute(i);
            isOutput[i] = output.contains(attribute.name());
        }
    }

    @SuppressWarnings("Duplicates")
    private void _createMapping() {
        int size = header.numAttributes();
        attributes = new HashMap<>();

        for (int i = 0; i < size; i++) {
            if (isOutput[i])
                continue;

            Attribute a = header.attribute(i);
            int delay = a.getDelayTime();
            if (delay >= 0) {
                Set<Integer> lst;
                if (attributes.containsKey(delay)) lst = attributes.get(delay);
                else attributes.put(delay, (lst = new HashSet<>()));
                lst.add(i);
            }
        }
    }

    private void _createCurves() {
        curves = new LearningCurve[delays.size()];

        int i = 0;
        for(int delay: delays) {
            curves[i] = new LearningCurve("learning evaluation instances");
            i++;
        }
    }

    @SuppressWarnings("Duplicates")
    private void _createFilters() {
        // Build the filter that duplicates the instances - tested
        DuplicateFilter copier = new DuplicateFilter(attributes.size());
        copier.setInputStream(stream);

        // Create the vector of SelectAttributesFilter
        selectors = new SelectAttributesFilter[attributes.size()];

        // Create the output string
        List<Integer> output = new LinkedList<>();
        for (int i = 0; i < isOutput.length; i++)
            if (isOutput[i])
                output.add(i + 1);

        String outputStr = makeStr(output);

        List<Integer> inputIndexes = new LinkedList<>();

        delays = new LinkedList<>(attributes.keySet());
        Collections.sort(delays);

        // Load the vector with filters
        int i = 0;
        for (Integer delay : delays) {
            for (Integer idx : attributes.get(delay))
                inputIndexes.add(idx+1);

            Collections.sort(inputIndexes);
            String inputStr = makeStr(inputIndexes);

            SelectAttributesFilter filter = new SelectAttributesFilter();
            filter.setInputStream(copier);
            filter.inputStringOption.setValue(inputStr);
            filter.outputStringOption.setValue(outputStr);
            filter.prepareForUse();
            selectors[i] = filter;
            i++;
        }
    }

    private void _createClassifiers() {
        Classifier c = (Classifier) getPreparedClassOption(this.classifierOption);
        classifiers = new Classifier[selectors.length];

        for (int i = 0; i < classifiers.length; i++) {
            classifiers[i] = c.copy();
        }
    }

    @SuppressWarnings("unchecked")
    private void _createEvaluators() {
        evaluators = new LearningPerformanceEvaluator[selectors.length];
        LearningPerformanceEvaluator evaluator = (LearningPerformanceEvaluator) getPreparedClassOption(this.evaluatorOption);

        for (int i = 0; i < evaluators.length; i++)
            evaluators[i] = (LearningPerformanceEvaluator<Example>) evaluator.copy();


    }

    private static String makeStr(Collection<?> obj) {
        String s = "";
        boolean isFirst = true;

        for (Object i : obj) {
            s += (isFirst ? "" : ",") + i.toString();
            isFirst = false;
        }

        return s;
    }
    //endregion

    //region CSV Methods
    private void _createHeader(PrintStream stream) {
        Collection<String> fields = new LinkedList<>();

        fields.add("Id");
        fields.add("True Class");

        for (int delay : delays)
            fields.add("Classifier #" + delay);

        //fields.add("Runtime");
        fields.add("Entropy");
        fields.add("First Time True Class");
        fields.add("Stability");
        fields.add("Stability - from first right");

        for (int delay : delays)
            fields.add("Precision #" + delay);

        fields.add("Average Precision");

        stream.println(makeStr(fields));
        stream.flush();
    }
    //endregion

    private PrintStream _createStream(FileOption option) {
        File file = option.getFile();
        if(file == null)
            return null;

        PrintStream result = null;
        if (file != null) {
            try {
                if (file.exists()) {
                    result = new PrintStream(
                            new FileOutputStream(file, true), true);
                } else {
                    result = new PrintStream(
                            new FileOutputStream(file), true);
                }
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Unable to open immediate result file: " + file, ex);
            }
        }

        return result;
    }

    private void _disposeStream(PrintStream stream) {
        if(stream == null)
            return;

        stream.flush();
        stream.close();
    }
    @Override
    protected Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
        _initialize();

        //ExampleStream stream = (ExampleStream) getPreparedClassOption(this.streamOption);

        //LearningPerformanceEvaluator evaluator = (LearningPerformanceEvaluator) getPreparedClassOption(this.evaluatorOption);

        /*
        LearningCurve learningCurve = new LearningCurve(
                "learning evaluation instances");

        //New for prequential methods
        if (evaluator instanceof WindowClassificationPerformanceEvaluator) {
            if (widthOption.getValue() != 1000) {
                System.out.println("DEPRECATED! Use EvaluatePrequential -e (WindowClassificationPerformanceEvaluator -w " + widthOption.getValue() + ")");
                return learningCurve;
            }
        }
        if (evaluator instanceof EWMAClassificationPerformanceEvaluator) {
            if (alphaOption.getValue() != .01) {
                System.out.println("DEPRECATED! Use EvaluatePrequential -e (EWMAClassificationPerformanceEvaluator -a " + alphaOption.getValue() + ")");
                return learningCurve;
            }
        }
        if (evaluator instanceof FadingFactorClassificationPerformanceEvaluator) {
            if (alphaOption.getValue() != .01) {
                System.out.println("DEPRECATED! Use EvaluatePrequential -e (FadingFactorClassificationPerformanceEvaluator -a " + alphaOption.getValue() + ")");
                return learningCurve;
            }
        }

        */

        int maxInstances = this.instanceLimitOption.getValue();
        long instancesProcessed = 0;
        int maxSeconds = this.timeLimitOption.getValue();
        int secondsElapsed = 0;
        monitor.setCurrentActivity("Evaluating learner...", -1.0);

        /// Create Print Streams
        PrintStream outputFileStream = _createStream(this.outputFileOption);
        PrintStream predictionsFileStream = _createStream(this.outputPredictionFileOption);
        PrintStream accuracyFileStream = _createStream(this.outputAccuracyFileOption);
        superEvaluator.setStream(predictionsFileStream);
        superEvaluator.writeHeader();

        /// FOR MEASUREMENTS
        boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
        long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
        long lastEvaluateStartTime = evaluateStartTime;
        double RAMHours = 0.0;

        /// FOR CLASSIFICATION
        int[] lastPrediction = new int[classifiers.length];
        double trueClass = 0;

        while (stream.hasMoreInstances()
                && ((maxInstances < 0) || (instancesProcessed < maxInstances))
                && ((maxSeconds < 0) || (secondsElapsed < maxSeconds))) {

            AtomicReferenceArray<Example> examples = new AtomicReferenceArray<>(new Example[classifiers.length]);

            for (int i = 0; i < classifiers.length; i++) {
                Example instanceExample = selectors[i].nextInstance();
                examples.set(i, instanceExample);
                Classifier classifier = classifiers[i];

                //noinspection unchecked
                double[] prediction = classifier.getVotesForInstance(instanceExample);
                lastPrediction[i] = prediction == null ||
                        prediction.length == 0 ? -1 : Utils.maxIndex(prediction);

                if (i == 0) {
                    Instance instance = ((Instance) instanceExample.getData());
                    trueClass = instance.classValue();
                }

                evaluators[i].addResult(instanceExample, prediction);

                classifier.trainOnInstance(instanceExample);
            }

            DelayPrediction pred = new DelayPrediction((int)trueClass, lastPrediction);
            predictions.add(pred);

            instancesProcessed++;

            superEvaluator.write(pred);

            if (instancesProcessed % this.sampleFrequencyOption.getValue() == 0
                    || stream.hasMoreInstances() == false) {
                long evaluateTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
                double time = TimingUtils.nanoTimeToSeconds(evaluateTime - evaluateStartTime);
                double timeIncrement = TimingUtils.nanoTimeToSeconds(evaluateTime - lastEvaluateStartTime);
                double RAMHoursIncrement = classifiers[0].measureByteSize() / (1024.0 * 1024.0 * 1024.0) * (timeIncrement / 3600.0); //GBs * Hours
                RAMHours += RAMHoursIncrement;
                lastEvaluateStartTime = evaluateTime;

                for (int i = 0; i < curves.length; i++) {
                    Measurement[] m = new Measurement[]{
                            new Measurement(
                                    "learning evaluation instances",
                                    instancesProcessed),
                            new Measurement(
                                    "evaluation time ("
                                            + (preciseCPUTiming ? "cpu "
                                            : "") + "seconds)",
                                    time),
                            new Measurement(
                                    "model cost (RAM-Hours)",
                                    RAMHours)
                    };

                    LearningPerformanceEvaluator eval = evaluators[i];
                    Classifier c = classifiers[i];
                    LearningEvaluation eval2 = new LearningEvaluation(m, eval, c);

                    curves[i].insertEntry(eval2);
                }
        }


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
                    for (LearningCurve curve : curves) monitor.setLatestResultPreview(curve.copy());
                }
                secondsElapsed = (int) TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()
                        - evaluateStartTime);
            }
    }

        _disposeStream(outputFileStream);
        _disposeStream(predictionsFileStream);
        return null;
    }
}