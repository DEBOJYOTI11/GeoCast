package jeeryweb.geocast.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import jeeryweb.geocast.Activities.ReliabilityResponse;
import jeeryweb.geocast.Models.InboxRowRecord;
import jeeryweb.geocast.Models.ReliabilitiesRowRecord;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Activities.UserProfileView;
import jeeryweb.geocast.databinding.ReliabilitiesRowRecordBinding;

public class ReliabilitiesListviewAdapter {

    ReliabilitiesListviewAdapter.ReliabilitiesRowRecordAdapter reliabilitiesRowRecordAdapter;
    ListView lv;
    Context con;
    List<ReliabilitiesRowRecord> rows;
    final String TAG ="Reliabilities";
    RequestQueue rq;

    public void recordsInListview(Context con, ListView lv, Activity activity, List<ReliabilitiesRowRecord> rows) {
        reliabilitiesRowRecordAdapter=new ReliabilitiesListviewAdapter.ReliabilitiesRowRecordAdapter(activity ,rows);
        this.lv=lv;
        this.rows=rows;
        this.con=con;
        lv.setAdapter(reliabilitiesRowRecordAdapter);
        rq = Volley.newRequestQueue(con);

    }
    public class ReliabilitiesRowRecordAdapter extends BaseAdapter implements Filterable {
        LayoutInflater inflater;
        Activity activity;
        ReliabilitiesListviewAdapter.ReliabilitiesRowRecordAdapter.ValueFilter valueFilter;
        List rows;
        List filter;

        public ReliabilitiesRowRecordAdapter( Activity activity, List rows) {
            this.activity = activity;
            this.filter=rows;
            this.rows = rows;
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(inflater==null)
                inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ReliabilitiesRowRecordBinding reliabilitiesRowRecordBinding= DataBindingUtil.inflate(inflater, R.layout.reliabilities_row_record ,parent,false);
            final ReliabilitiesRowRecord reliabilitiesRowRecord=(ReliabilitiesRowRecord) rows.get(position);
            Log.v("Reliabilities","pos= "+position+" ");
            /*
            if(position%2==0)
                rowRecordBinding.backRow.setBackgroundColor(Color.LTGRAY);
            else rowRecordBinding.backRow.setBackgroundColor(Color.GRAY);
            */
            reliabilitiesRowRecordBinding.Name.setText(reliabilitiesRowRecord.sender);
            String urlPic = reliabilitiesRowRecord.picture;
            int userID = reliabilitiesRowRecord.userID;

            Log.e("Reliabilities", "Url is "+urlPic);
            if(!urlPic.equals("NA")){
                Log.e("Reliabilities", "Url is "+urlPic);
                ImageRequest ir = new ImageRequest(urlPic,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                Log.e("Reliabilities", "Recieved response");
                                reliabilitiesRowRecordBinding.profileImage.setImageBitmap(response);
                            }
                        }, 0, 0, null, null);
                rq.add(ir);
            }

            reliabilitiesRowRecordBinding.displayLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open new fragment without nav bar
                    //show location of origin of message
                    //and path from current location to destination
                    Log.e("Reliabilities", "Clicked in adapter");

                    if(reliabilitiesRowRecord.type==0){
                        Intent i = new Intent(con, ReliabilityResponse.class);
                        i.putExtra("RReqUsername",reliabilitiesRowRecord.sender);
                        con.startActivity(i);
                    }
                    else if(reliabilitiesRowRecord.type==1){
                        Intent i = new Intent(con, UserProfileView.class);
                        i.putExtra("userID", reliabilitiesRowRecord.userID);
                        i.putExtra("Username", reliabilitiesRowRecord.sender);
                        con.startActivity(i);
                    }

                }
            });

            return reliabilitiesRowRecordBinding.getRoot();
        }

        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ReliabilitiesListviewAdapter.ReliabilitiesRowRecordAdapter.ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {
            InboxRowRecord inboxRowRecord;
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    List<InboxRowRecord> filterList = new ArrayList<>();
                    for (int i = 0; i < filter.size(); i++) {
                        inboxRowRecord= (InboxRowRecord) filter.get(i);
                        if ((inboxRowRecord.sender.toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(inboxRowRecord);
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = filter.size();
                    results.values = filter;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                rows = (List<InboxRowRecord>) results.values;
                notifyDataSetChanged();
            }

        }

    }
    //handling search in table rows view
    public void handleSearch(String newText)
    {
        reliabilitiesRowRecordAdapter.getFilter().filter(newText);
    }


}
