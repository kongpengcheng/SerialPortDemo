package com.example.shen.serialportdemo.activity;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.shen.serialportdemo.R;
import com.example.shen.serialportdemo.adapter.ListViewAdapter;
import com.example.shen.serialportdemo.common.SerialHelper;
import com.example.shen.serialportdemo.android_serialport_api.SerialPortFinder;
import com.example.shen.serialportdemo.bean.ComBean;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private SerialControl serialControl;
    private SerialPortFinder mSerialPortFinder;
    private TextView tvSerialPort, tvBaudRate;
    private EditText etContent;
    private RadioButton rbHex, rbTxt;
    private PopupWindow popupWindow = null;
    private ArrayList<String> allDevices, allBaudRate, alString;
    private ListViewAdapter adapter;
    private boolean isSerialPort = true,flag=false;
    private View contentView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //启动时隐藏软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
    }

    private void initView() {
        rbHex = (RadioButton) findViewById(R.id.rb_hex);
        rbTxt = (RadioButton) findViewById(R.id.rb_txt);
        tvSerialPort = (TextView) findViewById(R.id.tv_serial_port);
        tvBaudRate = (TextView) findViewById(R.id.tv_baud_rate);
        etContent = (EditText) findViewById(R.id.et_content);

        mSerialPortFinder = new SerialPortFinder();
        allDevices = new ArrayList<String>();
        allBaudRate = new ArrayList<>();
        alString=new ArrayList<>();
        adapter = new ListViewAdapter(this, alString);
        contentView = LayoutInflater.from(this).inflate(R.layout.layout_popup_window, null);
        listView = (ListView) contentView.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        try {
            String[] entryValues = mSerialPortFinder.getAllDevicesPath();
            for (int i = 0; i < entryValues.length; i++) {
                allDevices.add(entryValues[i]);
            }
        } catch (Exception e) {
            allDevices.add("/dev/ttyS1");
        }

        String[] list = getResources().getStringArray(R.array.baudrates_name);
        for (int i = 0; i < list.length; i++) {
            allBaudRate.add(list[i]);
        }

        //弹出串口列表
        tvSerialPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSerialPort=true;
                showPopupWindow(view);
            }
        });

        //弹出波特率列表
        tvBaudRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSerialPort=false;
                showPopupWindow(view);
            }
        });


        serialControl = new SerialControl();
        //打开串口
        final ToggleButton tbOpen = (ToggleButton) findViewById(R.id.tb_open);
        tbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                serialControl.setPort(tvSerialPort.getText().toString().trim());
                serialControl.setBaudRate(tvBaudRate.getText().toString().trim());
                if (isChecked) {
                    OpenComPort(serialControl);
                } else {
                    CloseComPort(serialControl);
                }
            }
        });

        //发送
        findViewById(R.id.bt_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPortData(serialControl, etContent.getText().toString().trim());
            }
        });
    }

    private void showPopupWindow(View view) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT, true);
            //设置动画效果
            popupWindow.setAnimationStyle(R.style.AnimationFade);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        alString.clear();
        if(isSerialPort){
            alString.addAll(allDevices);
        }else{
            alString.addAll(allBaudRate);
        }
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isSerialPort) {
                    tvSerialPort.setText(alString.get(position));
                }else{
                    tvBaudRate.setText(alString.get(position));
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(view);
    }

    //串口控制类
    private class SerialControl extends SerialHelper {
        public SerialControl() {

        }

        @Override
        protected void onDataReceived(final ComBean ComRecData) {

        }
    }

    //打开串口
    private void OpenComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
        } catch (SecurityException e) {
            ShowMessage("打开串口失败:没有串口读/写权限!");
        } catch (IOException e) {
            ShowMessage("打开串口失败:未知错误!");
        } catch (InvalidParameterException e) {
            ShowMessage("打开串口失败:参数错误!");
        }
    }

    //关闭串口
    private void CloseComPort(SerialHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    //显示消息
    private void ShowMessage(String sMsg) {
        Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
    }

    //串口发送
    private void sendPortData(SerialHelper ComPort, String sOut) {
        if (ComPort != null && ComPort.isOpen()) {
            if (rbHex.isChecked()) {
                ComPort.sendHex(sOut);
            } else {
                ComPort.sendTxt(sOut);
            }
        }
    }
}
