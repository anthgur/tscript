package ts.tree.visit.encode;

import ts.tree.UnOpcode;
import ts.tree.UnaryOperator;
import ts.tree.visit.Encode;

public class UnaryOps {
    public static String encode(UnaryOperator opNode, Encode.ReturnValue rhs) {
        final UnOpcode opcode = opNode.getOp();
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
        return "UnaryOpsSupport." + operator + "(" + rhs.result + ");\n";
    }
}
