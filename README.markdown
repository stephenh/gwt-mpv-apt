
gwtasyncgen is a Java 6 annotation processor that will automatically, as you type, update your GWT XxxAsync interfaces as you change the source interfaces.

Install
=======

* Download gwtasyncgen, put it in your project's build path
* In Eclipse, go to Project Settings, Java Compiler, Annotation Processing, and hit "Enable processing specific settings". Go to Factory path and hit "Enable project specific settings". Select the "gwtasyncgen.jar", hit Okay.
* In javac, pass "-processor org.exigencecorp.gwtasyncgen.Processor"

For any service interfaces annotated with "RemoteServiceRelativePath", gwtasyncgen should pick them up and generate an async version. If you have existing async versions, you should get compile errors about having two versions.

Caveats
=======

Eclipse
-------

In Eclipse, you *must* be running Eclipse *on* a JDK6 JRE. This does not mean running Eclipse on JDK5 and pointing it at a JDK6 installation in "Installed JREs". For Eclipse's code-generation-as-you-type to work, Eclipse must be on a JDK6 JRE.

For Mac users, this means you need Eclipse 3.5 64-bit because it is compatible with Apple's 64-bit-only JDK6. You will not be able to run Eclipse 3.3/3.4 on Apple's 64-bit JDK6 because the Eclipse 3.3/3.4 SWT bindings are only 32-bit.

