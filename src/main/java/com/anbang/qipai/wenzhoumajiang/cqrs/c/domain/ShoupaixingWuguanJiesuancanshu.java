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
	private boolean hongzhongPeng;
	private boolean hongzhongGang;
	private boolean facaiPeng;
	private boolean facaiGang;
	private boolean baibanPeng;
	private boolean baibanGang;

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
		danzhangdiao = (fangruShoupaiCount + caishenShu == 1);
		if (player.ifPengchu(MajiangPai.hongzhong)) {
			hongzhongPeng = true;
		}
		if (player.ifGangchu(MajiangPai.hongzhong)) {
			hongzhongGang = true;
		}
		if (player.ifPengchu(MajiangPai.facai)) {
			facaiPeng = true;
		}
		if (player.ifGangchu(MajiangPai.facai)) {
			facaiGang = true;
		}
		if (player.ifPengchu(MajiangPai.baiban)) {
			baibanPeng = true;
		}
		if (player.ifGangchu(MajiangPai.baiban)) {
			baibanGang = true;
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

	public boolean isHongzhongPeng() {
		return hongzhongPeng;
	}

	public void setHongzhongPeng(boolean hongzhongPeng) {
		this.hongzhongPeng = hongzhongPeng;
	}

	public boolean isHongzhongGang() {
		return hongzhongGang;
	}

	public void setHongzhongGang(boolean hongzhongGang) {
		this.hongzhongGang = hongzhongGang;
	}

	public boolean isFacaiPeng() {
		return facaiPeng;
	}

	public void setFacaiPeng(boolean facaiPeng) {
		this.facaiPeng = facaiPeng;
	}

	public boolean isFacaiGang() {
		return facaiGang;
	}

	public void setFacaiGang(boolean facaiGang) {
		this.facaiGang = facaiGang;
	}

	public boolean isBaibanPeng() {
		return baibanPeng;
	}

	public void setBaibanPeng(boolean baibanPeng) {
		this.baibanPeng = baibanPeng;
	}

	public boolean isBaibanGang() {
		return baibanGang;
	}

	public void setBaibanGang(boolean baibanGang) {
		this.baibanGang = baibanGang;
	}

}
