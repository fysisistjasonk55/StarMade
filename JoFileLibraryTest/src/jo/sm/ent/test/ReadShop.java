package jo.sm.ent.test;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class ReadShop extends ReadBase
{
    @Test
    public void readPlayerCharacter()
    {
        List<File> files = findFiles("_SHOP_");
        testFiles(files);
    }
}
