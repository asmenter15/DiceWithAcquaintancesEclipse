package com.androidiansoft.gaming.yahtzee.activities;

import android.os.Bundle;
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

public class GameFragmentManager extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	int androidGameId;
	String serverGameId;
	int single;
	int turn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.titlebarpager);
		
		getActionBar().setTitle(getIntent().getStringExtra("gameName"));

		androidGameId = getIntent().getIntExtra("androidGameId", 0);
		single = getIntent().getIntExtra("single", 0);
		turn = getIntent().getIntExtra("turn", 0);
		serverGameId = getIntent().getStringExtra("serverGameId");

		// Create the adapter that will return a fragment for each of the
		// sections of the gameboard
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public void onBackPressed() {
		// DO NOTHING
		Toast mToast = Toast.makeText(getApplicationContext(),
				"Don't go back you cheater!", Toast.LENGTH_SHORT);
		mToast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game_layout, menu);
		return true;
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
			// Setup Gameboard
			if (i == 0) {
				fragment = new Gameboard();
				Bundle args = new Bundle();
				args.putInt("androidGameId", androidGameId);
				args.putInt("turn", turn);
				args.putString("serverGameId", serverGameId);
				fragment.setArguments(args);
				// Setup Scoreboard
			} else if (i == 1) {
				fragment = new Scoreboard();
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

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.gameboard);
			case 1:
				return getString(R.string.scoresheet);
			}
			return null;
		}
	}
}
