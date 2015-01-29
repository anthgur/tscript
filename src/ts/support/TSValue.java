
package ts.support;

import ts.Message;

/**
 * The super class for all Tscript values.
 */
public abstract class TSValue
{
  //
  // References: i.e. getValue and putValue (section 8.7)
  //

  /** Get the value. This method is only overridden in TSReference.
   *  Otherwise just return "this".
   */
  public TSValue getValue()
  {
    return this;
  }

  /** Assign to the value. This method is only overridden in TSReference.
   *  Otherwise just report an error.
   */
  public void putValue(TSValue value)
  {
    Message.bug("putValue called for non-Reference type");
  }

  //
  // conversions (section 9)
  //

  /** Convert to Primitive. Override only in TSObject and TSReference.
   *  Otherwise just return "this". Note: type hint is not implemented.
   */
  public TSPrimitive toPrimitive()
  {
    return (TSPrimitive) this;
  }

  abstract public TSNumber toNumber();
  abstract public TSBoolean toBoolean();

  /** Convert to String. Override for all primitive types and TSReference.
   *  It can't be called toString because of Object.toString.
   */
  public TSString toStr()
  {
    TSPrimitive prim = this.toPrimitive();
    return prim.toStr();
  }

  @Override
  public String toString() {
    return this.toStr().unbox();
  }

  /** Perform an assignment. "this" is the left operand and the right
   *  operand is given by the parameter.
   */
  public final TSValue simpleAssignment(final TSValue right)
  {
    TSValue rightValue = right.getValue();
    this.putValue(rightValue);
    return rightValue;
  }

  //
  // test for null and undefined
  //

  /** Is this value Undefined? Override only in TSUndefined and
   *  TSReference.
   */
  public boolean isUndefined()
  {
    return false;
  }

}

