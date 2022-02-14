package com.example.uscfilms.ui.notifications;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.uscfilms.DetailActivity;
import com.example.uscfilms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import util.*;

import java.util.*;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    private SharedPreferences pref;

    private TextView tv;
    private RecyclerView watchlist_rv;

    private watchlistAdapter mAdapter;
    private ArrayList<JSONObject> mDatas;

    String watchStr=null;
    JSONArray jsonWatch=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        new common().translucentStatusBar(getActivity(),true);

        tv = root.findViewById(R.id.watchlist_text);
        tv.setVisibility(View.GONE);

        watchlist_rv = root.findViewById(R.id.watchlist_rv);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(watchlist_rv);

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    private void init() throws JSONException {

        watchlist_rv.setLayoutManager(new GridLayoutManager(getActivity(),3));

        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);

        /*
        Editor editor = pref.edit();
        editor.clear();
        editor.commit(); // commit changes
        */


        String watchStr = pref.getString("Watchlist",null);



        if(watchStr!=null)
        {

            jsonWatch = new JSONArray(watchStr);

            if(jsonWatch.length()>0)
            {
                // we are creating array list for storing our image urls.
                mDatas = new ArrayList<>();

                // adding the urls inside array list

                for(int i=0;i<jsonWatch.length();i++)
                {
                    //得到每一个key

                    try {

                        JSONObject itemObj = jsonWatch.getJSONObject(i);
                        mDatas.add(itemObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                mAdapter = new watchlistAdapter(getActivity(),mDatas,tv);
                watchlist_rv.setAdapter(mAdapter);

            }
            else
            {
                watchlist_rv.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
            }

        }
        else
        {
            watchlist_rv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT,0)
    {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            /*
            if (fromPosition < toPosition) {

                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDatas, i, i + 1);
                }

            } else {

                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDatas, i, i - 1);
                }

            }
            */

            Collections.swap(mDatas,fromPosition,toPosition);

            mAdapter.notifyItemMoved(fromPosition, toPosition);

            mAdapter.notifyDataSetChanged();

            jsonWatch = new JSONArray(mDatas);

            Editor editor = pref.edit();
            editor.remove("Watchlist");
            editor.putString("Watchlist",jsonWatch.toString());
            editor.commit(); // commit changes

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };


}