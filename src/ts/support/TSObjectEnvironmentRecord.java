package ts.support;

import ts.Message;

import java.util.Map;

public class TSObjectEnvironmentRecord extends TSEnvironmentRecord {
    private TSObject binding;

    public TSObjectEnvironmentRecord(TSObject obj) {
        this.binding = obj;
    }

    @Override
    boolean hasBinding(TSString name) {
        return binding.hasProperty(name);
    }

    @Override
    void createMutableBinding(TSString name, boolean isDeletable) {
        binding.put(name, TSObject.mutableBindingTemp);
    }

    @Override
    void setMutableBinding(TSString name, TSValue value) {
        binding.put(name, value);
    }

    // we are never in strict mode so don't bother with the flag
    // http://www.ecma-international.org/ecma-262/5.1/#sec-10.2.1.1.4
    @Override
    TSValue getBindingValue(TSString name) {
        if(!binding.hasProperty(name)) {
            return TSUndefined.value;
        }
        return binding.get(name);
    }

    @Override
    TSNumber deleteBinding(TSString name) {
        return null;
    }

    @Override
    public TSValue implicitThisValue() {
        // provideThis never true because no with statement
        // so always return undefined
        return TSUndefined.value;
    }
}
