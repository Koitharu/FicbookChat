package com.nv95.fbchat.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nv95.fbchat.ChatApp;
import com.nv95.fbchat.R;
import com.nv95.fbchat.utils.DayNightPalette;
import com.nv95.fbchat.utils.ThemeUtils;

/**
 * Created by nv95 on 11.08.16.
 */

public class LoginDialog implements View.OnClickListener, DialogInterface.OnClickListener {

    private final OnLoginListener mLoginListener;
    private final Dialog mDialog;
    private AutoCompleteTextView mAutoCompletteLogin;
    private EditText mEditTextPassword;
    private Button mButtonSignIn;
    private TextView mTextViewError;

    public LoginDialog(final Activity activity, OnLoginListener loginListener) {
        mLoginListener = loginListener;
        mDialog = new Dialog(activity);
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_login, null, false);
        mAutoCompletteLogin = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteLogin);
        mEditTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        mButtonSignIn = (Button) view.findViewById(R.id.buttonSignIn);
        mTextViewError = (TextView) view.findViewById(R.id.textViewError);

        DayNightPalette palette = ChatApp.getApplicationPalette();
        ThemeUtils.paintEditText(mAutoCompletteLogin, palette);
        ThemeUtils.paintEditText(mEditTextPassword, palette);

        mDialog.setContentView(view);
        if (activity instanceof DialogInterface.OnClickListener) {
            mDialog.setCancelable(true);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    ((DialogInterface.OnClickListener) activity).onClick(mDialog, DialogInterface.BUTTON_NEGATIVE);
                }
            });
        } else {
            mDialog.setCancelable(false);
        }
        mDialog.setTitle(R.string.welcome);
        mDialog.setOwnerActivity(activity);
        mButtonSignIn.setOnClickListener(this);
    }

    public void show() {
        mTextViewError.setVisibility(View.GONE);
        mDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignIn:
                if (mAutoCompletteLogin.getText().toString().trim().length() == 0) {
                    mAutoCompletteLogin.setError(view.getContext().getString(R.string.login_empty));
                } else if (mEditTextPassword.getText().toString().isEmpty()) {
                    mEditTextPassword.setError(view.getContext().getString(R.string.password_empty));
                } else {
                    RulesDialog.show(view.getContext(), this);
                }
                break;
        }
    }

    public void show(String reason) {
        mTextViewError.setText(reason);
        mTextViewError.setVisibility(View.VISIBLE);
        show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        mDialog.dismiss();
        mLoginListener.onLogin(
                mAutoCompletteLogin.getText().toString().trim(),
                mEditTextPassword.getText().toString()
        );
    }

    public interface OnLoginListener {
        void onLogin(String email, String password);
    }
}
