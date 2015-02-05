package ts.tree.visit.encode;

import ts.tree.UnaryOpcode;
import ts.tree.UnaryOperator;
import ts.tree.visit.Encode;

/**
 * Support for encoding unary operators
 */
public class UnaryOps {
    /**
     * Encodes a unary operator to Java
     * @param opNode The AST node to encode
     * @param rhs The result of the right hand side of the operator
     * @return A {@code String} of Java code that captures the semantics of the operator
     */
    public static String encode(UnaryOperator opNode, Encode.ReturnValue rhs) {
        final UnaryOpcode opcode = opNode.getOp();
        String operator = null;
        switch (opcode) {
            case NOT:
                operator = "logicalNot";
                break;
            case PLUS:
                operator = "plus";
                break;
            case MINUS:
                operator = "minus";
                break;
            default:
                assert false: "unexpected unary operator: " + opNode.getOpString();
        }

        // All unary operators call GetValue() before evaluation
        return "UnaryOpsSupport." + operator + "(" + rhs.result + ".getValue());\n";
    }
}
