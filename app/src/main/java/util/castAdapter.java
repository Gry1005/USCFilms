package util;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import com.example.uscfilms.R;

public class castAdapter extends RecyclerView.Adapter<castAdapter.linearViewHolder> {

    private Context mContext;

    private ArrayList<castData> mlist;

    public castAdapter(Context context, ArrayList<castData> list)
    {
        this.mContext = context;
        this.mlist = list;
    }


    @NonNull
    @Override
    public castAdapter.linearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new linearViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.cast_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull castAdapter.linearViewHolder holder, final int position) {

        holder.tv_name.setText(mlist.get(position).getName());

        Glide.with(holder.itemView)
                .load(mlist.get(position).getImgUrl())
                .fitCenter()
                .into(holder.iv_profile);

    }

    @Override
    public int getItemCount() {
        return this.mlist.size();
    }

    class linearViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private CircleImageView iv_profile;

        public linearViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.cast_item_name);
            iv_profile = itemView.findViewById(R.id.cast_item_image);

        }
    }
}
