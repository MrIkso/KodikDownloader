package com.mrikso.kodikdownloader.model;

import com.google.gson.annotations.SerializedName;

public class KodikUrlParams {

	@SerializedName("d")
	private String d;

	@SerializedName("d_sign")
	private String dSign;

	@SerializedName("pd")
	private String pd;

	@SerializedName("pd_sign")
	private String pdSign;

	@SerializedName("ref")
	private String ref;

	@SerializedName("ref_sign")
	private String refSign;

	@SerializedName("advert_debug")
	private boolean advertDebug;

	@SerializedName("first_url")
	private boolean firstUrl;

	public String getD() {
		return d;
	}

	public String getDSign() {
		return dSign;
	}

	public String getPd() {
		return pd;
	}

	public String getPdSign() {
		return pdSign;
	}

	public String getRef() {
		return ref;
	}

	public String getRefSign() {
		return refSign;
	}

	public boolean isAdvertDebug() {
		return advertDebug;
	}

	public boolean isFirstUrl() {
		return firstUrl;
	}

}
