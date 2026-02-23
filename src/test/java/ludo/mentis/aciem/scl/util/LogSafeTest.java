package ludo.mentis.aciem.scl.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogSafeTest {

    // Dummy class to access protected methods
    private static class LogSafeDummy extends LogSafe {
        public static String testNormalizeToSingleLine(String s) {
            return LogSafe.normalizeToSingleLine(s);
        }
        public static String testAbbreviate(String s, int max) {
            return LogSafe.abbreviate(s, max);
        }
        public static String testSha256Hex(String s) {
            return LogSafe.sha256Hex(s);
        }
        public static String testToHex(byte[] bytes) {
            return LogSafe.toHex(bytes);
        }
    }

    @Test
    void testOf() {
        assertEquals("<null>", LogSafe.of(null));
        
        String input = "Hello World";
        String result = LogSafe.of(input);
        assertTrue(result.startsWith("value='Hello World', len=11, sha256="));
        assertEquals(12, result.split("sha256=")[1].length());
    }

    @Test
    void testNormalizeToSingleLine() {
        assertEquals("Hello World", LogSafeDummy.testNormalizeToSingleLine("Hello World"));
        assertEquals("HelloWorld", LogSafeDummy.testNormalizeToSingleLine("Hello\nWorld"));
        assertEquals("HelloWorld", LogSafeDummy.testNormalizeToSingleLine("Hello\r\nWorld"));
        assertEquals("Hello World", LogSafeDummy.testNormalizeToSingleLine("  Hello   World  "));
        assertEquals("HelloWorld", LogSafeDummy.testNormalizeToSingleLine("Hello\tWorld"));
        assertEquals("HelloWorld", LogSafeDummy.testNormalizeToSingleLine("Hello\0World"));
        assertEquals("abc", LogSafeDummy.testNormalizeToSingleLine("a\u007Fb\u001Fc"));
    }

    @Test
    void testAbbreviate() {
        assertEquals("abc", LogSafeDummy.testAbbreviate("abc", 5));
        assertEquals("abc", LogSafeDummy.testAbbreviate("abc", 3));
        assertEquals("ab…", LogSafeDummy.testAbbreviate("abc", 2));
        assertEquals("…", LogSafeDummy.testAbbreviate("abc", 0));
    }

    @Test
    void testSha256Hex() {
        // sha256 of "test" is 9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08
        assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", LogSafeDummy.testSha256Hex("test"));
    }

    @Test
    void testToHex() {
        byte[] bytes = new byte[] { (byte)0x00, (byte)0x0F, (byte)0x10, (byte)0xFF };
        assertEquals("000f10ff", LogSafeDummy.testToHex(bytes));
    }
}
