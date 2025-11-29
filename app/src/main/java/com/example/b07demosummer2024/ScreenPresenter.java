package com.example.b07demosummer2024;

public class ScreenPresenter implements LoginContract.Presenter {
    private LoginContract.Viewer viewer;
    private Model model;

    public ScreenPresenter(Model model){
        this.model = model;
    }

    public void setViewer(LoginContract.Viewer viewer){
        this.viewer = viewer;
    }

    @Override
    public boolean login(String username, String password, int selectedID, int parent, int child, int provider){
        if(username.isEmpty() || password.isEmpty()){
            viewer.showErrorMessage("Username and password cannot be empty");
            return false;
        }
        if(selectedID == parent){
            if(!model.login("parent",username, password)) {
                viewer.showErrorMessage("Incorrect email or password. Make Sure the login type is correct");
                return false;
            }
        }
        else if(selectedID == child){
            if(!model.login("child",username, password)) {
                viewer.showErrorMessage("Incorrect email or password. Make Sure the login type is correct");
                return false;
            }
        }
        else if(selectedID == provider){
            if(!model.login("provider",username, password)) {
                viewer.showErrorMessage("Incorrect email or password. Make Sure the login type is correct");
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean VerifySignupCredentials(String username, String password, String reenterpassword, int selectedUser, int parent, int provider) {
        if(username.isEmpty() || password.isEmpty()){
            viewer.showErrorMessage("Username and password cannot be empty");
            return false;
        }
        if(!password.equals(reenterpassword)){
            viewer.showErrorMessage("Passwords do not match");
            return false;
        }else if (model.userExists(username,selectedUser,parent,provider)) {
            viewer.showErrorMessage("Username already exists");
            return false;
        }else if(model.securePassword(password)){
            viewer.showErrorMessage("*Password is not secure!\n\n" +
                    "        *A password must have\n\n" +
                    "        \t\t\t\t- Lowercase Character\n\n" +
                    "        \t\t\t\t- Uppercase Character\n\n" +
                    "        \t\t\t\t- Numeric Character\n\n" +
                    "        \t\t\t\t- Non-alphanumeric Character\n\n" +
                    "        \t\t\t\t- Minimum of 6 characters\n\n" +
                    "        \t\t\t\t- Maximum of 30 characters\n");
            return false;
        }
        return true;
    }

    @Override
    public boolean verification(String code) {
        return false;
    }

    @Override
    public void resetPassword(String email) {}
}
