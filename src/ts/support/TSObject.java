package ts.support;

import java.util.HashMap;
import java.util.Map;

public class TSObject extends TSValue {
    private static final TSObject nullPrototype
            = newDataProperty(TSNull.nullValue);

    private Map<TSString, TSValue> properties
            = new HashMap<TSString, TSValue>();
    protected TSString klass = TSString.create("Object");

    public TSObject() {
        properties.put(TSString.PROTOTYPE, nullPrototype);
    }

    public TSObject(TSObject proto) {
        properties.put(TSString.PROTOTYPE, proto);
    }

    static TSObject newDataProperty(TSValue value) {
        TSObject obj = new TSObject();
        obj.properties.put(TSString.create("value"), value);
        obj.properties.put(TSString.create("writable"), TSBoolean.trueValue);
        obj.properties.put(TSString.create("enumerable"), TSBoolean.trueValue);
        obj.properties.put(TSString.create("configurable"), TSBoolean.trueValue);
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

    public final TSValue getOwnProperty(TSString name) {
        final TSValue prop;
        if((prop = properties.get(name)) == null) {
            return TSUndefined.value;
        }
        return prop;
    }

    @Override
    public final TSValue getProperty(TSString name) {
        TSValue prop = getOwnProperty(name);
        TSValue proto;
        if (prop != TSUndefined.value) {
            return prop;
        }
        if((proto = properties.get(TSString.PROTOTYPE)) == TSNull.nullValue) {
            return TSUndefined.value;
        }
        return proto.getProperty(name);
    }
    
    public final boolean hasProperty(TSString name) {
        return getProperty(name) == TSUndefined.value;
    }

    // TODO property accessors/descriptors
    public final TSValue get(TSString name) {
        return getProperty(name);
    }

    public final void put(TSString name, TSValue value) {
        properties.put(name, value);
    }

    @Override
    public TSValue construct(TSValue[] args) {
        return new TSObject(this);
    }
}
