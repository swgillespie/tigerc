package org.swgillespie.tigerc.test.ast;

import junit.framework.Assert;
import org.junit.Test;
import org.swgillespie.tigerc.ast.*;

/**
 * Created by sean on 2/28/15.
 */
public class ConstnessTest {
    @Test
    public void testIntLiteralConstness() {
        ExpressionNode intLiteral = new IntegerLiteralNode(null, 42);
        Assert.assertTrue(intLiteral.isConst());
    }

    @Test
    public void testStringLiteralConstness() {
        ExpressionNode intLiteral = new StringLiteralNode(null, "hello");
        Assert.assertTrue(intLiteral.isConst());
    }

    @Test
    public void testInfixOpConstness() {
        ExpressionNode left = new IntegerLiteralNode(null, 42);
        ExpressionNode right = new IntegerLiteralNode(null, 99);
        ExpressionNode infixOp = new InfixExpressionNode(null, InfixOperator.Eq, left, right);
        Assert.assertTrue(infixOp.isConst());
    }
}
