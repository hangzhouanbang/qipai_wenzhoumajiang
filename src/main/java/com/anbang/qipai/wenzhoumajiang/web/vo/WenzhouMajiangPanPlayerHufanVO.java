package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangPanPlayerHufan;

public class WenzhouMajiangPanPlayerHufanVO {
	private boolean ruan;// 软牌
	private boolean ying;// 硬牌
	private boolean shuangfan;// 双翻
	private boolean sifan;// 四翻

	public WenzhouMajiangPanPlayerHufanVO() {

	}

	public WenzhouMajiangPanPlayerHufanVO(WenzhouMajiangPanPlayerHufan hufan) {
		if (hufan.isSifan()) {
			this.sifan = hufan.isSifan();
		} else if (hufan.isShuangfan()) {
			this.shuangfan = hufan.isShuangfan();
		} else if (!hufan.isRuan()) {
			this.ying = true;
		} else {
			this.ruan = true;
		}
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

	public boolean isYing() {
		return ying;
	}

	public void setYing(boolean ying) {
		this.ying = ying;
	}
}
