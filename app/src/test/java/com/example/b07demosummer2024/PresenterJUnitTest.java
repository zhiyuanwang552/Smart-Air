package com.example.b07demosummer2024;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PresenterJUnitTest {
    @Mock
    Model model;
    @Mock
    LoginContract.Viewer viewer;
    @Mock
    Communication callback;
    ScreenPresenter presenter;
    @Before
    public void Setup(){
        MockitoAnnotations.initMocks(this);
        presenter = new ScreenPresenter(model);
        presenter.setViewer(viewer);
    }

    @Test
    public void testLoginFailWithEmptyUsername(){
        presenter.login("","",1,1,2,3,callback);
        verify(callback).onFalse("Username and password cannot be empty");
        verifyNoInteractions(model);
    }
    @Test
    public void testLoginFailWithEmptyPassword(){
        presenter.login("hello@gmail.com","",1,1,2,3,callback);
        verify(callback).onFalse("Username and password cannot be empty");
        verifyNoInteractions(model);
    }
    @Test
    public void testFirstTimeLoginReturnsFalse() {
        when(model.CheckFirstTime()).thenReturn(false);
        boolean result = presenter.FirstTimeLogIn();
        verify(model).CheckFirstTime();
        assertFalse(result);
    }

    @Test
    public void testLoginSuccessForParent() {

        int parentID = 1, childID = 2, providerID = 3;

        presenter.login("bob", "pass", parentID, childID, parentID, providerID, callback);
        ArgumentCaptor<UserLoginCallBack> captor =
                ArgumentCaptor.forClass(UserLoginCallBack.class);
        verify(model).login(eq("parents"), eq("bob"), eq("pass"), captor.capture());
        captor.getValue().onSuccess();
        verify(callback).onTrue("parents");
    }

    @Test
    public void testLoginSuccessForChild() {

        int parentID = 1, childID = 2, providerID = 3;

        presenter.login("bob", "pass", childID, childID, parentID, providerID, callback);
        ArgumentCaptor<UserLoginCallBack> captor =
                ArgumentCaptor.forClass(UserLoginCallBack.class);
        verify(model).login(eq("children"), eq("bob"), eq("pass"), captor.capture());
        captor.getValue().onSuccess();
        verify(callback).onTrue("children");
    }

    @Test
    public void testLoginSuccessForProvider() {

        int parentID = 1, childID = 2, providerID = 3;

        presenter.login("bob", "pass", providerID, childID, parentID, providerID, callback);
        ArgumentCaptor<UserLoginCallBack> captor =
                ArgumentCaptor.forClass(UserLoginCallBack.class);
        verify(model).login(eq("providers"), eq("bob"), eq("pass"), captor.capture());
        captor.getValue().onSuccess();
        verify(callback).onTrue("providers");
    }

    @Test
    public void testLoginFailureFromModel() {
        int parentID = 1, childID = 2, providerID = 3;

        presenter.login("bob", "pass", parentID, childID, parentID, providerID, callback);
        ArgumentCaptor<UserLoginCallBack> captor =
                ArgumentCaptor.forClass(UserLoginCallBack.class);
        verify(model).login(eq("parents"), eq("bob"), eq("pass"), captor.capture());
        captor.getValue().onError("Wrong password");
        verify(callback).onFalse("Wrong password");
    }

    @Test
    public void testSignupEmptyFields() {
        presenter.VerifySignupCredentialsCreate("", "", "", 1, 1, 2, callback);
        verify(viewer).showMessage("Username and password cannot be empty");
        verifyNoInteractions(model);
    }

    @Test
    public void testSignupPasswordMismatch() {
        presenter.VerifySignupCredentialsCreate("user", "pass1", "pass2", 1, 1, 2, callback);
        verify(viewer).showMessage("Passwords do not match");
        verifyNoInteractions(model);
    }
    @Test
    public void testSignupWeakPassword() {
        when(model.securePassword("weak")).thenReturn(false);
        presenter.VerifySignupCredentialsCreate("user", "weak", "weak", 1, 1, 2, callback);
        verify(viewer).showMessage(anyString());
    }

    @Test
    public void testSignupSuccess() {

        when(model.securePassword("Strong123!")).thenReturn(true);
        int parentID = 1, providerID = 2;
        presenter.VerifySignupCredentialsCreate("john", "Strong123!", "Strong123!",
                parentID, parentID, providerID, callback);
        ArgumentCaptor<Communication> captor = ArgumentCaptor.forClass(Communication.class);
        verify(model).addUserToDatabase(eq("john"), eq("parents"), eq("Strong123!"), captor.capture());
        captor.getValue().onTrue("Account created");
        verify(viewer).showMessage("Account created");
        verify(callback).onTrue("Account created");
    }

    @Test
    public void testSignupFailed() {

        when(model.securePassword("Strong123!")).thenReturn(true);
        int parentID = 1, providerID = 2;
        presenter.VerifySignupCredentialsCreate("john", "Strong123!", "Strong123!",
                providerID, parentID, providerID, callback);
        ArgumentCaptor<Communication> captor = ArgumentCaptor.forClass(Communication.class);
        verify(model).addUserToDatabase(eq("john"), eq("providers"), eq("Strong123!"), captor.capture());
        captor.getValue().onFalse("Account creation failed");
        verify(viewer).showMessage("Account creation failed");
        verify(callback).onFalse("Account creation failed");
    }

    @Test
    public void testResetPasswordEmpty() {
        presenter.resetPassword("", callback);
        verify(callback).onFalse("Email cannot be empty");
        verifyNoInteractions(model);
    }
    @Test
    public void testResetPasswordSuccess() {
        ArgumentCaptor<Communication> captor =
                ArgumentCaptor.forClass(Communication.class);
        presenter.resetPassword("test@email.com", callback);
        verify(model).ResetPassword(eq("test@email.com"), captor.capture());
        captor.getValue().onTrue("Reset email sent");
        verify(callback).onTrue("Reset email sent");
    }

    @Test
    public void testResetPasswordFailed() {
        ArgumentCaptor<Communication> captor =
                ArgumentCaptor.forClass(Communication.class);
        presenter.resetPassword("test@email.com", callback);
        verify(model).ResetPassword(eq("test@email.com"), captor.capture());
        captor.getValue().onFalse("Password reset email failed");
        verify(callback).onFalse("Password reset email failed");
    }
}