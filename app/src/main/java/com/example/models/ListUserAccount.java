package com.example.models;

import java.util.ArrayList;

public class ListUserAccount {
    public static ArrayList<UserAccount> getUserAccounts()
    {
        ArrayList<UserAccount> database=new ArrayList<>();
        database.add(new UserAccount("admin","123","Admin","Quoc Viet",true));
        database.add(new UserAccount("user1","user123","Employee","Quoc Vinh",false));
        database.add(new UserAccount("user2","user123","Employee","An Ninh",false));
        return database;
    }
    public static UserAccount Login(String username, String password)
    {
        // query database
        ArrayList<UserAccount> database=getUserAccounts();
        for (UserAccount user : database) {
            if(user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password))
            {//login success
                return user;
            }
        }
        return null;
    }
}
