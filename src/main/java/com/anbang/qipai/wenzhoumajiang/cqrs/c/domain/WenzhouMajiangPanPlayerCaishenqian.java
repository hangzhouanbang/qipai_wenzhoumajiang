package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.player.MajiangPlayer;

public class WenzhouMajiangPanPlayerCaishenqian {

	private int caishenCount;
	private int value;

	public WenzhouMajiangPanPlayerCaishenqian() {

	}

	public WenzhouMajiangPanPlayerCaishenqian(MajiangPlayer player) {
		caishenCount = player.countGuipai();
	}

	public void calculate(boolean hu, boolean caishenqian) {
		int caishenfen = 0;
		if (caishenqian) {
			caishenfen = caishenCount;
			if (hu && caishenCount == 3) {
				caishenfen *= 2;
			}
		}
		value = caishenfen;
	}

	public int jiesuan(int delta) {
		return value += delta;
	}

	public int getCaishenCount() {
		return caishenCount;
	}

	public void setCaishenCount(int caishenCount) {
		this.caishenCount = caishenCount;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
