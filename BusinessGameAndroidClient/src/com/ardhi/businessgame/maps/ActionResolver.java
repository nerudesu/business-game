package com.ardhi.businessgame.maps;

import android.os.Bundle;

public interface ActionResolver {
	public void startAct(Class<?> activity, int flags);
	public void startAct(Class<?> activity, int flags, Bundle b);
	public void startAct(Class<?> activity, Bundle b);
	public void startAct(Class<?> activity);
	public void showToast(String text);
	public void startProgressDialog(String text);
	public void stopProgressDialog();
}
