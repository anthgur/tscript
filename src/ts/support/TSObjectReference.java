package ts.support;

public class TSObjectReference extends TSReference {
    TSObjectEnvironmentRecord base;

    public TSObjectReference(TSString name, TSObject obj) {
        super(name);
        this.base = new TSObjectEnvironmentRecord(obj);
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
}
