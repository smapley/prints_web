package com.smapley.prints.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lvrenyang.rwbt.BTHeartBeatThread;
import com.lvrenyang.rwusb.USBHeartBeatThread;
import com.lvrenyang.rwwifi.NETHeartBeatThread;
import com.lvrenyang.utils.DataUtils;
import com.lvrenyang.utils.FileUtils;
import com.smapley.prints.R;
import com.smapley.prints.adapter.Main_Viewpage_Adapter;
import com.smapley.prints.fragment.Print;
import com.smapley.prints.fragment.Set;
import com.smapley.prints.fragment.Web;
import com.smapley.prints.print.Global;
import com.smapley.prints.print.WorkService;
import com.smapley.prints.util.CustomViewPager;
import com.smapley.prints.util.HttpUtils;
import com.smapley.prints.util.MyData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity implements View.OnClickListener {


    private TextView title_item1;
    private static TextView title_item2;
    private TextView title_item3;

    private TextView bottom_item1;
    private TextView bottom_item2;
    private TextView bottom_item3;

    private CustomViewPager viewPager;
    public View bottom;

    private Main_Viewpage_Adapter pageViewAdapter;
    public Print print;
    public Set set;
    public Web web;
    private List<Fragment> fragmentList;


    private static Handler mHandler = null;
    private static String TAG = "MainActivity";
    private final int UPDATA = 2;
    private final int UPDATA2 = 1;
    private final int UPDATA3 = 5;
    private static final int PRINT = 3;
    private static Dialog dialog;
    private Map map;
    private String allidString;

    private static String title1;


    private static int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        position=0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_item1);
        builder.setMessage(R.string.dialog_item2);
        dialog = builder.create();

        initView();
        initViewPage();
        initPrint();
    }

    private void initView() {
        title_item1 = (TextView) findViewById(R.id.title_item1);
        title_item2 = (TextView) findViewById(R.id.title_item2);
        title_item3 = (TextView) findViewById(R.id.title_item3);

        bottom_item1 = (TextView) findViewById(R.id.bottom_item1);
        bottom_item2 = (TextView) findViewById(R.id.bottom_item2);
        bottom_item3 = (TextView) findViewById(R.id.bottom_item3);

        viewPager = (CustomViewPager) findViewById(R.id.fragment);
        bottom = findViewById(R.id.main_bottom);

        title_item1.setOnClickListener(this);
        title_item3.setOnClickListener(this);

        bottom_item1.setOnClickListener(this);
        bottom_item2.setOnClickListener(this);
        bottom_item3.setOnClickListener(this);

    }

    private void initViewPage() {
        fragmentList = new ArrayList<>();
        web = new Web();
        set = new Set();
        print = new Print();
        fragmentList.add(print);
        fragmentList.add(web);
        fragmentList.add(set);
        pageViewAdapter = new Main_Viewpage_Adapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(pageViewAdapter);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_item1:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请输入编号：");
                final EditText editText = new EditText(MainActivity.this);
                builder.setView(editText);
                builder.setNegativeButton(R.string.dialog_item5, null);
                builder.setPositiveButton(R.string.dialog_item7, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String allid = editText.getText().toString();
                        String user1 = web.name;
                        Intent intent = new Intent(MainActivity.this, DuiJiang.class);
                        intent.putExtra("allid", allid);
                        intent.putExtra("user1", web.name);
                        if (allid == null || allid.equals("")) {
                            Toast.makeText(MainActivity.this, "请输入编号", Toast.LENGTH_SHORT).show();
                        } else if (user1 == null || user1.equals("")) {
                            Toast.makeText(MainActivity.this, "用户id已过期，请重新登陆", Toast.LENGTH_SHORT).show();

                        } else {
                            startActivity(intent);
                        }

                    }
                });
                builder.create().show();
                break;
            case R.id.title_item3:
                upData(web.name);
                break;
            case R.id.bottom_item1:
                viewPagerGo(0);
                title_item2.setText(title1);
                break;
            case R.id.bottom_item2:
                viewPagerGo(1);
                title_item2.setText(web.url);
                break;
            case R.id.bottom_item3:
                viewPagerGo(2);
                title_item2.setText(title1);
                break;
        }
    }

    public void viewPagerGo(int num) {
        position=num;
        if (num == 0) {
            print.getData();
            title_item1.setVisibility(View.VISIBLE);
            title_item3.setVisibility(View.VISIBLE);
        } else {
            title_item1.setVisibility(View.GONE);
            title_item3.setVisibility(View.GONE);
        }
        if (num == 1) {
            web.initData();
        }
        viewPager.setCurrentItem(num);
    }

    private void initPrint() {
        // 初始化字符串资源
        InitGlobalString();
        mHandler = new MHandler(MainActivity.this);
        WorkService.addHandler(mHandler);

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }
    }

    private void InitGlobalString() {
        Global.toast_success = getString(R.string.toast_success);
        Global.toast_fail = getString(R.string.toast_fail);
        Global.toast_notconnect = getString(R.string.toast_notconnect);
        Global.toast_usbpermit = getString(R.string.toast_usbpermit);
    }


    private void upData2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("allid", allidString);
                mhandler.obtainMessage(UPDATA3, HttpUtils.updata(map, MyData.URL_updateZt1)).sendToTarget();
            }
        }).start();
    }


    private void upData(final String updata) {
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("user1", updata);
                mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_GETJILU1)).sendToTarget();
            }
        }).start();
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_item1);
            try {
                switch (msg.what) {
                    case UPDATA3:
                        Map resultmap = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(resultmap.get("newid").toString()) > 0) {
                            print.getData();
                        }
                        break;

                    case UPDATA2:
                        dialog.dismiss();
                        Map result = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(result.get("newid").toString()) > 0) {
                            Toast.makeText(MainActivity.this, "更新数据成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "没有数据需要更新！", Toast.LENGTH_SHORT).show();

                        }
                        break;
                    case UPDATA:
                        dialog.dismiss();

                        map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(map.get("count").toString()) > 0) {

                            mhandler.obtainMessage(PRINT).sendToTarget();

                        } else {
                            builder.setMessage(R.string.dialog_item3);
                            builder.setPositiveButton(R.string.dialog_item7, null);
                            dialog = builder.create();
                            dialog.show();
                        }
                        break;

                    case PRINT:
                        builder.setMessage(R.string.dialog_item4);
                        dialog = builder.create();
                        dialog.show();
                        allidString = map.get("allid").toString();
                        String allid = "编号：" + allidString;
                        String riqi = "日期：" + map.get("riqi").toString();
                        String name = "会员：" + map.get("ming").toString();
                        String qihao = "第" + web.qihao + "期，3天内有效！！";
                        String allnum = " 笔数 " + map.get("count") + "  总金额 " + map.get("allgold") + "元";
                        String lin = " ";
                        String lin2 = "————————————————————————————————";
                        String dataString = "1元头奖回扣100元";
                        List<Map<String, String>> list = JSON.parseObject(map.get("result").toString(), new TypeReference<List<Map<String, String>>>() {
                        });
                        Map<String, String> basemap = new HashMap();

                        basemap.put("number", "号码");
                        basemap.put("gold", "金额");
                        basemap.put("pei", "赔率");

                        list.add(0, basemap);


                        byte[] setHT = {0x1b, 0x44, 0x01, 0x0b, 0x17, 0x00, 0x1b, 0x61, 0x00, 0x1b, 0x39, 0x01, 0x1b, 0x21, 0x0c, 0x1b, 0x33, 0x30};
                        byte[] text1 = new byte[]{0x1b, 0x40, 0x1b, 0x61, 0x01, 0x1b, 0x21, 0x0c, 0x1b, 0x39, 0x01, 0x1b, 0x33, 0x30};
                        byte[] text0 = new byte[]{0x1b, 0x40, 0x1b, 0x61, 0x00, 0x1b, 0x21, 0x04, 0x1b, 0x39, 0x01, 0x1b, 0x33, 0x20};
                        byte[] text4 = new byte[]{0x1b, 0x40, 0x1b, 0x61, 0x00, 0x1b, 0x21, 0x04, 0x1b, 0x39, 0x01, 0x1b, 0x33, 0x10};
                        byte[] text3 = new byte[]{0x1b, 0x40, 0x1b, 0x61, 0x01, 0x1b, 0x21, 0x04, 0x1b, 0x39, 0x01, 0x1b, 0x33, 0x10};
                        byte[] HT = {0x09};
                        byte[] LF = {0x0d, 0x0a};

                        int num = 13;
                        int size = num + list.size() * 7 + 17;
                        byte[][] databuf = new byte[size][];


                        databuf[0] = text0;
                        databuf[1] = riqi.getBytes();
                        databuf[2] = LF;
                        databuf[3] = name.getBytes("UTF-8");
                        databuf[4] = LF;
                        databuf[5] = allid.getBytes();
                        databuf[6] = LF;
                        databuf[7] = dataString.getBytes();
                        databuf[8] = LF;
                        databuf[9] = text3;
                        databuf[10] = lin2.getBytes();
                        databuf[11] = LF;
                        databuf[12] = text1;


                        for (int i = 0; i < list.size(); i++) {

                            databuf[i * 7 + num] = setHT;
                            databuf[i * 7 + num + 1] = list.get(i).get("number").getBytes();
                            databuf[i * 7 + num + 2] = HT;
                            databuf[i * 7 + num + 3] = list.get(i).get("gold").getBytes();
                            databuf[i * 7 + num + 4] = HT;
                            String data = list.get(i).get("pei");
                            if (i != 0) {
                                data = "1:" + data;
                            }
                            Log.i("pei", data.toString() + "---->>" + data.getBytes());
                            for (; data.length() < 4; ) {
                                data += " ";
                            }
                            databuf[i * 7 + num + 5] = data.getBytes("UTF-8");
                            databuf[i * 7 + num + 6] = LF;
                            Log.i(TAG, "1");
                        }
                        Log.i(TAG, "1");

                        databuf[size - 17] = text4;
                        databuf[size - 16] = LF;
                        databuf[size - 15] = allnum.getBytes();
                        databuf[size - 14] = LF;
                        databuf[size - 13] = text3;
                        databuf[size - 12] = lin2.getBytes();
                        databuf[size - 11] = LF;
                        databuf[size - 10] = qihao.getBytes();
                        databuf[size - 9] = LF;
                        databuf[size - 8] = lin.getBytes();
                        databuf[size - 7] = LF;
                        databuf[size - 6] = lin.getBytes();
                        databuf[size - 5] = LF;
                        databuf[size - 4] = lin.getBytes();
                        databuf[size - 3] = LF;
                        databuf[size - 2] = lin.getBytes();
                        databuf[size - 1] = LF;
                        Log.i(TAG, "1");


                        byte[] buf = DataUtils.byteArraysToBytes(databuf);

                        if (WorkService.workThread.isConnected()) {
                            Bundle data = new Bundle();
                            data.putByteArray(Global.BYTESPARA1, buf);
                            data.putInt(Global.INTPARA1, 0);
                            data.putInt(Global.INTPARA2, buf.length);
                            WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                        } else {
                            dialog.dismiss();
                            builder.setMessage(R.string.dialog_item10);
                            builder.setNegativeButton(R.string.dialog_item5, null);
                            dialog = builder.create();
                            dialog.show();
                            Toast.makeText(MainActivity.this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            } catch (Exception e) {
            }
        }
    };

    static class MHandler extends Handler {

        WeakReference<MainActivity> mActivity;

        MHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity theActivity = mActivity.get();
            switch (msg.what) {
                case Global.CMD_POS_WRITERESULT: {
                    dialog.dismiss();

                    int result = msg.arg1;
                    if (result == 1) {
                        Toast.makeText(theActivity,R.string.dialog_item8,Toast.LENGTH_SHORT).show();

                        theActivity.upData2();

                    } else {
                        Toast.makeText(theActivity,R.string.dialog_item9,Toast.LENGTH_SHORT).show();

                    }

                    Log.v(TAG, "Result: " + result);
                    break;
                }
                /**
                 * DrawerService 的 onStartCommand会发送这个消息
                 */
                case Global.MSG_ALLTHREAD_READY: {
                    Log.v("MHandler", "MSG_ALLTHREAD_READY");
                    if (WorkService.workThread.isConnected()) {
                        title1=theActivity.getString(R.string.print1);
                    } else {
                        title1=theActivity.getString(R.string.print0);
                    }
                    if(position!=1){
                        title_item2.setText(title1);
                    }
                    FileUtils.AddToFile("MHandler MSG_ALLTHREAD_READY\r\n",
                            FileUtils.sdcard_dump_txt);
                    break;
                }

                case BTHeartBeatThread.MSG_BTHEARTBEATTHREAD_UPDATESTATUS:
                case NETHeartBeatThread.MSG_NETHEARTBEATTHREAD_UPDATESTATUS:
                case USBHeartBeatThread.MSG_USBHEARTBEATTHREAD_UPDATESTATUS: {
                    int statusOK = msg.arg1;
                    int status = msg.arg2;
                    Log.v(TAG,
                            "statusOK: " + statusOK + " status: "
                                    + DataUtils.byteToStr((byte) status));
                    if (statusOK == 1){
                        title1=theActivity.getString(R.string.print1);
                } else {
                    title1=theActivity.getString(R.string.print0);
                }
                if(position!=1){
                    title_item2.setText(title1);
                }

                    FileUtils.DebugAddToFile("statusOK: " + statusOK + " status: "
                                    + DataUtils.byteToStr((byte) status) + "\r\n",
                            FileUtils.sdcard_dump_txt);
                    break;
                }

                case Global.CMD_POS_PRINTPICTURERESULT:
                case Global.CMD_POS_WRITE_BT_FLOWCONTROL_RESULT: {
                    int result = msg.arg1;
                    Log.v(TAG, "Result: " + result);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
    }
}
