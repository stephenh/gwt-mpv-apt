package org.exigencecorp.structgen.examples;

import org.exigencecorp.structgen.Struct;

@Struct
public interface Struct1Spec {

    public static final int one = 1;

    String name();

    Integer count();

}
