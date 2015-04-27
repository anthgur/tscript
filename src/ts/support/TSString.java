package ts.support;

import java.util.regex.Pattern;

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
  static final TSString LENGTH = new TSString("length");
  static final TSObject STRING;

  static {
    STRING = new TSObject();
    STRING.put(TSString.create("split"), new TSFunctionObject(TSLexicalEnvironment.globalEnv, new String[]{}) {
      @Override
      public TSValue execute(TSValue ths, TSValue[] args, boolean isConstructor) {
        TSObject o = new TSObject();
        if (args.length != 2) {
          throw new TSException(TSString.create("invalid args {" + args.length + "}"));
        }
        if (!args[1].isString() || !args[0].isString()) {
          throw new TSTypeError(TSString.create("non string supplied to split"));
        }
        try {
          String[] split = args[0].toStr().unbox().split(args[1].toStr().unbox());
          Integer x = 0;
          for (String s : split) {
            o.put(TSString.create(x.toString()), TSString.create(s));
            x++;
          }
          o.put(TSString.create("length"), TSString.create(String.valueOf(split.length)));
          return o;
        } catch (Exception e) {
          throw new TSException(TSString.create("couldn't split"));
        }
      }
    });
  }

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
    return anObject instanceof TSString && value.equals(((TSString) anObject).toStr().unbox());
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

  public TSNumber toNumber() {
    return TSNumber.create(Double.parseDouble(value));
  }

  @Override
  public TSBoolean toBoolean() {
    // empty string => false
    // else => true
    // http://www.ecma-international.org/ecma-262/5.1/#sec-9.2
    return value.isEmpty() ? TSBoolean.falseValue : TSBoolean.trueValue;
  }

  @Override
  public boolean isString() {
    return true;
  }

  public TSString toStr() {
    return this;
  }
}
