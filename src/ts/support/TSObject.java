package ts.support;

import java.util.HashMap;
import java.util.Map;

public class TSObject extends TSValue {
    private Map<TSString, TSValue> properties
            = new HashMap<TSString, TSValue>();

    protected TSString klass = TSString.create("Object");
    protected TSValue  prototype = TSNull.nullValue;
    public TSPrimitive primitive = null;

    public TSObject() {}

    @Override
    public TSNumber toNumber() {
        return toPrimitive().toNumber();
    }

    @Override
    public TSBoolean toBoolean() {
        return TSBoolean.trueValue;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public TSObject toObject() {
        return this;
    }

    @Override
    public TSPrimitive toPrimitive() {
        return defaultValue(TSHint.NONE);
    }

    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.1
    public final TSValue getOwnProperty(TSString name) {
        final TSValue prop;
        if((prop = properties.get(name)) == null) {
            return TSUndefined.value;
        }
        return prop;
    }

    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.3
    public TSValue get(TSString name) {
        TSValue desc = getProperty(name);

        if (desc.isUndefined()) {
            return TSUndefined.value;
        }

        if (isPrototypeStr(name)) {
            return prototype;
        }
        TSValue prop = properties.get(name);
        return prop == null ? TSUndefined.value : prop;
    }

    public void put(TSString name, TSValue val) {
        if (name == null || val == null) {
            throw new AssertionError("null value supplied to TSObject.put");
        }
        if (isPrototypeStr(name)) {
            System.out.println("protoput");
            prototype = val;
        } else {
            properties.put(name, val);
        }
    }

    @Override
    public final TSValue getProperty(TSString name) {
        TSValue prop = getOwnProperty(name);
        if (!prop.isUndefined()) {
            return prop;
        }
        if(prototype.isNull()) {
            return TSUndefined.value;
        }
        return prototype.getProperty(name);
    }
    
    public final boolean hasProperty(TSString name) {
        return isPrototypeStr(name) || properties.containsKey(name);
    }

    protected boolean isPrototypeStr(TSString str) {
        return "prototype".equals(str.unbox());
    }

    // TODO fix this to work without descriptors
    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.8
    public final TSPrimitive defaultValue(TSHint hint) {
        if (primitive != null) {
            return primitive;
        }

        TSValue toString, valueOf;
        switch (hint) {
            // hint of "String"
            case STRING:
                toString = get(TSString.create("toString"));
                if (toString.isCallable()) {
                    TSPrimitive str = toString.asFunction().execute(this, new TSPrimitive[]{}, false);
                    if (str.isPrimitive()) {
                        return str;
                    }
                }
                valueOf = get(TSString.create("valueOf"));
                if (valueOf.isCallable()) {
                    TSPrimitive val = valueOf.asFunction().execute(this, new TSPrimitive[]{}, false);
                    if (val.isPrimitive()) {
                        return val;
                    }
                }
                //throw new TSTypeError(TSString.create("couldn't cast to string"));

            // the default is to fall through to number
            default:
            // hint of "Number"
            case NUMBER:
                valueOf = get(TSString.create("valueOf"));
                if (valueOf.isCallable()) {
                    TSPrimitive val = valueOf.asFunction().execute(this, new TSPrimitive[]{}, false);
                    if (val.isPrimitive()) {
                        return val;
                    }
                }
                toString = get(TSString.create("toString"));
                if (toString.isCallable()) {
                    TSPrimitive str = toString.asFunction().execute(this, new TSPrimitive[]{}, false);
                    if (str.isPrimitive()) {
                        return str;
                    }
                }
                throw new TSTypeError(TSString.create("couldn't cast to string"));
        }
    }

    @Override
    public TSObject construct(TSValue[] args) {
        TSObject obj = new TSObject();
        System.out.println("ctor prototype: " + prototype.isObject());
        obj.prototype = prototype;
        return obj;
    }
}
