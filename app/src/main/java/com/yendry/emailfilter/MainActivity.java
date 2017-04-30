package com.yendry.emailfilter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button search;
    private EditText editText;
    private ProgressDialog dialog;
    private ListView listView;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        textView = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.lv);
        editText = (EditText) findViewById(R.id.editText);
        search = (Button) findViewById(R.id.button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread(new MyThread());
                thread.start();
               dialog.show();
                hideSoftKeyboard();
            }
        });


    }

    public void html2text(String url) {
        final List list = new ArrayList<>();
        final List listFiltered = new ArrayList<>();


        Document doc = null;
        try {
            doc = Jsoup.connect("http://www." + url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc != null) {
            Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(doc.outerHtml());
            while (m.find()) {
                list.add(m.group());
            }
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).toString().contains("@" + url)) {
                listFiltered.add(list.get(i));
            }
        }
        Set<String> cleanList = new HashSet<String>(listFiltered);
        listFiltered.clear();
        listFiltered.addAll(cleanList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listFiltered.size()>0) {
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item, listFiltered);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item, listFiltered);
                    listView.setAdapter(adapter);
                    textView.setVisibility(View.INVISIBLE);
                }else{
                    textView.setVisibility(View.VISIBLE);
                    listView.setAdapter(null);
                }


            }
        });

        dialog.dismiss();

    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public class MyThread implements Runnable {

        @Override
        public void run() {
            html2text(editText.getText().toString());
        }
    }


}
