# Table of Symbols

- [Table of Symbols](#table-of-symbols)
    - [Overview](#overview)
        - [Imperative Symbol Table](#imperative-symbol-table)
        - [Functional Symbol Table](#functional-symbol-table)
        - [Symbols](#symbols)
    - [Details of Implementation](#details-of-implementation)
        - [MainTable](#maintable)
        - [ClassTable](#classtable)
        - [MethodTable](#methodtable)
        - [Exceptions](#exceptions)

## Overview

This table of symbols is a part of the semantic analysis phase of the compiler. It is responsible for building a symbol
table that keeps track of the names and types of variables and functions in the program. The symbol table is used by
other parts of the compiler, such as the [type checker](TypeChecking.md) and code generator, to ensure that the program
is well-formed and to generate correct code.

```java

// Implementation of the SymbolTableVisitor
public class SymbolTableVisitor implements Visitor<Void> {
    private MainTable mainTable = new MainTable();
    private ClassTable currentClassTable = null;
    private MethodTable currentMethodTable = null;
    private final ArrayList<SymbolTableException> errors = new ArrayList<>();
    private final boolean ignoreExtends = false;

    public Void visit(And a) {
        return null;
    }

    public Void visit(BooleanType b) {
        return null;
    }

    { ...}
}
```

### Imperative Symbol Table

The imperative symbol table is built using mutable data structures, such as maps and lists. It is updated in place as
the compiler traverses the AST, adding new symbols and checking for errors.

*Is the one we are using in our implementation.*

### Functional Symbol Table

The functional symbol table is built using immutable data structures, such as trees and lists. It is updated by creating
new copies of the symbol table with the new symbols added. This approach is more functional and can make it easier to
reason about the symbol table, but it can be less efficient than the imperative approach.

### Symbols

A symbol is a name that is associated with a type. In the symbol table, symbols are used to represent variables,
functions, classes, and other entities in the program. Each symbol has a name and a type, which is used to determine how
the symbol can be used in the program.

<div align="end">
  <a  href="#">
    ⬆️
  </a>
</div>

## Details of Implementation

### MainTable

The main table is the root of the symbol table. It contains a map of class names to class tables.

```java
public class MainTable {
    HashMap<String, ClassTable> map = new HashMap<>();
}
```

<div align="end">
  <a  href="#">
    ⬆️
  </a>
</div>

### ClassTable

The class table contains a map of field names to types and a map of method names to method tables. It also contains a
reference to the parent class table, if any.

*In SymbolTableVisitor we have a currentClassTable to keep track of the current class we are in.*

```java
public class ClassTable {
    private String className;
    private ClassTable parent;
    HashMap<String, Type> fieldsContext = new HashMap<>();
    HashMap<String, MethodTable> methodsContext = new HashMap<>();
}
```

<div align="end">
  <a  href="#">
    ⬆️
  </a>
</div>

### MethodTable

The method table contains a map of parameter names to types and a map of local variable names to types. It also contains
a reference to the parent class table.

*In SymbolTableVisitor we have a currentMethodTable to keep track of the current method we are in.*

```java
public class MethodTable {
    private String methodName;
    private Type methodReturnType;
    private ClassTable classParent;
    LinkedHashMap<String, Type> paramsContext = new LinkedHashMap<>();
    HashMap<String, Type> localsContext = new HashMap<>();
}
```

<div align="end">
  <a  href="#">
    ⬆️
  </a>
</div>

### Exceptions

The SymbolTableException is a custom exception that is thrown when an error occurs during the building of the symbol
table.

The SymbolTableVisitor don't throw exceptions, but it keeps track of them in a list of SymbolTableExceptions.

```java
public class SymbolTableException extends RuntimeException {
    public SymbolTableException(String errorMessage) {
        super(errorMessage);
    }

    public SymbolTableException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
```

---
<div align="end">
  <a  href="#">
    Return to top
  </a>
</div>