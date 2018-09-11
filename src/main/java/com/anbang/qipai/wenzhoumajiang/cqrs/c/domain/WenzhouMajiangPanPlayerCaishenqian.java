package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.player.MajiangPlayer;

public class WenzhouMajiangPanPlayerCaishenqian {

	private int caishenCount;
	private int totalscore;// 总得分
	private int value;// 单人

	public WenzhouMajiangPanPlayerCaishenqian() {

	}

	public WenzhouMajiangPanPlayerCaishenqian(MajiangPlayer player) {
		caishenCount = player.countGuipai();
	}

	public void calculate(boolean hu, boolean caishenqian, int playerCount) {
		int caishenfen = 0;
		if (caishenqian) {
			caishenfen = caishenCount;
			if (hu && caishenCount == 3) {
				caishenfen *= 2;
			}
		}
		value = caishenfen;
		totalscore = caishenfen * (playerCount - 1);
	}

	public int jiesuan(int delta) {
		return totalscore += delta;
	}

	public int getCaishenCount() {
		return caishenCount;
	}

	public void setCaishenCount(int caishenCount) {
		this.caishenCount = caishenCount;
	}

	public int getTotalscore() {
		return totalscore;
	}

	public void setTotalscore(int totalscore) {
		this.totalscore = totalscore;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
