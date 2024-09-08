package runner

import error.Error
import formatter.Formatter
import formatter.FormatterImpl
import interpreter.IntepreterProvider
import interpreter.Interpreter
import lexer.Lexer
import lexer.LexerInterface
import linter.LinterProvider
import node.staticpkg.StaticNode
import parser.elements.ParserInterface
import parser.elements.ParserProvider
import rule.basic.EndingRule
import rule.basic.IdentifierRule
import rule.basic.LetRule
import rule.basic.WhiteSpaceRule
import rule.control.CloseParenthesisRule
import rule.control.OpenParenthesisRule
import rule.control.ParenthesisRule
import rule.expression.AssignationRule
import rule.expression.DeclarationRule
import rule.inherent.PrintlnRule
import rule.literal.NumberLiteralRule
import rule.literal.StringLiteralRule
import rule.operation.DivideOperation
import rule.operation.MinusOperation
import rule.operation.MultiplyOperation
import rule.operation.OperationRule
import rule.operation.PlusOperation
import rule.typeid.BooleanIdRule
import rule.typeid.NumberIdRule
import rule.typeid.StringIdRule

class Operations(private val sourceFile: String, private val version: String) {

    private val lexerRules = listOf(
        LetRule(),
        PrintlnRule(),
        NumberIdRule(),
        BooleanIdRule(),
        StringIdRule(),
        WhiteSpaceRule(),
        IdentifierRule(),
        NumberLiteralRule(),
        StringLiteralRule(),
        DeclarationRule(),
        AssignationRule(),
        EndingRule(),
        OperationRule(listOf(PlusOperation, MinusOperation, MultiplyOperation, DivideOperation)),
        ParenthesisRule(listOf(OpenParenthesisRule, CloseParenthesisRule))
    )

    fun validate(): List<StaticNode> {
        val lexer: LexerInterface = Lexer(lexerRules, sourceFile)
        val tokenIterator = lexer.iterator()
        val parser: ParserInterface = ParserProvider(tokenIterator).getParser(version)
        // Lo que hace esto es forzar al parser a hacer su parse completo y devolver el resultado de to do en una lista
        // Esto creo que puede romperse si el archivo es muy grande
        return parser.iterator().asSequence().toList()
    }

    fun execute(): List<String> {
        val lexer: LexerInterface = Lexer(lexerRules, sourceFile)
        val tokenIterator = lexer.iterator()
        val parser: ParserInterface = ParserProvider(tokenIterator).getParser(version)
        val interpreter: Interpreter = IntepreterProvider(parser.iterator()).provideInterpreter(version)
        return interpreter.execute()
    }

    fun format(): String {
        val lexer: LexerInterface = Lexer(lexerRules, sourceFile)
        val tokenIterator = lexer.iterator()
        val parser: ParserInterface = ParserProvider(tokenIterator).getParser(version)
        // val interpreter: Interpreter = IntepreterProvider(parser.iterator()).provideInterpreter(version)
        val formatter: Formatter = FormatterImpl()
        return formatter.execute(parser.iterator())
    }

    fun analyze(): List<Error> {
        val lexer: LexerInterface = Lexer(lexerRules, sourceFile)
        val tokenIterator = lexer.iterator()
        val parser: ParserInterface = ParserProvider(tokenIterator).getParser(version)
        // val interpreter: Interpreter = IntepreterProvider(parser.iterator()).provideInterpreter(version)
        // val formatter: Formatter = FormatterImpl()
        val linter = LinterProvider().provideLinter("{ \"case\": \"camelCase\" , \"argument\": \"literal\" }")
        return linter.lint(parser.iterator())
    }
}
