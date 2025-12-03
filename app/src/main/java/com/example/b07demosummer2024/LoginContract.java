package com.example.b07demosummer2024;

import androidx.fragment.app.Fragment;

public interface LoginContract {
    interface Viewer {
        void showMessage(String message);

    }
    interface Presenter{
        void login(String username, String password, int selectedID, int parent, int child, int provider, Communication callback);

        void VerifySignupCredentialsCreate(String username, String password,
                                           String ReenterPassword,int selectedUser,int parent,int provider,Communication callback);

        boolean FirstTimeLogIn();

        void resetPassword(String email, Communication callback);
    }
}
