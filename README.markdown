
`gwt-mpv-apt` is a Java 6 annotation processor to help generate some of the boilerplate code involved in GWT projects.

Annotation processors enable compiler-/IDE-driven generated-as-you-type code generation based on Java files in your project.

`gwt-mpv-apt` currently will generate:

* `XxxAction/XxxResult` DTOs for [gwt-dispatch](http://code.google.com/p/gwt-dispatch/) as you change the `XxxSpec` class annotated with `@GenDispatch`
* `XxxEvent/XxxHandler` events for GWT events as you change the `XxxEventSpec` class annotated with `@GenEvent`

Also see these blog posts:

* [gwt-mpv-apt-1.1](http://www.draconianoverlord.com/2010/06/28/gwt-mpv-apt-1.1.html)

Install
=======

* Download `gwt-mpv-apt.jar`, put it in your project's classpath
  * See the [downloads](http://github.com/stephenh/gwt-mpv-apt/downloads) page or the [joist maven repo](http://repo.joist.ws/org/gwtmpv/gwt-mpv-apt/)
* In Eclipse, go to Project Settings, Java Compiler, Annotation Processing, and hit "Enable processing specific settings". Go to Factory path and hit "Enable project specific settings". Select the `gwt-mpv-apt.jar`, hit Okay.
* For `javac`, use JDK6 and it will pick up the processor from your classpath automatically

Examples
========

`@GenDispatch`
--------------

If you type:

    @GenDispatch
    public class SubmitUserSpec {
      Integer in1id;
      String in2name;

      boolean out1success;
      String[] out2messages;
    }

`gwt-mpv-apt` will generate two classes, `SubmitUserAction` and `SubmitUserResult`. `SubmitUserAction` will have fields, getters, and a constructor (or two as needed for serialization) for `id` and `name`. `SubmitResultResult` will have the same for `success`, and `messages`. `equals` and `hashCode` are also correctly implemented if you need it for caching/etc. purposes.

All told, this is ~100 lines of boilerplate code generated from ~8 lines in the spec class.

Notes:

* You must end your class name in `Spec`--it will be stripped and replaced with `Action` and `Result`
* The `1`/`2` numbers in the fields are optional, but Eclipse, due to a [bug](https://bugs.eclipse.org/bugs/show_bug.cgi?id=300408), will not return the fields in a deterministic order. `gwt-mpv-apt` will sort the fields alphabetically, so using the `in1`/`in2` convention will get deterministic ordering back.
* You can configure where Eclipse/`javac` puts the generated source code, which the GWT compiler will need access to
* If you want `gwt-dispatch` action and result classes to be used, use the annotation parameter `-AdispatchBasePackage=net.customware.gwt.dispatch.shared`

`@GenEvent`
-----------

If you type:

    @GenEvent
    public class FooChangedEventSpec {
      Foo p1foo;
      boolean p2originator;
    }

`gwt-mpv-apt` will generate two classes, `FooChangedEvent` and `FooChangedHandler`. `FooChangedEvent` will have fields, getters, and a constructor for `foo` and `originator`, plus static `getType()`, instance `dispatch`, etc., for it to function correctly as a `GwtEvent`. `FooChangedHandler` will be an interface with a `onFooChanged` method that takes a `FooChangedEvent` parameter.

Notes:

* You must end your class name in `EventSpec`--it will be stripped and replaced with `Event` and `Handler`
* Per `@GenDispatch`, the `p1`/`p2` prefixes are to enforce deterministic ordering
* Per `@GenDispatch`, you can configure where Eclipse/`javac` puts the generated source code, which GWT will need access to

Caveats
=======

Eclipse
-------

In Eclipse, you *must* be running Eclipse *on* a JDK6 JRE. This does not mean running Eclipse on JDK5 and pointing it at a JDK6 installation in "Installed JREs". For Eclipse's code-generation-as-you-type to work, Eclipse must be on a JDK6 JRE.

For Mac users, this means you need Eclipse 3.5 64-bit because it is compatible with Apple's 64-bit-only JDK6. You will not be able to run Eclipse 3.3/3.4 on Apple's 64-bit JDK6 because the Eclipse 3.3/3.4 SWT bindings are only 32-bit.

IntelliJ
--------

Has mediocre/near-useless annotation processor support last I checked.

Todo
====

* Handle base classes
* Builder/fluent methods?
* Default values in the spec
* Mutable fields on the event, e.g. claimed

Changelog
=========

* 1.2 - 2010-06-30
  * Add auto-detection of `gwt-platform`
  * Add `@GenDispatch` `baseAction` and `baseResult` parameters
  * Fix corrupted 1.1 release jar
* 1.1 - 2010-06-27
  * Add auto-detection of `gwt-dispatch` for `@GenDispatch`-generated DTOs
  * Add ivy to `build.xml` for downloading jars and publishing to the [maven repo](http://repo.joist.ws/org/gwtmpv/gwt-mpv-apt/)
* 1.0 - 2010-06-06
  * Project renamed from `gwtasyncgen` to `gwt-mpv-apt`

