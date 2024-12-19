package by.tem.validation;

import by.tem.exception.InvalidDataException;

public class CurrencyValidator {
    private static final int MAXIMUM_NAME_LENGTH = 30;
    private static final int MINIMUM_NAME_LENGTH = 2;
    private static final int CODE_LENGTH = 3;
    private static final int MAXIMUM_SIGN_LENGTH = 3;

    public static boolean isValidName(String name) {
        if (name == null) {
            throw new InvalidDataException("Name cannot be null.");
        }
        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new InvalidDataException("A length of the name must be not more than " + MAXIMUM_NAME_LENGTH + " symbols.");
        }
        if (name.length() < MINIMUM_NAME_LENGTH) {
            throw new InvalidDataException("A length of the name must be more than " + MINIMUM_NAME_LENGTH + " symbols.");
        }
        if (!name.matches("[a-zA-Z]+")) {
            throw new InvalidDataException("A name must consist only of English letters.");
        }
        return true;
    }

    public static boolean isValidCode(String code) {
        if (code == null) {
            throw new InvalidDataException("Code cannot be null.");
        }
        if (code.length() != CODE_LENGTH) {
            throw new InvalidDataException("A length of the code must be " + CODE_LENGTH + " symbols.");
        }
        if (!code.matches("[a-zA-Z]+")) {
            throw new InvalidDataException("A code must consist only English letters.");
        }
        return true;
    }

    public static boolean isValidSign(String sign) {
        if (sign == null) {
            throw new InvalidDataException("Sign cannot be null.");
        }
        if (sign.isEmpty()) {
            throw new InvalidDataException("A sign cannot be empty.");
        }
        if (sign.length() > MAXIMUM_SIGN_LENGTH) {
            throw new InvalidDataException("A length of the sign must be not more than " + MAXIMUM_SIGN_LENGTH + " symbols.");
        }
        return true;
    }
}
