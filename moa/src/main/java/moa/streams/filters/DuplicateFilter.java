package moa.streams.filters;

import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.core.Example;

/**
 * @author Bar Bokovza (barboko) & Leonid Rice (leorice)
 */
public class DuplicateFilter extends AbstractStreamFilter {
    private int amount;
    private int counter;
    private Example last;

    public DuplicateFilter(int amount) {
        this.amount = amount;
        this.counter = 1;
    }

    @Override
    protected void restartImpl() {
        this.counter = 1;
    }

    @Override
    public InstancesHeader getHeader() {
        return inputStream.getHeader();
    }

    @Override
    public Example nextInstance() {
        if(counter == 1)
            last = inputStream.nextInstance();

        counter++;

        if(counter > amount)
            counter = 1;

        return last;
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        sb.append("Filter to return the same instance k times.");
    }
}
