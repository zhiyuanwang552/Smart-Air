package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends Fragment implements LoginContract.Viewer {
    ScreenPresenter presenter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        presenter = new ScreenPresenter(new Model());
        presenter.setViewer(this);


        //attach the buttons of the fragments to variables
        ImageButton buttonLogin = view.findViewById(R.id.LoginButton);
        ImageButton buttonSignUp = view.findViewById(R.id.SignUpButton);
        ImageButton buttonForgotPassword = view.findViewById(R.id.imageButton3);
        RadioGroup SignInPerson = view.findViewById(R.id.loginUserTypeRadioGroup);
        EditText UserEmail = view.findViewById(R.id.LoginEmailAddress);
        EditText UserPassword = view.findViewById(R.id.LoginPassword);

        //procedure when login is clicked
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String Username = UserEmail.getText().toString();
                String password = UserPassword.getText().toString();
                int SelectedUserType = SignInPerson.getCheckedRadioButtonId();
                presenter.login(Username, password, SelectedUserType, R.id.radioButton,
                        R.id.radioButton2, R.id.radioButton3, new Communication(){
                            @Override
                            public void onTrue(String userType){
                                showMessage("Login Successful as " + userType);
                                loginSuccess(userType);
                            }
                            @Override
                            public void onFalse(String msg){
                                showMessage(msg);
                            }
                        });
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loadFragment(new ForgotPasswordFragment());
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loadFragment(new SignUpFragment());
            }
        });
        return view;
    }
    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void loginSuccess(String LoginType) {
        //Todo add in transition from login activity to main activity
        if(LoginType.equals("parents")){
            if(presenter.FirstTimeLogIn()){
                System.out.println("Parent Onboarding");
                loadFragment(new ParentOnboardingFragment());
            }else{
                goToMainPage();
            }
        }else if (LoginType.equals("children")){
            if(presenter.FirstTimeLogIn()){
                System.out.println("Children Onboarding");
                loadFragment(new ChildOnboardingFragment());
            }else {
                goToMainPage();
            }
        }else if (LoginType.equals("providers")){
            if(presenter.FirstTimeLogIn()){
                System.out.println("Provider Onboarding");
                loadFragment(new ProviderOnboardingFragment());
            }else {
                goToMainPage();
            }
        }
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToMainPage(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
