package com.exposure.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exposure.R;
import com.exposure.activities.ViewOtherProfileActivity;
import com.exposure.adapters.MapListItem;
import com.exposure.adapters.MapRecyclerViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.callback.OnMapItemPressedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment representing the map displaying all users available to match within, organised
 * according to their geographical distance from the current user
 */
public class MapFragment extends Fragment {

    private List<MapListItem> fifteenKM;
    private List<MapListItem> nineKM;
    private List<MapListItem> sixKM;
    private List<MapListItem> threeKM;
    private List<MapListItem> zeroKM;

    private MapRecyclerViewAdapter fifteenMapAdapter;
    private MapRecyclerViewAdapter nineMapAdapter;
    private MapRecyclerViewAdapter sixMapAdapter;
    private MapRecyclerViewAdapter threeMapAdapter;
    private MapRecyclerViewAdapter zeroMapAdapter;

    /**
     * Empty constructor for the map fragment
     */
    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Call upon creating the activity
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        assert null != getActivity();

        /* Placeholders */
        fifteenKM = new ArrayList<>();
        nineKM = new ArrayList<>();
        sixKM = new ArrayList<>();
        threeKM = new ArrayList<>();
        zeroKM = new ArrayList<>();

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

        OnMapItemPressedCallback callback = new OnMapItemPressedCallback() {
            @Override
            public void onPress(String uid) {
                onMapItemPressed(uid);
            }
        };

        if (null == fifteenMapAdapter) {
            fifteenMapAdapter = new MapRecyclerViewAdapter(fifteenKM, callback);
            fifteenKM.add(new MapListItem("uqWWk2SuhbenapK0ANzeT0kMebY2", notifyCallback));
        } else {
            fifteenMapAdapter.syncData();
        }

        if (null == nineMapAdapter) {
            nineMapAdapter = new MapRecyclerViewAdapter(nineKM, callback);
            nineKM.add(new MapListItem("lGSXDdEAlFaogmrWTHgVuxJHMmi1", notifyCallback));
        } else {
            nineMapAdapter.syncData();
        }

        if (null == sixMapAdapter) {
            sixMapAdapter = new MapRecyclerViewAdapter(sixKM, callback);
            sixKM.add(new MapListItem("PTIDi7lEIkb7PMOD7S4ihbPTecT2", notifyCallback));
        } else {
            sixMapAdapter.syncData();
        }

        if (null == threeMapAdapter) {
            threeMapAdapter = new MapRecyclerViewAdapter(threeKM, callback);
            threeKM.add(new MapListItem("LSBexRMVWrhjjS1bxOiRbsQ5D503", notifyCallback));
        } else {
            threeMapAdapter.syncData();
        }

        if (null == zeroMapAdapter) {
            zeroMapAdapter = new MapRecyclerViewAdapter(zeroKM, callback);
        } else {
            zeroMapAdapter.syncData();
        }

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

        return view;
    }

    /**
     * Upon clicking on another user's image on the map, user is taken to view their profile
     * @param uid the uid of the other user who's image has been clicked on
     */
    private void onMapItemPressed(String uid) {
        Intent intent = new Intent(getContext(), ViewOtherProfileActivity.class);
        intent.putExtra("Uid", uid);
        getContext().startActivity(intent);
    }
}