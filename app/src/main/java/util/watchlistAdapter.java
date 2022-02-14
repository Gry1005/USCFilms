package util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.uscfilms.DetailActivity;
import com.example.uscfilms.R;
import com.example.uscfilms.ReviewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;

public class watchlistAdapter extends RecyclerView.Adapter<watchlistAdapter.linearViewHolder> {

    private Context mContext;

    private ArrayList<JSONObject> mlist;

    private TextView tv;

    private SharedPreferences pref;

    private JSONArray jsonWatch=null;
    private int posInWatchlist=-1;

    public watchlistAdapter(Context context, ArrayList<JSONObject> list, TextView tv)
    {
        this.mContext = context;
        this.mlist = list;
        this.tv = tv;
    }


    @NonNull
    @Override
    public watchlistAdapter.linearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new linearViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.watchlist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final watchlistAdapter.linearViewHolder holder, final int position) {

        try {

            String imgURL = mlist.get(position).getString("posterURL");

            RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(30));//图片圆角为30

            Glide.with(holder.itemView)
                    .load(imgURL)
                    .fitCenter()
                    .apply(options)
                    .into(holder.im_poster);

            String type = mlist.get(position).getString("type");

            if(type.contains("movie"))
            {
                holder.tv_type.setText("Movie");
            }
            else
            {
                holder.tv_type.setText("TV");
            }

            String ID = mlist.get(position).getString("ID");
            holder.itemView.setTag(ID+"|"+type);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
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

            String name = mlist.get(position).getString("name");

            holder.im_remove.setTag(new watchlistData(ID,name,position));

            holder.im_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //notifyDataSetChanged();

                    watchlistData tag = (watchlistData) v.getTag();

                    String ID = tag.getID();
                    String name = tag.getName();
                    int position = tag.getPosition();

                    pref = mContext.getSharedPreferences("MyPref", 0);

                    String watchStr = pref.getString("Watchlist",null);

                    if(watchStr!=null)
                    {
                        try {
                            jsonWatch = new JSONArray(watchStr);

                            for(int i=0;i<jsonWatch.length();i++)
                            {
                                if(jsonWatch.getJSONObject(i).get("ID").equals(ID))
                                {
                                    posInWatchlist = i;
                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonWatch.remove(posInWatchlist);

                        Editor editor = pref.edit();
                        editor.remove("Watchlist");
                        editor.putString("Watchlist",jsonWatch.toString());
                        editor.commit(); // commit changes

                    }

                    //animation
                    notifyItemRemoved(position);
                    mlist.remove(position);
                    notifyDataSetChanged();

                    if(mlist.size()==0)
                    {
                        tv.setVisibility(View.VISIBLE);
                    }

                    Toast toast = Toast.makeText(mContext,"\""+name+"\""+" was removed from Watchlist",Toast.LENGTH_SHORT);
                    LinearLayout view = (LinearLayout) toast.getView();
                    TextView tv = (TextView) view.getChildAt(0);
                    view.setBackgroundResource(R.drawable.toast_back);
                    view.setPadding(40,40,40,40);
                    tv.setTextColor(Color.rgb(0,0,0));
                    tv.setTextSize(15);
                    toast.setView(view);
                    toast.show();

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return this.mlist.size();
    }

    class linearViewHolder extends RecyclerView.ViewHolder{

        private ImageView im_poster;
        private TextView tv_type;
        private ImageView im_remove;

        public linearViewHolder(@NonNull View itemView) {
            super(itemView);

            im_poster = itemView.findViewById(R.id.watchlist_item_image);
            tv_type = itemView.findViewById(R.id.watchlist_item_type);
            im_remove = itemView.findViewById(R.id.watchlist_item_remove);

        }
    }

}

