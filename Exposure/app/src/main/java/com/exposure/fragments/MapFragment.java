package com.exposure.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exposure.R;
import com.exposure.adapters.MapListItem;
import com.exposure.adapters.MapRecyclerViewAdapter;
import com.exposure.callback.OnCompleteCallback;

import java.util.ArrayList;
import java.util.List;

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

        fifteenKM.add(new MapListItem("uqWWk2SuhbenapK0ANzeT0kMebY2", notifyCallback));
        nineKM.add(new MapListItem("lGSXDdEAlFaogmrWTHgVuxJHMmi1", notifyCallback));
        sixKM.add(new MapListItem("PTIDi7lEIkb7PMOD7S4ihbPTecT2", notifyCallback));
        threeKM.add(new MapListItem("LSBexRMVWrhjjS1bxOiRbsQ5D503", notifyCallback));

        fifteenMapAdapter = new MapRecyclerViewAdapter(getActivity(), fifteenKM);
        nineMapAdapter = new MapRecyclerViewAdapter(getActivity(), nineKM);
        sixMapAdapter = new MapRecyclerViewAdapter(getActivity(), sixKM);
        threeMapAdapter = new MapRecyclerViewAdapter(getActivity(), threeKM);
        zeroMapAdapter = new MapRecyclerViewAdapter(getActivity(), zeroKM);

        RecyclerView fifteenMapRecyclerView = view.findViewById(R.id.map_15k_recycler);
        fifteenMapRecyclerView.setAdapter(fifteenMapAdapter);
        RecyclerView nineMapRecyclerView = view.findViewById(R.id.map_9k_recycler);
        nineMapRecyclerView.setAdapter(nineMapAdapter);
        RecyclerView sixMapRecyclerView = view.findViewById(R.id.map_6k_recycler);
        sixMapRecyclerView.setAdapter(sixMapAdapter);
        RecyclerView threeMapRecyclerView = view.findViewById(R.id.map_3k_recycler);
        threeMapRecyclerView.setAdapter(threeMapAdapter);
        RecyclerView zeroMapRecyclerView = view.findViewById(R.id.map_0k_recycler);
        zeroMapRecyclerView.setAdapter(zeroMapAdapter);



        return view;
    }


}