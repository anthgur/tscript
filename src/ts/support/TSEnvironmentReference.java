package ts.support;

import ts.Message;

/**
 * The class for Tscript References for lexical environment references
 * (<a href="http://www.ecma-international.org/ecma-262/5.1/#sec-8.7">ELS
 * 8.7</a>).
 *
 */
final class TSEnvironmentReference extends TSReference {
  TSEnvironmentRecord base;
  /** Create a Reference for a name in an environment. */
  TSEnvironmentReference(final TSString name, final TSEnvironmentRecord base) {
    super(name);
    this.base = base;
  }

  /** Is the reference not resolvable? That is, is the name not defined
   *  in the environment?
   */
  boolean isUnresolvableReference() {
    return false;
  }

  /** Environment references cannot be property references so this always
   *  returns false.
   */
  boolean isPropertyReference() {
    return false;
  }

  /** Get the value from the Reference. Issues an error and
   *  returns null if the name is not defined.
   */
  public TSValue getValue() {
    if (base == null) {
      throw new TSException(TSString.create("undefined identifier: " +
              this.getReferencedName().unbox()));
    }
    return base.getBindingValue(this.getReferencedName());
  }

  /** Assign a value to the name specified by the Reference. */
  public void putValue(final TSValue value) {
    if (base == null) {
      throw new TSException(TSString.create("undefined identifier: " +
              this.getReferencedName().unbox()));
    }
    base.setMutableBinding(this.getReferencedName(), value);
  }
}
