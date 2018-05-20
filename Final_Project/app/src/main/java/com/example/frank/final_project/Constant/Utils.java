package com.example.frank.final_project.Constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Frank on 2018/5/16.
 */

public class Utils {

    /**
     * Check email input validity
     * @param email input
     * @return validity
     */
    public static boolean emailInputIsLegal(String email){
        Pattern pattern = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Check password input validity
     * @param password input
     * @return validity
     */
    public static boolean passwordInputIsLegal(String password){
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{6,16}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * Check if passwords entered in two times are all the same
     * @param password First time input
     * @param confirm Second time input
     * @return result
     */
    public static boolean confirmPassword(String password, String confirm){
        return password.equals(confirm);
    }

    /**
     * Check password input validity
     * @param name input
     * @return validity
     */
    public static boolean nameInputIsLegal(String name){
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    /**
     * Check password input validity
     * @param phoneNum input
     * @return validity
     */
    public static boolean phoneNumInputIsLegal(String phoneNum){
        Pattern pattern = Pattern.compile("^[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNum);
        return matcher.matches();
    }
}
