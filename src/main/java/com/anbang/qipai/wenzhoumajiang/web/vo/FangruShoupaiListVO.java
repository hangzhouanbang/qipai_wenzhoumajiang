package com.anbang.qipai.wenzhoumajiang.web.vo;

import java.util.List;

import com.dml.majiang.pai.MajiangPai;

public class FangruShoupaiListVO {

	private List<MajiangPai> putongShoupaiList;
	private List<MajiangPai> guipaiShoupaiList;
	private int totalShoupaiCount;

	public FangruShoupaiListVO() {
	}

	public FangruShoupaiListVO(List<MajiangPai> fangruShoupaiList, List<MajiangPai> fangruGuipaiList,
			int totalShoupaiCount) {
		putongShoupaiList = fangruShoupaiList;
		guipaiShoupaiList = fangruGuipaiList;
		this.totalShoupaiCount = totalShoupaiCount;
	}

	public List<MajiangPai> getPutongShoupaiList() {
		return putongShoupaiList;
	}

	public void setPutongShoupaiList(List<MajiangPai> putongShoupaiList) {
		this.putongShoupaiList = putongShoupaiList;
	}

	public List<MajiangPai> getGuipaiShoupaiList() {
		return guipaiShoupaiList;
	}

	public void setGuipaiShoupaiList(List<MajiangPai> guipaiShoupaiList) {
		this.guipaiShoupaiList = guipaiShoupaiList;
	}

	public int getTotalShoupaiCount() {
		return totalShoupaiCount;
	}

	public void setTotalShoupaiCount(int totalShoupaiCount) {
		this.totalShoupaiCount = totalShoupaiCount;
	}

}
