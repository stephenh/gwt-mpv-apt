package org.exigencecorp.structgen.examples;

import junit.framework.Assert;
import junit.framework.TestCase;

public class Struct1Test extends TestCase {

    public void testConstructor() {
        Struct1 d = new Struct1(0, "name");
        Assert.assertEquals("name", d.name);
    }

    public void testEqualsPositive() {
        Struct1 a = new Struct1(0, "name");
        Struct1 b = new Struct1(0, "name");
        Assert.assertNotSame(a, b);
        Assert.assertEquals(a, b);
    }

    public void testEqualsNegative() {
        Struct1 a = new Struct1(0, "name");
        Struct1 b = new Struct1(0, "name2");
        Assert.assertNotSame(a, b);
        Assert.assertFalse(a.equals(b));
    }

    public void testHashCodePositive() {
        Struct1 a = new Struct1(0, "name");
        Struct1 b = new Struct1(0, "name");
        Assert.assertNotSame(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    public void testHashCodeNegative() {
        Struct1 a = new Struct1(0, "name");
        Struct1 b = new Struct1(0, "name2");
        Assert.assertNotSame(a, b);
        Assert.assertFalse(a.hashCode() == b.hashCode());
    }

}
