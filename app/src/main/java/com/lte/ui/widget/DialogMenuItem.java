package com.lte.ui.widget;

public class DialogMenuItem
{
	public String operName;
	public int resId;

	public DialogMenuItem(String operName, int resId) {
		this.operName = operName;
		this.resId = resId;
	}
	public DialogMenuItem(String operName) {
		this.operName = operName;
	}
}
