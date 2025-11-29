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

public class SignUpVerificationFragment extends Fragment implements LoginContract.Viewer{

    private static final String ARG_EMAIL = "email";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_USER_TYPE = "userType";

    ScreenPresenter screenpresenter;

    public static SignUpVerificationFragment newInstance(String email, String password, String userType) {
        SignUpVerificationFragment fragment = new SignUpVerificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PASSWORD, password);
        args.putString(ARG_USER_TYPE, userType);
        fragment.setArguments(args); // Attach the bundle to the fragment
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_verification, container, false);

        screenpresenter = new ScreenPresenter(new Model());
        screenpresenter.setViewer(this);

        Bundle arguments = getArguments();
        if(arguments != null){
            String password = arguments.getString(ARG_PASSWORD);
            String usertype = arguments.getString(ARG_USER_TYPE);
            String email = arguments.getString(ARG_EMAIL);
        }

        EditText verification_code = view.findViewById(R.id.VerifyCode);
        ImageButton verify_button = view.findViewById(R.id.imageButton6);
        ImageButton back_button = view.findViewById(R.id.imageButton4);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new LoginFragment());
            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verification = verification_code.getText().toString();
                //TODO: adding check for valid verification code
                if(screenpresenter.verification(verification)){
                    loadFragment(new SuccessfulActionFragment());

                }else showErrorMessage("Incorrect Verification code");
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
