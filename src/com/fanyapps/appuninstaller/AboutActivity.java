package com.fanyapps.appuninstaller;

import android.app.*;
import android.os.*;

public class AboutActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		setContentView(R.layout.about_layout);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO & ActionBar.DISPLAY_SHOW_TITLE);
		super.onCreate(savedInstanceState);
	}
}
