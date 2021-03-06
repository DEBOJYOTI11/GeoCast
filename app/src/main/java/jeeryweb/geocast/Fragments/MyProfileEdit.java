package jeeryweb.geocast.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import jeeryweb.geocast.Activities.Home;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class MyProfileEdit extends Fragment {

    private TextView doneTextButton;
    private EditText occupationPP,agePP,genderPP,phonePP;
    private SharedPrefHandler sharedPrefHandler;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;
    String bio , phnumfin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.activity_my_profile_edit_fragment, container, false);

        requestQueue = Volley.newRequestQueue(getContext());
        sharedPrefHandler = new SharedPrefHandler(getContext());

        occupationPP = (EditText) view.findViewById(R.id.activity_myprofile_profession);
        agePP = (EditText) view.findViewById(R.id.activity_myprofile_age);
        genderPP = (EditText) view.findViewById(R.id.activity_myprofile_gender);
        phonePP = (EditText) view.findViewById(R.id.activity_myprofile_phno);
        doneTextButton  = (TextView)view.findViewById(R.id.activity_myprofile_donebutton);


        if(sharedPrefHandler.getOccupation() != null)
            occupationPP.setText(sharedPrefHandler.getOccupation());
        if(sharedPrefHandler.getAge() != null)
            agePP.setText(sharedPrefHandler.getAge());
        if(sharedPrefHandler.getGender() != null)
            genderPP.setText(sharedPrefHandler.getGender());
        if(sharedPrefHandler.getPhoneNo() != null)
            phonePP.setText(sharedPrefHandler.getPhoneNo());

        doneTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create fragment and give it an argument specifying the article it should show

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Updating");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                String occu = occupationPP.getText().toString();
                String ag = agePP.getText().toString();
                String gen = genderPP.getText().toString();
                String phno = phonePP.getText().toString();

                //check if phone no is valid
                phnumfin = phno;

                bio = ag+"|"+occu+"|"+gen;

                sharedPrefHandler.saveBio(occu,gen,ag,phno);

                //update Bio in database
                StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.updateBio,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();

                                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                                    //if(response.contains("complete"))
                                    replaceFragment();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Username", Home.username);
                        params.put("Bio", bio);
                        params.put("PhoneNo", phnumfin);

                        return params;
                    }
                };

                requestQueue.add(stringRequest);




                //manager.popBackStack();

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void replaceFragment()
    {
        MyProfileShow newFragment = new MyProfileShow();
        Bundle args = new Bundle();
        //args.putInt(MyProfileEdit.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentManager manager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();



        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.profile_fragment_replace, newFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }
}
