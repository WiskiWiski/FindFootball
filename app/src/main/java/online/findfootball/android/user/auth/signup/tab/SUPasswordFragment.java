package online.findfootball.android.user.auth.signup.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import online.findfootball.android.R;
import online.findfootball.android.user.auth.AuthUserObj;

/**
 * Created by Timur on 09.07.2017.
 */

public class SUPasswordFragment extends BaseSUFragment {
    private EditText editText;

    public SUPasswordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.su_fragment_password, container, false);
        editText = (EditText) rootView.findViewById(R.id.edit_text_password);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return rootView;

    }

    @Override
    public void saveResult(Object o) {
        if (editText != null) {
            AuthUserObj authUserObj = (AuthUserObj) o;
            authUserObj.setPassword(editText.getText().toString());
        }
    }

    @Override
    public void updateView(Object o) {
        if (editText != null) {
            AuthUserObj authUserObj = (AuthUserObj) o;
            editText.setText(authUserObj.getPassword());
        }
    }

    @Override
    public boolean verifyData(boolean notifyUser) {
        if (editText == null) {
            return false;
        }
        String password = String.valueOf(editText.getText());
        if (password.isEmpty()) {
            if (notifyUser) {
                Toast.makeText(getContext(), getString(R.string.su_email_frg_empty_password),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else {
            return true;
        }
        editText.requestFocus();
        return false;
    }
}
