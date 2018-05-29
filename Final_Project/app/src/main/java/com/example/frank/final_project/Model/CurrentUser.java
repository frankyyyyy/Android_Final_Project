package com.example.frank.final_project.Model;

public class CurrentUser {

    private static String userId;
    private static String userName;
    private static String oppositeId;
    private static String oppositeName;
    private static String storeName;
    private static Cake currentCake;
    private static User.Role userRole;
    private static String userEmail;
    private static String userPassword;

    public CurrentUser(){
    }


    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        CurrentUser.userId = userId;
    }

    public static User.Role getUserRole() {
        return userRole;
    }

    public static void setUserRole(User.Role userRole) {
        CurrentUser.userRole = userRole;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        CurrentUser.userEmail = userEmail;
    }

    public static String getUserPassword() {
        return userPassword;
    }

    public static void setUserPassword(String userPassword) {
        CurrentUser.userPassword = userPassword;
    }

    public static String getOppositeId() {
        return oppositeId;
    }

    public static void setOppositeId(String oppositeId) {
        CurrentUser.oppositeId = oppositeId;
    }

    public static String getOppositeName() {
        return oppositeName;
    }

    public static void setOppositeName(String oppositeName) {
        CurrentUser.oppositeName = oppositeName;
    }

    public static Cake getCurrentCake() {
        return currentCake;
    }

    public static void setCurrentCake(Cake currentCake) {
        CurrentUser.currentCake = currentCake;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        CurrentUser.userName = userName;
    }

    public static String getStoreName() {
        return storeName;
    }

    public static void setStoreName(String storeName) {
        CurrentUser.storeName = storeName;
    }
}
