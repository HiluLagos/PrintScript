package parser.strategies

import elements.RightSideParser
import node.Node
import node.dynamic.DynamicNode
import node.dynamic.VariableType
import node.staticpkg.AssignationType
import node.staticpkg.DeclarationType
import node.staticpkg.ExpressionType
import token.Ending
import token.NativeMethod
import token.TokenInterface
import token.TokenType

class AssignationStrategy(
    allowedTypes: Set<TokenType>,
    private val methodStrategy: MethodStrategy
) : ParseStrategy {

    private val rightSideParser: RightSideParser = RightSideParser(allowedTypes) // Pasar los tipos permitidos

    override fun parse(tokenInterfaces: List<TokenInterface>, currentIndex: Int, statementNodes: MutableList<Node>):
        Int {
        require(statementNodes.isNotEmpty()) {
            "'=' cannot be used alone, missing previous argument at: ${tokenInterfaces[currentIndex].position}"
        }
        return when (val lastNode = statementNodes.last()) {
            is VariableType -> parseExpression(tokenInterfaces, currentIndex, statementNodes)
            is DeclarationType -> parseAssignation(tokenInterfaces, currentIndex, statementNodes)
            else -> throw IllegalArgumentException(
                "'=' cannot be used with first argument ${lastNode.javaClass} at: " +
                    "${tokenInterfaces[currentIndex].position}"
            )
        }
    }

    private fun parseExpression(tokens: List<TokenInterface>, currentIndex: Int, statementNodes: MutableList<Node>):
        Int {
        val variableNode = statementNodes.last() as VariableType
        if (tokens[currentIndex + 1].type == NativeMethod) {
            // Logica de Native method
            val nextIndex = methodStrategy.parse(tokens, currentIndex + 1, statementNodes)
            val methodNode = statementNodes.last()
            require(methodNode is DynamicNode) { "Something went wrong at: ${tokens[currentIndex + 1].position}" }
            val expressionNode = ExpressionType(variableNode, methodNode)
            statementNodes.add(expressionNode)
            return nextIndex
        } else {
            val (rightHandSideNode, nextIndex) = rightSideParser.parseRightHandSide(tokens, currentIndex + 1, Ending)
            val expressionNode = ExpressionType(variableNode, rightHandSideNode)
            statementNodes.add(expressionNode)
            return nextIndex
        }
    }

    private fun parseAssignation(tokens: List<TokenInterface>, currentIndex: Int, statementNodes: MutableList<Node>):
        Int {
        val declarationNode = statementNodes.last() as DeclarationType
        if (tokens[currentIndex + 1].type == NativeMethod) {
            // Logica de Native method
            val nextIndex = methodStrategy.parse(tokens, currentIndex + 1, statementNodes)
            val methodNode = statementNodes[statementNodes.lastIndex]
            require(methodNode is DynamicNode) { "Something went wrong at: ${tokens[currentIndex + 1].position}" }
            val expressionNode = AssignationType(declarationNode, methodNode)
            statementNodes.add(expressionNode)
            return nextIndex
        } else {
            val (rightHandSideNode, nextIndex) = rightSideParser.parseRightHandSide(tokens, currentIndex + 1, Ending)
            val expressionNode = AssignationType(declarationNode, rightHandSideNode)
            statementNodes.add(expressionNode)
            return nextIndex
        }
    }
}
