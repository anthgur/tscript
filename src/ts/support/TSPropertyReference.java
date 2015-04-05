package ts.support;

import ts.Message;

public class TSPropertyReference extends TSReference {
    final TSValue base;

    public TSPropertyReference(TSString name, TSValue base) {
        super(name, null);
        this.base = base;
    }

    @Override
    TSValue getBase() {
        return base;
    }

    @Override
    boolean isUnresolvableReference() {
        return base.isUndefined();
    }

    @Override
    boolean isPropertyReference() {
        return base.isObject() || hasPrimitiveBase();
    }

    @Override
    boolean hasPrimitiveBase() {
        return base.isBoolean() || base.isString() || base.isNumber();
    }

    @Override
    public void putValue(TSValue value) {
        if (isUnresolvableReference()) {
            Message.bug("well fuck");
        }
        if (hasPrimitiveBase()) {
            Message.bug("putValue on PropertyReference with primitive base");
        }
        base.toObject().put(getReferencedName(), value);
    }
}
