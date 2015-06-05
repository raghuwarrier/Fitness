package fitness.com.fitness;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.app.Activity;

public class WelcomeActivity extends ActionBarActivity implements View.OnClickListener{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Welcome");

        setSupportActionBar(toolbar);
        findViewById(R.id.next).setOnClickListener(this);

    }




    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.next) {
            Intent intent = new Intent(this, TermsAndConditionsActivity.class);
            startActivity(intent);
        }
    }


}
