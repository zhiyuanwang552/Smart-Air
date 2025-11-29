package com.example.b07demosummer2024;

public interface LoginContract {
    interface Viewer {
        void showErrorMessage(String message);


    }
    interface Presenter{
        boolean login(String username, String password, int selectedID, int parent, int child, int provider);

        boolean VerifySignupCredentials(String username, String password, String ReenterPassword,int selectedUser,int parent,int provider);

        boolean verification(String code);

        void resetPassword(String email);
    }
}
