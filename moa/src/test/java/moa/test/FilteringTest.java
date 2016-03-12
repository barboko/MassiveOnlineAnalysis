package moa.test;

import com.yahoo.labs.samoa.instances.Instance;
import moa.streams.MultiTargetArffFileStream;
import org.junit.Test;


/**
 * @author Bar Bokovza (barboko) & Leonid Rice (leorice)
 */
public class FilteringTest {
    //region members
    private MultiTargetArffFileStream stream;
    //endregion

    @Test
    public void run() {
        stream = new MultiTargetArffFileStream(ClassLoader.getSystemResource("moa/classifiers/data/small_regression.arff").getPath(), "4-6");

        Instance inst = stream.nextInstance().getData();
        System.out.println(inst.inputAttribute(0).name());
        inst.deleteAttributeAt(0);
        System.out.println(inst.inputAttribute(0).name());
    }
}
