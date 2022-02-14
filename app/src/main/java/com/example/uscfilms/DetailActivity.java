package com.example.uscfilms;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.uscfilms.ui.notifications.NotificationsFragment;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.smarteist.autoimageslider.SliderView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import util.*;

public class DetailActivity extends AppCompatActivity {

    //private String baseURL = "http://10.0.2.2:8080/api/posts/";
    private String baseURL = "https://winter-sequence-308604.wl.r.appspot.com/api/posts/";

    SharedPreferences pref;

    View loading_view;

    private TextView tv_title;
    private ImageView image_poster;
    private YouTubePlayerView video_player;

    private TextView sub_overview;
    private ReadMoreTextView tv_overview;

    private TextView sub_genres;
    private TextView tv_genres;

    private TextView sub_year;
    private TextView tv_year;

    private ImageView facebookShare;
    private ImageView twitterShare;
    private ImageView im_watchList;

    private TextView sub_cast;
    private TextView sub_review;
    private TextView sub_recommend;

    private RecyclerView cast_rv;
    private RecyclerView review_rv;
    private RecyclerView recommend_rv;

    private JSONObject detailItem;
    private String name;
    private String posterURL;
    private String ID;
    private String type;
    private String tmdbURL="";
    private JSONArray videoArr;
    private String videoKey="";
    private String genres="";
    private Boolean inWatchlist=false;
    private int posInWatchlist=-1;

    private JSONArray castArr;
    private JSONArray reviewArr;
    private JSONArray recArray;
    private JSONArray jsonWatch=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        new common().translucentStatusBar(DetailActivity.this,true);

        Bundle bundle = getIntent().getExtras();

        ID = bundle.getString("ID");
        type = bundle.getString("type");

        loading_view = findViewById(R.id.detail_loading);

        //init
        tv_title = findViewById(R.id.detail_text_title);

        //tv_title.setText("ID:"+ID+" Type:"+type);

        image_poster = findViewById(R.id.detail_image_poster);
        image_poster.setVisibility(View.GONE);
        video_player = findViewById(R.id.detail_youtube_player_view);
        getLifecycle().addObserver(video_player);

        sub_overview = findViewById(R.id.detail_sub_overview);
        tv_overview = findViewById(R.id.detail_overview);

        sub_genres = findViewById(R.id.detail_sub_genres);
        tv_genres = findViewById(R.id.detail_genres);

        sub_year = findViewById(R.id.detail_sub_years);
        tv_year = findViewById(R.id.detail_years);

        facebookShare = findViewById(R.id.detail_share_facebook);
        twitterShare = findViewById(R.id.detail_share_twitter);

        //Share
        tmdbURL = "http://www.themoviedb.org/"+type+"/"+ID;
        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fURL = "https://www.facebook.com/sharer/sharer.php?u="+ tmdbURL +"%2F&amp;"+ "src=sdkpreparse";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fURL));
                DetailActivity.this.startActivity(browserIntent);
            }
        });

        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tURL = "https://twitter.com/intent/tweet?text="+"Check this out!"+"&url="+tmdbURL;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tURL));
                DetailActivity.this.startActivity(browserIntent);
            }
        });

        //watchlist

        im_watchList = findViewById(R.id.detail_add_to_watchlist);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        String watchStr = pref.getString("Watchlist",null);

        if(watchStr==null)
        {
            inWatchlist = false;
        }
        else
        {
            try {
                jsonWatch = new JSONArray(watchStr);

                for(int i=0;i<jsonWatch.length();i++)
                {
                    if(jsonWatch.getJSONObject(i).getString("ID").equals(ID))
                    {
                        inWatchlist = true;
                        im_watchList.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
                        posInWatchlist = i;
                        break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        im_watchList.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                if(inWatchlist)
                {
                    jsonWatch.remove(posInWatchlist);

                    Editor editor = pref.edit();
                    editor.remove("Watchlist");
                    editor.putString("Watchlist",jsonWatch.toString());
                    editor.commit(); // commit changes

                    Toast toast = Toast.makeText(DetailActivity.this,name+" was removed from Watchlist",Toast.LENGTH_SHORT);
                    LinearLayout view = (LinearLayout) toast.getView();
                    TextView tv = (TextView) view.getChildAt(0);
                    view.setBackgroundResource(R.drawable.toast_back);
                    view.setPadding(40,40,40,40);
                    tv.setTextColor(Color.rgb(0,0,0));
                    tv.setTextSize(15);
                    toast.setView(view);
                    toast.show();

                    inWatchlist = false;
                    im_watchList.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
                }
                else
                {
                    //添加
                    if(jsonWatch==null)
                    {
                        jsonWatch = new JSONArray();
                    }

                    pref = getApplicationContext().getSharedPreferences("MyPref", 0);

                    String watchStr = pref.getString("Watchlist",null);

                    if(watchStr!=null)
                    {
                        try {

                            jsonWatch = new JSONArray(watchStr);

                            for(int i=0;i<jsonWatch.length();i++)
                            {
                                if(jsonWatch.getJSONObject(i).get("ID").equals(ID))
                                {
                                    inWatchlist = true;
                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(!inWatchlist)
                    {
                        Editor editor = pref.edit();

                        JSONObject itemObject = new JSONObject();
                        try {
                            itemObject.put("ID", ID);
                            itemObject.put("name", name);
                            itemObject.put("posterURL", posterURL);
                            itemObject.put("type", type);
                            jsonWatch.put(itemObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        editor.remove("Watchlist");
                        editor.putString("Watchlist",jsonWatch.toString());

                        editor.commit(); // commit changes

                        inWatchlist = true;
                    }

                    Toast toast = Toast.makeText(DetailActivity.this,name+" was added to Watchlist",Toast.LENGTH_SHORT);
                    LinearLayout view = (LinearLayout) toast.getView();
                    TextView tv = (TextView) view.getChildAt(0);
                    view.setBackgroundResource(R.drawable.toast_back);
                    view.setPadding(40,40,40,40);
                    tv.setTextColor(Color.rgb(0,0,0));
                    tv.setTextSize(15);
                    toast.setView(view);
                    toast.show();

                    inWatchlist = true;

                    im_watchList.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);

                }

            }
        });

        //RecyclerView

        sub_cast = findViewById(R.id.detail_sub_cast);

        cast_rv = findViewById(R.id.detail_cast_rv);
        cast_rv.setLayoutManager(new GridLayoutManager(DetailActivity.this,3));

        sub_review = findViewById(R.id.detail_sub_review);
        review_rv = findViewById(R.id.detail_review_rv);
        review_rv.setLayoutManager(new LinearLayoutManager(DetailActivity.this));

        sub_recommend = findViewById(R.id.detail_sub_recommend);
        recommend_rv = findViewById(R.id.detail_recommend_rv);
        LinearLayoutManager llm = new LinearLayoutManager(DetailActivity.this);
        llm.setOrientation(RecyclerView.HORIZONTAL);
        recommend_rv.setLayoutManager(llm);

        if(type.contains("movie"))
        {

            //movie
            //movie detail
            String url = baseURL + "movieDetail?id="+ID;

            JsonObjectRequest jsonObjectReq = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            detailItem = response;

                            try {

                                name = detailItem.get("title").toString();

                                tv_title.setText(name);

                                String imageURL = "https://image.tmdb.org/t/p/w500"+detailItem.get("backdrop_path").toString();

                                posterURL = "https://image.tmdb.org/t/p/w500"+detailItem.get("poster_path").toString();

                                if(imageURL=="")
                                {
                                    imageURL = "https://bytes.usc.edu/cs571/s21_JSwasm00/hw/HW6/imgs/movie-placeholder.jpg";
                                }

                                Glide.with(DetailActivity.this)
                                        .load(imageURL)
                                        .fitCenter()
                                        .into(image_poster);

                                String overview = detailItem.get("overview").toString();
                                if(overview!="")
                                {
                                    tv_overview.setText(overview);
                                }
                                else
                                {
                                    sub_overview.setVisibility(View.GONE);
                                    tv_overview.setVisibility(View.GONE);
                                }


                                //genres
                                JSONArray genreArr = detailItem.getJSONArray("genres");

                                if(genreArr.length()>0)
                                {
                                    for(int i=0;i<genreArr.length();i++)
                                    {
                                        if(i<genreArr.length()-1)
                                        {
                                            genres=genres+genreArr.getJSONObject(i).get("name").toString()+", ";
                                        }
                                        else
                                        {
                                            genres=genres+genreArr.getJSONObject(i).get("name").toString();
                                        }
                                    }

                                    tv_genres.setText(genres);

                                }
                                else
                                {
                                    sub_genres.setVisibility(View.GONE);
                                    tv_genres.setVisibility(View.GONE);
                                }

                                //year
                                String date = detailItem.get("release_date").toString();
                                if(date!="")
                                {
                                    String year = date.split("-")[0];
                                    tv_year.setText(year);
                                }
                                else
                                {
                                    sub_year.setVisibility(View.GONE);
                                    tv_year.setVisibility(View.GONE);
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            tv_title.setText("Error: "+error.getCause().toString());
                        }
                    });

            //movie video
            String url2 = baseURL + "movieVideo?id="+ID;

            JsonArrayRequest jsonArrayReq2 = new JsonArrayRequest(url2,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            videoArr = response;


                            try {
                                for(int i=0;i<videoArr.length();i++)
                                {

                                    if(videoArr.getJSONObject(i).get("site").toString().contains("YouTube") &&
                                            videoArr.getJSONObject(i).get("type").toString().contains("Trailer"))
                                    {
                                        videoKey = videoArr.getJSONObject(i).get("key").toString();
                                    }

                                }

                                if(videoKey=="")
                                {
                                    for(int i=0;i<videoArr.length();i++)
                                    {

                                        if(videoArr.getJSONObject(i).get("site").toString().contains("YouTube"))
                                        {
                                            videoKey = videoArr.getJSONObject(i).get("key").toString();
                                        }

                                    }
                                }

                                if(videoKey=="")
                                {
                                    video_player.setVisibility(View.GONE);
                                    image_poster.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    video_player.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                                        @Override
                                        public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                                            // do stuff with it
                                            youTubePlayer.cueVideo(videoKey, 0);
                                        }
                                    });
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });


            //cast
            String url3 = baseURL + "movieCast?id="+ID;

            JsonArrayRequest jsonArrayReq3 = new JsonArrayRequest(url3,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            castArr = response;

                            if(castArr.length()>0)
                            {
                                int min = Math.min(castArr.length(),6);

                                // we are creating array list for storing our image urls.
                                ArrayList<castData> sliderDataArrayList = new ArrayList<>();

                                // adding the urls inside array list
                                for(int i=0;i<min;i++)
                                {
                                    String msURL = null;
                                    try {
                                        msURL = "https://image.tmdb.org/t/p/w500"+castArr.getJSONObject(i).get("profile_path").toString();
                                        sliderDataArrayList.add(new castData(msURL,castArr.getJSONObject(i).get("name").toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                cast_rv.setAdapter(new castAdapter(DetailActivity.this,sliderDataArrayList));
                            }
                            else
                            {
                                sub_cast.setVisibility(View.GONE);
                                cast_rv.setVisibility(View.GONE);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });

            //reviews
            String url4 = baseURL + "movieReviews?id="+ID;

            JsonArrayRequest jsonArrayReq4 = new JsonArrayRequest(url4,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            reviewArr = response;

                            if(reviewArr.length()>0)
                            {
                                int min = Math.min(reviewArr.length(),3);
                                // we are creating array list for storing our image urls.
                                ArrayList<JSONObject> sliderDataArrayList = new ArrayList<>();

                                // adding the urls inside array list
                                for(int i=0;i<min;i++)
                                {
                                    try {
                                        sliderDataArrayList.add(reviewArr.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                review_rv.setAdapter(new reviewAdapter(DetailActivity.this,sliderDataArrayList));
                            }
                            else
                            {
                                sub_review.setVisibility(View.GONE);
                                review_rv.setVisibility(View.GONE);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });


            //recommend
            String url5 = baseURL+"movieRec?id="+ID;

            JsonArrayRequest jsonArrayReq5 = new JsonArrayRequest(url5,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            recArray = response;

                            if(recArray.length()>0)
                            {

                                int min = Math.min(recArray.length(),10);

                                // we are creating array list for storing our image urls.
                                ArrayList<HorRvData> sliderDataArrayList = new ArrayList<>();

                                // adding the urls inside array list
                                for(int i=0;i<min;i++)
                                {
                                    String msURL = null;
                                    try {
                                        msURL = "https://image.tmdb.org/t/p/w500"+recArray.getJSONObject(i).get("poster_path").toString();
                                        sliderDataArrayList.add(new HorRvData(msURL, "movie", recArray.getJSONObject(i)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                recommend_rv.setAdapter(new recAdapter(DetailActivity.this,sliderDataArrayList));
                                loading_view.setVisibility(View.GONE);
                            }
                            else
                            {
                                sub_recommend.setVisibility(View.GONE);
                                recommend_rv.setVisibility(View.GONE);
                                loading_view.setVisibility(View.GONE);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });

            // Access the RequestQueue through your singleton class.
            MySingleton ReqQue = MySingleton.getInstance(DetailActivity.this);
            ReqQue.addToRequestQueue(jsonObjectReq);
            ReqQue.addToRequestQueue(jsonArrayReq2);
            ReqQue.addToRequestQueue(jsonArrayReq3);
            ReqQue.addToRequestQueue(jsonArrayReq4);
            ReqQue.addToRequestQueue(jsonArrayReq5);
        }
        else
        {
            //tv
            //tv detail
            String url =  baseURL + "tvDetail?id="+ID;

            JsonObjectRequest jsonObjectReq = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            detailItem = response;

                            try {

                                name = detailItem.get("name").toString();

                                tv_title.setText(name);

                                String imageURL = "https://image.tmdb.org/t/p/w500"+detailItem.get("backdrop_path").toString();

                                posterURL = "https://image.tmdb.org/t/p/w500"+detailItem.get("poster_path").toString();

                                if(imageURL=="")
                                {
                                    imageURL = "https://bytes.usc.edu/cs571/s21_JSwasm00/hw/HW6/imgs/movie-placeholder.jpg";
                                }

                                Glide.with(DetailActivity.this)
                                        .load(imageURL)
                                        .fitCenter()
                                        .into(image_poster);

                                String overview = detailItem.get("overview").toString();
                                if(overview!="")
                                {
                                    tv_overview.setText(overview);
                                }
                                else
                                {
                                    sub_overview.setVisibility(View.GONE);
                                    tv_overview.setVisibility(View.GONE);
                                }


                                //genres
                                JSONArray genreArr = detailItem.getJSONArray("genres");

                                if(genreArr.length()>0)
                                {
                                    for(int i=0;i<genreArr.length();i++)
                                    {
                                        if(i<genreArr.length()-1)
                                        {
                                            genres=genres+genreArr.getJSONObject(i).get("name").toString()+", ";
                                        }
                                        else
                                        {
                                            genres=genres+genreArr.getJSONObject(i).get("name").toString();
                                        }
                                    }

                                    tv_genres.setText(genres);

                                }
                                else
                                {
                                    sub_genres.setVisibility(View.GONE);
                                    tv_genres.setVisibility(View.GONE);
                                }

                                //year
                                String date = detailItem.get("first_air_date").toString();
                                if(date!="")
                                {
                                    String year = date.split("-")[0];
                                    tv_year.setText(year);
                                }
                                else
                                {
                                    sub_year.setVisibility(View.GONE);
                                    tv_year.setVisibility(View.GONE);
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            tv_title.setText("Error: "+error.getCause().toString());
                        }
                    });


            //tv video
            String url2 = baseURL + "tvVideo?id="+ID;

            JsonArrayRequest jsonArrayReq2 = new JsonArrayRequest(url2,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            videoArr = response;


                            try {
                                for(int i=0;i<videoArr.length();i++)
                                {

                                    if(videoArr.getJSONObject(i).get("site").toString().contains("YouTube") &&
                                            videoArr.getJSONObject(i).get("type").toString().contains("Trailer"))
                                    {
                                        videoKey = videoArr.getJSONObject(i).get("key").toString();
                                    }

                                }

                                if(videoKey=="")
                                {
                                    for(int i=0;i<videoArr.length();i++)
                                    {

                                        if(videoArr.getJSONObject(i).get("site").toString().contains("YouTube"))
                                        {
                                            videoKey = videoArr.getJSONObject(i).get("key").toString();
                                        }

                                    }
                                }

                                if(videoKey=="")
                                {
                                    video_player.setVisibility(View.GONE);
                                    image_poster.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    video_player.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
                                        @Override
                                        public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                                            // do stuff with it
                                            youTubePlayer.cueVideo(videoKey, 0);
                                        }
                                    });
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });


            //cast
            String url3 = baseURL + "tvCast?id="+ID;

            JsonArrayRequest jsonArrayReq3 = new JsonArrayRequest(url3,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            castArr = response;

                            if(castArr.length()>0)
                            {
                                int min = Math.min(castArr.length(),6);

                                // we are creating array list for storing our image urls.
                                ArrayList<castData> sliderDataArrayList = new ArrayList<>();

                                // adding the urls inside array list
                                for(int i=0;i<min;i++)
                                {
                                    String msURL = null;
                                    try {
                                        msURL = "https://image.tmdb.org/t/p/w500"+castArr.getJSONObject(i).get("profile_path").toString();
                                        sliderDataArrayList.add(new castData(msURL,castArr.getJSONObject(i).get("name").toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                cast_rv.setAdapter(new castAdapter(DetailActivity.this,sliderDataArrayList));
                            }
                            else
                            {
                                sub_cast.setVisibility(View.GONE);
                                cast_rv.setVisibility(View.GONE);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });

            //reviews
            String url4 = baseURL + "tvReviews?id="+ID;

            JsonArrayRequest jsonArrayReq4 = new JsonArrayRequest(url4,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            reviewArr = response;

                            if(reviewArr.length()>0)
                            {
                                int min = Math.min(reviewArr.length(),3);
                                // we are creating array list for storing our image urls.
                                ArrayList<JSONObject> sliderDataArrayList = new ArrayList<>();

                                // adding the urls inside array list
                                for(int i=0;i<min;i++)
                                {
                                    try {
                                        sliderDataArrayList.add(reviewArr.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                review_rv.setAdapter(new reviewAdapter(DetailActivity.this,sliderDataArrayList));
                            }
                            else
                            {
                                sub_review.setVisibility(View.GONE);
                                review_rv.setVisibility(View.GONE);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });


            //recommend
            String url5 = baseURL+"tvRec?id="+ID;

            JsonArrayRequest jsonArrayReq5 = new JsonArrayRequest(url5,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            recArray = response;

                            if(recArray.length()>0)
                            {

                                int min = Math.min(recArray.length(),10);

                                // we are creating array list for storing our image urls.
                                ArrayList<HorRvData> sliderDataArrayList = new ArrayList<>();

                                // adding the urls inside array list
                                for(int i=0;i<min;i++)
                                {
                                    String msURL = null;
                                    try {
                                        msURL = "https://image.tmdb.org/t/p/w500"+recArray.getJSONObject(i).get("poster_path").toString();
                                        sliderDataArrayList.add(new HorRvData(msURL, "tv", recArray.getJSONObject(i)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                recommend_rv.setAdapter(new recAdapter(DetailActivity.this,sliderDataArrayList));

                                loading_view.setVisibility(View.GONE);
                            }
                            else
                            {
                                sub_recommend.setVisibility(View.GONE);
                                recommend_rv.setVisibility(View.GONE);
                                loading_view.setVisibility(View.GONE);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            tv_title.setText("Volley error: "+error.getCause().toString());

                        }
                    });

            // Access the RequestQueue through your singleton class.
            MySingleton ReqQue = MySingleton.getInstance(DetailActivity.this);
            ReqQue.addToRequestQueue(jsonObjectReq);
            ReqQue.addToRequestQueue(jsonArrayReq2);
            ReqQue.addToRequestQueue(jsonArrayReq3);
            ReqQue.addToRequestQueue(jsonArrayReq4);
            ReqQue.addToRequestQueue(jsonArrayReq5);

        }


    }

}