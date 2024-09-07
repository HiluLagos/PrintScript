package node.staticpkg

import node.TypeValue
import node.dynamic.DynamicNode
import operations.StaticVisitorV1

class AssignationType(val declaration: DeclarationType, val value: DynamicNode) : StaticNode {
    override fun visit(visitor: StaticVisitorV1) {
        visitor.acceptAssignation(this)
    }

    override fun run(
        valueMap: Map<String, Pair<Boolean, TypeValue>>,
        version: String
    ): StaticResult {
        val (map, _) = declaration.run(valueMap, version)
        val output = value.execute(map, version)
        return StaticResult(updateValueMap(map, declaration.name, output), emptyList())
    }

    override fun toString(): String {
        return "AssignationType(declaration='$declaration', value=$value)"
    }

    private fun updateValueMap(
        originalMap: Map<String, Pair<Boolean, TypeValue>>,
        key: String,
        newValue: TypeValue
    ): Map<String, Pair<Boolean, TypeValue>> {
        val updatedMap = originalMap.toMutableMap()
        if (originalMap[key]?.first == true) {
            require(originalMap[key]!!.second.type == newValue.type) {
                "Type mismatch: expected ${originalMap[key]!!.second.type}, found ${newValue.type}"
            }
            updatedMap[key] = Pair(true, newValue)
        }
        return updatedMap
    }
}
