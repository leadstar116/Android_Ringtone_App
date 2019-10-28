package randyg.titlewaves;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AdUtil
{
	public static void setupAds(MainActivity mainActivity)
	{
		MobileAds.initialize(mainActivity, mainActivity.getString(R.string.ad_app_id));
		AdView adView = mainActivity.findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
		adView.loadAd(builder.build());
	}
}

