package moa.utility;


public class PredictionList extends OverrideList<DelayPrediction> {

    //region Constructors
    public PredictionList(int capacity) {
        super(capacity);
    }
    //endregion

    //region Methods
    public double accuracy(int index) {
        int count = 0;

        for(DelayPrediction prediction: this.data) {
            if(prediction.isTruePrediction(index))
                count++;
        }

        return (double)count / size();
    }
    //endregion

}
