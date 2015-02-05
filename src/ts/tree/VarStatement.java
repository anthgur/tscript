package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class VarStatement extends Statement {
    List<Statement> varDeclList;

    public VarStatement(final Location loc, final List<Statement> varDeclList) {
        super(loc);
        this.varDeclList = varDeclList;
    }

    public List<Statement> getVarDeclList() {
        return varDeclList;
    }

    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
