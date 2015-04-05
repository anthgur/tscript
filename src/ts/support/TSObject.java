package ts.support;

import java.util.HashMap;
import java.util.Map;

public class TSObject extends TSValue {
    public static final TSObject globalObj;
    public static final TSObject mutableBindingTemp = new TSObject();

    static {
        globalObj = new TSObject();
        globalObj.put(TSString.create("undefined"), TSUndefined.value);
        globalObj.put(TSString.create("NaN"), TSNumber.create(Double.NaN));
        globalObj.put(TSString.create("Infinity"), TSNumber.create(Double.POSITIVE_INFINITY));
    }

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

    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.3
    public TSValue get(TSString name) {
        return getProperty(name);
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

    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.1
    public final TSValue getOwnProperty(TSString name) {
        if(isPrototypeStr(name)) {
            return prototype;
        }
        if (properties.containsKey(name)) {
            return properties.get(name);
        }
        return TSUndefined.value;
    }

    public final boolean hasProperty(TSString name) {
        // handle undefined on the global object specially because
        // property attributes aren't implemented
        if (this == globalObj && "undefined".equals(name.unbox())) {
            return true;
        }
        return getProperty(name) != TSUndefined.value;
    }

    public void put(TSString name, TSValue val) {
        if (name == null || val == null) {
            throw new AssertionError("null value supplied to TSObject.put");
        }
        if (isPrototypeStr(name)) {
            prototype = val;
        } else {
            properties.put(name, val);
        }
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
        obj.prototype = prototype;
        return obj;
    }
}
