package by.tem.validation;

import java.util.regex.Pattern;

public class CurrencyValidator {
    private static final int MAXIMUM_NAME_LENGTH = 30;
    private static final int MINIMUM_NAME_LENGTH = 2;
    private static final int CODE_LENGTH = 3;
    private static final int MAXIMUM_SIGN_LENGTH = 3;
    private static final Pattern VALID_PATTERN = Pattern.compile("[a-zA-Z]+");

    public boolean isValidName(String name) {
        return name != null
               && name.length() >= MINIMUM_NAME_LENGTH
               && name.length() <= MAXIMUM_NAME_LENGTH
               && VALID_PATTERN.matcher(name).matches();
    }

    public boolean isValidCode(String code) {
        return code != null
               && code.length() == CODE_LENGTH
               && VALID_PATTERN.matcher(code).matches();
    }

    public boolean isValidSign(String sign) {
        return sign != null && !sign.isEmpty() && sign.length() <= MAXIMUM_SIGN_LENGTH;
    }
}
