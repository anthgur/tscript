package ts.support;

/**
 * Represents (Tscript) String values
 * (<a href="http://www.ecma-international.org/ecma-262/5.1/#sec-8.4">ELS
 * 8.4</a>).
 * <p>
 * This class only currently allows String values to be created and does
 * not yet support operations on them.
 */
public final class TSString extends TSPrimitive {
  private final String value;

  // use the "create" method instead
  private TSString(final String value) {
    this.value = value;
  }

  /** Get the value of the String. */
  public String unbox() {
    return value;
  }

  /** Overrides Object.abstractEquals because TSString used as key for Map */
  public boolean equals(Object anObject) {
    if (anObject instanceof TSString) {
      return value.equals(((TSString) anObject).unbox());
    }
    return false;
  }

  /** Need to override Object.hashcode() when overriding Object.abstractEquals() */
  public int hashCode() {
    return value.hashCode();
  }

  /** Create a Tscript String from a Java String. */
  public static TSString create(final String value) {
    // could use hashmap to screen for common strings?
    return new TSString(value);
  }

  /** Convert String to Number. Not yet Implemented. */
  public TSNumber toNumber() {
    assert false : "not implemented";
    return null;
  }

  @Override
  public TSBoolean toBoolean() {
    // empty string => false
    // else => true
    // http://www.ecma-international.org/ecma-262/5.1/#sec-9.2
    return value.isEmpty() ? TSBoolean.falseValue : TSBoolean.trueValue;
  }

  /** Override in TSValue */
  public TSString toStr() {
    return this;
  }
}
