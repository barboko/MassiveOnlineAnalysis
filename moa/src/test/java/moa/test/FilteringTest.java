package moa.test;

import com.yahoo.labs.samoa.instances.Instance;
import moa.streams.ArffFileStream;
import moa.streams.filters.DuplicateFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author Bar Bokovza (barboko) & Leonid Rice (leorice)
 */
public class FilteringTest {
    @Test
    public void run() {
        ArffFileStream stream = new ArffFileStream("copy.arff", -1);
        DuplicateFilter filter = new DuplicateFilter(3);
        filter.setInputStream(stream);
        filter.prepareForUse();

        Instance instance = stream.nextInstance().getData();
        Instance instance2 = stream.nextInstance().getData();
        Instance instance3 = stream.nextInstance().getData();
        Instance instance4 = stream.nextInstance().getData();

        Assert.assertEquals(instance, instance2);
        Assert.assertEquals(instance, instance3);
        Assert.assertNotEquals(instance, instance4);
    }
}
