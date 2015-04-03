
package ts.support;

/**
 * The super class for all Tscript primitive values.
 */
public abstract class TSPrimitive extends TSValue {
    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public TSObject toObject() {
        TSObject obj = new TSObject();
        obj.primitive = this;
        return obj;
    }
}
