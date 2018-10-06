import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CThreadHintsTest {

  @Test
  public void testConstructor() {
    String url = "https://storage.googleapis.com/..../work-at-vimeo-2.mp4";
    long fsize = 1007190762L;
    long chunkSize = (long) Math.ceil(fsize * 1.0 / 5);
    boolean acceptR = true;

    ProtectionDomain domain = CThreadHintsTest.class.getProtectionDomain();
    URL classUrl = domain.getCodeSource().getLocation();
    String path = new File(classUrl.getPath()).getPath();

    CThreadHints hints = new CThreadHints(url, fsize, chunkSize, acceptR, path);

    assertEquals(url, hints.URL);
    assertEquals(fsize, hints.fsize);
    assertEquals(chunkSize, hints.chunkSize);
    assertTrue(hints.acceptsRanges);
    assertEquals(Paths.get(path).getParent().toAbsolutePath().toString(), hints.dir);
    assertEquals(Paths.get(path).getFileName().toString(), hints.fname);
  }
}
