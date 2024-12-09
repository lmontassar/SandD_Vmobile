package com.example.sandd_vmobile.util;

import android.content.Context;
import com.example.sandd_vmobile.model.User;
import java.io.*;

public class UserSerializer {
    private static final String FILE_NAME = "user_data.ser";

    public static void saveUser(Context context, User user) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME); // Full path
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
            System.out.println("File saved at: " + file.getAbsolutePath()); // Log the file path
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User loadUser(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME); // Full path
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            User user = (User) in.readObject();
            in.close();
            fileIn.close();
            return user;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearUser(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME); // Full path
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("User data file deleted: " + file.getAbsolutePath());
                } else {
                    System.err.println("Failed to delete user data file: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("No user data file to delete.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}