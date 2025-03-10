---
layout: default
title: CodeNarc - Design Rules
---  

# Design Rules  ("*rulesets/design.xml*")



## AbstractClassWithPublicConstructor Rule

*Since CodeNarc 0.14*

Checks for `abstract` classes that define a `public` constructor, which is useless and confusing.

The following code produces a violation:

```
    abstract class MyClass {
        MyClass() { }
    }
```


## AbstractClassWithoutAbstractMethod Rule

*Since CodeNarc 0.12*

The abstract class does not contain any abstract methods. An abstract class suggests an incomplete implementation,
which is to be completed by subclasses implementing the abstract methods. If the class is intended to be used as a
base class only (not to be instantiated directly) a protected constructor can be provided prevent direct instantiation.

Example:

```
    public abstract class MyBaseClass {
        void method1() {  }
        void method2() {  }
        // consider using abstract methods or removing
        // the abstract modifier and adding protected constructors
    }
```

The following examples all pass:

```
    abstract class MyClass extends AbstractParent {
        // OK because parent is named Abstract.*
    }
    abstract class MyClass extends BaseParent{
        // OK because parent is named Base.*
    }
```

This rule has a single `enhancedMode` property which defaults to `false`. When set to `true`, this rule
will run in [enhanced mode](./codenarc-enhanced-classpath-rules.html) and will not produce a violation when an
abstract class extends an abstract superclass.


## AssignmentToStaticFieldFromInstanceMethod Rule

*Since CodeNarc 0.24*

Checks for assignment to a static field from an instance method.

Influenced by the **AssignmentToNonFinalStatic** rule from **PMD**, and the
**ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD** rule from **FindBugs**.

Example of violations:

```
    class MyClass {
        private static field1
        protected static String field2 = 'abc'
        public static int field3 = 123
        static String property1 = 'abc'
        private static final NAME = 'joe'

        private void doStuff() {
            field1 = new Object()       // violation
            field2 = 'xxx'              // violation
            field3 = 999                // violation
            property1 = 'xxx'           // violation

            final NAME = 'martin'       // no violation; local var hides static field
        }
    }
```


## BooleanMethodReturnsNull Rule

*Since CodeNarc 0.11*

Checks for a method with `Boolean` return type that returns an explicit `null`. A method that
returns either `Boolean.TRUE`, `Boolean.FALSE` or `null` is an accident waiting to happen.
This method can be invoked as though it returned a value of type `boolean`, and the compiler will
insert automatic *unboxing* of the `Boolean` value. If a `null` value is returned, this will
result in a `NullPointerException`.


## BuilderMethodWithSideEffects Rule

*Since CodeNarc 0.16*

A builder method is defined as one that creates objects. As such, they should never be of void return type. If a method
is named build, create, or make, then it should always return a value.

This rule has one property: `methodNameRegex`. The default value is (make.*|create.*|build.*). Update this property
if you have some  other naming convention for your builder methods.

Example of violations:

```

    class MyClass {

            void make() { /* ... */ }
            void makeSomething() { /* ... */ }

            void create() { /* ... */ }
            void createSomething() { /* ... */ }

            void build() { /* ... */ }
            void buildSomething() { /* ... */ }
    }
```

## CloneableWithoutClone Rule


Checks for classes that implement the `java.lang.Cloneable` interface without implementing
the `clone()` method.

Here is an example of code that produces a violation:

```
    class BadClass implements Cloneable {
        def someMethod()
    }
```


## CloseWithoutCloseable Rule

*Since CodeNarc 0.12*

If a class defines a `void close()` method then that class should implement `java.io.Closeable`.

This rule has a single `enhancedMode` property which defaults to `false`. When set to `true`, this rule
will run in [enhanced mode](./codenarc-enhanced-classpath-rules.html) and will not produce a violation when a class
implements `close` and extends a class that itself implements `Closeable`.


## CompareToWithoutComparable Rule

*Since CodeNarc 0.12*

If you implement a compareTo method then you should also implement the `Comparable` interface. If you
don't then you could possibly get an exception if the Groovy == operator is invoked on your object.
This is an issue fixed in Groovy 1.8 but present in previous versions.

This rule has a single `enhancedMode` property which defaults to `false`. When set to `true`, this rule
will run in [enhanced mode](./codenarc-enhanced-classpath-rules.html) and will not produce a violation when a class
implements `compareTo` and extends a class that itself implements `Comparable`.

Here is an example of code that produces a violation:

```
    class BadClass {
        int compareTo(Object o) { ... }
    }
```

Known limitations:

  * When not running in enhanced mode, this rule is not able to determine if the class extends a superclass that itself
    implements `Comparable`, or if it implements an interface that extends `Comparable`. In those cases, this
    rule produces a false violation.


## ConstantsOnlyInterface Rule

*Since CodeNarc 0.12*

An interface should be used only to model a behaviour of a class: using an interface as a container of constants is
a poor usage pattern. Example:

```
    public interface ConstantsInterface {
        public static final int CONSTANT_1 = 0
        public static final String CONSTANT_2 = "1"
    }
```


## EmptyMethodInAbstractClass Rule

*Since CodeNarc 0.12*

An empty method in an abstract class should be abstract instead, as developer may rely on this empty implementation
rather than code the appropriate one.

```
    abstract class MyClass {
        def couldBeAbstract_1() {
            return null  // Should be abstract method
        }

        void couldBeAbstract_2() {
            // Should be abstract method
        }
    }
```


## FinalClassWithProtectedMember Rule

*Since CodeNarc 0.12*

This rule finds classes marked final that contain `protected` members. If a class is `final` then it may not be
subclassed, and there is therefore no point in having a member with `protected` visibility. Either the class should
not be `final` or the member should be private or protected.


## ImplementationAsType Rule


Checks for use of the following concrete classes when specifying the type of a method
parameter, closure parameter, constructor parameter, method return type or field
type. The corresponding interfaces should be used to specify the type instead.

  * java.util.ArrayList
  * java.util.GregorianCalendar
  * java.util.HashMap
  * java.util.HashSet
  * java.util.Hashtable
  * java.util.LinkedHashMap
  * java.util.LinkedHashSet
  * java.util.LinkedList
  * java.util.TreeMap
  * java.util.TreeSet
  * java.util.Vector
  * java.util.concurrent.ArrayBlockingQueue
  * java.util.concurrent.ConcurrentHashMap
  * java.util.concurrent.ConcurrentLinkedQueue
  * java.util.concurrent.CopyOnWriteArrayList
  * java.util.concurrent.CopyOnWriteArraySet
  * java.util.concurrent.DelayQueue
  * java.util.concurrent.LinkedBlockingQueue
  * java.util.concurrent.PriorityBlockingQueue
  * java.util.concurrent.PriorityQueue
  * java.util.concurrent.SynchronousQueue

Here are examples of code that produces violations:

```
    // Method parameter
    void myMethod(ArrayList list) {                   // violation
        ...
    }

    // Constructor parameter
    class MyClass {
        MyClass(java.util.HashSet set) {              // violation
            ...
        }
    }

    // Closure parameter
    def closure = { PriorityQueue queue -> ... }      // violation

    // Method return type
    GregorianCalendar calculateDate(int num) {        // violation
        ...
    }

    // Field type
    class MyClass {
        Hashtable map                                 // violation
    }
```


## Instanceof Rule

*Since CodeNarc 0.22*

Checks for use of the `instanceof` operator. Prefer using *polymorphism* instead.

Use the `ignoreTypeNames` property to configure ignored type names (the class name specified as the
right-hand expression of the `instanceof`). It defaults to ignoring `instanceof` checks against exception classes.

Here are a couple references that discuss the problems with using `instanceof` and the preference
for using *polymorphism* instead:

  * [Beware of instanceof operator](http://www.javapractices.com/topic/TopicAction.do?Id=31)
  * [How does one use polymorphism instead of instanceof? (And why?)](http://stackoverflow.com/questions/4192837/how-does-one-use-polymorphism-instead-of-instanceof-and-why)

By default, the rule does not analyze test files. This rule sets the default value of the
*doNotApplyToFilesMatching* property to ignore file names ending in 'Spec.groovy', 'Test.groovy', 'Tests.groovy'
or 'TestCase.groovy'.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreTypeNames             | Specifies one or more (comma-separated) class names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | "*Exception" |

  Example of violations:

```
    class MyClass {
        boolean isRunnable = this instanceof Runnable       // violation
    }
```


## LocaleSetDefault Rule

*Since CodeNarc 0.20*

Checks for calls to `Locale.setDefault()`, or `Locale.default = Xxx`, which sets the Locale
across the entire JVM. That can impact other applications on the same web server, for instance.

From the java.util.Locale javadoc for `setDefault`:
*Since changing the default locale may affect many different areas of functionality, this method
should only be used if the caller is prepared to reinitialize locale-sensitive code running within
the same Java Virtual Machine.*

Example of violations:

```
    Locale.setDefault(Locale.UK)                                // violation
    java.util.Locale.setDefault(Locale.FRANCE)                  // violation
    Locale.setDefault(Locale.Category.DISPLAY, Locale.JAPAN)    // violation

    Locale.default = Locale.UK                                  // violation
```


## NestedForLoop Rule

*Since CodeNarc 0.23*

Reports classes with nested for loops.

Example of violations:

```
for (int i = 0; i * 100; ++i) {
    for (int j = 0; j * 100; ++j) { // violation
        println i + j
    }
}

for (int i = 0; i * 100; ++i) {
    for (int j = 0; j * 100; ++j) { // violation
        println i + j
    }
    for (int j = 0; j * 100; ++j) { // violation
        println i + j
    }
}

for (int i = 0; i * 100; ++i) {
    for (int j = 0; j * 100; ++j) { // violation
        for (int k = 0; k * 100; ++k) { // violation
            println i + j + k
        }
    }
}
```


## OptionalCollectionReturnType Rule

*Since CodeNarc 2.0.0*

Do not declare a method return type of `Optional<List>` (or `Collection`, `ArrayList`, `Set`, `Map`, `HashMap`, etc.). Return an empty collection instead.  See [The Java Optional class: 11 more recipes for preventing null pointer exceptions](https://blogs.oracle.com/javamagazine/the-java-optional-class-11-more-recipes-for-preventing-null-pointer-exceptions). 

This rule checks for `Optional<collection-type>` return types, where *collection-type* is one of these common collection interfaces or implementation classes:
 - `Collection`
 - `List` (and `ArrayList`, `LinkedList`)
 - `Set` (and `HashSet`, `LinkedHashSet`, `EnumSet`)
 - `SortedSet` (and `TreeSet`)
 - `Map` (and `HashMap`, `LinkedHashMap`, `EnumMap`)
 - `SortedMap` (and `TreeMap`)


Example of violations:

```
    class MyClass {
        Optional<Collection<Object>> getCollection() { }        // violation

        private Optional<List> getList() { }                    // violation
        Optional<ArrayList<String>> getArrayList() { }          // violation
        
        protected Optional<Set<BigDecimal>> getSet() { }        // violation
        Optional<HashSet<Boolean>> getHashSet() { }             // violation

        Optional<Map<Integer, String>> getMap() { }             // violation
        Optional<TreeMap<String, String>> getTreeMap() { }      // violation
    }
```


## OptionalField Rule

*Since CodeNarc 2.0.0*

Do not use an `Optional` as a field type. See [The Java Optional class: 11 more recipes for preventing null pointer exceptions](https://blogs.oracle.com/javamagazine/the-java-optional-class-11-more-recipes-for-preventing-null-pointer-exceptions).

Example of violations:

```
    class MyClass {
        Optional<Integer> count;                            // violation
        public String name;
        public Optional<String> alias = Optional.of("x")    // violation
        protected static Optional<Object> lock              // violation
    }
```


## OptionalMethodParameter Rule

*Since CodeNarc 2.0.0*

Do not use an `Optional` as a parameter type for a method or constructor. See [The Java Optional class: 11 more recipes for preventing null pointer exceptions](https://blogs.oracle.com/javamagazine/the-java-optional-class-11-more-recipes-for-preventing-null-pointer-exceptions).

Example of violations:

```
    class MyClass {
        protected MyClass(Optional<Integer> count) { }                  // violation
        MyClass(Optional<String> name, Optional<Integer> sum) { }       // 2 violations
        private MyClass(Optional something) { }                         // violation

        void doStuff(Optional<Integer> count) { }                       // violation
        public String getName() { return 'abc' }
        int count(Optional<String> alias, Optional<Integer> total) { }  // 2 violations
        private doSomething(Optional something) { }                     // violation
    }
```


## PrivateFieldCouldBeFinal Rule

*Since CodeNarc 0.17*

This rule finds `private` fields that are only set within a *constructor* or *field initializer*.
Such fields can safely be made `final`.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreFieldNames            | Specifies one or more (comma-separated) field names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |
| ignoreJpaEntities           | Specifies whether fields defined inside classes annotated with @Entity or @MappedSuperclass JPA annotations should be ignored (i.e., that should not cause a rule violation). | `false` |

## PublicInstanceField Rule

*Since CodeNarc 0.14*

Using public fields is considered to be a bad design. Use properties instead.

Example of violations:

```
    class Person {
        public String name
    }
```


## ReturnsNullInsteadOfEmptyArray Rule

*Since CodeNarc 0.11*

If you have a method or closure that returns an array, then when there are no results return a zero-length
(empty) array rather than `null`. It is often a better design to return a zero-length array rather than a
`null` reference to indicate that there are no results (i.e., an *empty* list of results). This way,
no explicit check for `null` is needed by clients of the method.


## ReturnsNullInsteadOfEmptyCollection Rule

*Since CodeNarc 0.11*

If you have a method or closure that returns a collection, then when there are no results return a zero-length
(empty) collection rather than `null`. It is often a better design to return a zero-length collection
rather than a `null` reference to indicate that there are no results (i.e., an *empty* list of results).
This way, no explicit check for `null` is needed by clients of the method.


## SimpleDateFormatMissingLocale Rule

*Since CodeNarc 0.12*

Be sure to specify a `Locale` when creating a new instance of `SimpleDateFormat`; the class is locale-sensitive. If you
instantiate `SimpleDateFormat` without a `Locale` parameter, it will format the date and time according to the default
`Locale`. Both the pattern and the `Locale` determine the format. For the same pattern, `SimpleDateFormat` may format a
date and time differently if the Locale varies.

```
    // violation, missing locale
    new SimpleDateFormat('pattern')

    // OK, includes locale
    new SimpleDateFormat('pattern', Locale.US)

    // OK, includes a variable that perhaps is a locale
    new SimpleDateFormat('pattern', locale)
```


## StatelessSingleton Rule

*Since CodeNarc 0.14*

There is no point in creating a stateless Singleton because there is nothing within the class that needs guarding and
no side effects to calling the constructor. Just create new instances of the object or write a Utility class with
static methods. In the long term, Singletons can cause strong coupling and hard to change systems.

If the class has any fields at all, other than a self reference, then it is not considered stateless. A self reference
is a field of the same type as the enclosing type, or a field named instance or _instance. The field name self reference
is a property named instanceRegex that defaults to the value 'instance|_instance'

Example of violations:

```
    @groovy.lang.Singleton
    class Service {
       // violation: the class has no fields but is marked Singleton
        void processItem(item){
        }
    }

    class Service {
       // violation: the class has no fields other than 'instance' but is marked Singleton
        static instance
        void processItem(item){
        }
    }

    class Service {                                       // violation
        static Service service
        void processItem(item){
        }
    }
```


## ToStringReturnsNull Rule

*Since CodeNarc 0.21*

Checks for `toString()` methods that return `null`. This is unconventional and could
cause unexpected `NullPointerExceptions` from normal or implicit use of `toString()`.

Example of violations:

```
    class MyClass {
        String toString() {
            if (foo()) {
                return 'MyClass'
            } else {
                return null         // violation
            }
        }
    }

    class MyClass {
        String toString() {
            calculateStuff()
            null                    // violation
        }
    }

    class MyClass {
        String toString() {         // violation - implicit return of null
        }
    }
```
