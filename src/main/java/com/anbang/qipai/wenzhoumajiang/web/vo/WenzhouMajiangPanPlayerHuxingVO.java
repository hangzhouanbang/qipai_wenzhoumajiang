package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangPanPlayerHuxing;

public class WenzhouMajiangPanPlayerHuxingVO {
	private boolean sancaishen;// 三财神
	private boolean qiangganghu;// 抢杠胡
	private boolean badui;// 八对
	private boolean tianhu;// 天胡
	private boolean dihu;// 地胡
	private boolean danzhangdiao;// 单张吊
	private boolean quanqiushen;// 全求神
	private boolean shuangcaiguiwei;// 双财神归位
	private boolean pengpenghu;// 碰碰胡
	private boolean gangkai;// 杠上开花
	private boolean qingyise;// 清一色
	private boolean hunyise;// 混一色
	private boolean zhongfabai;// 中发白
	private boolean caishenniudui;// 财神牛对
	private boolean sancaiguiwei;// 三财神归位

	public WenzhouMajiangPanPlayerHuxingVO() {

	}

	public WenzhouMajiangPanPlayerHuxingVO(WenzhouMajiangPanPlayerHuxing huxing) {
		if (huxing != null) {
			if (huxing.isSancaiguiwei()) {
				sancaiguiwei = true;
			} else if (huxing.isCaishenniudui()) {
				caishenniudui = true;
			} else if (huxing.isZhongfabai()) {
				zhongfabai = true;
			} else if (huxing.isHunyise()) {
				hunyise = true;
			} else if (huxing.isQingyise()) {
				qingyise = true;
			} else if (huxing.isGangkai()) {
				gangkai = true;
			} else if (huxing.isPengpenghu()) {
				pengpenghu = true;
			} else if (huxing.isShuangcaiguiwei()) {
				shuangcaiguiwei = true;
			} else if (huxing.isQuanqiushen()) {
				quanqiushen = true;
			} else if (huxing.isDanzhangdiao()) {
				danzhangdiao = true;
			} else if (huxing.isDihu()) {
				dihu = true;
			} else if (huxing.isTianhu()) {
				tianhu = true;
			} else if (huxing.isBadui()) {
				badui = true;
			} else if (huxing.isQiangganghu()) {
				qiangganghu = true;
			} else if (huxing.isSancaishen()) {
				sancaishen = true;
			} else {

			}
		}
	}

	public boolean isSancaishen() {
		return sancaishen;
	}

	public void setSancaishen(boolean sancaishen) {
		this.sancaishen = sancaishen;
	}

	public boolean isQiangganghu() {
		return qiangganghu;
	}

	public void setQiangganghu(boolean qiangganghu) {
		this.qiangganghu = qiangganghu;
	}

	public boolean isBadui() {
		return badui;
	}

	public void setBadui(boolean badui) {
		this.badui = badui;
	}

	public boolean isTianhu() {
		return tianhu;
	}

	public void setTianhu(boolean tianhu) {
		this.tianhu = tianhu;
	}

	public boolean isDihu() {
		return dihu;
	}

	public void setDihu(boolean dihu) {
		this.dihu = dihu;
	}

	public boolean isDanzhangdiao() {
		return danzhangdiao;
	}

	public void setDanzhangdiao(boolean danzhangdiao) {
		this.danzhangdiao = danzhangdiao;
	}

	public boolean isQuanqiushen() {
		return quanqiushen;
	}

	public void setQuanqiushen(boolean quanqiushen) {
		this.quanqiushen = quanqiushen;
	}

	public boolean isShuangcaiguiwei() {
		return shuangcaiguiwei;
	}

	public void setShuangcaiguiwei(boolean shuangcaiguiwei) {
		this.shuangcaiguiwei = shuangcaiguiwei;
	}

	public boolean isPengpenghu() {
		return pengpenghu;
	}

	public void setPengpenghu(boolean pengpenghu) {
		this.pengpenghu = pengpenghu;
	}

	public boolean isGangkai() {
		return gangkai;
	}

	public void setGangkai(boolean gangkai) {
		this.gangkai = gangkai;
	}

	public boolean isQingyise() {
		return qingyise;
	}

	public void setQingyise(boolean qingyise) {
		this.qingyise = qingyise;
	}

	public boolean isHunyise() {
		return hunyise;
	}

	public void setHunyise(boolean hunyise) {
		this.hunyise = hunyise;
	}

	public boolean isZhongfabai() {
		return zhongfabai;
	}

	public void setZhongfabai(boolean zhongfabai) {
		this.zhongfabai = zhongfabai;
	}

	public boolean isCaishenniudui() {
		return caishenniudui;
	}

	public void setCaishenniudui(boolean caishenniudui) {
		this.caishenniudui = caishenniudui;
	}

	public boolean isSancaiguiwei() {
		return sancaiguiwei;
	}

	public void setSancaiguiwei(boolean sancaiguiwei) {
		this.sancaiguiwei = sancaiguiwei;
	}

}
