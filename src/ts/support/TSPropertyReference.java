package ts.support;

import ts.Message;

public class TSPropertyReference extends TSReference {
    public final TSValue base;

    public TSPropertyReference(TSString name, TSValue base) {
        super(name);
        this.base = base;
    }

    @Override
    boolean isUnresolvableReference() {
        return base.isUndefined();
    }

    @Override
    public boolean isPropertyReference() {
        return base.isObject() || hasPrimitiveBase();
    }

    @Override
    boolean hasPrimitiveBase() {
        return base.isBoolean() || base.isString() || base.isNumber();
    }

    @Override
    public void putValue(TSValue value) {
        if (isUnresolvableReference()) {
            TSObject.globalObj.put(getReferencedName(), value);
            return;
        }
        if (hasPrimitiveBase()) {
            Message.bug("putValue on PropertyReference with primitive base");
        }
        base.toObject().put(getReferencedName(), value);
    }

    @Override
    public TSValue getValue() {
        if (isUnresolvableReference()) {
            throw new TSException(TSString.create("undefined identifier: " +
                    this.getReferencedName().unbox()));
        }
        if (hasPrimitiveBase()) {
            return base.toObject().getProperty(getReferencedName());
        } else {
            if (!base.isObject()) {
                Message.bug("Non-primitive base, but not TSObject type.");
            }
            return base.toObject().get(getReferencedName());
        }
    }
}
