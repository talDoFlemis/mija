package org.example.visitor

import jdk.jshell.spi.ExecutionControl.NotImplementedException
import org.example.ast.*

object ASTVisitorImpl : ASTVisitor {
    private fun <A> acceptThis(block: A.(ASTVisitor) -> Unit): A.() -> Unit =
        { block(ASTVisitorImpl) }


    override fun visit(program: Program?): Unit = program!!.run {
        acceptThis(MainClass::accept)(mainClass)
        classes.classDecls
            .forEach(acceptThis(ClassDecl::accept))
    }

    override fun visit(a: And?): Unit = a!!.run {
        acceptThis(Expression::accept)(lhe)
        acceptThis(Expression::accept)(rhe)
    }

    override fun visit(b: BooleanType?) {
        TODO("Not yet implemented")
    }

    override fun visit(n: Not?): Unit = n!!.run {
        acceptThis(Expression::accept)(e)
    }

    override fun visit(t: True?): Unit = Unit

    override fun visit(f: False?): Unit = Unit

    override fun visit(i: Identifier?): Unit = Unit

    override fun visit(c: Call?): Unit = c!!.run {
        acceptThis(Expression::accept)(owner)
        acceptThis(Identifier::accept)(method)
        expressionList.list.forEach(acceptThis(Expression::accept))
    }

    override fun visit(i: IdentifierExpression?): Unit = Unit

    override fun visit(i: IdentifierType?): Unit =
        throw Exception("Wrong Visitor Exception")

    override fun visit(n: NewObject?): Unit = n!!.run {
        acceptThis(Identifier::accept)(identifier)
    }

    override fun visit(t: This?): Unit = Unit

    override fun visit(a: ArrayLookup?): Unit = a!!.run {
        acceptThis(Expression::accept)(array)
        acceptThis(Expression::accept)(idx)
    }

    override fun visit(a: ArrayAssign?): Unit = a!!.run {
        acceptThis(Identifier::accept)(identifier)
        acceptThis(Expression::accept)(index)
        acceptThis(Expression::accept)(value)
    }

    override fun visit(a: ArrayLength?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(p: Plus?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(m: Minus?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(t: Times?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(i: IntegerLiteral?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(i: IntegerType?): Unit = throw Exception("Wrong Visitor Exception")

    override fun visit(i: IntArrayType?): Unit = throw Exception("Wrong Visitor Exception")

    override fun visit(l: LessThan?): Unit = l!!.run {
        acceptThis(Expression::accept)(lhe)
        acceptThis(Expression::accept)(rhe)
    }

    override fun visit(n: NewArray?): Unit = n!!.run {
        acceptThis(Expression::accept)(size)
    }

    override fun visit(w: While?): Unit = w!!.run {
        acceptThis(Expression::accept)(condition)
        acceptThis(Statement::accept)(body)
    }

    override fun visit(i: If?): Unit = i!!.run {
        acceptThis(Expression::accept)(condition)
        acceptThis(Statement::accept)(thenBranch)
        acceptThis(Statement::accept)(elseBranch)
    }

    override fun visit(a: Assign?): Unit = a!!.run {
        acceptThis(Identifier::accept)(identifier)
        acceptThis(Expression::accept)(value)
    }

    override fun visit(s: Sout?): Unit = s!!.run {
        acceptThis(Expression::accept)(expression)
    }

    override fun visit(b: Block?): Unit = b!!.run {
        statements.statements
            .forEach(acceptThis(Statement::accept))
    }

    override fun visit(m: MainClass?): Unit = TODO()


    override fun visit(c: ClassDeclSimple?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(c: ClassDeclExtends?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(m: MethodDecl?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(v: VarDecl?): Unit {
        TODO("Not yet implemented")
    }

    override fun visit(f: Formal?): Unit {
        TODO("Not yet implemented")
    }
}