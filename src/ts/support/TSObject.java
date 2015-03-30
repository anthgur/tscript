package ts.support;

import java.util.HashMap;
import java.util.Map;

public class TSObject extends TSValue {
    private Map<TSString, TSValue> properties
            = new HashMap<TSString, TSValue>();

    protected TSString klass = TSString.create("Object");
    protected TSValue  prototype = TSNull.nullValue;

    public TSObject() {}

    public TSObject(TSObject prototype) {
        this.prototype = prototype;
    }

    static TSObject newDataProperty(TSValue value) {
        TSObject obj = new TSObject();
        obj.properties.put(TSString.create("value"), value);
        obj.properties.put(TSString.create("writable"), TSBoolean.trueValue);
        obj.properties.put(TSString.create("enumerable"), TSBoolean.trueValue);
        obj.properties.put(TSString.create("configurable"), TSBoolean.trueValue);
        return obj;
    }

    static TSObject newAccessorProperty() {
        TSObject obj = new TSObject();
        obj.properties.put(TSString.create("get"), TSUndefined.value);
        obj.properties.put(TSString.create("set"), TSUndefined.value);
        obj.properties.put(TSString.create("writable"), TSBoolean.falseValue);
        obj.properties.put(TSString.create("enumerable"), TSBoolean.falseValue);
        obj.properties.put(TSString.create("configurable"), TSBoolean.falseValue);
        return obj;
    }

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

    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.1
    public final TSValue getOwnProperty(TSString name) {
        final TSValue prop;
        if((prop = properties.get(name)) == null) {
            return TSUndefined.value;
        }
        TSObject d = new TSObject();
        TSObject x = (TSObject)properties.get(name);
        if (isDataDescriptor(x)) {
            d.properties.put(TSString.VALUE, x.properties.get(TSString.VALUE));
            d.properties.put(TSString.WRITABLE, x.properties.get(TSString.WRITABLE));
        } else {
            d.properties.put(TSString.GET, x.properties.get(TSString.GET));
            d.properties.put(TSString.SET, x.properties.get(TSString.SET));
        }
        d.properties.put(TSString.ENUMERABLE, x.properties.get(TSString.ENUMERABLE));
        d.properties.put(TSString.CONFIGURABLE, x.properties.get(TSString.CONFIGURABLE));
        return prop;
    }

    boolean isDataDescriptor(TSValue desc) {
        if (desc.isUndefined()) {
            return false;
        }
        // if the descriptor doesn't have the value and writable property
        // it's not a data descriptor, otherwise it is
        return !(desc.getProperty(TSString.create("value")) == null
                && desc.getProperty(TSString.create("writable")) == null);
    }

    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.3
    public TSValue get(TSString name) {
        TSValue desc = getProperty(name);

        if (desc.isUndefined()) {
            return TSUndefined.value;
        }

        if (isDataDescriptor(desc)) {
            return ((TSObject)desc).properties.get(TSString.VALUE);
        }

        TSValue getter = desc.getProperty(TSString.GET);

        if (getter.isUndefined()) {
            return TSUndefined.value;
        }

        return getter.asFunction().execute(this, new TSValue[]{}, false);
    }

    public void put(TSString name, TSValue val) {

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
        return getProperty(name) == TSUndefined.value;
    }

    // http://www.ecma-international.org/ecma-262/5.1/#sec-8.12.8
    public final TSValue defaultValue(char hint) {
        final TSValue toString, valueOf;
        switch (hint) {
            // hint of "String"
            case 's':
                toString = get(TSString.create("toString"));
                if (toString.isCallable()) {
                    TSValue str = toString.asFunction().execute(this, new TSValue[]{}, false);
                    if (str.isPrimitive()) {
                        return str;
                    }
                }
                valueOf = get(TSString.create("valueOf"));
                if (valueOf.isCallable()) {
                    TSValue val = valueOf.asFunction().execute(this, new TSValue[]{}, false);
                    if (val.isPrimitive()) {
                        return val;
                    }
                }
                throw new TSTypeError(TSString.create("couldn't cast to string"));

            // the default is to fall through to number
            default:
            // hint of "Number"
            case 'n':
                valueOf = get(TSString.create("valueOf"));
                if (valueOf.isCallable()) {
                    TSValue val = valueOf.asFunction().execute(this, new TSValue[]{}, false);
                    if (val.isPrimitive()) {
                        return val;
                    }
                }
                toString = get(TSString.create("toString"));
                if (toString.isCallable()) {
                    TSValue str = toString.asFunction().execute(this, new TSValue[]{}, false);
                    if (str.isPrimitive()) {
                        return str;
                    }
                }
                throw new TSTypeError(TSString.create("couldn't cast to string"));
        }
    }

    @Override
    public TSValue construct(TSValue[] args) {
        return new TSObject(this);
    }
}
