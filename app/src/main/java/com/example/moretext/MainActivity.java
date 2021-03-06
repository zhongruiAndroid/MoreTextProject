package com.example.moretext;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.zr.MoreTextLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

public class MainActivity extends Activity implements View.OnClickListener {
    private View btExpand;
    private CheckBox cbUseAnim;
    private View btEndTips;
    private EditText etEndTips;
    private CheckBox cbCollapseTipsNewLine;
    private CheckBox cbTipsLeft;
    private CheckBox cbTipsHidden;
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
        btExpand = findViewById(R.id.btExpand);
        btExpand.setOnClickListener(this);

        btEndTips = findViewById(R.id.btEndTips);
        btEndTips.setOnClickListener(this);

        etEndTips = findViewById(R.id.etEndTips);
        etEndTips.setText(mtl.getEllipsize());

        cbUseAnim = findViewById(R.id.cbUseAnim);
        cbUseAnim.setChecked(mtl.isUseAnim());
        cbUseAnim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mtl.setUseAnim(isChecked);
            }
        });

        cbCollapseTipsNewLine = findViewById(R.id.cbCollapseTipsNewLine);
        cbCollapseTipsNewLine.setChecked(mtl.isCollapseTipsNewLine());
        cbCollapseTipsNewLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mtl.setCollapseTipsNewLine(isChecked);
            }
        });

        cbTipsLeft = findViewById(R.id.cbTipsLeft);
        cbTipsLeft.setChecked(mtl.getCollapseTipsGravity()==MoreTextLayout.GRAVITY_LEFT);
        cbTipsLeft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mtl.setCollapseTipsGravity(isChecked?MoreTextLayout.GRAVITY_LEFT:MoreTextLayout.GRAVITY_RIGHT);
            }
        });
        cbTipsHidden = findViewById(R.id.cbTipsHidden);
        cbTipsHidden.setChecked(mtl.isCollapseTipsHidden());
        cbTipsHidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mtl.setCollapseTipsHidden(isChecked);
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
                mtl.setText("?????????????????? ???????????????????????????????????????????????????8.36?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                break;
            case R.id.bt1:
                mtl.setMinLine(1);
                break;
            case R.id.bt2:
                mtl.setMinLine(2);
                break;
            case R.id.bt3:
                mtl.setMinLine(3);
                break;
            case R.id.btExpand:
                mtl.setExpand(!mtl.isExpand());
                break;
            case R.id.btEndTips:
                mtl.setEllipsize(etEndTips.getText());
                break;
        }
    }
}
