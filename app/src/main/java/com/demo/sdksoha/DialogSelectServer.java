package com.demo.sdksoha;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import vn.sohagame.sdkdemo.R;


/**
 * demo dialog select server, don't use in your project
 */
public class DialogSelectServer extends Dialog {
    private static final String S_API_CHARACTER_DEMO = "http://soap.soha.vn/api/a/GET/mobile/CharacterDemo";
    private ArrayList<RolesObject> listData;
    private ICallbackSelectServer callbackSelectServer;

    private Context context;
    private RadioGroup rdServer;
    private CustomAdapter adapter;
    private int areaId;

    DialogSelectServer(@NonNull Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.dialog_select_server);
        listData = new ArrayList<>();
        adapter = new CustomAdapter(listData);
        rdServer = findViewById(R.id.rdServer);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (callbackSelectServer != null) {
                    callbackSelectServer.onSelectItem((RolesObject) adapter.getItem(i), areaId);
                    dismiss();
                }
            }
        });
        new ShohaParse().execute(S_API_CHARACTER_DEMO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @SuppressLint("ResourceType")
    private void setArrCharac(ArrayList<AreaObject> arrCharac) {
        if (arrCharac != null && arrCharac.size() > 0) {
            for (final AreaObject areaObject : arrCharac) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setId(areaObject.getAreaId());
                radioButton.setText(areaObject.getAreaName());
                rdServer.addView(radioButton);
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listData.clear();
                        areaId = areaObject.getAreaId();
                        listData.addAll(areaObject.getRoles());
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            rdServer.check(arrCharac.get(0).getAreaId());
            areaId = arrCharac.get(0).getAreaId();
            listData.addAll(arrCharac.get(0).getRoles());
            adapter.notifyDataSetChanged();
        }
    }

    private static class CustomAdapter extends BaseAdapter {

        ArrayList<RolesObject> listData;

        CustomAdapter(ArrayList<RolesObject> listData) {
            this.listData = listData;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int i) {
            return listData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                convertView = layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            RolesObject item = listData.get(position);
//            viewHolder.tvText.setTextSize(30);
            viewHolder.tvText.setText(item.getRoleName());
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tvText;

        ViewHolder(View view) {
            tvText = view.findViewById(android.R.id.text1);
        }
    }

    public interface ICallbackSelectServer {
        void onSelectItem(RolesObject character, int areaId);
    }

    public void setCallbackSelectServer(ICallbackSelectServer callbackSelectServer) {
        this.callbackSelectServer = callbackSelectServer;
    }


    public static class RolesObject {

        @SerializedName("role_id")
        private int roleId;
        @SerializedName("role_name")
        private String roleName;
        @SerializedName("role_level")
        private int roleLevel;

        public int getRoleId() {
            return roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public int getRoleLevel() {
            return roleLevel;
        }
    }

    public static class AreaObject {

        @SerializedName("area_id")
        private int areaId;
        @SerializedName("area_name")
        private String areaName;
        @SerializedName("roles")
        private ArrayList<RolesObject> roles;

        public AreaObject(int areaId, String areaName, ArrayList<RolesObject> roles) {
            this.areaId = areaId;
            this.areaName = areaName;
            this.roles = roles;
        }

        public int getAreaId() {
            return areaId;
        }

        public String getAreaName() {
            return areaName;
        }

        public ArrayList<RolesObject> getRoles() {
            return roles;
        }
    }

    private class ShohaParse extends AsyncTask<String, Void, ArrayList<AreaObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<AreaObject> doInBackground(String... strings) {
            try {
                String urlShoha = strings[0];
                Log.e("toannt", urlShoha);
                URL url = new URL(urlShoha);
                InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
                Type collectionType = new TypeToken<Collection<AreaObject>>() {
                }.getType();
                return new Gson().fromJson(reader, collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<AreaObject> areaObjects) {
            super.onPostExecute(areaObjects);
            setArrCharac(areaObjects);
        }
    }
}
