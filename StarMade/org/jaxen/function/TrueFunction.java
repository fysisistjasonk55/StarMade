package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

public class TrueFunction
  implements Function
{
  public Object call(Context context, List args)
    throws FunctionCallException
  {
    if (args.size() == 0) {
      return evaluate();
    }
    throw new FunctionCallException("true() requires no arguments.");
  }
  
  public static Boolean evaluate()
  {
    return Boolean.TRUE;
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.jaxen.function.TrueFunction
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */