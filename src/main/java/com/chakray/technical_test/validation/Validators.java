package com.chakray.technical_test.validation;

import java.util.regex.Pattern;

public class Validators {
    private static final Pattern RFC_PATTERN = Pattern.compile("^[A-Z&Ñ]{3,4}\\d{6}[A-Z0-9]{3}$", Pattern.CASE_INSENSITIVE);

    public static boolean isValidRfc(String taxId) {
        if (taxId == null) return false;
        return RFC_PATTERN.matcher(taxId.trim()).matches();
    }

    public static boolean isValidPhoneFormat(String phone) {
        if (phone == null) return false;
        if (!phone.matches("^[0-9+ ()-]+$")) return false;
        String digits = phone.replaceAll("\\D", "");
        return digits.length() >= 10 && digits.substring(digits.length()-10).length() == 10;
    }
}
