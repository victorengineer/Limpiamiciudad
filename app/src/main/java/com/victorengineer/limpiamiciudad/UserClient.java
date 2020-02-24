package com.victorengineer.limpiamiciudad;

import android.app.Application;

import com.victorengineer.limpiamiciudad.models.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
