package moa.test;

import moa.core.Example;
import moa.streams.MultiTargetArffFileStream;
import moa.streams.filters.DuplicateFilter;
import org.junit.Test;


/**
 * @author Bar Bokovza (barboko) & Leonid Rice (leorice)
 */
public class FilteringTest {
    @Test
    public void run() {
        MultiTargetArffFileStream stream = new MultiTargetArffFileStream(ClassLoader.getSystemResource("moa/classifiers/data/small_regression.arff").getPath(), "4-6");

        DuplicateFilter filter = new DuplicateFilter();
        filter.setInputStream(stream);
        filter.restart();

        Example example = filter.nextInstance();
        example = filter.nextInstance();
        example = filter.nextInstance();
        example = filter.nextInstance();
        example = filter.nextInstance();
        example = filter.nextInstance();
        example = filter.nextInstance();

    }
}
