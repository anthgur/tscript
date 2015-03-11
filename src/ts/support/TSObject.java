package ts.support;

import java.util.HashMap;
import java.util.Map;

public class TSObject extends TSValue {
    private Map<TSString, TSValue> properties = new HashMap<TSString, TSValue>();
    private TSValue prototype = TSNull.nullValue;

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
        if (prop != TSUndefined.value) {
            return prop;
        }
        if(prototype == TSNull.nullValue) {
            return TSUndefined.value;
        }
        return prototype.getProperty(name);
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
}
