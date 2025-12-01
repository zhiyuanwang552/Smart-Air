package com.example.b07demosummer2024;

public class ScreenPresenter implements LoginContract.Presenter {
    private LoginContract.Viewer viewer;
    private final Model model;

    public ScreenPresenter(Model model){
        this.model = model;
    }

    public void setViewer(LoginContract.Viewer viewer){
        this.viewer = viewer;
    }

    @Override
    public void login(String username, String password, int selectedID, int parent,
                      int child, int provider, Communication callback){
        if(username.isEmpty() || password.isEmpty()){
            callback.onFalse("Username and password cannot be empty");
            return;
        }
        String userType;
        if(selectedID == parent){
            userType = "parents";
        }else if(selectedID == child){
            userType = "children";
        }else{
            userType = "providers";
        }

        model.login(username, password, new UserLoginCallBack() {
            @Override
            public void onSuccess() {

                if(selectedID == parent){
                    callback.onTrue("parents");
                }else if(selectedID == child){
                    callback.onTrue("children");
                }else if(selectedID == provider){
                    callback.onTrue("providers");
                }
            }
            @Override
            public void onError(String msg) {
                callback.onFalse(msg);
            }
        });
    }
    @Override
    public boolean FirstTimeLogIn() {
        return model.CheckFirstTime();
    }
    @Override
    public void VerifySignupCredentialsCreate(String username, String password,
                                              String reenterpassword, int selectedUser, int parent, int provider, Communication callback) {
        if(username.isEmpty() || password.isEmpty()){
            viewer.showMessage("Username and password cannot be empty");
            return;
        }
        if(!password.equals(reenterpassword)) {
            viewer.showMessage("Passwords do not match");
            return;
        }
        if(!model.securePassword(password)){
            viewer.showMessage("*Password is not secure!\n\n" +
                    "        *A password must have\n\n" +
                    "        \t\t\t\t- Lowercase Character\n\n" +
                    "        \t\t\t\t- Uppercase Character\n\n" +
                    "        \t\t\t\t- Numeric Character\n\n" +
                    "        \t\t\t\t- Special Character\n\n" +
                    "        \t\t\t\t- Minimum of 6 characters\n\n" +
                    "        \t\t\t\t- Maximum of 30 characters\n");
            return;
        }
        String userType;
        if(selectedUser == parent){
            userType = "parents";
        }else{
            userType = "providers";
        }

        model.addUserToDatabase(username,userType,password, new Communication(){
            @Override
            public void onTrue(String message) {
                viewer.showMessage(message);
                callback.onTrue(message);
            }
            @Override
            public void onFalse(String message) {
                viewer.showMessage(message);
                callback.onFalse(message);
            }
        });

//                model.sendVerificationCodeCreate(username,password);

    }
    @Override
    public void resetPassword(String email, Communication callback) {
        if(email.isEmpty()){
            callback.onFalse("Email cannot be empty");
            return;
        }
        model.ResetPassword(email,new Communication(){
            @Override
            public void onTrue(String message) {
                callback.onTrue(message);
            }
            @Override
            public void onFalse(String message) {
                callback.onFalse(message);
            }
        });

    }
}
