package moa.streams.filters;

import com.yahoo.labs.samoa.instances.Instance;
import moa.streams.MultiTargetArffFileStream;
import moa.streams.generators.multilabel.MultilabelArffFileStream;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestDefaultValueFilter {
    //region Members
    private static double epsilon=0.00000001;
    private static MultilabelArffFileStream stream;
    private static DefaultValueFilter filter;
    //endregion

    //region Test Methods
    @BeforeClass
    public static void create() {
        MultiTargetArffFileStream stream=new MultiTargetArffFileStream(ClassLoader.getSystemResource("moa/classifiers/data/small_regression.arff").getPath(), "4-6");
        filter= new DefaultValueFilter();
        filter.setInputStream(stream);
        filter.attributesOption.setValue("2-5,8");
    }

    @Test
    public void testNextInstance(){
        Instance inst = filter.nextInstance().getData();

        assertEquals(0.0, inst.value(1), epsilon);
        assertEquals(0.0, inst.value(2), epsilon);
        assertEquals(0.0, inst.value(3), epsilon);
        assertEquals(0.0, inst.value(4), epsilon);
        assertEquals(0.0, inst.value(7), epsilon);

    }
    //endregion
}
