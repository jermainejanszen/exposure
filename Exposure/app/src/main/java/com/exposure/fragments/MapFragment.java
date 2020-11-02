package com.exposure.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.exposure.R;
import com.exposure.activities.MainActivity;
import com.exposure.activities.ViewOtherProfileActivity;
import com.exposure.adapters.MapListItem;
import com.exposure.adapters.MapRecyclerViewAdapter;
import com.exposure.callback.OnCompleteCallback;
import com.exposure.callback.OnMapItemPressedCallback;
import com.exposure.constants.RequestCodes;
import com.exposure.handlers.DistanceHandler;
import com.exposure.handlers.UserInformationHandler;
import com.exposure.user.CurrentUser;

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

    private List<CurrentUser> allUsers;

    private MapRecyclerViewAdapter fifteenMapAdapter;
    private MapRecyclerViewAdapter nineMapAdapter;
    private MapRecyclerViewAdapter sixMapAdapter;
    private MapRecyclerViewAdapter threeMapAdapter;
    private MapRecyclerViewAdapter zeroMapAdapter;

    private View view;
    private RelativeLayout loadingCover;

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

    /**
     * On creating the view, TODO
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        loadingCover = view.findViewById(R.id.map_loading_cover);
        loadingCover.setVisibility(View.VISIBLE);

        assert null != getActivity();

        allUsers = new ArrayList<>();

        UserInformationHandler.downloadOtherUsers(allUsers, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    setup(view);
                }
            }
        });

        return view;
    }

    /**
     * TODO
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RequestCodes.VIEW_PROFILE_REQUEST == requestCode) {
            loadingCover.setVisibility(View.VISIBLE);
            allUsers.clear();
            UserInformationHandler.downloadOtherUsers(allUsers, new OnCompleteCallback() {
                @Override
                public void update(boolean success, String message) {
                    if (success) {
                        setup(view);
                    }
                }
            });
        }
    }

    /**
     * Sets up the map view showing all users, ordered on their screen by their geographical
     * distance from the current user
     * @param view the view
     */
    private void setup(View view) {

        OnMapItemPressedCallback callback = new OnMapItemPressedCallback() {
            @Override
            public void onPress(String uid) {
                onMapItemPressed(uid);
            }
        };

        if (null == fifteenKM) {
            fifteenKM = new ArrayList<>();
        } else {
            fifteenKM.clear();
        }

        if (null == nineKM) {
            nineKM = new ArrayList<>();
        } else {
            nineKM.clear();
        }

        if (null == sixKM) {
            sixKM = new ArrayList<>();
        } else {
            sixKM.clear();
        }

        if (null == threeKM) {
            threeKM = new ArrayList<>();
        } else {
            threeKM.clear();
        }

        if (null == zeroKM) {
            zeroKM = new ArrayList<>();
        } else {
            zeroKM.clear();
        }

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

    /**
     * Upon clicking on another user's image on the map, user is taken to view their profile
     * @param uid the uid of the other user who's image has been clicked on
     */
    private void onMapItemPressed(String uid) {
        Intent intent = new Intent(getContext(), ViewOtherProfileActivity.class);
        intent.putExtra("Uid", uid);
        startActivityForResult(intent, RequestCodes.VIEW_PROFILE_REQUEST);
    }

    /**
     * Adds users to the different distance ranges on the maps page according to their geographical
     * distance from the current user
     */
    private void mapUsersToRegions() {
        final OnCompleteCallback finishedCallback = new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    fifteenMapAdapter.notifyDataSetChanged();
                    nineMapAdapter.notifyDataSetChanged();
                    sixMapAdapter.notifyDataSetChanged();
                    threeMapAdapter.notifyDataSetChanged();
                    zeroMapAdapter.notifyDataSetChanged();
                }
                loadingCover.setVisibility(View.INVISIBLE);
            }
        };

        final OnCompleteCallback intermediateCallback = new OnCompleteCallback() {
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
        final CurrentUser currentUser = MainActivity.getCurrentUser();

        /* download all connections of the current user */
        UserInformationHandler.downloadCurrentUserConnections(currentUser, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    boolean newConnections = false;

                    for (int i = 0; i < allUsers.size(); i++) {
                        CurrentUser otherUser = allUsers.get(i);

                        if (currentUser.isConnected(otherUser.getUid())) {
                            continue;
                        }

                        OnCompleteCallback notifyCallback =
                                (i == allUsers.size() - 1) ? finishedCallback : intermediateCallback;

                        newConnections = true;

                        /* add user to the correct distance range according to their distance
                        * from the current user */
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
                    if (!newConnections) {
                        finishedCallback.update(true, "finished");
                    }
                } else {
                    finishedCallback.update(false, "failed to download user data");
                }
            }
        });
    }
}