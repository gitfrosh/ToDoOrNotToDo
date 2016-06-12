package de.ueberdiespree.todoornottodov02;

/**
 * Created by ulrike on 12.04.16.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ulrike on 12.04.16.
 */
public class PasswordValidator {

    // stellt sicher dass das Passwort numerisch und 6 Ziffern lang ist

    private static final String PASSWORD_PATTERN =
            "[0-9]{6}";
    private Pattern pattern;

    public PasswordValidator() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    public boolean validate(final String hex) {

        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();

    }
}