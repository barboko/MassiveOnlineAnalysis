package moa.streams.filters;

import com.github.javacliparser.StringOption;
import com.yahoo.labs.samoa.instances.*;
import moa.core.InstanceExample;

import java.util.ArrayList;
import java.util.List;

public class SelectAttributesFilter extends AbstractMultiLabelStreamFilter implements MultiLabelStreamFilter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public StringOption inputStringOption= new StringOption("inputStringOption", 'i', "Selection of attributes to be used as input.", "1") ;
	public StringOption outputStringOption= new StringOption("outputStringOption", 'o', "Selection of attributes to be used as output.", "-1") ;
	protected InstancesHeader dataset;
	protected Selection inputsSelected;
	//List of Nodes  , that each node is a range
	protected Selection outputsSelected;

    @Override
    public String getPurposeString() {
        return "Selects input and output attributes.";
    }

	@Override
	public InstancesHeader getHeader() {
		return dataset;
	}

	@Override
	public InstanceExample nextInstance() {
		Instance instance = (Instance) ((Instance) this.inputStream.nextInstance().getData());
		if(dataset==null){
			initialize(instance);
		}		
		return new InstanceExample(processInstance(instance));
	}

	private void initialize(Instance instance) {
		inputsSelected=getSelection(inputStringOption.getValue());
		outputsSelected=getSelection(outputStringOption.getValue());
		int totAttributes=inputsSelected.numValues()+outputsSelected.numValues();
		Instances ds= new Instances();
		List<Attribute> v = new ArrayList<>(totAttributes);
		List<Integer> indexValues = new ArrayList<>(totAttributes);
		int ct=0;
		for (int i=0; i<inputsSelected.numEntries();i++)
		{
			for (int j=inputsSelected.getStart(i); j<=inputsSelected.getEnd(i);j++){
				v.add(instance.attribute(j-1));
				indexValues.add(ct);
				ct++;
			}
		}
		
		for (int i=0; i<outputsSelected.numEntries();i++)
		{
			for (int j=outputsSelected.getStart(i); j<=outputsSelected.getEnd(i);j++){
				v.add(instance.attribute(j-1));
				indexValues.add(ct);
				ct++;
			}
		}

		ds.setAttributes(v,indexValues);
		Range r= new Range("-" + outputsSelected.numValues());
		r.setUpper(totAttributes);
		ds.setRangeOutputIndices(r);
		dataset= new InstancesHeader(ds);

		if(dataset.numOutputAttributes() > 0) {
			String name = dataset.outputAttribute(0).name();

			for(int i = 0; i < dataset.numAttributes(); i++)
				if(dataset.attribute(i).name().equals(name))
				{
					dataset.setClassIndex(i);
					break;
				}
		}
	}

	private Selection getSelection(String text) {
		Selection s = new Selection();
		String [] parts=text.trim().split(",");
		for (String p : parts)
		{
			int index=p.indexOf('-');
			if(index <= 0) {//is a single entry
				int val = Integer.parseInt(p);
				s.add(val < 0 ? getHeader().numAttributes() - val : val);
			}
			else
			{
				String [] vals =p.split("-");
				s.add(Integer.parseInt(vals[0]),Integer.parseInt(vals[1]));
			}
		}
		return s;
	}

	@Override
	public void getDescription(StringBuilder sb, int indent) {
	}

	@Override
	protected void restartImpl() {

	}
	
	private Instance processInstance(Instance instance) {
		double [] attValues = new double[dataset.numAttributes()];
		Instance newInstance=new InstanceImpl(instance.weight(),attValues);
		
		int count=0;
		for (int i=0; i<inputsSelected.numEntries(); i++){
			int start=inputsSelected.getStart(i)-1;
			int end=inputsSelected.getEnd(i)-1;
			for (int j=start; j<=end; j++){
				newInstance.setValue(count, instance.value(j));
				count++;
			}
		}
		
		for (int i=0; i<outputsSelected.numEntries(); i++){
			int start=outputsSelected.getStart(i)-1;
			int end=outputsSelected.getEnd(i)-1;
			for (int j=start; j<=end; j++){
				newInstance.setValue(count, instance.value(j));
				count++;
			}
		}
		newInstance.setDataset(dataset);
		return newInstance;
	}

}
