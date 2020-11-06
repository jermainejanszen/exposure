package com.exposure.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
     * Called upon creating the activity
     * @param savedInstanceState saved instance state for the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            return;
        }

        final DocumentReference userProfileRef = FirebaseFirestore.getInstance()
                .collection("Profiles")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userProfileRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (null != error) {
                    return;
                }

                if (null != value && value.exists()) {
                    final CurrentUser currentUser = new CurrentUser(
                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                    UserInformationHandler.convertDocumentSnapshotToUser(value, currentUser);
                    UserInformationHandler.downloadCurrentUserConnections(
                            currentUser,
                            new OnCompleteCallback() {
                        @Override
                        public void update(boolean success, String message) {
                            for (int i = allUsers.size() - 1; i >= 0; i--) {
                                CurrentUser user = allUsers.get(i);
                                if (currentUser.isConnected(user.getUid())) {
                                    allUsers.remove(i);
                                    removeConnectedUserFromRegions(user.getUid());
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Initialises the view of the maps fragment and loads any of the required
     * information if it has not already been loaded.
     * @param inflater inflater to convert the maps fragment xml to a view
     * @param container view group for the view
     * @param savedInstanceState previously save instance state
     * @return newly created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        view = inflater.inflate(R.layout.fragment_map, container, false);

        loadingCover = view.findViewById(R.id.map_loading_cover);

        assert null != getActivity();

        /* Load all users if they haven't already been loaded */
        if (null == allUsers) {
            loadingCover.setVisibility(View.VISIBLE);
            allUsers = new ArrayList<>();
            updateMapUsers();
        } else {
            setup(view);
        }

        return view;
    }

    /**
     * Updates the current users map items to accommodate for new users as well as new
     * connections
     */
    private void updateMapUsers() {
        UserInformationHandler.downloadOtherUsers(allUsers, new OnCompleteCallback() {
            @Override
            public void update(boolean success, String message) {
                if (success) {
                    setup(view);
                    mapUsersToRegions();
                }
            }
        });
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
        }

        if (null == nineKM) {
            nineKM = new ArrayList<>();
        }

        if (null == sixKM) {
            sixKM = new ArrayList<>();
        }

        if (null == threeKM) {
            threeKM = new ArrayList<>();
        }

        if (null == zeroKM) {
            zeroKM = new ArrayList<>();
        }

        if (null == fifteenMapAdapter) {
            fifteenMapAdapter = new MapRecyclerViewAdapter(fifteenKM, callback);
        }

        if (null == nineMapAdapter) {
            nineMapAdapter = new MapRecyclerViewAdapter(nineKM, callback);
        }

        if (null == sixMapAdapter) {
            sixMapAdapter = new MapRecyclerViewAdapter(sixKM, callback);
        }

        if (null == threeMapAdapter) {
            threeMapAdapter = new MapRecyclerViewAdapter(threeKM, callback);
        }

        if (null == zeroMapAdapter) {
            zeroMapAdapter = new MapRecyclerViewAdapter(zeroKM, callback);
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
    }

    /**
     * Adds users to the different distance ranges on the maps page according to their geographical
     * distance from the current user
     */
    private void mapUsersToRegions() {

        final CurrentUser currentUser = MainActivity.getCurrentUser();

        OnCompleteCallback callback = new OnCompleteCallback() {
            int calls = 0;

            @Override
            public synchronized void update(boolean success, String message) {
                calls += 1;

                if (calls >= allUsers.size()) {
                    fifteenMapAdapter.notifyDataSetChanged();
                    nineMapAdapter.notifyDataSetChanged();
                    sixMapAdapter.notifyDataSetChanged();
                    threeMapAdapter.notifyDataSetChanged();
                    zeroMapAdapter.notifyDataSetChanged();
                    loadingCover.setVisibility(View.INVISIBLE);
                }
            }
        };

        for (int i = allUsers.size() - 1; i >= 0; i--) {
            CurrentUser user = allUsers.get(i);
            if (currentUser.isConnected(user.getUid())) {
                allUsers.remove(i);
            }
        }

        for (int i = 0; i < allUsers.size(); i++) {
            CurrentUser otherUser = allUsers.get(i);

            int distance = DistanceHandler.distanceInKM(currentUser, otherUser);
            if (distance <= 2) {
                zeroKM.add(new MapListItem(otherUser.getUid(), callback));
            } else if (distance <= 5) {
                threeKM.add(new MapListItem(otherUser.getUid(), callback));
            } else if (distance <= 8) {
                sixKM.add(new MapListItem(otherUser.getUid(), callback));
            } else if (distance <= 15) {
                nineKM.add(new MapListItem(otherUser.getUid(), callback));
            } else {
                fifteenKM.add(new MapListItem(otherUser.getUid(), callback));
            }
        }
    }

    /**
     * Removes the user with the given idea from their respective container.
     * Called to remove a user once they have been connected.
     * @param uid uid of the user to remove
     */
    private void removeConnectedUserFromRegions(String uid) {

        for (MapListItem item : zeroKM) {
            if (item.getUid().equals(uid)) {
                zeroKM.remove(item);
                zeroMapAdapter.notifyDataSetChanged();
                return;
            }
        }

        for (MapListItem item : threeKM) {
            if (item.getUid().equals(uid)) {
                threeKM.remove(item);
                threeMapAdapter.notifyDataSetChanged();
                return;
            }
        }

        for (MapListItem item : sixKM) {
            if (item.getUid().equals(uid)) {
                sixKM.remove(item);
                sixMapAdapter.notifyDataSetChanged();
                return;
            }
        }

        for (MapListItem item : nineKM) {
            if (item.getUid().equals(uid)) {
                nineKM.remove(item);
                nineMapAdapter.notifyDataSetChanged();
                return;
            }
        }

        for (MapListItem item : fifteenKM) {
            if (item.getUid().equals(uid)) {
                fifteenKM.remove(item);
                fifteenMapAdapter.notifyDataSetChanged();
                return;
            }
        }

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
}