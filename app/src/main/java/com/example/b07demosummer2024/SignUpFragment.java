package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SignUpFragment extends Fragment implements LoginContract.Viewer{
    ScreenPresenter presenter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        EditText EmailEditText = view.findViewById(R.id.editTextTextEmailAddress2);
        EditText PasswordEditText = view.findViewById(R.id.editTextTextPassword);
        EditText ReenterPasswordEditText = view.findViewById(R.id.editTextTextPassword2);
        RadioGroup UserTypeRadioGroup = view.findViewById(R.id.userTypeRadioGroup);
        presenter = new ScreenPresenter(new Model());
        presenter.setViewer(this);

        ImageButton SignUpButton = view.findViewById(R.id.imageButton2);
        ImageButton BackButton = view.findViewById(R.id.imageButton);


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new LoginFragment());
            }
        });

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = EmailEditText.getText().toString();
                String Password = PasswordEditText.getText().toString();
                String ReenterPassword = ReenterPasswordEditText.getText().toString();
                String UserType;
                int SelectedUserType = UserTypeRadioGroup.getCheckedRadioButtonId();
                presenter.VerifySignupCredentialsCreate(Email,Password,ReenterPassword,
                        SelectedUserType,R.id.radioButton,R.id.radioButton2,new Communication(){
                            @Override
                            public void onTrue(String message) {
                                loadFragment(new SuccessfulActionFragment());
                            }
                            @Override
                            public void onFalse(String message){}

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
