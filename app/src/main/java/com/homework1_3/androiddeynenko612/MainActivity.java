package com.homework1_3.androiddeynenko612;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String TITLE = "title";
    private final static String SUBTITLE = "subtitle";
    private final static String TEXT = "text";
    private final static String PREF = "pref";
    private final static  String keyListBundle = "keyListBundle";
    private final static String LOG_TAG = "Save data";
    List<Map<String, String>> simpleAdapterContent;
    private SharedPreferences sharedPref;
    private ListView list;
    private String result;
    private String[] content;
    ArrayList<Integer> saveListBundle = new ArrayList<>();
    BaseAdapter listContentAdapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateList();
        content = prepareContent();
        listContentAdapter = createAdapter(content);
        list.setAdapter(listContentAdapter);
        listContentAdapter.notifyDataSetChanged();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                view.animate().setDuration(20).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                saveListBundle.add(position);
                                simpleAdapterContent.remove(position);
                                listContentAdapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
                list.onSaveInstanceState();
            }
        });

        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // Будет вызван, когда пользователь потянет список вниз
            @Override
            public void onRefresh() {
                updateList();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void updateList() {
        sharedPref = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if (sharedPref.contains(TEXT)) {
            result = sharedPref.getString(TEXT, null);

        } else {
            String largeText = getString(R.string.large_text);

            sharedPref.edit()
                    .putString(TEXT, largeText)
                    .apply();
            result = largeText;
        }
        content = prepareContent();
    }

    @NonNull
    private BaseAdapter createAdapter(String[] values) {
        list = findViewById(R.id.list);
        simpleAdapterContent = new ArrayList<>();

        for (String value : values) {
            Map<String, String> row = new HashMap<>();
            row.put(TITLE, value);
            row.put(SUBTITLE, String.valueOf(value.length()));
            simpleAdapterContent.add(row);
        }
        return new SimpleAdapter(
                this,
                simpleAdapterContent,
                R.layout.lists,
                new String[]{TITLE, SUBTITLE},
                new int[]{R.id.textItem1, R.id.textItem2}
        );
    }

    @NonNull
    private String[] prepareContent() {
        return result.split("\n\n");
    }

    @Override
    public String toString() {
        return
                " " + sharedPref;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(keyListBundle, saveListBundle);
        Log.d(LOG_TAG, "onSaveInstanceState");
        //Bundle.putIntegerArrayList(saveListBundle);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        saveListBundle = savedInstanceState.getIntegerArrayList(keyListBundle);
        int targetPosition;
        for (int i = 0; i < saveListBundle.size(); i++ ){
            targetPosition = saveListBundle.get(i).intValue();
            simpleAdapterContent.remove(targetPosition);
            listContentAdapter.notifyDataSetChanged();
        }
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }
}
