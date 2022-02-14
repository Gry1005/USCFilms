package util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.*;
import android.view.MenuItem;

import java.util.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uscfilms.DetailActivity;
import com.example.uscfilms.MainActivity;
import com.example.uscfilms.R;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HorRvAdapter extends RecyclerView.Adapter<HorRvAdapter.linearViewHolder> {

    private Context mContext;

    private ArrayList<HorRvData> mlist;

    private int curPos;
    private SharedPreferences pref;
    private boolean inWatchlist=false;
    private String mID="";
    private String mName="";
    private String mType="";
    private String mPosterURL="";

    private String watchStr=null;
    private JSONArray jsonWatch=null;
    private int posInWatchlist=-1;

    public HorRvAdapter(Context context, ArrayList<HorRvData> list)
    {
        this.mContext = context;
        this.mlist = list;
    }


    @NonNull
    @Override
    public HorRvAdapter.linearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new linearViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.hor_rv_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HorRvAdapter.linearViewHolder holder, final int position) {

        RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(30));//图片圆角为30

        Glide.with(holder.itemView)
                .load(mlist.get(position).getImgUrl())
                .fitCenter()
                .apply(options)
                .into(holder.imageView);

        String ID="";
        String type=mlist.get(position).getType();

        try {
            ID = mlist.get(position).getItem().get("id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.imageView.setTag(ID+"|"+type);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);

                String tag = (String) v.getTag();

                //Toast.makeText(mContext,tag,Toast.LENGTH_SHORT).show();

                String[] strArr = tag.split("\\|");
                String ID = strArr[0];
                String type = strArr[1];

                Bundle bundle = new Bundle();
                bundle.putString("ID",ID);
                bundle.putString("type",type);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });

        holder.more.setTag(position);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initializing the popup menu and giving the reference as current context
                final PopupMenu popupMenu = new PopupMenu(mContext,v);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                //Toast.makeText(mContext, "You Clicked " + v.getTag(), Toast.LENGTH_SHORT).show();

                curPos = (Integer) v.getTag();

                pref = mContext.getSharedPreferences("MyPref", 0);

                try {

                    mID = mlist.get(curPos).getItem().getString("id");
                    mType = mlist.get(curPos).getType();
                    mPosterURL = "https://image.tmdb.org/t/p/w500"+mlist.get(curPos).getItem().getString("poster_path");

                    if(mType.contains("movie"))
                    {
                        mName = mlist.get(curPos).getItem().getString("title");
                    }
                    else
                    {
                        mName = mlist.get(curPos).getItem().getString("name");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String watchStr = pref.getString("Watchlist",null);

                MenuItem  gMenuItem=popupMenu.getMenu().findItem(R.id.menu_watchlist);

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
                            if(jsonWatch.getJSONObject(i).getString("ID").equals(mID))
                            {
                                inWatchlist = true;
                                posInWatchlist = i;
                                gMenuItem.setTitle("Remove from Watchlist");
                                break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked

                        String url = "";

                        try {
                            String ID = mlist.get(curPos).getItem().get("id").toString();
                            url = "http://www.themoviedb.org/"+mlist.get(curPos).getType()+"/"+ID;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        switch (menuItem.getItemId()){
                            case R.id.menu_tmdb:
                            {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                mContext.startActivity(browserIntent);
                                break;
                            }
                            case R.id.menu_facebook:{

                                String fURL = "https://www.facebook.com/sharer/sharer.php?u="+ url+"%2F&amp;"+ "src=sdkpreparse";
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fURL));
                                mContext.startActivity(browserIntent);
                                break;
                            }
                            case R.id.menu_twitter:{

                                String tURL = "https://twitter.com/intent/tweet?text="+"Check this out!"+"&url="+url;
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tURL));
                                mContext.startActivity(browserIntent);
                                break;

                            }
                            case R.id.menu_watchlist:{

                                if(inWatchlist)
                                {
                                    jsonWatch.remove(posInWatchlist);

                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.remove("Watchlist");
                                    editor.putString("Watchlist",jsonWatch.toString());
                                    editor.commit(); // commit changes

                                    Toast toast = Toast.makeText(mContext,mName+" was removed from Watchlist",Toast.LENGTH_SHORT);
                                    LinearLayout view = (LinearLayout) toast.getView();
                                    TextView tv = (TextView) view.getChildAt(0);
                                    view.setBackgroundResource(R.drawable.toast_back);
                                    view.setPadding(40,40,40,40);
                                    tv.setTextColor(Color.rgb(0,0,0));
                                    tv.setTextSize(15);
                                    toast.setView(view);
                                    toast.show();

                                    inWatchlist = false;
                                }
                                else
                                {

                                    if(jsonWatch==null)
                                    {
                                        jsonWatch = new JSONArray();
                                    }

                                    SharedPreferences.Editor editor = pref.edit();
                                    JSONObject itemObject = new JSONObject();
                                    try {
                                        itemObject.put("ID", mID);
                                        itemObject.put("name", mName);
                                        itemObject.put("posterURL", mPosterURL);
                                        itemObject.put("type", mType);
                                        jsonWatch.put(jsonWatch.length(),itemObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    editor.remove("Watchlist");
                                    editor.putString("Watchlist",jsonWatch.toString());

                                    editor.commit(); // commit changes

                                    Toast toast = Toast.makeText(mContext,mName+" was added to Watchlist",Toast.LENGTH_SHORT);
                                    LinearLayout view = (LinearLayout) toast.getView();
                                    TextView tv = (TextView) view.getChildAt(0);
                                    view.setBackgroundResource(R.drawable.toast_back);
                                    view.setPadding(40,40,40,40);
                                    tv.setTextColor(Color.rgb(0,0,0));
                                    tv.setTextSize(15);
                                    toast.setView(view);
                                    toast.show();

                                    inWatchlist = true;

                                }

                                break;
                            }
                            default:break;
                        }

                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.mlist.size();
    }

    class linearViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        private View more;

        public linearViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.hor_rv_item_image);
            more = itemView.findViewById(R.id.hor_rv_item_more);

        }
    }
}
