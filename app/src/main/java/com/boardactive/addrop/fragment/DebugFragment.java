package com.boardactive.addrop.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.boardactive.addrop.R;
import com.boardactive.addrop.activity.MessagesActivity;
import com.boardactive.addrop.model.User;
import com.boardactive.bakit.BoardActive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebugFragment extends Fragment {
    private static final String TAG = "DebugFragment";

    private BoardActive mBoardActive;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DebugFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HelpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpFragment newInstance(String param1, String param2) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBoardActive = new BoardActive(getContext());

        View view =  inflater.inflate(R.layout.fragment_debug, container, false);

        AppCompatButton btnViewMessages = view.findViewById(R.id.btn_view_messages);
        btnViewMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesActivity.navigate(getActivity());
            }
        });


        AppCompatButton btnFCMToken = view.findViewById(R.id.btn_fcm_token);
        btnFCMToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                String fcmToken = task.getResult().getToken();
                                showAlert("FCM Token", fcmToken);
                            }
                        });

            }
        });

        AppCompatButton btnAppVars = view.findViewById(R.id.btn_app_vars);
        btnAppVars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("appUrl", mBoardActive.getAppUrl());
                    obj.put("appId", mBoardActive.getAppId());
                    obj.put("appKey", mBoardActive.getAppKey());
                    showAlert("Show App Info", obj.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        AppCompatButton btnRegisterDevice = view.findViewById(R.id.btn_register_device);
        btnRegisterDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    String fcmToken = task.getResult().getToken();
                                    mBoardActive.setAppToken(fcmToken);

                                    mBoardActive.initialize();

                                    mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
                                        @Override
                                        public void onResponse(Object value) {
                                            Log.d("[BAkit]", value.toString());

                                            Gson gson = new Gson();
                                            User user = gson.fromJson(value.toString(), User.class);
                                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("USER_DETAILS", user.toString());
                                            editor.commit();
                                            showAlert("User Details", value.toString());
                                        }
                                    });

                                }
                            });
                }
        });

        AppCompatButton btnPostLocation = view.findViewById(R.id.btn_post_location);
        btnPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        AppCompatButton btnPostEvent = view.findViewById(R.id.btn_post_event);
        btnPostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void FCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String fcmToken = task.getResult().getToken();
                        showAlert("FCM Token", fcmToken);
                    }
                });

    }

    public void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private String getSharedPreference(String name) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String value = settings.getString(name,"");
        return value;
    }


}
