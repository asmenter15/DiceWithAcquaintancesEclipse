package com.androidiansoft.gaming.yahtzee.activities;

import java.util.Date;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.androidiansoft.gaming.yahtzee.networking.NetworkJobs;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private Context mContext;
	private Date mLastUpdated;

	public SyncAdapter(Context context) {
		super(context, true);
		mContext = context;
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		mLastUpdated = new Date();
		if (extras.getString("synctype") != null) {
			if (extras.getString("synctype").equals("turnpost")) {
				NetworkJobs.updateDataFromTurn(extras.getInt("gameid"),
						extras.getInt("scorechoice"), extras.getInt("points"),
						extras.getInt("user"), extras.getInt("turn"),
						extras.getInt("totalpoints"), extras.getInt("valid"),
						extras.getInt("conBonus"), extras.getInt("creBonus"));

			}
		}
	}

}
