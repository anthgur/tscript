package ts.support;

public class TSObjectReference extends TSReference {
    public TSObjectReference(TSString name, TSObject obj) {
        super(name, new TSObjectEnvironmentRecord(obj));
    }

    @Override
    boolean isPropertyReference() {
        return true;
    }

    @Override
    boolean isUnresolvableReference() {
        // TODO not sure what to do here
        return base == null;
    }

    @Override
    boolean hasPrimitiveBase() {
        return base.isPrimitive();
    }
}
