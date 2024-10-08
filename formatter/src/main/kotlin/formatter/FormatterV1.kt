package formatter

import node.staticpkg.AssignationType
import node.staticpkg.DeclarationType
import node.staticpkg.ExpressionType
import node.staticpkg.IdentifierType
import node.staticpkg.ModifierType
import node.staticpkg.PrintLnType
import node.staticpkg.StaticNode

class FormatterV1(var iterator: Iterator<StaticNode>) : FormatterInterface {

    private val output: MutableList<String> = mutableListOf()

    override fun format(): String {
        while (iterator.hasNext()) {
            val node = iterator.next()
            matchNode(node)
        }
        return getOutput()
    }

    private fun matchNode(node: StaticNode) {
        when (node) {
            is AssignationType, is DeclarationType, is ExpressionType,
            is IdentifierType, is ModifierType, is PrintLnType -> {
                val result = node.format("1.0", 0)
                output.add(result)
            }
            else -> throw IllegalArgumentException("Operation not supported")
        }
    }

    private fun getOutput(): String {
        return output.joinToString("\n")
    }
}
