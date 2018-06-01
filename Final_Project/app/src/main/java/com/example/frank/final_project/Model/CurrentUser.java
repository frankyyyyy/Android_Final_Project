package com.example.frank.final_project.Model;

public class CurrentUser {

    private static String userId;
    private static String userName;
    private static String photoUri;
    private static String oppositeId;
    private static String oppositeName;
    private static Chef chef;
    private static Store store;
    private static Boolean storeStatus;
    private static Cake cake;
    private static User.Role userRole;
    private static String userEmail;
    private static String userPassword;

    private static String chatTargetId;
    private static String chatTargetName;

    public CurrentUser(){
    }

    public static void clear(){
        userId = null;
        userName = null;
        photoUri = null;
        oppositeId = null;
        oppositeName = null;
        chef = null;
        store = null;
        cake = null;
        userRole = null;
        userEmail = null;
        userPassword = null;
        chatTargetId = null;
        chatTargetName = null;
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

    public static Cake getCake() {
        return cake;
    }

    public static void setCake(Cake cake) {
        CurrentUser.cake = cake;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        CurrentUser.userName = userName;
    }

    public static Store getStore() {
        return store;
    }

    public static void setStore(Store store) {
        CurrentUser.store = store;
    }

    public static Boolean getStoreStatus() {
        return storeStatus;
    }

    public static void setStoreStatus(Boolean storeStatus) {
        CurrentUser.storeStatus = storeStatus;
    }

    public static String getChatTargetId() {
        return chatTargetId;
    }

    public static void setChatTargetId(String chatTargetId) {
        CurrentUser.chatTargetId = chatTargetId;
    }

    public static String getChatTargetName() {
        return chatTargetName;
    }

    public static void setChatTargetName(String chatTargetName) {
        CurrentUser.chatTargetName = chatTargetName;
    }

    public static String getPhotoUri() {
        return photoUri;
    }

    public static void setPhotoUri(String photoUri) {
        CurrentUser.photoUri = photoUri;
    }

    public static Chef getChef() {
        return chef;
    }

    public static void setChef(Chef chef) {
        CurrentUser.chef = chef;
    }
}
