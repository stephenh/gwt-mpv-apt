package org.exigencecorp.structgen.examples;

import java.util.List;

import org.exigencecorp.structgen.Struct;

@Struct
public interface Struct2Spec {

    // Make sure generics work
    List<String> names();

}
