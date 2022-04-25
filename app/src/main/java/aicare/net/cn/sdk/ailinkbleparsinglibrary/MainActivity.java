package aicare.net.cn.sdk.ailinkbleparsinglibrary;

import android.os.Bundle;
import android.util.Log;

import com.besthealth.bhBodyComposition120.BhBodyComposition;

import androidx.appcompat.app.AppCompatActivity;
import cn.net.aicare.algorithmutil.BodyFatData;
import cn.net.aicare.modulelibrary.module.utils.AicareBleConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BodyFatData bodyFatData = AicareBleConfig.getBodyFatData(0, 1, 25, 65, 170, 500);
        Log.i("TAG",bodyFatData.toString());
        Log.i("TAG", new BhBodyComposition().getBodyComposition()+"");
    }
}
