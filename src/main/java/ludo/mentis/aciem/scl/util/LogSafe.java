package ludo.mentis.aciem.scl.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Produces safe-to-log representations of user-controlled strings:
 * - Single-line (no CR/LF)
 * - Control characters removed
 * - Bounded length
 * - Includes a short fingerprint to correlate without logging full raw input
 */
public class LogSafe {

    private static final int PREVIEW_LIMIT = 32;

    protected LogSafe() {
    }

    public static String of(final String userProvided) {
        if (userProvided == null) {
            return "<null>";
        }

        final String normalized = normalizeToSingleLine(userProvided);
        final String preview = abbreviate(normalized, PREVIEW_LIMIT);
        final String fp = sha256Hex(userProvided).substring(0, 12); // short fingerprint

        return "value='" + preview + "', len=" + userProvided.length() + ", sha256=" + fp;
    }

    protected static String normalizeToSingleLine(final String s) {
        final StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);

            // Drop ASCII control chars (incl. \r \n \t \0 ...), plus DEL.
            if (c <= 0x1F || c == 0x7F) {
                continue;
            }

            // Keep it single-line and readable; collapse other whitespace to a plain space.
            if (Character.isWhitespace(c)) {
                out.append(' ');
                continue;
            }

            out.append(c);
        }

        // Collapse repeated spaces and trim.
        return out.toString().replaceAll("\\s{2,}", " ").trim();
    }

    protected static String abbreviate(final String s, final int max) {
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "…";
    }

    protected static String sha256Hex(final String s) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // Practically impossible on a standard JDK; still keep logs safe if it happens.
            return "na";
        }
    }

    protected static String toHex(final byte[] bytes) {
        final char[] hex = "0123456789abcdef".toCharArray();
        final char[] out = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            final int v = bytes[i] & 0xFF;
            out[i * 2] = hex[v >>> 4];
            out[i * 2 + 1] = hex[v & 0x0F];
        }

        return new String(out);
    }
}