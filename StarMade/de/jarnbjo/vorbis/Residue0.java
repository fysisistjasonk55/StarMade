package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;

class Residue0
  extends Residue
{
  protected Residue0(BitInputStream source, SetupHeader header)
    throws VorbisFormatException, IOException
  {
    super(source, header);
  }
  
  protected int getType()
  {
    return 0;
  }
  
  protected void decodeResidue(VorbisStream vorbis, BitInputStream source, Mode mode, int local_ch, boolean[] doNotDecodeFlags, float[][] vectors)
    throws VorbisFormatException, IOException
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     de.jarnbjo.vorbis.Residue0
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */