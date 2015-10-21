/*
 * 
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

/**
 * The Class InstanceImpl.
 * @author abifet
 */
public class InstanceImpl implements MultiLabelInstance {
    //region Members
    /**
     * The weight.
     */
    protected double weight;

    /**
     * The instance data.
     */
    protected InstanceData instanceData;

    /**
     * The instance information.
     */
    protected InstancesHeader instanceHeader;
    //endregion

    //region Constructors
    /**
     * Instantiates a new instance.
     * Copy Constructor.
     * @param inst the inst
     */
    public InstanceImpl(InstanceImpl inst) {
        this.weight = inst.weight;
        this.instanceData = inst.instanceData.copy();
        this.instanceHeader = inst.instanceHeader;
    }

    //
    /**
     * Instantiates a new Dense instance.
     *
     * @param weight the weight
     * @param res the res
     */
    public InstanceImpl(double weight, double[] res) {
        this.weight = weight;
        this.instanceData = new DenseInstanceData(res);
    }


    /**
     * Instantiates a new Sparse instance.
     *
     * @param weight the weight
     * @param attributeValues the attribute values
     * @param indexValues the index values
     * @param numberAttributes the number attributes
     */
    public InstanceImpl(double weight, double[] attributeValues, int[] indexValues, int numberAttributes) {
        this.weight = weight;
        this.instanceData = new SparseInstanceData(attributeValues, indexValues, numberAttributes);
    }

    /**
     * Instantiates a new instance.
     *
     * @param weight the weight
     * @param instanceData the instance data
     */
    public InstanceImpl(double weight, InstanceData instanceData) {
        this.weight = weight;
        this.instanceData = instanceData;
    }

    /**
     * Instantiates a new instance.
     *
     * @param numAttributes the num attributes
     */
    public InstanceImpl(int numAttributes) {
        this.instanceData = new DenseInstanceData(new double[numAttributes]); //JD
        this.weight = 1;
    }
    //endregion

    //region Weight Property
    /**
     * Weight.
     *
     * @return the double
     */
    @Override
    public double weight() {
        return weight;
    }

    /**
     * Sets the weight.
     *
     * @param weight the new weight
     */
    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }
    //endregion

    /**
     * Attribute.
     *
     * @param instAttIndex the inst att index
     * @return the attribute
     */
    @Override
    public Attribute attribute(int instAttIndex) {
        return this.instanceHeader.attribute(instAttIndex);
    }

    /**
     * Delete attribute at.
     *
     * @param i the i
     */
    @Override
    public void deleteAttributeAt(int i) {
        this.instanceData.deleteAttributeAt(i);
    }

    /**
     * Insert attribute at.
     *
     * @param i the i
     */
    @Override
    public void insertAttributeAt(int i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Num attributes.
     * @return the int
     */
    @Override
    public int numAttributes() {
        return this.instanceData.numAttributes();
    }

    /**
     * Value.
     * @param instAttIndex the inst att index
     * @return the double
     */
    @Override
    public double value(int instAttIndex) {
        return this.instanceData.value(instAttIndex);
    }

    /**
     * Checks if is missing.
     * @param instAttIndex the inst att index
     * @return true, if is missing
     */
    @Override
    public boolean isMissing(int instAttIndex) {
        return this.instanceData.isMissing(instAttIndex);
    }

    /**
     * Num values.
     * @return the int
     */
    @Override
    public int numValues() {
        return this.instanceData.numValues();
    }

    /**
     * Index.
     *
     * @param i the i
     * @return the int
     */
    @Override
    public int index(int i) {
        return this.instanceData.index(i);
    }

    /**
     * Value sparse.
     *
     * @param i the i
     * @return the double
     */
    @Override
    public double valueSparse(int i) {
        return this.instanceData.valueSparse(i);
    }

    /**
     * Checks if is missing sparse.
     *
     * @param p the p
     * @return true, if is missing sparse
     */
    @Override
    public boolean isMissingSparse(int p) {
        return this.instanceData.isMissingSparse(p);
    }

    /**
     * Value.
     *
     * @param attribute the attribute
     * @return the double
     */
    @Override
    public double value(Attribute attribute) {
        return value(attribute.index());

    }

    /**
     * String value.
     *
     * @param i the i
     * @return the string
     */
    @Override
    public String stringValue(int i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * To double array.
     *
     * @return the double[]
     */
    @Override
    public double[] toDoubleArray() {
        return this.instanceData.toDoubleArray();
    }

    /**
     * Sets the value.
     *
     * @param numAttribute the num attribute
     * @param d the d
     */
    @Override
    public void setValue(int numAttribute, double d) {
        this.instanceData.setValue(numAttribute, d);
    }

    /**
     * Class value.
     *
     * @return the double
     */
    @Override
    public double classValue() {
        return this.instanceData.value(classIndex());
    }

    /**
     * Class index.
     *
     * @return the int
     */
    @Override
    public int classIndex() {
        int classIndex = instanceHeader.classIndex();
        return classIndex != Integer.MAX_VALUE ? classIndex : 0;
    }

    /**
     * Num classes.
     *
     * @return the int
     */
    @Override
    public int numClasses() {
        return this.instanceHeader.numClasses();
    }

    /**
     * Class is missing.
     *
     * @return true, if successful
     */
    @Override
    public boolean classIsMissing() {
        return this.instanceData.isMissing(classIndex());
    }

    /**
     * Class attribute.
     *
     * @return the attribute
     */
    @Override
    public Attribute classAttribute() {
        return this.instanceHeader.attribute(classIndex());
    }

    /**
     * Sets the class value.
     *
     * @param d the new class value
     */
    @Override
    public void setClassValue(double d) {
        this.setValue(classIndex(), d);
    }

    //region Copy Constructor
    /**
     * Deep Copy - Copy Constructor
     * @return the instance
     */
    @Override
    public Instance copy() {
        return new InstanceImpl(this);
    }
    //endregion

    //region Dataset Property
    /**
     * Dataset - Getter
     * @return the instances
     */
    @Override
    public Instances dataset() {
        return this.instanceHeader;
    }

    /**
     * Sets the dataset.
     *  Dataset - Setter
     * @param dataset the new dataset
     */
    @Override
    public void setDataset(Instances dataset) {
        this.instanceHeader = new InstancesHeader(dataset);
    }
    //endregion

    /**
     * Adds the sparse values.
     *
     * @param indexValues the index values
     * @param attributeValues the attribute values
     * @param numberAttributes the number attributes
     */
    @Override
    public void addSparseValues(int[] indexValues, double[] attributeValues, int numberAttributes) {
        this.instanceData = new SparseInstanceData(attributeValues, indexValues, numberAttributes); //???
    }

    //region ToString
    /**
     * Text representation of a InstanceImpl.
     */
    @Override
    public String toString() {
        double[] aux = this.instanceData.toDoubleArray();
        StringBuilder str = new StringBuilder();

        for (double anAux : aux)
            str.append(anAux).append(" ");

        return str.toString();
    }
    //endregion

    @Override
    public int numInputAttributes() {
        return this.instanceHeader.numInputAttributes();
    }

    @Override
    public int numOutputAttributes() {
        return numberOutputTargets();
    }
    
    @Override
    public int numberOutputTargets() {
        return this.instanceHeader.numOutputAttributes();
    }

    @Override
    public double classValue(int instAttIndex) {
        return valueOutputAttribute(instAttIndex);
    }

    @Override
    public void setClassValue(int indexClass, double valueAttribute) {
        InstanceInformation instanceInformation = this.instanceHeader.getInstanceInformation();
        this.instanceData.setValue(instanceInformation.outputAttributeIndex(indexClass), valueAttribute);

    }

    @Override
    public Attribute outputAttribute(int outputIndex) {
        InstanceInformation instanceInformation = this.instanceHeader.getInstanceInformation();
        return instanceInformation.outputAttribute(outputIndex);
    }

    @Override
    public Attribute inputAttribute(int attributeIndex) {
        InstanceInformation instanceInformation = this.instanceHeader.getInstanceInformation();
        return instanceInformation.inputAttribute(attributeIndex);
    }

    @Override
    public double valueInputAttribute(int attributeIndex) {
        InstanceInformation instanceInformation = this.instanceHeader.getInstanceInformation();
        return this.instanceData.value(instanceInformation.inputAttributeIndex(attributeIndex));
    }

    @Override
    public double valueOutputAttribute(int attributeIndex) {
        InstanceInformation instanceInformation = this.instanceHeader.getInstanceInformation();
        return this.instanceData.value(instanceInformation.outputAttributeIndex(attributeIndex));
    }
}
