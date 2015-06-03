package fitness.com.fitness;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    Switch switchbtn;

    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchbtn = (Switch)findViewById(R.id.swtch);
        switchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    Log.d("onchange", String.valueOf(isChecked));
                }else{
                    Log.d("onchange", String.valueOf(isChecked));
                }
            }
        });
        applyStyle(switchbtn.getTextOn(), switchbtn.getTextOff());

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

    public void togglestatehandler(View v){
        Switch switchbtn = (Switch)v;
        boolean isChecked = switchbtn.isChecked();

        if(isChecked){
            Toast.makeText(context, "STARTED......", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "STOPPED......", Toast.LENGTH_SHORT).show();
        }

    }
}
