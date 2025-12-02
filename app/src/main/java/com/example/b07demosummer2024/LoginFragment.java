package com.example.b07demosummer2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

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
        String loginType = "loginType";
        String curr_uid = "curr_uid";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getUid();
        Log.d("LoginDebug", "Your current uid is " + uid);
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("local_info", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("loginType", LoginType);
        editor.putString("curr_uid", uid);
        editor.apply();

        if (presenter.FirstTimeLogIn()) {
            loadFragment(new OnboardingFragment());
        } else {
            goToMainPage();
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
        requireActivity().finish();
    }
}
