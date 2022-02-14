package util;

import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;
import java.text.*;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import com.example.uscfilms.DetailActivity;
import com.example.uscfilms.R;
import com.example.uscfilms.ReviewActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.linearViewHolder> {

    private Context mContext;

    private ArrayList<JSONObject> mlist;

    public reviewAdapter(Context context, ArrayList<JSONObject> list)
    {
        this.mContext = context;
        this.mlist = list;
    }


    @NonNull
    @Override
    public reviewAdapter.linearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new linearViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.review_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull reviewAdapter.linearViewHolder holder, final int position) {

        try {

            String author = mlist.get(position).get("author").toString();

            String dateStr = mlist.get(position).getString("created_at");

            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss");

            Date date = df2.parse(dateStr);

            DateFormat df5 = new SimpleDateFormat("E, MMM dd yyyy");

            String final_date = df5.format(date);

            String name_date = "by "+author+" on "+final_date;

            holder.tv_name_date.setText(name_date);

            float rating = (float) 0.0;

            String rateStr = mlist.get(position).getJSONObject("author_details").get("rating").toString();

            if(rateStr!=null && rateStr!="null")
            {

                rating = Float.valueOf(rateStr);
            }

            String rate = rating/2 + "/5";

            holder.tv_rating.setText(rate);

            String content = mlist.get(position).get("content").toString();

            if(content==null)
            {
                content = "";
            }

            holder.tv_content.setText(content);

            //click
            holder.itemView.setTag(new reviewData(rate,name_date,content));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    reviewData rD = (reviewData) v.getTag();

                    Intent intent = new Intent(mContext, ReviewActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("Rate",rD.getRating());
                    bundle.putString("Name_date",rD.getName_date());
                    bundle.putString("Content",rD.getContent());
                    intent.putExtras(bundle);

                    mContext.startActivity(intent);

                }
            });

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return this.mlist.size();
    }

    class linearViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name_date;
        private TextView tv_rating;
        private TextView tv_content;

        public linearViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name_date = itemView.findViewById(R.id.review_item_name_date);
            tv_rating = itemView.findViewById(R.id.review_item_rating);
            tv_content = itemView.findViewById(R.id.review_item_content);

        }
    }
}
