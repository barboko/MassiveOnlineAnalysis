


package moa.streams.filters;

import com.github.javacliparser.StringOption;
import com.yahoo.labs.samoa.instances.*;
import moa.core.Example;
import moa.core.InstanceExample;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.Double.NaN;

public class DefaultValueFilter
        extends AbstractMultiLabelStreamFilter
        implements MultiLabelStreamFilter {

    //region Options
    public StringOption attributesOption = new StringOption("attributesOption", 'a', "Selection of attributes to be defaulted", "1") ;
    //endregion

    //region Members
    private InstancesHeader _header;
    private Set<Integer> _attributes;
    //endregion

    //region Implementation
    @Override
    protected void restartImpl() {

    }

    @Override
    public InstancesHeader getHeader() {
        return _header;
    }

    @Override
    public Example<Instance> nextInstance() {
        Instance instance = inputStream.nextInstance().getData();
        if(_header == null) initialize(instance);
        return processInstance(instance);
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {

    }
    //endregion

    //region Helpers
    private Example<Instance> processInstance(Instance instance) {
        if(instance == null || _attributes == null)
            return null;

        int size = _header.numAttributes();
        double [] attValues = new double[size];
        for(int i = 0; i < size; i++)
            attValues[i] = instance.value(i);

        Instance result = new InstanceImpl(instance.weight(), attValues);

        for(int i : _attributes)
            result.setValue(i, NaN);
        return new InstanceExample(result);
    }

    private void initialize(Instance instance) {
        if(instance == null)
            return;
        createMapping();

        Instances ds = new Instances();
        List<Attribute> attr = new LinkedList<>();
        for(int k = 0; k < instance.numAttributes(); k++)
            attr.add(instance.attribute(k));

        ds.setAttributes(attr);
        Range r = new Range("-"+instance.numAttributes());
        r.setUpper(instance.numAttributes());
        ds.setClassIndex(instance.classIndex());
        _header = new InstancesHeader(ds);
    }

    private void createMapping() {
        String str = attributesOption.getValue();
        _attributes = new HashSet<>();
        String[] parts= str.trim().split(",");

        for (String p : parts)
        {
            int index = p.indexOf('-');
            if(index == -1) { //is a single entry
                _attributes.add(Integer.parseInt(p) -1);
            }
            else
            {
                String[] values = p.split("-");
                int a = Integer.parseInt(values[0])-1,
                        b = Integer.parseInt(values[1])-1;

                for(int i = a; i <= b; i++)
                    _attributes.add(i);
            }
        }
    }
    //endregion
}


