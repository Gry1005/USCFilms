package util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.uscfilms.DetailActivity;
import com.example.uscfilms.R;

import org.json.JSONException;

import java.util.ArrayList;

public class recAdapter extends RecyclerView.Adapter<recAdapter.linearViewHolder> {

    private Context mContext;

    private ArrayList<HorRvData> mlist;

    private int curPos;


    public recAdapter(Context context, ArrayList<HorRvData> list)
    {
        this.mContext = context;
        this.mlist = list;
    }


    @NonNull
    @Override
    public recAdapter.linearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new linearViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.rec_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull recAdapter.linearViewHolder holder, final int position) {

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

    }

    @Override
    public int getItemCount() {
        return this.mlist.size();
    }

    class linearViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public linearViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.rec_item_image);

        }
    }
}

