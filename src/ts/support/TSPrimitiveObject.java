package ts.support;

// TODO this is a placeholder
public class TSPrimitiveObject extends TSObject {
    private TSPrimitive primitive;

    public TSPrimitiveObject(TSPrimitive primitive) {
        this.primitive = primitive;
    }

    @Override
    public TSPrimitive toPrimitive() {
        return primitive;
    }
}
