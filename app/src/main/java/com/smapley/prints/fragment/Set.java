package com.smapley.prints.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.smapley.prints.R;
import com.smapley.prints.activity.SearchBTActivity;

/**
 * Created by smapley on 15/10/23.
 */
public class Set extends Fragment {

    private TextView item0;
    private TextView item1;
    private TextView item2;
    private TextView item3;
    private TextView menu1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.set,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        item0 = (TextView) view.findViewById(R.id.set_item0);
        item1 = (TextView) view.findViewById(R.id.set_item1);
        item2 = (TextView) view.findViewById(R.id.set_item2);
        item3 = (TextView) view.findViewById(R.id.set_item3);
        item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("请输入网址:");
                final EditText editText =new EditText(getActivity());
                builder.setView(editText);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("URL",getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("url", editText.getText().toString());
                        editor.commit();
                    }
                });
                builder.create().show();
            }
        });
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchBTActivity.class));
            }
        });
        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}
