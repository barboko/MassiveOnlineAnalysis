package moa.core;

/**
 * Interface for something unknown.
 * TODO: Check what is all about it.
 * @param <T>
 */
public interface Example<T extends Object> {
    T getData();
    double weight();
    void setWeight(double weight);
} 
