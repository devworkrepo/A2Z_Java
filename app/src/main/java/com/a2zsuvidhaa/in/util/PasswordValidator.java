package com.a2zsuvidhaa.in.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator{

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    public static boolean validate(final String password){
         Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
         Matcher matcher = pattern.matcher(password);
        return matcher.matches();

    }
}