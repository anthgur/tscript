
package ts.support;

/**
 * The super class for Tscript References
 * (<a href="http://www.ecma-international.org/ecma-262/5.1/#sec-8.7">ELS
 * 8.7</a>).
 * <p>
 * The derived classes are:
 * <ul>
 * <li> TSEnvironmentReference
 * <li> TSpropertyReference - not implemented yet.
 * </ul>
 *
 */
abstract class TSReference extends TSValue {
  private final TSString name;
  protected final TSEnvironmentRecord base;

  TSReference(final TSString name, final TSEnvironmentRecord base) {
    this.name = name;
    this.base = base;
  }

  TSString getReferencedName() {
    return name;
  }

  /** Is it a property reference? */
  abstract boolean isPropertyReference();

  /** Is it a unresolvable reference (not defined)? */
  abstract boolean isUnresolvableReference();

  boolean hasPrimitiveBase() {
    return base.isPrimitive();
  }

  // http://www.ecma-international.org/ecma-262/5.1/#sec-8.7.2
  @Override
  public void putValue(TSValue value) {
    if (isUnresolvableReference()) {
      // TODO put into global object
    } else if (isPropertyReference()) {
      // TODO puts into primitives/objects
      if (!hasPrimitiveBase()) {

      }
    } else {
      base.setMutableBinding(name, value);
    }
  }

  @Override
  public TSValue getValue() {
    if (isUnresolvableReference()) {
      throw new TSException(TSString.create("couldn't resolve reference"));
    }
    if (isPropertyReference()) {
      // TODO some magic needs to happen here
      /*
      if (!hasPrimitiveBase()) {
        base.getBindingValue(name);
      }
      */
      return base.getBindingValue(name);
    }
    throw new AssertionError("fell through getValue on a Reference");
  }

  //
  // the following are methods that are inherited from TSValue
  // and just require that getValue be called
  //

  /** Get value from reference and convert it to primitive type.<br>
   * (not public as not used outside of package)<br>
   * (type hint not supported)
   */
  public final TSPrimitive toPrimitive() {
    return this.getValue().toPrimitive();
  }

  /** Get value from reference and convert it to number type. */
  public final TSNumber toNumber() {
    return this.getValue().toNumber();
  }

  @Override
  public final TSBoolean toBoolean() {
    return this.getValue().toBoolean();
  }

  /** Get value from reference and convert it to string type. */
  public final TSString toStr() {
    return this.getValue().toStr();
  }

  /** Get value from reference and see if it is undefined. */
  public final boolean isUndefined() {
    return this.getValue().isUndefined();
  }

  @Override
  public final boolean isReference() {
    return true;
  }
}


