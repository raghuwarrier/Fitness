package fitness.com.fitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class LinkAccountActivity extends ActionBarActivity implements View.OnClickListener{


    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_account);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        findViewById(R.id.link).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_link_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        //TODO
        //validate and updateAccountNumber in database
        if (view.getId() == R.id.link) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("infoRead" ,"true");
            editor.commit();

            Intent intent = new Intent(this, FitnessActivity.class);
            startActivity(intent);
        }
    }
}
