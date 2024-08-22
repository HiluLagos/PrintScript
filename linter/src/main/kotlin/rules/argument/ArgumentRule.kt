package rules.argument

import error.Error
import node.staticpkg.PrintLnType

// Pensando como el chess, las reglas se podrian llegar a anidar entre si (por eso se llama Rule, es unica)
interface ArgumentRule {
    fun analyzeArguments(printLnType: PrintLnType): List<Error>
}
