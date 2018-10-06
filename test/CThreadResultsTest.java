import org.junit.Test;

import static org.junit.Assert.*;

public class CThreadResultsTest {

  @Test
  public void testGetFailed() {
    CThreadResults results = CThreadResults.getFailed(0, 1);
    assertEquals(0, results.id);
    assertEquals(-1, results.bytesDownloaded);
    assertEquals(CThreadResults.UNKNOWN, results.msg);
    assertNull(results.checksum);
    assertFalse(results.finished);
    assertEquals(0, results.id);
  }
}
