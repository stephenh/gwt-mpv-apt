
`gwt-mpv-apt` is a Java 6 annotation processor to help generate some of the boilerplate code involved in GWT projects.

Annotation processors enable compiler-/IDE-driven generated-as-you-type code generation based on Java files in your project.

`gwt-mpv-apt` currently will generate:

* `XxxAction/XxxResult` DTOs for [gwt-dispatch](http://code.google.com/p/gwt-dispatch/) as you change the `XxxSpec` class annotated with `@GenDispatch`
* `XxxEvent/XxxHandler` events for GWT events as you change the `XxxEventSpec` class annotated with `@GenEvent`
* `XxxPlace` classes for [gwt-mpv](http://www.gwtmpv.org) places

Also see these blog posts:

* [gwt-mpv-apt-1.1](http://www.draconianoverlord.com/2010/06/28/gwt-mpv-apt-1.1.html)
* [Cutting down GWT verbosity](http://rombertw.wordpress.com/2010/07/03/cutting-down-gwts-verbosity-with-gwt-mpv-apt/)

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
      @In(1)
      Integer id;
      @In(2)
      String name;

      @Out(1)
      boolean success;
      @Out(2)
      String[] messages;
    }

`gwt-mpv-apt` will generate two classes, `SubmitUserAction` and `SubmitUserResult`. `SubmitUserAction` will have fields, getters, and a constructor (or two as needed for serialization) for `id` and `name`. `SubmitResultResult` will have the same for `success`, and `messages`. `equals` and `hashCode` are also correctly implemented if you need it for caching/etc. purposes.

All told, this is ~100 lines of boilerplate code generated from ~8 lines in the spec class.

Notes:

* You must end your class name in `Spec`--it will be stripped and replaced with `Action` and `Result`
* The `@In(1)`/`@In(2)` annotations are required for deterministic ordering due to an Eclipse [bug](https://bugs.eclipse.org/bugs/show_bug.cgi?id=300408)
* You can configure where Eclipse/`javac` puts the generated source code, which the GWT compiler will need access to
* `gwt-mpv-apt` should auto-detect whether you are using `gwt-dispatch` or `gwt-platform` and use the respective `Action`/`Result` interfaces

`@GenEvent`
-----------

If you type:

    @GenEvent
    public class FooChangedEventSpec {
      @Param(1)
      Foo foo;
      @Param(2)
      boolean originator;
    }

`gwt-mpv-apt` will generate two classes, `FooChangedEvent` and `FooChangedHandler`. `FooChangedEvent` will have fields, getters, and a constructor for `foo` and `originator`, plus static `getType()`, instance `dispatch`, etc., for it to function correctly as a `GwtEvent`. `FooChangedHandler` will be an interface with a `onFooChanged` method that takes a `FooChangedEvent` parameter.

Notes:

* You must end your class name in `EventSpec`--it will be stripped and replaced with `Event` and `Handler`
* Per `@GenDispatch`, the `@Param(1)`/`@Param(2)` annotations are to enforce deterministic ordering
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

Community
=========

For discussion or feedback, either:

* Post on the [gwt-mpv-apt](http://groups.google.com/group/gwt-mpv-apt) mailing list
* File bugs over in [issues](http://github.com/stephenh/gwt-mpv-apt/issues) if you come across them

Credits
=======

`gwt-mpv-apt` grew out of projects at [Bizo](http://www.bizo.com), a business demographics advertising company. Thanks to Bizo for their initial and continued support of the project.

Todo
====

* Handle base classes (done for `@GenDispatch`)
* Builder/fluent methods?
* Default values in the spec
* Mutable fields on the event, e.g. claimed

Changelog
=========

* 2.1 - 2011-05-26
  * Deleting `XxxSpec` classes now automatically deletes the derived artifacts (used to require a clean build)
* 2.0 - 2011-05-26
  * Add support for `com.google.web.bindery` events--by default all events extend the new `Event` unless `@GenEvent(gwtEvent = true)` is used
* 1.12 - 2010-11-30
  * Add `EventBus` parameter to generated `DispatchUiCommand`s
* 1.11 - 2010-10-12
  * Make handlers their own top-level class (works better for code completion)
  * Add `SuppressWarnings("all")` for HandlerManager deprecation
  * Remove gwt-mpv event bus
* 1.10 - 2010-09-21
  * Add a static `XxxPlace.NAME` field for accessing place names
* 1.9 - 2010-09-18
  * Add a static `fire` method for each available event bus (`HandlerManager`, gwt-presenter, gwt-mpv, etc.)
* 1.8 - 2010-09-18
  * Add `newRequest` static method to generated places
* 1.7 - 2010-09-14
  * Add `@GenPlace` annotation for generating gwt-mpv places
* 1.6 - 2010-07-22
  * Add static `XxxEvent.fire` method for `@GenEvent` events
* 1.5 - 2010-07-22
  * Update for gwt-platform 0.3 package name change
* 1.4 - 2010-07-22
  * Add `@In`/`@Out` field annotations for `@GenDispatch`
  * Add `@Param` field annotation for `@GenEvent`
* 1.3 - 2010-07-02
  * Fix error reporting so it shows up in Eclipse Problems view (Robert Munteanu)
  * Use a new pom that does not include the jarjar'd dependencies (Robert Munteanu)
* 1.2 - 2010-06-30
  * Add auto-detection of `gwt-platform`
  * Add `@GenDispatch` `baseAction` and `baseResult` parameters
  * Fix corrupted 1.1 release jar
* 1.1 - 2010-06-27
  * Add auto-detection of `gwt-dispatch` for `@GenDispatch`-generated DTOs
  * Add ivy to `build.xml` for downloading jars and publishing to the [maven repo](http://repo.joist.ws/org/gwtmpv/gwt-mpv-apt/)
* 1.0 - 2010-06-06
  * Project renamed from `gwtasyncgen` to `gwt-mpv-apt`

