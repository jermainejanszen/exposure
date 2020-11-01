package com.exposure.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exposure.R;
import com.exposure.activities.MainActivity;
import com.exposure.activities.ViewOtherProfileActivity;
import com.exposure.adapters.MapListItem;
import com.exposure.adapters.MapRecyclerViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.callback.OnMapItemPressedCallback;
import com.exposure.handlers.DistanceHandler;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.user.CurrentUser;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class MapFragment extends Fragment {

    private List<MapListItem> fifteenKM;
    private List<MapListItem> nineKM;
    private List<MapListItem> sixKM;
    private List<MapListItem> threeKM;
    private List<MapListItem> zeroKM;

    private List<CurrentUser> allUsers;

    private MapRecyclerViewAdapter fifteenMapAdapter;
    private MapRecyclerViewAdapter nineMapAdapter;
    private MapRecyclerViewAdapter sixMapAdapter;
    private MapRecyclerViewAdapter threeMapAdapter;
    private MapRecyclerViewAdapter zeroMapAdapter;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        assert null != getActivity();

        allUsers = new ArrayList<>();

        UserInformationHandler.downloadUsers(allUsers, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    setup(view);
                }
            }
        });


        return view;
    }

    private void setup(View view) {

        /* Placeholders */
        fifteenKM = new ArrayList<>();
        nineKM = new ArrayList<>();
        sixKM = new ArrayList<>();
        threeKM = new ArrayList<>();
        zeroKM = new ArrayList<>();

        OnMapItemPressedCallback callback = new OnMapItemPressedCallback() {
            @Override
            public void onPress(String uid) {
                onMapItemPressed(uid);
            }
        };

        if (null == fifteenMapAdapter) {
            fifteenMapAdapter = new MapRecyclerViewAdapter(fifteenKM, callback);
        } else {
            fifteenMapAdapter.syncData();
        }

        if (null == nineMapAdapter) {
            nineMapAdapter = new MapRecyclerViewAdapter(nineKM, callback);
        } else {
            nineMapAdapter.syncData();
        }

        if (null == sixMapAdapter) {
            sixMapAdapter = new MapRecyclerViewAdapter(sixKM, callback);
        } else {
            sixMapAdapter.syncData();
        }

        if (null == threeMapAdapter) {
            threeMapAdapter = new MapRecyclerViewAdapter(threeKM, callback);
        } else {
            threeMapAdapter.syncData();
        }

        if (null == zeroMapAdapter) {
            zeroMapAdapter = new MapRecyclerViewAdapter(zeroKM, callback);
        } else {
            zeroMapAdapter.syncData();
        }

        mapUsersToRegions();

        RecyclerView fifteenMapRecyclerView = view.findViewById(R.id.map_15k_recycler);
        RecyclerView nineMapRecyclerView = view.findViewById(R.id.map_9k_recycler);
        RecyclerView sixMapRecyclerView = view.findViewById(R.id.map_6k_recycler);
        RecyclerView threeMapRecyclerView = view.findViewById(R.id.map_3k_recycler);
        RecyclerView zeroMapRecyclerView = view.findViewById(R.id.map_0k_recycler);

        fifteenMapRecyclerView.setAdapter(fifteenMapAdapter);
        nineMapRecyclerView.setAdapter(nineMapAdapter);
        sixMapRecyclerView.setAdapter(sixMapAdapter);
        threeMapRecyclerView.setAdapter(threeMapAdapter);
        zeroMapRecyclerView.setAdapter(zeroMapAdapter);
    }

    private void onMapItemPressed(String uid) {
        Intent intent = new Intent(getContext(), ViewOtherProfileActivity.class);
        intent.putExtra("Uid", uid);
        getContext().startActivity(intent);
    }

    private void mapUsersToRegions() {
        OnCompleteCallback notifyCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    fifteenMapAdapter.notifyDataSetChanged();
                    nineMapAdapter.notifyDataSetChanged();
                    sixMapAdapter.notifyDataSetChanged();
                    threeMapAdapter.notifyDataSetChanged();
                    zeroMapAdapter.notifyDataSetChanged();
                }
            }
        };

        //TODO: Perhaps be better to pass in current user as a serializable
        CurrentUser currentUser = MainActivity.getCurrentUser();

        for (CurrentUser otherUser: allUsers) {
            if (otherUser.getUid().equals(currentUser.getUid())) {
                continue;
            }
            int distance = DistanceHandler.distanceInKM(currentUser, otherUser);
            if (distance <= 2) {
                zeroKM.add(new MapListItem(otherUser.getUid(), notifyCallback));
            } else if (distance <= 5) {
                threeKM.add(new MapListItem(otherUser.getUid(), notifyCallback));
            } else if (distance <= 8) {
                sixKM.add(new MapListItem(otherUser.getUid(), notifyCallback));
            } else if (distance <= 15) {
                nineKM.add(new MapListItem(otherUser.getUid(), notifyCallback));
            } else {
                fifteenKM.add(new MapListItem(otherUser.getUid(), notifyCallback));
            }
        }
    }
}