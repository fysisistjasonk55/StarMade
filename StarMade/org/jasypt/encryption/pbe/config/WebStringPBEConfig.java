package org.jasypt.encryption.pbe.config;

import org.jasypt.commons.CommonUtils;

public class WebStringPBEConfig
  extends WebPBEConfig
  implements StringPBEConfig
{
  private String stringOutputType = null;
  
  public void setStringOutputType(String stringOutputType)
  {
    this.stringOutputType = CommonUtils.getStandardStringOutputType(stringOutputType);
  }
  
  public String getStringOutputType()
  {
    return this.stringOutputType;
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.jasypt.encryption.pbe.config.WebStringPBEConfig
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */