package com.example.uscfilms.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.*;
import android.content.Context;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.toolbox.*;
import com.android.volley.*;
import com.bumptech.glide.Glide;
import com.example.uscfilms.MainActivity;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.example.uscfilms.R;

import util.*;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    //private String baseURL = "http://10.0.2.2:8080/api/posts/";
    private String baseURL = "https://winter-sequence-308604.wl.r.appspot.com/api/posts/";

    private Context mContext;

    private JSONArray nowPlaying;
    private JSONArray topRatedMovies;
    private JSONArray popularMovies;

    private JSONArray trendingTV;
    private JSONArray topRatedTV;
    private JSONArray popularTV;

    View loading_view;

    private TextView homeText2;

    private TextView movieText;

    private TextView TVText;

    private SliderView sliderViewMovie;
    private RecyclerView rv_top_rated_movie;
    private RecyclerView rv_popular_movie;

    private SliderView sliderViewTV;
    private RecyclerView rv_top_rated_tv;
    private RecyclerView rv_popular_tv;

    private TextView foot_tmdb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = getActivity();

        loading_view = root.findViewById(R.id.home_loading);

        homeText2 = root.findViewById(R.id.home_text_USC);

        movieText = root.findViewById(R.id.home_text_movie);
        TVText = root.findViewById(R.id.home_text_TV);

        foot_tmdb = root.findViewById(R.id.home_text_tmdb);

        // initializing the slider view.
        sliderViewMovie = root.findViewById(R.id.movieSlider);
        sliderViewTV = root.findViewById(R.id.TVSlider);

        sliderViewTV.setVisibility(View.GONE);

        // recyclerView

        rv_top_rated_movie = root.findViewById(R.id.rv_top_rated_movie);
        LinearLayoutManager llm1 = new LinearLayoutManager(getActivity().getApplicationContext());
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_top_rated_movie.setLayoutManager(llm1);

        rv_popular_movie = root.findViewById(R.id.rv_popular_movie);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity().getApplicationContext());
        llm2.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_popular_movie.setLayoutManager(llm2);

        rv_top_rated_tv = root.findViewById(R.id.rv_top_rated_tv);
        LinearLayoutManager llm3 = new LinearLayoutManager(getActivity().getApplicationContext());
        llm3.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_top_rated_tv.setLayoutManager(llm3);

        rv_popular_tv = root.findViewById(R.id.rv_popular_tv);
        LinearLayoutManager llm4 = new LinearLayoutManager(getActivity().getApplicationContext());
        llm4.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_popular_tv.setLayoutManager(llm4);

        rv_top_rated_tv.setVisibility(View.GONE);
        rv_popular_tv.setVisibility(View.GONE);

        //Click event

        movieText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieText.setTextColor(Color.WHITE);
                TVText.setTextColor(getResources().getColor(R.color.bottomColor));

                sliderViewMovie.setVisibility(View.VISIBLE);
                rv_top_rated_movie.setVisibility(View.VISIBLE);
                rv_popular_movie.setVisibility(View.VISIBLE);

                sliderViewTV.setVisibility(View.GONE);
                rv_top_rated_tv.setVisibility(View.GONE);
                rv_popular_tv.setVisibility(View.GONE);
            }
        });

        TVText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TVText.setTextColor(Color.WHITE);
                movieText.setTextColor(getResources().getColor(R.color.bottomColor));

                sliderViewTV.setVisibility(View.VISIBLE);
                rv_top_rated_tv.setVisibility(View.VISIBLE);
                rv_popular_tv.setVisibility(View.VISIBLE);

                sliderViewMovie.setVisibility(View.GONE);
                rv_top_rated_movie.setVisibility(View.GONE);
                rv_popular_movie.setVisibility(View.GONE);
            }
        });

        foot_tmdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/"));
                startActivity(browserIntent);
            }
        });

        //now playing movie
        String url = baseURL + "currentPlayingMovies";

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                       nowPlaying = response;

                       //解析json
                        try {

                            // we are creating array list for storing our image urls.
                            ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

                            // adding the urls inside array list
                            for(int i=0;i<6;i++)
                            {
                                String msURL = "https://image.tmdb.org/t/p/w500"+nowPlaying.getJSONObject(i).get("poster_path").toString();
                                String ID = nowPlaying.getJSONObject(i).get("id").toString();
                                sliderDataArrayList.add(new SliderData(msURL,ID,"movie"));
                            }

                            // passing this array list inside our adapter class.
                            SliderAdapter adapter = new SliderAdapter(getActivity().getApplicationContext(), sliderDataArrayList);

                            // below method is used to set auto cycle direction in left to
                            // right direction you can change according to requirement.
                            sliderViewMovie.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

                            // below method is used to
                            // setadapter to sliderview.
                            sliderViewMovie.setSliderAdapter(adapter);

                            // below method is use to set
                            // scroll time in seconds.
                            sliderViewMovie.setScrollTimeInSec(3);

                            // to set it scrollable automatically
                            // we use below method.
                            sliderViewMovie.setAutoCycle(true);

                            // to start autocycle below method is used.
                            sliderViewMovie.startAutoCycle();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        homeText2.setText("Volley error: "+error.getCause().toString());

            }
        });


        //trending TV

        String url2 = baseURL + "trendingTVs";

        JsonArrayRequest jsonArrayReq2 = new JsonArrayRequest(url2,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        trendingTV = response;

                        //解析json
                        try {

                            // we are creating array list for storing our image urls.
                            ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

                            // adding the urls inside array list
                            for(int i=0;i<6;i++)
                            {
                                String msURL = "https://image.tmdb.org/t/p/w500"+trendingTV.getJSONObject(i).get("poster_path").toString();
                                String ID = trendingTV.getJSONObject(i).get("id").toString();
                                sliderDataArrayList.add(new SliderData(msURL,ID,"tv"));
                            }

                            // passing this array list inside our adapter class.
                            SliderAdapter adapter = new SliderAdapter(getActivity().getApplicationContext(), sliderDataArrayList);

                            // below method is used to set auto cycle direction in left to
                            // right direction you can change according to requirement.
                            sliderViewTV.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

                            // below method is used to
                            // setadapter to sliderview.
                            sliderViewTV.setSliderAdapter(adapter);

                            // below method is use to set
                            // scroll time in seconds.
                            sliderViewTV.setScrollTimeInSec(3);

                            // to set it scrollable automatically
                            // we use below method.
                            sliderViewTV.setAutoCycle(true);

                            // to start autocycle below method is used.
                            sliderViewTV.startAutoCycle();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        homeText2.setText("Volley error: "+error.getCause().toString());

                    }
                });



        //top rated movie
        String url3 = baseURL + "topMovies";

        JsonArrayRequest jsonArrayReq3 = new JsonArrayRequest(url3,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        topRatedMovies = response;

                        // we are creating array list for storing our image urls.
                        ArrayList<HorRvData> sliderDataArrayList = new ArrayList<>();

                        // adding the urls inside array list
                        for(int i=0;i<10;i++)
                        {
                            String msURL = null;
                            try {
                                msURL = "https://image.tmdb.org/t/p/w500"+topRatedMovies.getJSONObject(i).get("poster_path").toString();
                                sliderDataArrayList.add(new HorRvData(msURL, "movie", topRatedMovies.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        rv_top_rated_movie.setAdapter(new HorRvAdapter(mContext,sliderDataArrayList));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        homeText2.setText("Volley error: "+error.getCause().toString());

                    }
                });

        //popular movie
        String url4 = baseURL + "popularMovies";

        JsonArrayRequest jsonArrayReq4 = new JsonArrayRequest(url4,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        popularMovies = response;

                        // we are creating array list for storing our image urls.
                        ArrayList<HorRvData> sliderDataArrayList = new ArrayList<>();

                        // adding the urls inside array list
                        for(int i=0;i<10;i++)
                        {
                            String msURL = null;
                            try {
                                msURL = "https://image.tmdb.org/t/p/w500"+popularMovies.getJSONObject(i).get("poster_path").toString();
                                sliderDataArrayList.add(new HorRvData(msURL, "movie", popularMovies.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        rv_popular_movie.setAdapter(new HorRvAdapter(mContext,sliderDataArrayList));

                        loading_view.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        homeText2.setText("Volley error: "+error.getCause().toString());

                    }
                });


        //top rated tv
        String url5 = baseURL + "topTVs";

        JsonArrayRequest jsonArrayReq5 = new JsonArrayRequest(url5,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        topRatedTV = response;

                        // we are creating array list for storing our image urls.
                        ArrayList<HorRvData> sliderDataArrayList = new ArrayList<>();

                        // adding the urls inside array list
                        for(int i=0;i<10;i++)
                        {
                            String msURL = null;
                            try {
                                msURL = "https://image.tmdb.org/t/p/w500"+topRatedTV.getJSONObject(i).get("poster_path").toString();
                                sliderDataArrayList.add(new HorRvData(msURL, "tv", topRatedTV.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        rv_top_rated_tv.setAdapter(new HorRvAdapter(mContext,sliderDataArrayList));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        homeText2.setText("Volley error: "+error.getCause().toString());

                    }
                });

        //popular tv
        String url6 = baseURL + "popularTVs";

        JsonArrayRequest jsonArrayReq6 = new JsonArrayRequest(url6,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        popularTV = response;

                        // we are creating array list for storing our image urls.
                        ArrayList<HorRvData> sliderDataArrayList = new ArrayList<>();

                        // adding the urls inside array list
                        for(int i=0;i<10;i++)
                        {
                            String msURL = null;
                            try {
                                msURL = "https://image.tmdb.org/t/p/w500"+popularTV.getJSONObject(i).get("poster_path").toString();
                                sliderDataArrayList.add(new HorRvData(msURL, "tv", popularTV.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        rv_popular_tv.setAdapter(new HorRvAdapter(mContext,sliderDataArrayList));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        homeText2.setText("Volley error: "+error.getCause().toString());

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton mys = MySingleton.getInstance(getActivity().getApplicationContext());
        mys.addToRequestQueue(jsonArrayReq);
        mys.addToRequestQueue(jsonArrayReq2);
        mys.addToRequestQueue(jsonArrayReq3);
        mys.addToRequestQueue(jsonArrayReq4);
        mys.addToRequestQueue(jsonArrayReq5);
        mys.addToRequestQueue(jsonArrayReq6);


        return root;
    }
}