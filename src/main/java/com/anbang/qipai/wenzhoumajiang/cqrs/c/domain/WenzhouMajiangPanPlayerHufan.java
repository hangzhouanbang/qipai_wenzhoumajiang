package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

public class WenzhouMajiangPanPlayerHufan {

	private boolean ruan;// 软牌
	private boolean shuangfan;// 双翻
	private boolean sifan;// 四翻
	private WenzhouMajiangPanPlayerHuxing huxing;
	private int value;

	public void calculate(boolean teshushuangfan, boolean lazila) {
		int fan = 0;
		if (lazila && huxing.isSancaiguiwei()) {
			sifan = true;
		} else if (teshushuangfan) {
			if (huxing.isTianhu()) {
				shuangfan = true;
			} else if (huxing.isSancaishen()) {
				shuangfan = true;
			} else if (huxing.isDihu()) {
				shuangfan = true;
			} else if (huxing.isBadui() && !ruan) {
				shuangfan = true;
			} else if (huxing.isDanzhangdiao()) {
				shuangfan = true;
			} else if (huxing.isQuanqiushen()) {
				shuangfan = true;
			} else if (huxing.isShuangcaiguiwei()) {
				shuangfan = true;
			} else if (huxing.isPengpenghu()) {
				shuangfan = true;
			} else if (huxing.isGangkai()) {
				shuangfan = true;
			} else if (huxing.isQingyise()) {
				shuangfan = true;
			} else if (huxing.isHunyise()) {
				shuangfan = true;
			} else if (huxing.isZhongfabai()) {
				shuangfan = true;
			} else if (huxing.isCaishenniudui()) {
				shuangfan = true;
			}
		} else {
			if (huxing.isSancaishen()) {
				shuangfan = true;
			} else if (huxing.isTianhu()) {
				shuangfan = true;
			} else if (huxing.isDihu()) {
				shuangfan = true;
			} else if (huxing.isBadui() && !ruan) {
				shuangfan = true;
			}
		}
		if (huxing.isBadui()) {
			ruan = false;
		}
		if (sifan) {
			fan = 8;
		} else if (shuangfan) {
			fan = 4;
		} else if (!ruan) {
			fan = 2;
		} else {
			fan = 1;
		}
		value = fan;
	}

	public boolean isRuan() {
		return ruan;
	}

	public void setRuan(boolean ruan) {
		this.ruan = ruan;
	}

	public boolean isShuangfan() {
		return shuangfan;
	}

	public void setShuangfan(boolean shuangfan) {
		this.shuangfan = shuangfan;
	}

	public boolean isSifan() {
		return sifan;
	}

	public void setSifan(boolean sifan) {
		this.sifan = sifan;
	}

	public WenzhouMajiangPanPlayerHuxing getHuxing() {
		return huxing;
	}

	public void setHuxing(WenzhouMajiangPanPlayerHuxing huxing) {
		this.huxing = huxing;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
