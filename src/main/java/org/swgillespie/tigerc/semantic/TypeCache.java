package org.swgillespie.tigerc.semantic;

import org.swgillespie.tigerc.ast.ExpressionNode;
import org.swgillespie.tigerc.common.CompilerAssert;

import java.util.HashMap;
import java.util.Map;

public final class TypeCache {
    private Map<ExpressionNode, Type> typeMap;

    public TypeCache() {
        this.typeMap = new HashMap<>();
    }

    public void put(ExpressionNode node, Type ty) {
        this.typeMap.put(node, ty);
    }

    public Type get(ExpressionNode node) {

        Type value = this.typeMap.get(node);
        CompilerAssert.check(value != null, "failed to retrieve type for expression: " + value);
        return value;
    }

    public void clear() {
        this.typeMap.clear();
    }
}