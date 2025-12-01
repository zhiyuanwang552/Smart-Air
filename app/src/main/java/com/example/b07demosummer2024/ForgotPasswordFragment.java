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
    ScreenPresenter presenter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ImageButton BackButton = view.findViewById(R.id.imageButton4);
        ImageButton VerifyEmail = view.findViewById(R.id.imageButton6);
        EditText Email = view.findViewById(R.id.LoginEmailAddress);
        presenter = new ScreenPresenter(new Model());
        presenter.setViewer(this);

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new LoginFragment());
            }
        });


        VerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailAddress = Email.getText().toString();
                presenter.resetPassword(EmailAddress, new Communication(){
                    @Override
                    public void onTrue(String msg){
                        showMessage(msg);
                        loadFragment(new SuccessfulActionFragment());
                    }
                    @Override
                    public void onFalse(String msg){
                        showMessage(msg);
                    }
                });
            }
        });

        return view;

    }
    @Override public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
