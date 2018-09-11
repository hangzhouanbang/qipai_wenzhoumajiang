package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.HashSet;
import java.util.Set;

import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.player.MajiangPlayer;

/**
 * 手牌型无关结算参数
 * 
 * @author lsc
 *
 */
public class ShoupaixingWuguanJiesuancanshu {

	private int baibanShu;
	private int caishenShu;
	private boolean allXushupaiInSameCategory;
	private boolean hasZipai;
	private boolean qingyise;
	private boolean hunyise;
	private int chichupaiZuCount;
	private int fangruShoupaiCount;
	private boolean danzhangdiao;
	private MajiangPai guipaiType;
	private boolean guipaiIsZhongFaBai;
	private int hongzhongCount;
	private int facaiCount;
	private int baibanCount;

	public ShoupaixingWuguanJiesuancanshu(MajiangPlayer player) {
		baibanShu = player.countPublicPai();
		caishenShu = player.countGuipai();
		Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
		guipaiTypeSet.toArray(guipaiTypes);
		guipaiType = guipaiTypes[0];
		if (guipaiType.equals(MajiangPai.hongzhong) || guipaiType.equals(MajiangPai.facai)
				|| guipaiType.equals(MajiangPai.baiban)) {
			setGuipaiIsZhongFaBai(true);
		}
		allXushupaiInSameCategory = player.allXushupaiInSameCategory();
		Set<MajiangPai> paiSet = new HashSet<>();
		if (!guipaiTypeSet.contains(MajiangPai.dongfeng)) {
			paiSet.add(MajiangPai.dongfeng);
		}
		if (!guipaiTypeSet.contains(MajiangPai.nanfeng)) {
			paiSet.add(MajiangPai.nanfeng);
		}
		if (!guipaiTypeSet.contains(MajiangPai.xifeng)) {
			paiSet.add(MajiangPai.xifeng);
		}
		if (!guipaiTypeSet.contains(MajiangPai.beifeng)) {
			paiSet.add(MajiangPai.beifeng);
		}
		if (!guipaiTypeSet.contains(MajiangPai.hongzhong)) {
			paiSet.add(MajiangPai.hongzhong);
		}
		if (!guipaiTypeSet.contains(MajiangPai.facai)) {
			paiSet.add(MajiangPai.facai);
		}
		if (!guipaiTypeSet.contains(MajiangPai.baiban) && MajiangPai.isZipai(guipaiType)) {
			paiSet.add(MajiangPai.baiban);
		}
		qingyise = (allXushupaiInSameCategory && !player.hasOneOfPaiInSet(paiSet));
		hunyise = (allXushupaiInSameCategory && player.hasOneOfPaiInSet(paiSet));
		chichupaiZuCount = player.countChichupaiZu();
		fangruShoupaiCount = player.getFangruShoupaiList().size();
		danzhangdiao = fangruShoupaiCount == 1 && chichupaiZuCount == 0;
		hongzhongCount = 0;
		facaiCount = 0;
		baibanCount = 0;
		for (MajiangPai pai : player.getFangruShoupaiList()) {
			if (MajiangPai.hongzhong.equals(pai)) {
				hongzhongCount += 1;
			}
			if (MajiangPai.facai.equals(pai)) {
				facaiCount += 1;
			}
			if (MajiangPai.baiban.equals(pai)) {
				baibanCount += 1;
			}
		}
		MajiangPai gangmoShoupai = player.getGangmoShoupai();
		if (gangmoShoupai != null) {
			if (MajiangPai.hongzhong.equals(gangmoShoupai)) {
				hongzhongCount += 1;
			}
			if (MajiangPai.facai.equals(gangmoShoupai)) {
				facaiCount += 1;
			}
			if (MajiangPai.baiban.equals(gangmoShoupai)) {
				baibanCount += 1;
			}
		}
	}

	public int getBaibanShu() {
		return baibanShu;
	}

	public void setBaibanShu(int baibanShu) {
		this.baibanShu = baibanShu;
	}

	public int getCaishenShu() {
		return caishenShu;
	}

	public void setCaishenShu(int caishenShu) {
		this.caishenShu = caishenShu;
	}

	public boolean isAllXushupaiInSameCategory() {
		return allXushupaiInSameCategory;
	}

	public void setAllXushupaiInSameCategory(boolean allXushupaiInSameCategory) {
		this.allXushupaiInSameCategory = allXushupaiInSameCategory;
	}

	public boolean isHasZipai() {
		return hasZipai;
	}

	public void setHasZipai(boolean hasZipai) {
		this.hasZipai = hasZipai;
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

	public int getChichupaiZuCount() {
		return chichupaiZuCount;
	}

	public void setChichupaiZuCount(int chichupaiZuCount) {
		this.chichupaiZuCount = chichupaiZuCount;
	}

	public int getFangruShoupaiCount() {
		return fangruShoupaiCount;
	}

	public void setFangruShoupaiCount(int fangruShoupaiCount) {
		this.fangruShoupaiCount = fangruShoupaiCount;
	}

	public boolean isDanzhangdiao() {
		return danzhangdiao;
	}

	public void setDanzhangdiao(boolean danzhangdiao) {
		this.danzhangdiao = danzhangdiao;
	}

	public MajiangPai getGuipaiType() {
		return guipaiType;
	}

	public void setGuipaiType(MajiangPai guipaiType) {
		this.guipaiType = guipaiType;
	}

	public boolean isGuipaiIsZhongFaBai() {
		return guipaiIsZhongFaBai;
	}

	public void setGuipaiIsZhongFaBai(boolean guipaiIsZhongFaBai) {
		this.guipaiIsZhongFaBai = guipaiIsZhongFaBai;
	}

	public int getHongzhongCount() {
		return hongzhongCount;
	}

	public void setHongzhongCount(int hongzhongCount) {
		this.hongzhongCount = hongzhongCount;
	}

	public int getFacaiCount() {
		return facaiCount;
	}

	public void setFacaiCount(int facaiCount) {
		this.facaiCount = facaiCount;
	}

	public int getBaibanCount() {
		return baibanCount;
	}

	public void setBaibanCount(int baibanCount) {
		this.baibanCount = baibanCount;
	}
}
