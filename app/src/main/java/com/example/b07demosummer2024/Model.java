package com.example.b07demosummer2024;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private final DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference("parents");
    private final DatabaseReference providerRef = FirebaseDatabase.getInstance().getReference("providers");

    private final DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("children");

    public void login(String userType,String username, String password,UserLoginCallBack callback){
        String selectUsername = "";
        if(userType.equals("children"))
            selectUsername = username + "@gmail.com";
        else
            selectUsername = username;
        myAuth.signInWithEmailAndPassword(selectUsername,password).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                callback.onError("Login failed, make sure the email and password are correct");
                return;
            }
            FirebaseUser user = myAuth.getCurrentUser();
            if (user == null){
                callback.onError("User is null");
                return;
            }
            DatabaseReference userTypeRef = FirebaseDatabase.getInstance().getReference(userType).
                    child(user.getUid()).child("userType");
            DatabaseReference userPassRef = FirebaseDatabase.getInstance().getReference(userType).
                    child(user.getUid()).child("password");
            userTypeRef.get().addOnCompleteListener(task1 -> {
                if(!task1.isSuccessful()){
                    callback.onError("Error getting user type");
                    return;
                }
                String data = task1.getResult().getValue(String.class);
                if(data != null){
                    if(data.equals(userType)){
                        userPassRef.get().addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()){
                                String passData = task2.getResult().getValue(String.class);
                                if(passData != null && !passData.equals(password)){
                                    userPassRef.setValue(password);
                                }
                            }
                        });
                        callback.onSuccess();
                    }
                }else{
                    callback.onError("User type mismatch");
                    myAuth.signOut();
                }
            });
        });
    }

    public boolean CheckFirstTime(){
        FirebaseUser user = myAuth.getCurrentUser();
        if(user != null && user.getMetadata() != null){
            return user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp();
        }
        return false;
    }
    private DatabaseReference getRef(String userType, String uid){
        DatabaseReference checkRef = null;
        switch (userType) {
            case "parents":
                checkRef = FirebaseDatabase.getInstance().getReference("parents").child(uid);
                break;
            case "providers":
                checkRef = FirebaseDatabase.getInstance().getReference("providers").child(uid);
                break;
            case "children":
                checkRef = FirebaseDatabase.getInstance().getReference("children").child(uid);
                break;
            default:
                return null;
        }
        return checkRef;
    }
    public boolean securePassword(String password){

        if (password.length() < 6 || password.length() > 30) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }

    public void addUserToDatabase(String username, String UserType, String password, Communication callback){
        myAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(UserType.equals("parents")){
                    FirebaseUser user = myAuth.getCurrentUser();
                    if(user == null) return;
                    String uid = user.getUid();
                    Map<String,String> newUserData = new HashMap<>();
                    newUserData.put("uid",uid);
                    newUserData.put("email",username);
                    newUserData.put("password",password);
                    newUserData.put("userType",UserType);
                    parentRef.child(uid).setValue(newUserData)
                            .addOnCompleteListener(writeTask -> {
                                myAuth.signOut();
                                callback.onTrue("Account Successfully Created!");
                            });
                }else if(UserType.equals("providers")){
                    FirebaseUser user = myAuth.getCurrentUser();
                    if(user == null) return;
                    String uid = user.getUid();
                    Map<String,String> newUserData = new HashMap<>();
                    newUserData.put("uid",uid);
                    newUserData.put("email",username);
                    newUserData.put("password",password);
                    newUserData.put("userType",UserType);
                    providerRef.child(uid).setValue(newUserData)
                            .addOnCompleteListener(writeTask -> {
                                myAuth.signOut();
                                callback.onTrue("Account Successfully Created!");
                            });
                }
            }else{
                callback.onFalse("Account Creation Failed");
            }
        });
    }
    public void ResetPassword(String email, Communication callback){
        myAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                callback.onTrue("Password reset email sent");
            }else{
                callback.onFalse("Password reset email failed");
            }
        });
    }
}
