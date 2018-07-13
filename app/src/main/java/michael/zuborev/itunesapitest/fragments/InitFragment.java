package michael.zuborev.itunesapitest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import michael.zuborev.itunesapitest.MainActivity;
import michael.zuborev.itunesapitest.R;

//This fragment appears when user open the app
public class InitFragment extends Fragment implements TextView.OnEditorActionListener {

    private static final String LOG_TAG = InitFragment.class.getSimpleName();

    TextInputEditText mEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_init, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(LOG_TAG, "Adding editText");
        mEditText = getActivity().findViewById(R.id.textinputedittext_start_screen);
        mEditText.setOnEditorActionListener(this);
        Log.d(LOG_TAG, "EditText has been added & onEditorActionListener has been set");
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.d(LOG_TAG, "Key has been pressed. KeyCode is " + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Log.d(LOG_TAG, "KeyCode is \'Enter\'. Starting fragment change process");
            Log.d(LOG_TAG, "Creating fragment manager");
            FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            Log.d(LOG_TAG, "Fragment manager has been created. Starting replace fragment transaction");

            Log.d(LOG_TAG, "Adding search inquiry in the new fragment");
            Bundle mBundle = new Bundle();
            mBundle.putString("search", mEditText.getText().toString());
            ScrollFragment mFragment = new ScrollFragment();
            mFragment.setArguments(mBundle);
            Log.d(LOG_TAG, "Bundle has been prepared");
            mFragmentTransaction.replace(R.id.fragment_placeholder, mFragment);
            Log.d(LOG_TAG, "Committing");
            mFragmentTransaction.commit();
            Log.d(LOG_TAG, "Committed");
            return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEditText.setInputType(InputType.TYPE_NULL);
    }

}
