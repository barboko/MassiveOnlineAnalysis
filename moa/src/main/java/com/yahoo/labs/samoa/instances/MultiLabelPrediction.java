/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.  
 */
package com.yahoo.labs.samoa.instances;

import moa.core.DoubleVector;

public class MultiLabelPrediction implements Prediction {
    protected DoubleVector[] prediction;

    public MultiLabelPrediction() {
        this(0);
    }

    public MultiLabelPrediction(int numOutputAttributes) {
        prediction = new DoubleVector[numOutputAttributes];
        for (int i = 0; i < numOutputAttributes; i++)
            prediction[i] = new DoubleVector();
    }

    @Override
    public int numOutputAttributes() {
        return prediction.length;
    }

    @Override
    public int numClasses(int outputAttributeIndex) {
        return prediction[outputAttributeIndex].numValues();
    }

    @Override
    public double[] getVotes(int outputAttributeIndex) {
        return prediction[outputAttributeIndex].getArrayCopy();
    }

    @Override
    public double[] getVotes() {
        return getVotes(0);
    }

    @Override
    public double getVote(int outputAttributeIndex, int classIndex) {
        return prediction[outputAttributeIndex].getValue(classIndex);
    }

    @Override
    public void setVotes(int outputAttributeIndex, double[] votes) {
        for (int i = 0; i < votes.length; i++)
            prediction[outputAttributeIndex].setValue(i, votes[i]);
    }

    @Override
    public void setVotes(double[] votes) {
        setVotes(0, votes);
    }

    @Override
    public void setVote(int outputAttributeIndex, int classIndex, double vote) {
        prediction[outputAttributeIndex].setValue(classIndex, vote);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < prediction.length; i++) {
            sb.append("Out ").append(i).append(": ");
            for (int c = 0; c < prediction[i].numValues(); c++) {
                sb.append((int) (prediction[i].getValue(c) * 1000) / 1000.0).append(" ");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean hasVotesForAttribute(int outputAttributeIndex) {
        return prediction.length >= (outputAttributeIndex + 1) && ((prediction[outputAttributeIndex].numValues() != 0));
    }

    @Override
    public int size() {
        return prediction.length;
    }

}
