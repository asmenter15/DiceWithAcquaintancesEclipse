package com.androidiansoft.gaming.yahtzee.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;

import com.androidiansoft.gaming.yahtzee.activities.R;
import com.androidiansoft.gaming.yahtzee.fragments.Gameboard;
import com.androidiansoft.gaming.yahtzee.fragments.Scoreboard;

public class GameListFragmentManager extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	int androidGameId;
	String serverGameId;
	int single;
	int turn;
	static SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager);
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		
		// Create the adapter that will return a fragment for each of the
		// sections of the gameboard
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			// Setup GameList
			if (i == 0) {
				fragment = new GameList();
				Bundle args = new Bundle();
				fragment.setArguments(args);
				// Setup Stats
			} else if (i == 1) {
				fragment = new Stats();
				Bundle args = new Bundle();
				args.putInt("androidGameId", androidGameId);
				args.putString("serverGameId", serverGameId);
				args.putInt("turn", turn);
				fragment.setArguments(args);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

	}
}
