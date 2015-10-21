package com.yahoo.labs.samoa.instances;

/**
 * Defines the different types of an attribute in SAMOA
 * @author Bar Bokovza
 */

public enum AttributeType {
    @Stringer(value = "integer")
    Integer,
    @Stringer(value = "real")
    Real,
    @Stringer(value = "numeric")
    Numeric,
    @Stringer(value = "string")
    String,
    @Stringer(value = "date")
    Date
}
