import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class Crc32cTest {

  @Parameters
  public static Collection<Object[]> data() {
    String sn = "123456780";
    return Arrays.asList(
        new Object[][] {
          {"", "0"},
          {"a", "c1d04330"},
          {"abc", "364b3fb7"},
          {"message digest", "2bd79d0"},
          {"abcdefghijklmnopqrstuvwxyz", "9ee6ef25"},
          {"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "a245d57d"},
          {
            "123456789012345678901234567890"
                + "123456789012345678901234567890"
                + "12345678901234567890",
            "477a6781"
          },
          {"123456789", "e3069283"}
        });
  }

  private byte[] inputBytes;
  private String expectedAsHex;

  public Crc32cTest(String input, String expected) {
    this.inputBytes = input.getBytes();
    this.expectedAsHex = expected;
  }

  @Test
  public void testUpdateGetValue() {
    Crc32c crc = new Crc32c();
    crc.update(inputBytes, 0, inputBytes.length);
    assertEquals(expectedAsHex, Long.toHexString(crc.getValue()));
  }
}
