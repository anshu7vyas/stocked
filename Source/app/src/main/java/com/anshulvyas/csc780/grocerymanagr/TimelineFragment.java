package com.anshulvyas.csc780.grocerymanagr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.anshulvyas.csc780.grocerymanagr.Adapters.TimelineAdapter;
import com.anshulvyas.csc780.grocerymanagr.Model.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Hosts the listView of all the items that user has kept the track of
 * - with appropriate flags, i.e., stocked, expired or consumed.
 */
public class TimelineFragment extends Fragment {

    private List<Product> timelineList, filterTimelineList;
    private DBManager dbManager;
    private ListView timelineListView;

    /**
     * Called to do initial creation of a fragment. (overridden)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DBManager(getActivity());
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        filterTimelineList = new ArrayList<>();

        timelineList = dbManager.getAllProducts();
        if (timelineList.size() > 0) {
            Log.i("~!@#TIMELINEFRAGMENT", timelineList.get(0).toString());

            /**
             * Checking for the items that are in the shopping list items and not in the product items.
             */
            for (int i = 0; i < timelineList.size(); i++) {
                if (!timelineList.get(i).isShoppingCheck()) {
                    filterTimelineList.add(timelineList.get(i));
                }
            }

            timelineListView = (ListView) view.findViewById(R.id.listView_timeline_product);
            final TimelineAdapter timelineAdapter = new TimelineAdapter(getActivity().getBaseContext(), R.layout.list_view_timeline,
                    filterTimelineList);
            timelineListView.setAdapter(timelineAdapter);

            timelineAdapter.setNotifyOnChange(true);
            timelineAdapter.notifyDataSetChanged();

        } else {
            Log.i("~!@#TIMELINEFRAGMENT", "list view not displayed");
        }
        return view;

    }
}
