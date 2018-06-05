package com.example.frank.final_project.Model;

/**
 *  For current user info temp cache purpose
 */
public class CurrentUser {

    private static String userId;
    private static String userName;
    private static String userHeadPhotoUri;
    private static String userEmail;
    private static String userPassword;

    private static String oppositeId;
    private static String oppositeName;
    private static String oppositePhotoUri;

    private static User.Role userRole;

    private static Customer customer;
    private static Chef chef;
    private static Store store;
    private static Cake cake;

    private static String chatTargetId;
    private static String chatTargetName;

    public CurrentUser(){
    }

    /**
     *  Empty all attributes when user log out
     */
    public static void clear(){
        userId = null;
        userName = null;
        userHeadPhotoUri = null;
        oppositeId = null;
        oppositeName = null;
        oppositePhotoUri = null;
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

    public static Chef getChef() {
        return chef;
    }

    public static void setChef(Chef chef) {
        CurrentUser.chef = chef;
    }

    public static Customer getCustomer() {
        return customer;
    }

    public static void setCustomer(Customer customer) {
        CurrentUser.customer = customer;
    }

    public static String getUserHeadPhotoUri() {
        return userHeadPhotoUri;
    }

    public static void setUserHeadPhotoUri(String userHeadPhotoUri) {
        CurrentUser.userHeadPhotoUri = userHeadPhotoUri;
    }

    public static String getOppositePhotoUri() {
        return oppositePhotoUri;
    }

    public static void setOppositePhotoUri(String oppositePhotoUri) {
        CurrentUser.oppositePhotoUri = oppositePhotoUri;
    }

    public static Store getStore() {
        return store;
    }

    public static void setStore(Store store) {
        CurrentUser.store = store;
    }
}
