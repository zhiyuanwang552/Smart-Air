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

public class NewPasswordFragment extends Fragment implements LoginContract.Viewer{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_password, container, false);

        EditText NewPassword = view.findViewById(R.id.LoginPassword);
        EditText ReenterPassword = view.findViewById(R.id.reenterPassword);
        ImageButton BackButton = view.findViewById(R.id.imageButton7);
        ImageButton PasswordResetButton = view.findViewById(R.id.imageButton8);

        PasswordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String NewPasswordText = NewPassword.getText().toString();
                String ReenterPasswordText = ReenterPassword.getText().toString();
                //TODO Check for valid password

                loadFragment(new SuccessfulActionFragment());

            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new LoginFragment());
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
