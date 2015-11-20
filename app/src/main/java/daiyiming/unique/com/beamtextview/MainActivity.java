package daiyiming.unique.com.beamtextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import daiyiming.unique.com.beamtextview.View.BeamTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BeamTextView btv_view = (BeamTextView) findViewById(R.id.btv_view);
        //分别可以通过方法或者在布局中修改属性去修改参数
        btv_view.setText("代一鸣O(∩_∩)O~~");
    }
}
