package org.exigencecorp.structgen.examples;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class Struct2Test extends TestCase {

    public void testConstructor() {
        List<String> strings = new ArrayList<String>();
        strings.add("one");
        Struct2 d = new Struct2(strings);
        Assert.assertEquals("one", d.names.get(0));
    }

}
