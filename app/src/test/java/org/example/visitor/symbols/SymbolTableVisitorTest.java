package org.example.visitor.symbols;

import org.example.ast.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTableVisitorTest {
    private static ClassTable getMockedClassTable() {
        return ClassTable.builder()
                .className("class")
                .parent(null)
                .build();
    }

    private static MethodTable getMockedMethodTable() {
        return MethodTable.builder()
                .methodName("method")
                .methodReturnType(null)
                .classParent(getMockedClassTable())
                .build();
    }

    private static Stream<Arguments> getMockedFormalsAndArgs() {
        return Stream.of(
                Arguments.of(
                        new HashMap<>() {{
                            put("tubias", new IntegerType());
                        }}
                ),
                Arguments.of(
                        new HashMap<>() {{
                            put("tubias", new IntArrayType());
                            put("tubias2", new IntegerType());
                        }}
                ),
                Arguments.of(
                        new HashMap<>() {{
                            put("tubias", new BooleanType());
                            put("tubias2", new IntArrayType());
                            put("tubias3", new IntegerType());
                        }}
                ),
                Arguments.of(
                        new HashMap<>() {{
                            put("tubias", new BooleanType());
                            put("tubias2", new IntArrayType());
                            put("tubias3", new IntegerType());
                            put("tubias4", new IdentifierType("deeznuts"));
                        }}
                )
        );
    }

    static Stream<Arguments> shouldInsertFormalIntoMethod() {
        return getMockedFormalsAndArgs();
    }

    @ParameterizedTest
    @DisplayName("Should insert a formal into a method")
    @MethodSource
    void shouldInsertFormalIntoMethod(HashMap<String, Type> expectedFormals) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        visitor.setCurrentMethodTable(getMockedMethodTable());

        // ACT
        for (var entry : expectedFormals.entrySet()) {
            var formal = new Formal(entry.getValue(), entry.getKey());
            formal.accept(visitor);
        }

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        HashMap<String, Type> params = visitor.getCurrentMethodTable().getParamsContext();
        assertEquals(expectedFormals, params);
    }

    @Test
    @DisplayName("Should show an error when inserting a duplicated formal into a method")
    void shouldShowErrorWhenInsertingDuplicatedFormalIntoMethod() {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        visitor.setCurrentMethodTable(getMockedMethodTable());
        var formal = new Formal(new IntegerType(), "tubias");
        formal.accept(visitor);

        // ACT
        formal.accept(visitor);

        // ASSERT
        assertEquals(1, visitor.getErrors().size());
        assertEquals(1, visitor.getCurrentMethodTable().getParamsContext().size());
        assertTrue(visitor.getErrors().getFirst().toString().contains("already defined"));
    }

    static Stream<Arguments> shouldInsertVarDeclIntoClass() {
        return getMockedFormalsAndArgs();
    }

    @ParameterizedTest
    @DisplayName("Should insert a var decl into a class")
    @MethodSource
    void shouldInsertVarDeclIntoClass(HashMap<String, Type> expectedFields) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        visitor.setCurrentClassTable(getMockedClassTable());

        // ACT
        for (var entry : expectedFields.entrySet()) {
            var varDecl = new VarDecl(entry.getValue(), entry.getKey());
            varDecl.accept(visitor);
        }

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        HashMap<String, Type> fields = visitor.getCurrentClassTable().getFieldsContext();
        assertEquals(expectedFields, fields);
    }

    static Stream<Arguments> shouldInsertVarDeclIntoMethod() {
        return getMockedFormalsAndArgs();
    }

    @ParameterizedTest
    @DisplayName("Should insert a var decl into a method")
    @MethodSource
    void shouldInsertVarDeclIntoMethod(HashMap<String, Type> expectedLocals) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        visitor.setCurrentMethodTable(getMockedMethodTable());

        // ACT
        for (var entry : expectedLocals.entrySet()) {
            var varDecl = new VarDecl(entry.getValue(), entry.getKey());
            varDecl.accept(visitor);
        }

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        HashMap<String, Type> locals = visitor.getCurrentMethodTable().getLocalsContext();
        assertEquals(expectedLocals, locals);
    }

    static Stream<Arguments> shouldShowErrorWhenInsertingDuplicatedVarDeclIntoClass() {
        return Stream.of(
                Arguments.of(SymbolTableVisitor.builder().currentMethodTable(getMockedMethodTable()).build(), new VarDecl(new IntegerType(), "tubias")),
                Arguments.of(SymbolTableVisitor.builder().currentClassTable(getMockedClassTable()).build(), new VarDecl(new IntegerType(), "tubias"))
        );
    }

    @ParameterizedTest
    @DisplayName("Should show an error when inserting a duplicated var decl into a class or method")
    @MethodSource
    void shouldShowErrorWhenInsertingDuplicatedVarDeclIntoClass(SymbolTableVisitor visitor, VarDecl varDecl) {
        // ARRANGE
        varDecl.accept(visitor);

        // ACT
        varDecl.accept(visitor);

        // ASSERT
        assertEquals(1, visitor.getErrors().size());
        assertTrue(visitor.getErrors().getFirst().toString().contains("already defined"));
    }

    static Stream<Arguments> shouldAddMethodToClassTable() {
        return Stream.of(
                Arguments.of(MethodDecl
                                .builder()
                                .type(new IntegerType())
                                .identifier("method")
                                .statements(new StatementList())
                                .formals(new FormalList(new ArrayList<>() {{
                                    add(new Formal(new IntegerType(), "tubias"));
                                }}))
                                .returnExpression(new IntegerLiteral(1))
                                .varDecls(new VarDeclList())
                                .build(),
                        MethodTable
                                .builder()
                                .classParent(getMockedClassTable())
                                .methodName("method")
                                .paramsContext(new LinkedHashMap<>() {{
                                    put("tubias", new IntegerType());
                                }})
                                .methodReturnType(new IntegerType())
                                .build()
                ),
                Arguments.of(MethodDecl
                                .builder()
                                .type(new IntegerType())
                                .identifier("method")
                                .statements(new StatementList())
                                .formals(new FormalList())
                                .returnExpression(new IntegerLiteral(1))
                                .varDecls(new VarDeclList(new ArrayList<>() {{
                                    add(new VarDecl(new IntegerType(), "tubias"));
                                }}))
                                .build(),
                        MethodTable
                                .builder()
                                .classParent(getMockedClassTable())
                                .methodName("method")
                                .localsContext(new HashMap<>() {{
                                    put("tubias", new IntegerType());
                                }})
                                .methodReturnType(new IntegerType())
                                .build()
                ),
                Arguments.of(MethodDecl
                                .builder()
                                .type(new IntegerType())
                                .identifier("method")
                                .statements(new StatementList())
                                .formals(new FormalList())
                                .returnExpression(new IntegerLiteral(1))
                                .varDecls(new VarDeclList())
                                .build(),
                        MethodTable
                                .builder()
                                .classParent(getMockedClassTable())
                                .methodName("method")
                                .methodReturnType(new IntegerType())
                                .build()
                ),
                Arguments.of(MethodDecl
                                .builder()
                                .type(new IntegerType())
                                .identifier("method")
                                .statements(new StatementList())
                                .formals(new FormalList(new ArrayList<>() {{
                                    add(new Formal(new IntegerType(), "tubias"));
                                }}))
                                .returnExpression(new IntegerLiteral(1))
                                .varDecls(new VarDeclList(new ArrayList<>() {{
                                    add(new VarDecl(new IntegerType(), "tubias"));
                                }}))
                                .build(),
                        MethodTable
                                .builder()
                                .classParent(getMockedClassTable())
                                .methodName("method")
                                .methodReturnType(new IntegerType())
                                .localsContext(new HashMap<>() {{
                                    put("tubias", new IntegerType());
                                }})
                                .paramsContext(new LinkedHashMap<>() {{
                                    put("tubias", new IntegerType());
                                }})
                                .build()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("Should add a new method to a class")
    @MethodSource
    void shouldAddMethodToClassTable(MethodDecl methodDecl, MethodTable expectedMethodTable) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        visitor.setCurrentClassTable(getMockedClassTable());

        // ACT
        methodDecl.accept(visitor);

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        assertNull(visitor.getCurrentMethodTable());
        assertEquals(1, visitor.getCurrentClassTable().getMethodsContext().size());

        MethodTable methodTable = visitor.getCurrentClassTable().getMethodsContext().get(expectedMethodTable.getMethodName());
        assertEquals(expectedMethodTable, methodTable);
        assertEquals(expectedMethodTable.getClassParent().getClassName(), methodTable.getClassParent().getClassName());
    }

    @Test
    @DisplayName("Should show an error when adding a duplicated method to a class")
    void shouldShowErrorWhenAddingDuplicatedMethodToClassTable() {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        visitor.setCurrentClassTable(getMockedClassTable());
        var methodDecl = MethodDecl.builder()
                .type(new IntegerType())
                .identifier("method")
                .statements(new StatementList())
                .formals(new FormalList())
                .returnExpression(new IntegerLiteral(1))
                .varDecls(new VarDeclList())
                .build();
        methodDecl.accept(visitor);

        // ACT
        methodDecl.accept(visitor);

        // ASSERT
        assertEquals(1, visitor.getErrors().size());
        assertTrue(visitor.getErrors().getFirst().toString().contains("already defined"));
    }

    static Stream<Arguments> shouldAddClassDeclSimpleToMainTable() {
        return Stream.of(
                Arguments.of(new ArrayList<>() {{
                                 add(ClassDeclSimple.builder().className(new Identifier("class")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                             }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                }})
                                .build()
                ),
                Arguments.of(new ArrayList<>() {{
                                 add(ClassDeclSimple.builder().className(new Identifier("class")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                                 add(ClassDeclSimple.builder().className(new Identifier("class2")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                             }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                    put("class2", ClassTable.builder().className("class2").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                }})
                                .build()
                ),
                Arguments.of(new ArrayList<>() {{
                                 add(ClassDeclSimple.builder()
                                         .className(new Identifier("class"))
                                         .fields(new VarDeclList(new ArrayList<>() {{
                                             add(new VarDecl(new IntegerType(), "tubias"));
                                         }}))
                                         .build());
                                 add(ClassDeclSimple.builder()
                                         .className(new Identifier("class2"))
                                         .fields(new VarDeclList(new ArrayList<>() {{
                                             add(new VarDecl(new IntegerType(), "tubias"));
                                         }}))
                                         .build());
                             }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder()
                                            .className("class")
                                            .parent(null)
                                            .fieldsContext(new HashMap<>() {{
                                                put("tubias", new IntegerType());
                                            }})
                                            .methodsContext(new HashMap<>()).build());
                                    put("class2", ClassTable.builder()
                                            .className("class2")
                                            .parent(null)
                                            .fieldsContext(new HashMap<>() {{
                                                put("tubias", new IntegerType());
                                            }})
                                            .methodsContext(new HashMap<>()).build());
                                }})
                                .build()
                ),
                Arguments.of(new ArrayList<>() {{
                                 add(ClassDeclSimple.builder()
                                         .className(new Identifier("class"))
                                         .methods(new MethodDeclList(new ArrayList<>() {{
                                             add(MethodDecl.builder()
                                                     .type(new IntegerType())
                                                     .identifier("methodHere")
                                                     .statements(new StatementList())
                                                     .formals(new FormalList())
                                                     .returnExpression(new IntegerLiteral(1))
                                                     .varDecls(new VarDeclList())
                                                     .build());
                                         }}))
                                         .build());
                                 add(ClassDeclSimple.builder()
                                         .className(new Identifier("class2"))
                                         .methods(new MethodDeclList(new ArrayList<>() {{
                                             add(MethodDecl.builder()
                                                     .type(new IntegerType())
                                                     .identifier("methodHere")
                                                     .statements(new StatementList())
                                                     .formals(new FormalList())
                                                     .returnExpression(new IntegerLiteral(1))
                                                     .varDecls(new VarDeclList())
                                                     .build());
                                         }}))
                                         .build());
                             }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder()
                                            .className("class")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("methodHere", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).build())
                                                        .methodName("methodHere")
                                                        .methodReturnType(new IntegerType())
                                                        .build());
                                            }}).build());
                                    put("class2", ClassTable.builder()
                                            .className("class2")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("methodHere", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).build())
                                                        .methodName("methodHere")
                                                        .methodReturnType(new IntegerType())
                                                        .build());
                                            }}).build());
                                }})
                                .build()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("Should add a ClassDeclSimple to the main table")
    @MethodSource
    void shouldAddClassDeclSimpleToMainTable(ArrayList<ClassDeclSimple> classesDecl, MainTable expectedClassesTable) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();

        // ACT
        for (var classDeclSimple : classesDecl)
            classDeclSimple.accept(visitor);

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        assertNull(visitor.getCurrentClassTable());
        assertEquals(expectedClassesTable.getMap().size(), visitor.getMainTable().getMap().size());
        HashMap<String, ClassTable> classTable = visitor.getMainTable().getMap();
        assertEquals(expectedClassesTable.getMap(), classTable);
    }

    @Test
    @DisplayName("Should show an error when adding a duplicated ClassDeclSimple to the main table")
    void shouldShowErrorWhenAddingDuplicatedClassDeclSimpleToMainTable() {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        var classDeclSimple = ClassDeclSimple.builder()
                .className(new Identifier("class"))
                .fields(new VarDeclList())
                .methods(new MethodDeclList())
                .build();
        classDeclSimple.accept(visitor);

        // ACT
        classDeclSimple.accept(visitor);

        // ASSERT
        assertEquals(1, visitor.getErrors().size());
        assertTrue(visitor.getErrors().getFirst().toString().contains("already defined"));
    }

    static Stream<Arguments> shouldAddClassDeclExtendsToMainTable() {
        return Stream.of(
                Arguments.of(
                        new ArrayList<>() {{
                            add(ClassDeclSimple.builder().className(new Identifier("class")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                        }},
                        new ArrayList<>() {{
                            add(ClassDeclExtends.builder().className(new Identifier("class2")).parent(new Identifier("class")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                        }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                    put("class2", ClassTable.builder().className("class2").parent(ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build()).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                }})
                                .build()
                ),
                Arguments.of(
                        new ArrayList<>() {{
                            add(ClassDeclSimple.builder().className(new Identifier("class")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                        }},
                        new ArrayList<>() {{
                            add(ClassDeclExtends.builder().className(new Identifier("class2")).parent(new Identifier("class")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                            add(ClassDeclExtends.builder().className(new Identifier("class3")).parent(new Identifier("class2")).fields(new VarDeclList()).methods(new MethodDeclList()).build());
                        }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                    put("class2", ClassTable.builder().className("class2").parent(ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build()).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                    put("class3", ClassTable.builder().className("class3").parent(ClassTable.builder().className("class2").parent(ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build()).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build()).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build());
                                }})
                                .build()
                ),
                Arguments.of(
                        new ArrayList<>() {{
                            add(ClassDeclSimple.builder()
                                    .className(new Identifier("class"))
                                    .fields(new VarDeclList(new ArrayList<>() {{
                                        add(new VarDecl(new IntegerType(), "tubias"));
                                    }})).build());
                        }},
                        new ArrayList<>() {{
                            add(ClassDeclExtends.builder()
                                    .className(new Identifier("class2"))
                                    .parent(new Identifier("class"))
                                    .fields(new VarDeclList(new ArrayList<>() {{
                                        add(new VarDecl(new IntegerType(), "tubias2"));
                                    }})).build());
                        }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder()
                                            .className("class")
                                            .parent(null)
                                            .fieldsContext(new HashMap<>() {{
                                                put("tubias", new IntegerType());
                                            }}).build());
                                    put("class2", ClassTable.builder()
                                            .className("class2")
                                            .parent(ClassTable.builder()
                                                    .className("class")
                                                    .parent(null)
                                                    .fieldsContext(new HashMap<>() {{
                                                        put("tubias", new IntegerType());
                                                    }}).build())
                                            .fieldsContext(new HashMap<>() {{
                                                put("tubias", new IntegerType());
                                                put("tubias2", new IntegerType());
                                            }}).build());
                                }})
                                .build()
                ),
                Arguments.of(
                        new ArrayList<>() {{
                            add(ClassDeclSimple.builder()
                                    .className(new Identifier("class"))
                                    .methods(new MethodDeclList(new ArrayList<>() {{
                                        add(MethodDecl.builder()
                                                .type(new IntegerType())
                                                .identifier("methodHere")
                                                .statements(new StatementList())
                                                .formals(new FormalList())
                                                .returnExpression(new IntegerLiteral(1))
                                                .varDecls(new VarDeclList())
                                                .build());
                                    }})).build());
                        }},
                        new ArrayList<>() {{
                            add(ClassDeclExtends.builder()
                                    .className(new Identifier("class2"))
                                    .parent(new Identifier("class"))
                                    .methods(new MethodDeclList(new ArrayList<>() {{
                                        add(MethodDecl.builder()
                                                .type(new IntegerType())
                                                .identifier("methodHere2")
                                                .statements(new StatementList())
                                                .formals(new FormalList())
                                                .returnExpression(new IntegerLiteral(1))
                                                .varDecls(new VarDeclList())
                                                .build());
                                    }})).build());
                        }},
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder()
                                            .className("class")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("methodHere", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).build())
                                                        .methodName("methodHere")
                                                        .methodReturnType(new IntegerType())
                                                        .build());
                                            }}).build());
                                    put("class2", ClassTable.builder()
                                            .className("class2")
                                            .parent(ClassTable.builder()
                                                    .className("class")
                                                    .parent(null)
                                                    .methodsContext(new HashMap<>() {{
                                                        put("methodHere", MethodTable.builder()
                                                                .classParent(ClassTable.builder().className("class").parent(null).build())
                                                                .methodName("methodHere")
                                                                .methodReturnType(new IntegerType())
                                                                .build());
                                                    }}).build())
                                            .methodsContext(new HashMap<>() {{
                                                put("methodHere", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).build())
                                                        .methodName("methodHere")
                                                        .methodReturnType(new IntegerType())
                                                        .build());
                                                put("methodHere2", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).build())
                                                        .methodName("methodHere2")
                                                        .methodReturnType(new IntegerType())
                                                        .build());
                                            }}).build());
                                }})
                                .build()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("Should add a ClassDeclExtends to the main table")
    @MethodSource
    void shouldAddClassDeclExtendsToMainTable(ArrayList<ClassDeclSimple> classesDeclSimple, ArrayList<ClassDeclExtends> classesDeclExtends, MainTable expectedClassesTable) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();

        // ACT
        classesDeclSimple.forEach(clazz -> clazz.accept(visitor));
        classesDeclExtends.forEach(clazz -> clazz.accept(visitor));

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        assertNull(visitor.getCurrentClassTable());
        assertEquals(expectedClassesTable.getMap().size(), visitor.getMainTable().getMap().size());
        HashMap<String, ClassTable> classTable = visitor.getMainTable().getMap();
        assertEquals(expectedClassesTable.getMap(), classTable);
    }

    @Test
    @DisplayName("Should show an error when adding a ClassDeclExtends that contains an undefined parent class")
    void shouldShowErrorWhenAddingClassDeclExtendsWithUndefinedParent() {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        var classDeclExtends = ClassDeclExtends.builder()
                .className(new Identifier("class"))
                .parent(new Identifier("class2"))
                .fields(new VarDeclList())
                .methods(new MethodDeclList())
                .build();

        // ACT
        classDeclExtends.accept(visitor);

        // ASSERT
        assertEquals(1, visitor.getErrors().size());
        assertTrue(visitor.getErrors().getFirst().toString().contains("was not defined"));
    }

    @Test
    @DisplayName("Should show an error when adding a duplicated ClassDeclExtends to the main table")
    void shouldShowErrorWhenAddingDuplicatedClassDeclExtendsToMainTable() {
        // ARRANGE
        var visitor = new SymbolTableVisitor();
        var classDeclSimple = ClassDeclSimple.builder()
                .className(new Identifier("class"))
                .fields(new VarDeclList())
                .methods(new MethodDeclList())
                .build();
        classDeclSimple.accept(visitor);
        var classDeclExtends = ClassDeclExtends.builder()
                .className(new Identifier("class2"))
                .parent(new Identifier("class"))
                .fields(new VarDeclList())
                .methods(new MethodDeclList())
                .build();
        classDeclExtends.accept(visitor);

        // ACT
        classDeclExtends.accept(visitor);

        // ASSERT
        assertEquals(1, visitor.getErrors().size());
        assertTrue(visitor.getErrors().getFirst().toString().contains("already defined"));
    }

    static Stream<Arguments> shouldInsertMainClassIntoMainTable() {
        return Stream.of(
                Arguments.of(
                        MainClass.builder()
                                .className(new Identifier("class"))
                                .statements(new StatementList())
                                .build(),
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder()
                                            .className("class")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("main", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build())
                                                        .methodName("main")
                                                        .methodReturnType(null)
                                                        .build());
                                            }}).build());
                                }})
                                .build()
                ),
                Arguments.of(
                        MainClass.builder()
                                .className(new Identifier("Gepeto"))
                                .statements(new StatementList())
                                .build(),
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("Gepeto", ClassTable.builder()
                                            .className("Gepeto")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("main", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("Gepeto").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build())
                                                        .methodName("main")
                                                        .methodReturnType(null)
                                                        .build());
                                            }}).build());
                                }})
                                .build()
                ),
                Arguments.of(
                        MainClass.builder()
                                .className(new Identifier("Factorial"))
                                .statements(new StatementList())
                                .build(),
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("Factorial", ClassTable.builder()
                                            .className("Factorial")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("main", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("Factorial").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build())
                                                        .methodName("main")
                                                        .methodReturnType(null)
                                                        .build());
                                            }}).build());
                                }})
                                .build()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("Should insert a main class into the main table")
    @MethodSource
    void shouldInsertMainClassIntoMainTable(MainClass mainClass, MainTable expectedMainTable) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();

        // ACT
        mainClass.accept(visitor);

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        assertNull(visitor.getCurrentClassTable());
        assertEquals(expectedMainTable.getMap().size(), visitor.getMainTable().getMap().size());
        HashMap<String, ClassTable> classTable = visitor.getMainTable().getMap();
        assertEquals(expectedMainTable.getMap(), classTable);
    }

    static Stream<Arguments> shouldInsertEntireProgramIntoMainTable() {
        return Stream.of(
                Arguments.of(
                        Program.builder()
                                .mainClass(MainClass.builder()
                                        .className(new Identifier("class"))
                                        .statements(new StatementList())
                                        .build())
                                .classes(new ClassDeclList(new ArrayList<>() {{
                                    add(ClassDeclSimple.builder()
                                            .className(new Identifier("class2"))
                                            .fields(new VarDeclList())
                                            .methods(new MethodDeclList())
                                            .build());
                                    add(ClassDeclSimple.builder()
                                            .className(new Identifier("class3"))
                                            .fields(new VarDeclList())
                                            .methods(new MethodDeclList())
                                            .build());
                                }}))
                                .build(),
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder()
                                            .className("class")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("main", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build())
                                                        .methodName("main")
                                                        .methodReturnType(null)
                                                        .build());
                                            }}).build());
                                    put("class2", ClassTable.builder()
                                            .className("class2")
                                            .parent(null)
                                            .fieldsContext(new HashMap<>())
                                            .methodsContext(new HashMap<>())
                                            .build());
                                    put("class3", ClassTable.builder()
                                            .className("class3")
                                            .parent(null)
                                            .fieldsContext(new HashMap<>())
                                            .methodsContext(new HashMap<>())
                                            .build());
                                }})
                                .build()
                ),
                Arguments.of(
                        Program.builder()
                                .mainClass(MainClass.builder()
                                        .className(new Identifier("class"))
                                        .statements(new StatementList())
                                        .build())
                                .classes(new ClassDeclList(new ArrayList<>() {{
                                    add(ClassDeclSimple.builder()
                                            .className(new Identifier("class2"))
                                            .fields(new VarDeclList(new ArrayList<>() {{
                                                add(new VarDecl(new IntegerType(), "tubias"));
                                            }}))
                                            .build());
                                    add(ClassDeclExtends.builder()
                                            .className(new Identifier("class3"))
                                            .parent(new Identifier("class2"))
                                            .build());
                                }}))
                                .build(),
                        MainTable.builder()
                                .map(new HashMap<>() {{
                                    put("class", ClassTable.builder()
                                            .className("class")
                                            .parent(null)
                                            .methodsContext(new HashMap<>() {{
                                                put("main", MethodTable.builder()
                                                        .classParent(ClassTable.builder().className("class").parent(null).fieldsContext(new HashMap<>()).methodsContext(new HashMap<>()).build())
                                                        .methodName("main")
                                                        .methodReturnType(null)
                                                        .build());
                                            }}).build());
                                    put("class2", ClassTable.builder()
                                            .className("class2")
                                            .parent(null)
                                            .fieldsContext(new HashMap<>() {{
                                                put("tubias", new IntegerType());
                                            }})
                                            .build());
                                    put("class3", ClassTable.builder()
                                            .className("class3")
                                            .parent(ClassTable.builder().className("class2").parent(null).fieldsContext(new HashMap<>() {{
                                                put("tubias", new IntegerType());
                                            }}).build())
                                            .fieldsContext(new HashMap<>() {{
                                                put("tubias", new IntegerType());
                                            }})
                                            .build());
                                }}).build()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("Should insert entire program into the main table")
    @MethodSource
    void shouldInsertEntireProgramIntoMainTable(Program program, MainTable expectedMainTable) {
        // ARRANGE
        var visitor = new SymbolTableVisitor();

        // ACT
        program.accept(visitor);

        // ASSERT
        assertEquals(0, visitor.getErrors().size());
        assertNull(visitor.getCurrentClassTable());
        assertEquals(expectedMainTable.getMap().size(), visitor.getMainTable().getMap().size());
        HashMap<String, ClassTable> classTable = visitor.getMainTable().getMap();
        assertEquals(expectedMainTable.getMap(), classTable);
    }
}
