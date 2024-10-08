package type

import interpreter.InterpreterProvider
import node.PrimType
import node.dynamic.LiteralType
import node.dynamic.LiteralValue
import node.dynamic.VariableType
import node.staticpkg.AssignationType
import node.staticpkg.DeclarationType
import node.staticpkg.ExpressionType
import node.staticpkg.IdentifierType
import node.staticpkg.ModifierType
import node.staticpkg.PrintLnType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ExpressionTypeTest {
    // Test for expressionType
    @Test
    fun `expressionType val test`() {
        val assignationTypeA = AssignationType(
            DeclarationType(
                ModifierType(
                    "val",
                    false
                ),
                IdentifierType(PrimType.STRING),
                "a"
            ),
            LiteralType(LiteralValue.NumberValue(1))
        )
        val assignationTypeB = AssignationType(
            DeclarationType(
                ModifierType(
                    "var",
                    true
                ),
                IdentifierType(PrimType.STRING),
                "b"
            ),
            LiteralType(LiteralValue.NumberValue(1))
        )
        val expressionType = ExpressionType(
            VariableType("a", null),
            VariableType("b", null)
        )
        val list = listOf(assignationTypeA, assignationTypeB, expressionType)
        val interpreter = InterpreterProvider(list.iterator()).provideInterpreter("1.0")
        val output: Iterator<String> = interpreter.iterator()
        assertThrows<IllegalArgumentException> {
            output.asSequence().toList()
        }
    }

    @Test
    fun `expressionType var test`() {
        val assignationTypeA = AssignationType(
            DeclarationType(
                ModifierType(
                    "var",
                    true
                ),
                IdentifierType(PrimType.NUMBER),
                "a"
            ),
            LiteralType(LiteralValue.NumberValue(1))
        )
        val assignationTypeB = AssignationType(
            DeclarationType(
                ModifierType(
                    "var",
                    true
                ),
                IdentifierType(PrimType.NUMBER),
                "b"
            ),
            LiteralType(LiteralValue.NumberValue(2))
        )
        val expressionType = ExpressionType(
            VariableType("a", null),
            VariableType("b", null)
        )
        val printLnType = PrintLnType(VariableType("a", null))
        val list = listOf(assignationTypeA, assignationTypeB, expressionType, printLnType)
        val interpreter = InterpreterProvider(list.iterator()).provideInterpreter("1.0")
        assertEquals("2", interpreter.iterator().next())
    }
}
