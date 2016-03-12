package moa.streams.filters;

import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.core.Example;

import java.util.HashSet;

/**
 * @author Bar Bokovza (barboko) & Leonid Rice (leorice)
 */
public class DuplicateFilter extends AbstractStreamFilter {
    private int amount;
    private int counter;
    private Example last;

    @Override
    protected void restartImpl() {
        counter = 0;

        HashSet<Integer> delays = new HashSet<Integer>();
        InstancesHeader header = getHeader();
        for(int i = 0; i < header.size(); i++) {
            int k = header.attribute(i).getDelayTime();
            if(k >= 0)
                delays.add(k);
        }

        amount = delays.size();
    }

    @Override
    public InstancesHeader getHeader() {
        return inputStream.getHeader();
    }

    @Override
    public Example nextInstance() {
        if(counter + 1 >= amount)
        {
            last = inputStream.nextInstance();
            counter = 0;
        } else
            counter++;
        return last;
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {

    }
}
