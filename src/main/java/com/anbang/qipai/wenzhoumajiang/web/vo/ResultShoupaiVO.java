package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.player.shoupai.GuipaiDangPai;
import com.dml.majiang.player.shoupai.ShoupaiJiesuanPai;

public class ResultShoupaiVO {

	private MajiangPai pai;
	private boolean caishen;
	private boolean hupai;

	public ResultShoupaiVO(ShoupaiJiesuanPai shoupaiJiesuanPai) {
		pai = shoupaiJiesuanPai.getYuanPaiType();
		hupai = shoupaiJiesuanPai.isLastActionPai();
		caishen = shoupaiJiesuanPai.dangType().equals(GuipaiDangPai.dangType);
	}

	public ResultShoupaiVO(MajiangPai pai) {
		this.pai = pai;
		caishen = false;
		hupai = false;
	}

	public MajiangPai getPai() {
		return pai;
	}

	public void setPai(MajiangPai pai) {
		this.pai = pai;
	}

	public boolean isCaishen() {
		return caishen;
	}

	public void setCaishen(boolean caishen) {
		this.caishen = caishen;
	}

	public boolean isHupai() {
		return hupai;
	}

	public void setHupai(boolean hupai) {
		this.hupai = hupai;
	}

}
