package fitness.com.fitness;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.xmlpull.v1.XmlPullParserException;
import java.net.URL;
import java.net.HttpURLConnection;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class TermsAndConditionsActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static final String TAG = "TermsAndConditions";

    private Toolbar toolbar;

    private GoogleApiClient mGoogleApiClient;


    //* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;


    Switch switchbtn;

    /**
     * True if the sign-in button was clicked.  When true, we know to resolve all
     * issues preventing sign-in without waiting.
     */
    private boolean mSignInClicked;

    private boolean mIntentInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandconditions);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
       SignInButton button = (SignInButton)findViewById( R.id.plus_sign_in_button);
        button.setEnabled(false);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        switchbtn = (Switch)findViewById(R.id.swtch);
        Log.i(TAG, (String) switchbtn.getTextOff());
        switchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    Log.d("onchange", String.valueOf(isChecked));
                    findViewById(R.id.plus_sign_in_button).setEnabled(true);
                } else {
                    findViewById(R.id.plus_sign_in_button).setEnabled(false);
                    Log.d("onchange", String.valueOf(isChecked));
                }
            }
        });
        applyStyle(switchbtn.getTextOn(), switchbtn.getTextOff());


        findViewById(R.id.plus_sign_in_button).setOnClickListener(this);
    }

    public void applyStyle(CharSequence switchTxtOn, CharSequence switchTxtOff){

        Spannable styleText = new SpannableString(switchTxtOn);
        StyleSpan style = new StyleSpan(Typeface.NORMAL);
        styleText.setSpan(style, 0, switchTxtOn.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        styleText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, switchTxtOn.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        switchbtn.setTextOn(styleText);

        styleText = new SpannableString(switchTxtOff);
        styleText.setSpan(style, 0, switchTxtOff.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        styleText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, switchTxtOff.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        switchbtn.setTextOff(styleText);

    }




    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.plus_sign_in_button && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }


    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG,"Connected");
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LinkAccountActivity.class);
        startActivity(intent);

    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        }
    }

    public void toggled(View view){
        ToggleButton toggleButton = (ToggleButton)view;
        if(toggleButton.isChecked()){
            toggleButton.setBackground(getResources().getDrawable(R.drawable.toggle_on));
            findViewById(R.id.plus_sign_in_button).setEnabled(true);
        }else {
            toggleButton.setBackground(getResources().getDrawable(R.drawable.toggle_off));
            findViewById(R.id.plus_sign_in_button).setEnabled(false);
        }
    }
}
