package com.peak.utils;

import java.util.regex.Pattern;

public class ValidatorUtil {

    public static boolean isMobile(String value){
        if ((value != null) && (!value.isEmpty())) {
            boolean matches = Pattern.matches("^1[3-9]\\d{9}$", value);
            return matches;
        }
        return false;
    }

}
