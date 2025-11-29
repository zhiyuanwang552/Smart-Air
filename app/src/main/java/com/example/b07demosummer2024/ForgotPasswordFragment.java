package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ForgotPasswordFragment extends Fragment implements LoginContract.Viewer {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ImageButton BackButton = view.findViewById(R.id.imageButton4);
        ImageButton GetVerificationCode = view.findViewById(R.id.imageButton5);
        ImageButton VerifyCode = view.findViewById(R.id.imageButton6);
        EditText Email = view.findViewById(R.id.LoginEmailAddress);
        EditText VerificationCode = view.findViewById(R.id.VerifyCode);

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new LoginFragment());
            }
        });

        GetVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailAddress = Email.getText().toString();
                //TODO: add sending email code
            }
        });

        VerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailAddress = Email.getText().toString();
                String VerificationCodeText = VerificationCode.getText().toString();
                //TODO: adding check for valid verification code
                loadFragment(new NewPasswordFragment());
            }
        });

        return view;

    }
    @Override public void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
