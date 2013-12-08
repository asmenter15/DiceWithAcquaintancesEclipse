/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.androidiansoft.gaming.yahtzee.activities;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 
 * @author pc
 */
public class DataSyncAdapterService extends Service {

	private static final Object sSyncAdapterLock = new Object();

	private static SyncAdapter sSyncAdapter = null;

	@Override
	public void onCreate() {
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new SyncAdapter(getApplicationContext());
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}
}
