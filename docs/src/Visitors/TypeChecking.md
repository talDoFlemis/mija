# Type Checking

- [Type Checking](#type-checking)
    - [Overview](#overview)
    - [Type Checking in AST](#type-checking-in-ast)
    - [Type Checking Visitor](#type-checking-visitor)
    - [Error Handling](#error-handling)

## Overview

Type checking is a process of verifying and enforcing the constraints of types in a program. The type checking process
can be performed at compile-time or run-time. Type checking is a fundamental part of the compiler or interpreter. It
helps to catch bugs at the early stages of development and ensures that the program is well-typed.

## Type Checking in AST

Type checking in AST is a process of verifying and enforcing the constraints of types in the AST nodes. The type
checking process can be performed at compile-time or run-time. Type checking in AST can be implemented using the visitor
pattern. The type checking visitor traverses the AST and performs type checking on the AST nodes.

## Type Checking Visitor

In our compiler, we have implemented a type checking visitor that traverses the AST and performs type checking on the
AST nodes. The type checking visitor is implemented as a subclass of the AST visitor. The type checking visitor
implements the visit methods for each AST node type. The visit methods perform type checking on the AST nodes.

The class TypeCheckingVisitor implements `Visitor<Type>` and SemanticAnalysisStrategy

```java
// T is the return type of the visitor, which is Type in this case
public interface Visitor<T> {
    T visit(And a);

    T visit(BooleanType b);

    { ...}
}

// SemanticAnalysisStrategy is an interface that defines a method
// to check the semantics of a program
public interface SemanticAnalysisStrategy {
    boolean isSemanticsOk(Program program);

    void isSemanticsOkOrThrow(Program program) throws SemanticAnalysisException;
}
```

## Error Handling

When the type-checker detects a type error or an undeclared identifier, it should print an error message and continue –
because the programmer would like to be told of all the errors in the program.

---
<div align="end">
  <a  href="#">
    Return to top
  </a>
</div>