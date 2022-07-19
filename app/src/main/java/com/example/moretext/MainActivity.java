package com.example.moretext;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.zr.MoreTextLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

public class MainActivity extends Activity implements View.OnClickListener {
    private AppCompatCheckBox cb;
    private Button btSetText;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private TextView tvContent;
    private TextView tvTest;
    private MoreTextLayout mtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
    }

    private void initView() {
        mtl = findViewById(R.id.mtl);
        cb = (AppCompatCheckBox) findViewById(R.id.cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    mtl.expand ();
                } else {
//                    mtl.collapse ();
                }
            }
        });
        btSetText = (Button) findViewById(R.id.btSetText);
        btSetText.setOnClickListener(this);

        bt1 = (Button) findViewById(R.id.bt1);
        bt1.setOnClickListener(this);

        bt2 = (Button) findViewById(R.id.bt2);
        bt2.setOnClickListener(this);

        bt3 = (Button) findViewById(R.id.bt3);
        bt3.setOnClickListener(this);

        tvContent = findViewById(R.id.tvContent);
        tvContent.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSetText:
                mtl.setText("在此前比亚迪海豚的预售发布会上，比亚迪就曾向媒体以及消费者介绍了海洋生物系列的产品规划。海洋系列是比亚迪在未来着重发力的产品序列，聚焦纯电汽车市场，旨在为消费者提供更时尚、更个性、更符合年轻人审美的车型选择。目前，海洋系列的首款车型——比亚迪海豚已于成都车展期间正式上市，售价区间为9.38万-12.18万元");
//                SimpleDialog.showDialog(this);
//                mtl.setText("在此前比亚迪海豚的预售发布会上，比亚迪就曾向媒体以及消费者介绍了海洋生物系列的产品规划。海洋系列是比亚迪在未来着重发力的产品序列，聚焦纯电汽车市场，旨在为消费者提供更时尚、更个性、更符合年轻人审美的车型选择。目前，海洋系列的首款车型——比亚迪海豚已于成都车展期间正式上市，售价区间为9.38万-12.18万元");
                break;
            case R.id.bt1:
//                mtl.setMaxLines(1);
                break;
            case R.id.bt2:
//                mtl.setMaxLines(2);
                break;
            case R.id.bt3:
//                mtl.setMaxLines(3);
                break;
            case R.id.tvContent:
                Log.i("=====","=====tvContent1");
//                mtl.setMaxLines(3);
                tvContent.setText("在此前比亚迪海豚的预售发布会上，比亚迪就曾向媒体以及消费者介绍了海洋生物系列的产品规划。海洋系列是比亚迪在未来着重发力的产品序列，聚焦纯电汽车市场，旨在为消费者提供更时尚、更个性、更符合年轻人审美的车型选择。目前，海洋系列的首款车型——比亚迪海豚已于成都车展期间正式上市，售价区间为9.38万-12.18万元");
                Log.i("=====","=====tvContent2");
                break;
        }
//        mtl.complete();
    }
}
