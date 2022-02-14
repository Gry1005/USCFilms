package com.example.uscfilms.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.uscfilms.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import util.HorRvAdapter;
import util.HorRvData;
import util.MySingleton;
import util.searchAdapter;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    //private String baseURL = "http://10.0.2.2:8080/api/posts/";
    private String baseURL = "https://winter-sequence-308604.wl.r.appspot.com/api/posts/";

    private Context mContext;

    private SearchView svSearch;
    private RecyclerView result_rv;
    private TextView no_result;

    private JSONArray results;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mContext = getActivity();

        no_result = root.findViewById(R.id.dashboard_no_result);
        no_result.setVisibility(View.GONE);

        //recyclerview

        result_rv = root.findViewById(R.id.dashboard_result_rv);

        result_rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        svSearch = root.findViewById(R.id.dashboard_search);

        int id = svSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) svSearch.findViewById(id);
        textView.setTextColor(Color.WHITE);//字体颜色
        textView.setTextSize(20);//字体、提示字体大小
        textView.setHintTextColor(Color.parseColor("#919da3"));//提示字体颜色**

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) svSearch.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        svSearch.onActionViewExpanded();

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String msg = svSearch.getQuery().toString();

                if(msg!="")
                {
                    //no_result.setText("input:"+msg);

                    String url = baseURL + "multiSearch?id="+msg;

                    JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {

                                    results = response;

                                    if(results!=null && results.length()>0)
                                    {
                                        // we are creating array list for storing our image urls.
                                        ArrayList<HorRvData> sliderDataArrayList = new ArrayList<>();

                                        // adding the urls inside array list
                                        for(int i=0;i<10;i++)
                                        {
                                            String msURL = null;
                                            try {
                                                msURL = "https://image.tmdb.org/t/p/w500"+results.getJSONObject(i).get("backdrop_path").toString();
                                                String type = results.getJSONObject(i).get("media_type").toString();
                                                sliderDataArrayList.add(new HorRvData(msURL, type, results.getJSONObject(i)));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        result_rv.setAdapter(new searchAdapter(mContext,sliderDataArrayList));
                                        no_result.setVisibility(View.GONE);
                                        result_rv.setVisibility(View.VISIBLE);

                                    }
                                    else
                                    {
                                        no_result.setVisibility(View.VISIBLE);
                                        result_rv.setVisibility(View.GONE);
                                    }



                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    no_result.setVisibility(View.GONE);
                                    result_rv.setVisibility(View.GONE);

                                }
                            });

                    MySingleton mys = MySingleton.getInstance(getActivity().getApplicationContext());
                    mys.addToRequestQueue(jsonArrayReq);
                }
                else
                {
                    no_result.setVisibility(View.VISIBLE);
                    result_rv.setVisibility(View.GONE);
                }

                return false;
            }
        });


        return root;
    }
}