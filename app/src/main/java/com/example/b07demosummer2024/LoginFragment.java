package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

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
                System.out.println("Username: " + Username);
                System.out.println("Password: " + password);
                //TODO: adding check for valid password and username
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
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
