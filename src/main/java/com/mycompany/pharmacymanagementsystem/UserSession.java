/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.pharmacymanagementsystem;

public class UserSession {

    private static String userId;
    private static String userType;
    private static String firstName;

    public static void setUser(
            String id,
            String type,
            String name) {

        userId = id;
        userType = type;
        firstName = name;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getUserType() {
        return userType;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static boolean isOwner() {
        return "Owner".equals(userType);
    }

    public static boolean isEmployee() {
        return "Employee".equals(userType);
    }

    public static void clear() {
        userId = null;
        userType = null;
        firstName = null;
    }
}

