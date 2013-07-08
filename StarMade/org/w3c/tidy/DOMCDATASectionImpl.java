package org.w3c.tidy;

import org.w3c.dom.CDATASection;

public class DOMCDATASectionImpl
  extends DOMTextImpl
  implements CDATASection
{
  protected DOMCDATASectionImpl(Node paramNode)
  {
    super(paramNode);
  }
  
  public String getNodeName()
  {
    return "#cdata-section";
  }
  
  public short getNodeType()
  {
    return 4;
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.w3c.tidy.DOMCDATASectionImpl
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */