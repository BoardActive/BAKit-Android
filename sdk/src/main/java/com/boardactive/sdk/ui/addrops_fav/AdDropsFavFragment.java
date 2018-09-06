package com.boardactive.sdk.ui.addrops_fav;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.boardactive.sdk.R;
import com.boardactive.sdk.adapters.AdDropsAdapter;
import com.boardactive.sdk.models.AdDrops;
import com.boardactive.sdk.ui.addrops.AdDropsPresenter;
import com.boardactive.sdk.ui.addrops.AdDropsViewInterface;

import java.util.List;

public class AdDropsFavFragment  extends Fragment implements AdDropsFavViewInterface {

    RecyclerView rvAdDrops;
    ProgressBar progressBar;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager mViewPager;

    private String TAG = "AdDropsFavFragment";

    RecyclerView.Adapter adapter;
    AdDropsFavPresenter addropsFavPresenter;


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public AdDropsFavFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ad_drops, container, false);

        rvAdDrops = (RecyclerView) rootView.findViewById(R.id.rvAdDrops);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        setupMVP();
        setupViews();
        getAdDropList();

        return rootView;
    }

    private void setupMVP() {
        addropsFavPresenter = new AdDropsFavPresenter(this);
    }

    private void setupViews(){
        rvAdDrops.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getAdDropList() {

        addropsFavPresenter.getAdDropsFav(getActivity().getBaseContext());

    }

    @Override
    public void showToast(String str) {
        Toast.makeText(getActivity(),str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayAdDrops(List<AdDrops> addrops) {
        if(addrops!=null) {
            adapter = new AdDropsAdapter(addrops, getActivity());
            rvAdDrops.setAdapter(adapter);
        }else{
            Log.d(TAG,"AdDropsFav response null");
        }
    }

    @Override
    public void displayError(String e) {

        showToast(e);

    }

}

