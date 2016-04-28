package com.exjobbandroidapplication.Resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anders on 2016-04-26.
 */
public class inputCheck {

    private static final String emailRegex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Checks an email to se if its valid.
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Checks a password to se if its valid.
     * @param password
     * @return
     */
    public static boolean isPasswordValid(String password) {
        //TODO: Ändra till längre lösenord.
        return password.length() > 2;
    }

    /**
     * Checks if two passwords are matching
     * @param password1
     * @param password2
     * @return
     */
    public static boolean arePasswordsMatching(String password1, String password2){
        return password1.equals(password2);
    }
}
