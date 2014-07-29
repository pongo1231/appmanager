package com.fanyapps.appuninstaller;


import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity
{
	LoaderTask loader = new LoaderTask();
	private List<PackageInfo> apps;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.main_layout);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setHomeButtonEnabled(false);
		loader.execute();
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.actions_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case(R.id.action_about):
				startActivity(new Intent(this, AboutActivity.class));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	ListView.OnItemClickListener clickListener = new ListView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> p, View v, int pos, long id)
		{
			try
			{
				Uri uri = Uri.fromParts("package", getPackageManager().getPackageInfo(apps.get(pos).applicationInfo.packageName, getPackageManager().GET_META_DATA).packageName, null);
				Intent it = new Intent(Intent.ACTION_DELETE, uri);
				startActivity(it);
			}
			catch (PackageManager.NameNotFoundException e)
			{
				Toast.makeText(getApplicationContext(), "Couldn't Uninstall. Please contact developer or see log!", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private class LoaderTask extends AsyncTask
	{
		private int i = 0;
		private ArrayAdapter<String> adapter;
		private List<String> appsString;
		private ProgressDialog dialog;
		private PackageManager pm;
		private ListView list;

		@Override
		protected void onPreExecute()
		{
			dialog = new ProgressDialog(MainActivity.this);
			pm = getPackageManager();
			apps = pm.getInstalledPackages(getPackageManager().GET_META_DATA);
			appsString = new ArrayList<String>();
			adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_layout, appsString) {
				@Override
				public View getView(final int position, View convertView, ViewGroup parent)
				{
					if (convertView == null)
					{
						LayoutInflater inflater = (LayoutInflater) getApplicationContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView = inflater.inflate(R.layout.list_layout, null);
					}
					TextView tv1 = (TextView) convertView
						.findViewById(R.id.text);
					TextView tv2 = (TextView) convertView.findViewById(R.id.installerPackageText);
					TextView tv3 = (TextView) convertView.findViewById(R.id.packageText);
					ImageView imageClick = (ImageView) convertView
						.findViewById(R.id.image);

					tv1.setText(apps.get(position).applicationInfo.loadLabel(pm).toString());
					try
					{
						tv2.setHint(null);
						tv2.setText(pm.getPackageInfo(pm.getInstallerPackageName(apps.get(position).applicationInfo.packageName), pm.GET_META_DATA).applicationInfo.loadLabel(pm).toString());
					}
					catch (PackageManager.NameNotFoundException e)
					{
						tv2.setHint("Unknown Source");
						tv2.setText(null);
					}
					tv3.setText(apps.get(position).applicationInfo.packageName);
					imageClick.setImageDrawable(apps.get(position).applicationInfo.loadIcon(pm));
					return convertView;
				}
			};
			list = (ListView) findViewById(R.id.listView);
			list.setOnItemClickListener(clickListener);
			if (!(appsString.size() == apps.size()))
			{
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dialog.setProgressNumberFormat("%2d Apps");
				dialog.setMax(apps.size());
				dialog.setMessage("Loading Apps...");
				dialog.setCancelable(false);
				dialog.show();
			}
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object[] p1)
		{
			for (i = 0; i < apps.size(); i++)
			{
				ApplicationInfo p = apps.get(i).applicationInfo;
				appsString.add(p.loadLabel(pm).toString());
				dialog.setProgress(i);
			}
			Collections.sort(appsString);
			return null;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			adapter.notifyDataSetChanged();
			list.setAdapter(adapter);
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			super.onPostExecute(result);
		}
	}
}
