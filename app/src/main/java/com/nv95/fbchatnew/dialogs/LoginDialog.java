package com.nv95.fbchatnew.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nv95.fbchatnew.ChatApp;
import com.nv95.fbchatnew.R;
import com.nv95.fbchatnew.utils.DayNightPalette;
import com.nv95.fbchatnew.utils.ThemeUtils;

/**
 * Created by nv95 on 11.08.16.
 */

public class LoginDialog implements View.OnClickListener {

    private final OnLoginListener mLoginListener;
    private final Dialog mDialog;
    private AutoCompleteTextView mAutoCompletteLogin;
    private EditText mEditTextPassword;
    private Button mButtonSignIn;
    private TextView mTextViewHelp;

    public LoginDialog(final Activity activity, OnLoginListener loginListener) {
        mLoginListener = loginListener;
        mDialog = new Dialog(activity);
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_login, null, false);
        mAutoCompletteLogin = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteLogin);
        mEditTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        mButtonSignIn = (Button) view.findViewById(R.id.buttonSignIn);
        mTextViewHelp = (TextView) view.findViewById(R.id.textViewHelp);

        DayNightPalette palette = ChatApp.getApplicationPalette();
        mTextViewHelp.setTextColor(palette.getAccentColor());
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
        mDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignIn:
                mDialog.dismiss();
                mLoginListener.onLogin(
                        mAutoCompletteLogin.getText().toString().trim(),
                        mEditTextPassword.getText().toString()
                );
                break;
        }
    }

    public void show(String reason) {
        mEditTextPassword.setError(reason);
        show();
    }

    public interface OnLoginListener {
        void onLogin(String email, String password);
    }
}
