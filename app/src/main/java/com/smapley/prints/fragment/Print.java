package com.smapley.prints.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.prints.R;
import com.smapley.prints.activity.MainActivity;
import com.smapley.prints.listview.SwipeMenu;
import com.smapley.prints.listview.SwipeMenuCreator;
import com.smapley.prints.listview.SwipeMenuItem;
import com.smapley.prints.listview.SwipeMenuListView;
import com.smapley.prints.util.HttpUtils;
import com.smapley.prints.util.MyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smapley on 15/10/23.
 */
public class Print extends Fragment implements View.OnClickListener {

    private View keybord;
    private TextView back;
    private TextView nowText;
    private TextView numText;
    private TextView jineText;
    private RelativeLayout numLayout;
    private RelativeLayout jinLayout;
    private SwipeMenuListView listView;
    private TextView numCart;
    private TextView jinCart;
    private TextView keyitem1;
    private TextView keyitem2;
    private TextView keyitem3;
    private TextView keyitem4;
    private TextView keyitem5;
    private TextView keyitem6;
    private TextView keyitem7;
    private TextView keyitem8;
    private TextView keyitem9;
    private TextView keyitem10;
    private TextView keyitem11;
    private TextView keyitem12;
    private TextView keyitem13;
    private TextView keyitem14;
    private TextView keyitem15;
    private TextView tag;
    private final int UPDATA = -1;
    private final int GETDATA = -2;
    private String number;
    private String money;
    private List<Map<String, String>> dataList = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private ProgressDialog dialog;
    private boolean jine = false;
    private int xian = 0;
    private int dao = 0;
    private Map<String, String> baseMap = new HashMap<>();
    private SharedPreferences sharedPreferences;

    private boolean hasPoint = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.print, container, false);
        sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("提示：");
        baseMap.put("number", "号码");
        baseMap.put("gold", "金额");
        baseMap.put("pei", "赔率");
        dataList.add(baseMap);
        initView(view);
        getData();
        return view;
    }

    private void initView(View view) {
        back = (TextView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keybord.setVisibility(View.GONE);
                ((MainActivity) getActivity()).bottom.setVisibility(View.VISIBLE);

            }
        });
        numLayout = (RelativeLayout) view.findViewById(R.id.table_item1_layout);
        jinLayout = (RelativeLayout) view.findViewById(R.id.table_item2_layout);
        numCart = (TextView) view.findViewById(R.id.table_item1_clo);
        jinCart = (TextView) view.findViewById(R.id.table_item2_clo);
        keybord = view.findViewById(R.id.print_keybord);
        numText = (TextView) view.findViewById(R.id.num_text);
        jineText = (TextView) view.findViewById(R.id.jine_text);
        numLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNumText("");
            }
        });
        jinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goJinText(false);
                jineText.setText("");
            }
        });

        keyitem1 = (TextView) view.findViewById(R.id.key_item1);
        keyitem2 = (TextView) view.findViewById(R.id.key_item2);
        keyitem3 = (TextView) view.findViewById(R.id.key_item3);
        keyitem4 = (TextView) view.findViewById(R.id.key_item4);
        keyitem5 = (TextView) view.findViewById(R.id.key_item5);
        keyitem6 = (TextView) view.findViewById(R.id.key_item6);
        keyitem7 = (TextView) view.findViewById(R.id.key_item7);
        keyitem8 = (TextView) view.findViewById(R.id.key_item8);
        keyitem9 = (TextView) view.findViewById(R.id.key_item9);
        keyitem10 = (TextView) view.findViewById(R.id.key_item10);
        keyitem11 = (TextView) view.findViewById(R.id.key_item11);
        keyitem12 = (TextView) view.findViewById(R.id.key_item12);
        keyitem13 = (TextView) view.findViewById(R.id.key_item13);
        keyitem14 = (TextView) view.findViewById(R.id.key_item14);
        keyitem15 = (TextView) view.findViewById(R.id.key_item15);
        tag = (TextView) view.findViewById(R.id.text_tag);

        keyitem1.setOnClickListener(this);
        keyitem2.setOnClickListener(this);
        keyitem3.setOnClickListener(this);
        keyitem4.setOnClickListener(this);
        keyitem5.setOnClickListener(this);
        keyitem6.setOnClickListener(this);
        keyitem7.setOnClickListener(this);
        keyitem8.setOnClickListener(this);
        keyitem9.setOnClickListener(this);
        keyitem10.setOnClickListener(this);
        keyitem11.setOnClickListener(this);
        keyitem12.setOnClickListener(this);
        keyitem13.setOnClickListener(this);
        keyitem14.setOnClickListener(this);
        keyitem15.setOnClickListener(this);


        listView = (SwipeMenuListView) view.findViewById(R.id.listView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                if (position != 0) {
                    final Map item = dataList.get(position);
                    switch (index) {
                        case 0:
                            dialog.setMessage("正在撤销投注。。。");
                            dialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HashMap map = new HashMap();
                                    map.put("id", item.get("id").toString());
                                    map.put("murl", Web.url);
                                    map.put("ip", Web.ip);
                                    map.put("cookie", Web.CookieStr);
                                    map.put("formhash", Web.formhash);
                                    map.put("user1", Web.name);
                                    map.put("qishu", Web.qihao);


                                    mhandler.obtainMessage(position, HttpUtils.updata(map, MyData.URL_TUIMA)).sendToTarget();
                                }
                            }).start();


                            break;
                    }

                }
                return false;
            }
        });


        simpleAdapter = new SimpleAdapter(getActivity(), dataList, R.layout.list_item,
                new String[]{"number", "gold", "pei"},
                new int[]{R.id.list_item1, R.id.list_item2, R.id.list_item3});
        listView.setDivider(null);
        listView.setAdapter(simpleAdapter);
    }

    private void goJinText(boolean tag) {
        hasPoint = false;
        nowText = jineText;
        jine = tag;
        if (jine) {
            nowText.setBackgroundColor(getResources().getColor(R.color.back3));
        } else {
            nowText.setBackgroundColor(getResources().getColor(R.color.back3));
        }
        keybord.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).bottom.setVisibility(View.GONE);
        keybord.setBackgroundResource(R.color.back2);
        numCart.setVisibility(View.GONE);
        jinCart.setVisibility(View.VISIBLE);
        listView.smoothScrollToPosition(simpleAdapter.getCount() - 1);
    }

    private void goNumText(String text) {
        hasPoint = false;
        nowText = numText;
        nowText.setText(text);
        keybord.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).bottom.setVisibility(View.GONE);
        keybord.setBackgroundResource(R.color.bac1);
        numCart.setVisibility(View.VISIBLE);
        jinCart.setVisibility(View.GONE);
        listView.smoothScrollToPosition(simpleAdapter.getCount() - 1);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.key_item4:
                if (xian == 0) {
                    tag.setText("现");
                    xian = 1;
                    dao = 0;
                } else {
                    tag.setText(" ");
                    xian = 0;
                }

                break;
            case R.id.key_item8:
                if (dao == 0) {
                    tag.setText("倒");
                    xian = 0;
                    dao = 1;
                } else {
                    tag.setText(" ");
                    dao = 0;
                }
                break;
            case R.id.key_item14:

                if (nowText == numText) {
                    if (nowText.getText().length() < 3) {
                        nowText.setText(nowText.getText().toString() + ((TextView) view).getText().toString());
                    } else {
                        nowText.setText(nowText.getText().toString() + ((TextView) view).getText().toString());
                        goJinText(true);
                    }
                }

                break;


            case R.id.key_item12:
                upData();
                goNumText("");
                xian = 0;
                dao = 0;
                tag.setText(" ");

                break;
            case R.id.key_item15:
                if (nowText == jineText && !jine && !hasPoint) {
                    nowText.setText(nowText.getText().toString() + ((TextView) view).getText().toString());
                    hasPoint = true;
                }
                break;

            default:
                if (nowText == numText && nowText.getText().length() < 3 || nowText == jineText) {
                    if (jine) {
                        nowText.setBackgroundColor(getResources().getColor(R.color.back3));
                        nowText.setText(((TextView) view).getText().toString());
                        jine = false;
                    } else {
                        nowText.setText(nowText.getText().toString() + ((TextView) view).getText().toString());
                    }
                } else if (nowText.length() == 3) {
                    nowText.setText(nowText.getText().toString() + ((TextView) view).getText().toString());
                    goJinText(true);
                }
                break;


        }

    }

    public void getData() {
        Log.i("getdata", "----->>1");

        final String name = sharedPreferences.getString("name", Web.name);
        Log.i("getdata", "----->>2" + name);
        if (name != null && !name.equals("")) {
            Log.i("getdata", "----->>3" + name);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap map = new HashMap();
                    map.put("user1", name);
                    mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETJILU1)).sendToTarget();
                }
            }).start();
        }

    }

    private void upData() {
        number = numText.getText().toString();
        money = jineText.getText().toString();
        final int sizixian = xian;
        final int zhuan = dao;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("number", number);
                map.put("money", money);
                map.put("ip", Web.ip);
                map.put("user1", Web.name);
                map.put("qishu", Web.qihao);
                map.put("cookie", Web.CookieStr);
                map.put("murl", Web.url);
                map.put("sizixian", sizixian);
                map.put("zhuan", zhuan);
                mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_INDEX)).sendToTarget();
            }
        }).start();
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                dialog.dismiss();
                switch (msg.what) {
                    case GETDATA:
                        Map map1 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(map1.get("count").toString()) > 0) {
                            List<Map<String, String>> list = JSON.parseObject(map1.get("result").toString(), new TypeReference<List<Map<String, String>>>() {
                            });
                            dataList.clear();
                            dataList.add(baseMap);
                            dataList.addAll(list);
                            simpleAdapter.notifyDataSetChanged();
                            listView.smoothScrollToPosition(simpleAdapter.getCount() - 1);
                        } else if (Integer.parseInt(map1.get("count").toString()) == 0) {
                            dataList.clear();
                            dataList.add(baseMap);
                            simpleAdapter.notifyDataSetChanged();
                        }
                        break;
                    case UPDATA:

                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(map.get("count").toString()) > 0) {
                            List<Map> list = JSON.parseObject(map.get("result").toString(), new TypeReference<List<Map>>() {
                            });
                            for (int i = 0; i < list.size(); i++) {
                                Map resultmap = list.get(i);
                                Map dataMap = new HashMap();
                                dataMap.put("count", map.get("count").toString());
                                dataMap.put("allgold", map.get("allgold").toString());
                                dataMap.put("allid", map.get("allid").toString());
                                dataMap.put("number", resultmap.get("number").toString());
                                dataMap.put("gold", resultmap.get("gold").toString());
                                dataMap.put("pei", resultmap.get("pei").toString());
                                dataMap.put("id", resultmap.get("id").toString());
                                dataList.add(dataMap);
                            }
                            simpleAdapter.notifyDataSetChanged();
                            listView.smoothScrollToPosition(simpleAdapter.getCount() - 1);
                            getData();


                        } else {
                            Toast.makeText(getActivity(), "下注失败，请重新登录", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    default:

                        int result = JSON.parseObject(msg.obj.toString(), new TypeReference<Integer>() {
                        });
                        switch (result) {
                            case 1:
                                Toast.makeText(getActivity(), "退码成功！", Toast.LENGTH_SHORT).show();
                                dataList.remove(msg.what);
                                simpleAdapter.notifyDataSetChanged();
                                getData();

                                break;

                            case -2:
                                Toast.makeText(getActivity(), "退码已过期！", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), "退码失败！", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
