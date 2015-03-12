package ts.support;

// TODO this is a placeholder
public class TSPrimitiveObject extends TSObject {
    private TSPrimitive primitive;

    public TSPrimitiveObject(TSValue primitive) {
        if(!primitive.isPrimitive()) {
            throw new TSTypeError(TSString.create("couldn't cast to primitive"));
        }
        this.primitive = primitive.toPrimitive();
    }

    @Override
    public TSPrimitive toPrimitive() {
        return primitive;
    }
}
