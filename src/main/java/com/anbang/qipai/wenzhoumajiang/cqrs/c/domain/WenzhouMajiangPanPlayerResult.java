package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.pan.result.PanPlayerResult;

public class WenzhouMajiangPanPlayerResult extends PanPlayerResult {

	private WenzhouMajiangPanPlayerHufan hufan;

	private WenzhouMajiangPanPlayerCaishenqian caishenqian;

	private WenzhouMajiangGang gang;

	private int score;// 一盘的结算分

	private int totalScore;

	private boolean tongpei;

	public boolean isTongpei() {
		return tongpei;
	}

	public void setTongpei(boolean tongpei) {
		this.tongpei = tongpei;
	}

	public WenzhouMajiangPanPlayerHufan getHufan() {
		return hufan;
	}

	public void setHufan(WenzhouMajiangPanPlayerHufan hufan) {
		this.hufan = hufan;
	}

	public WenzhouMajiangPanPlayerCaishenqian getCaishenqian() {
		return caishenqian;
	}

	public void setCaishenqian(WenzhouMajiangPanPlayerCaishenqian caishenqian) {
		this.caishenqian = caishenqian;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public WenzhouMajiangGang getGang() {
		return gang;
	}

	public void setGang(WenzhouMajiangGang gang) {
		this.gang = gang;
	}
}
