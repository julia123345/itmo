package myapp.auth.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hash(String plain) {
        return BCrypt.hashpw(plain == null ? "" : plain, BCrypt.gensalt(10));
    }

    public static boolean check(String plain, String hash) {
        if (hash == null || hash.isBlank()) return false;
        try {
            return BCrypt.checkpw(plain == null ? "" : plain, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

