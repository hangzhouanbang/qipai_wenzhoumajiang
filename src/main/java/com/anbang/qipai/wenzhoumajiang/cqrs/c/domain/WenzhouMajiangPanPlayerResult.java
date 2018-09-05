package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;
import java.util.Set;

import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.player.chupaizu.ChichuPaiZu;
import com.dml.majiang.player.chupaizu.GangchuPaiZu;
import com.dml.majiang.player.chupaizu.PengchuPaiZu;
import com.dml.majiang.player.shoupai.ShoupaiPaiXing;
import com.dml.majiang.position.MajiangPosition;

public class WenzhouMajiangPanPlayerResult {

	private String playerId;

	private MajiangPosition menFeng;

	private boolean hu;

	private int totalScore;

	private ShoupaiPaiXing bestShoupaiPaiXing;

	/**
	 * 手牌列表（包含鬼牌和刚摸的，不包含公开牌）
	 */
	private List<MajiangPai> shoupaiList;

	/**
	 * 公开的牌，不能行牌
	 */
	private List<MajiangPai> publicPaiList;

	/**
	 * 标示什么牌是鬼牌
	 */
	private Set<MajiangPai> guipaiTypeSet;

	private List<ChichuPaiZu> chichupaiZuList;
	private List<PengchuPaiZu> pengchupaiZuList;
	private List<GangchuPaiZu> gangchupaiZuList;

	public int countCaishen() {
		int count = 0;
		for (MajiangPai pai : shoupaiList) {
			if (guipaiTypeSet.contains(pai)) {
				count++;
			}
		}
		return count;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public MajiangPosition getMenFeng() {
		return menFeng;
	}

	public void setMenFeng(MajiangPosition menFeng) {
		this.menFeng = menFeng;
	}

	public boolean isHu() {
		return hu;
	}

	public void setHu(boolean hu) {
		this.hu = hu;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public ShoupaiPaiXing getBestShoupaiPaiXing() {
		return bestShoupaiPaiXing;
	}

	public void setBestShoupaiPaiXing(ShoupaiPaiXing bestShoupaiPaiXing) {
		this.bestShoupaiPaiXing = bestShoupaiPaiXing;
	}

	public List<MajiangPai> getShoupaiList() {
		return shoupaiList;
	}

	public void setShoupaiList(List<MajiangPai> shoupaiList) {
		this.shoupaiList = shoupaiList;
	}

	public Set<MajiangPai> getGuipaiTypeSet() {
		return guipaiTypeSet;
	}

	public void setGuipaiTypeSet(Set<MajiangPai> guipaiTypeSet) {
		this.guipaiTypeSet = guipaiTypeSet;
	}

	public List<MajiangPai> getPublicPaiList() {
		return publicPaiList;
	}

	public void setPublicPaiList(List<MajiangPai> publicPaiList) {
		this.publicPaiList = publicPaiList;
	}

	public List<ChichuPaiZu> getChichupaiZuList() {
		return chichupaiZuList;
	}

	public void setChichupaiZuList(List<ChichuPaiZu> chichupaiZuList) {
		this.chichupaiZuList = chichupaiZuList;
	}

	public List<PengchuPaiZu> getPengchupaiZuList() {
		return pengchupaiZuList;
	}

	public void setPengchupaiZuList(List<PengchuPaiZu> pengchupaiZuList) {
		this.pengchupaiZuList = pengchupaiZuList;
	}

	public List<GangchuPaiZu> getGangchupaiZuList() {
		return gangchupaiZuList;
	}

	public void setGangchupaiZuList(List<GangchuPaiZu> gangchupaiZuList) {
		this.gangchupaiZuList = gangchupaiZuList;
	}

}
