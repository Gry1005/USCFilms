package util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.uscfilms.DetailActivity;
import com.example.uscfilms.R;

import org.json.JSONException;

import java.util.ArrayList;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.linearViewHolder> {

    private Context mContext;

    private ArrayList<HorRvData> mlist;

    private int curPos;


    public searchAdapter(Context context, ArrayList<HorRvData> list)
    {
        this.mContext = context;
        this.mlist = list;
    }


    @NonNull
    @Override
    public searchAdapter.linearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new linearViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.search_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull searchAdapter.linearViewHolder holder, final int position) {

        RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(50));//图片圆角为50

        Glide.with(holder.itemView)
                .load(mlist.get(position).getImgUrl())
                .fitCenter()
                .apply(options)
                .into(holder.imageView);

        String type_year="";
        String rateStr="";
        String name="";

        try {

            if(mlist.get(position).getType().contains("movie"))
            {
                name = mlist.get(position).getItem().get("title").toString().toUpperCase();

                String year = mlist.get(position).getItem().get("release_date").toString().split("-")[0];
                type_year = "MOVIE ("+ year +")";
                type_year = type_year.toUpperCase();

            }
            else
            {
                name = mlist.get(position).getItem().get("name").toString().toUpperCase();

                String year = mlist.get(position).getItem().get("first_air_date").toString().split("-")[0];
                type_year = "TV ("+ year +")";
                type_year = type_year.toUpperCase();
            }

            float rating = (float) 0.0;

            rateStr = mlist.get(position).getItem().get("vote_average").toString();

            if(rateStr!=null && rateStr!="null")
            {

                rating = Float.valueOf(rateStr);
            }

            rateStr = String.valueOf(rating/2);


            holder.type_year.setText(type_year);
            holder.rate.setText(rateStr);
            holder.title.setText(name);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String ID="";
        String type=mlist.get(position).getType();

        try {
            ID = mlist.get(position).getItem().get("id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    }

    @Override
    public int getItemCount() {
        return this.mlist.size();
    }

    class linearViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView type_year;
        private TextView rate;
        private TextView title;

        public linearViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.search_item_image);
            type_year = itemView.findViewById(R.id.search_item_type_year);
            rate = itemView.findViewById(R.id.search_item_rate);
            title = itemView.findViewById(R.id.search_item_name);

        }
    }
}

