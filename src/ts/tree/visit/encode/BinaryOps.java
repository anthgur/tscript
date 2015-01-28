package ts.tree.visit.encode;

import ts.tree.*;
import ts.tree.visit.Encode;

public class BinaryOps {
    public static String encode(final BinaryOperator opNode,
                                final Encode.ReturnValue lhs,
                                final Encode.ReturnValue rhs) {
        final BinaryOpcode opCode = opNode.getOp();
        String operator = null;
        switch (opCode) {
            // Handle assignment specially because it's baked into TSValue
            case ASSIGN:
                return lhs.result + ".simpleAssignment(" + rhs.result + ");\n";
            case ADD:
                operator = "add";
                break;
            case MULTIPLY:
                operator = "multiply";
                break;
            default:
                assert false: "unexpected binary operator: " + opNode.getOpString();
        }
        return "BinaryOpsSupport." + operator + "("
                + lhs.result + ".getValue(),"
                + rhs.result + ".getValue());\n";
    }
}
