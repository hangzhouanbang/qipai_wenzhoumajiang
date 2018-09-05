package com.anbang.qipai.wenzhoumajiang.web.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangPanPlayerResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerDbo;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.Shunzi;
import com.dml.majiang.player.chupaizu.ChichuPaiZu;
import com.dml.majiang.player.chupaizu.GangchuPaiZu;
import com.dml.majiang.player.chupaizu.PengchuPaiZu;
import com.dml.majiang.player.shoupai.ShoupaiDuiziZu;
import com.dml.majiang.player.shoupai.ShoupaiGangziZu;
import com.dml.majiang.player.shoupai.ShoupaiKeziZu;
import com.dml.majiang.player.shoupai.ShoupaiPaiXing;
import com.dml.majiang.player.shoupai.ShoupaiShunziZu;

public class WenzhouMajiangPanPlayerResultVO {

	private String playerId;
	private String nickname;
	private String headimgurl;
	private boolean zhuang;
	private boolean hu;
	private boolean zimo;
	private boolean dianpao;
	private List<MajiangPai> publicPaiList;
	private List<MajiangPai> caishenList;
	private List<List<ResultShoupaiVO>> resultShoupaiZuList = new ArrayList<>();
	private List<Shunzi> shunziList = new ArrayList<>();
	private List<MajiangPai> keziTypeList = new ArrayList<>();
	private List<GangchuPaiZuVO> gangchuList = new ArrayList<>();

	/**
	 * 这个是打了几炮
	 */
	private int pao;

	/**
	 * 这个是非结算的胡数
	 */
	private int hushu;

	/**
	 * 这个是结算分
	 */
	private int score;

	public WenzhouMajiangPanPlayerResultVO(MajiangGamePlayerDbo gamePlayerDbo, String zhuangPlayerId, boolean zimo,
			String dianpaoPlayerId, WenzhouMajiangPanPlayerResult panPlayerResult) {
		playerId = gamePlayerDbo.getPlayerId();
		nickname = gamePlayerDbo.getNickname();
		headimgurl = gamePlayerDbo.getHeadimgurl();
		if (playerId.equals(zhuangPlayerId)) {
			zhuang = true;
		}
		hu = panPlayerResult.isHu();
		publicPaiList = new ArrayList<>(panPlayerResult.getPublicPaiList());

		List<ChichuPaiZu> chichuPaiZuList = panPlayerResult.getChichupaiZuList();
		for (ChichuPaiZu chichuPaiZu : chichuPaiZuList) {
			shunziList.add(chichuPaiZu.getShunzi());
		}

		List<PengchuPaiZu> pengchupaiZuList = panPlayerResult.getPengchupaiZuList();
		for (PengchuPaiZu pengchuPaiZu : pengchupaiZuList) {
			keziTypeList.add(pengchuPaiZu.getKezi().getPaiType());
		}

		List<GangchuPaiZu> gangchupaiZuList = panPlayerResult.getGangchupaiZuList();
		for (GangchuPaiZu gangchuPaiZu : gangchupaiZuList) {
			gangchuList.add(new GangchuPaiZuVO(gangchuPaiZu));
		}

		if (hu) {
			this.zimo = zimo;
			ShoupaiPaiXing shoupaiPaiXing = panPlayerResult.getBestShoupaiPaiXing();
			List<ShoupaiShunziZu> shunziList = shoupaiPaiXing.getShunziList();
			for (ShoupaiShunziZu shoupaiShunziZu : shunziList) {
				List<ResultShoupaiVO> shoupaiList = new ArrayList<>();
				resultShoupaiZuList.add(shoupaiList);
				shoupaiList.add(new ResultShoupaiVO(shoupaiShunziZu.getPai1()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiShunziZu.getPai2()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiShunziZu.getPai3()));
			}

			List<ShoupaiKeziZu> keziList = shoupaiPaiXing.getKeziList();
			for (ShoupaiKeziZu shoupaiKeziZu : keziList) {
				List<ResultShoupaiVO> shoupaiList = new ArrayList<>();
				resultShoupaiZuList.add(shoupaiList);
				shoupaiList.add(new ResultShoupaiVO(shoupaiKeziZu.getPai1()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiKeziZu.getPai2()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiKeziZu.getPai3()));
			}

			List<ShoupaiGangziZu> gangziList = shoupaiPaiXing.getGangziList();
			for (ShoupaiGangziZu shoupaiGangziZu : gangziList) {
				List<ResultShoupaiVO> shoupaiList = new ArrayList<>();
				resultShoupaiZuList.add(shoupaiList);
				shoupaiList.add(new ResultShoupaiVO(shoupaiGangziZu.getPai1()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiGangziZu.getPai2()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiGangziZu.getPai3()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiGangziZu.getPai4()));
			}

			List<ShoupaiDuiziZu> duiziList = shoupaiPaiXing.getDuiziList();
			for (ShoupaiDuiziZu shoupaiDuiziZu : duiziList) {
				List<ResultShoupaiVO> shoupaiList = new ArrayList<>();
				resultShoupaiZuList.add(shoupaiList);
				shoupaiList.add(new ResultShoupaiVO(shoupaiDuiziZu.getPai1()));
				shoupaiList.add(new ResultShoupaiVO(shoupaiDuiziZu.getPai2()));
			}

		} else {
			if (!zimo) {
				if (playerId.equals(dianpaoPlayerId)) {
					dianpao = true;
				}
			}
			List<MajiangPai> shoupaiList = panPlayerResult.getShoupaiList();
			Set<MajiangPai> guipaiTypeSet = panPlayerResult.getGuipaiTypeSet();
			caishenList = new ArrayList<>();
			List<ResultShoupaiVO> list = new ArrayList<>();
			resultShoupaiZuList.add(list);
			for (MajiangPai pai : shoupaiList) {
				if (guipaiTypeSet.contains(pai)) {
					caishenList.add(pai);
				} else {
					list.add(new ResultShoupaiVO(pai));
				}
			}
		}
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getNickname() {
		return nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public boolean isZhuang() {
		return zhuang;
	}

	public boolean isHu() {
		return hu;
	}

	public boolean isZimo() {
		return zimo;
	}

	public boolean isDianpao() {
		return dianpao;
	}

	public List<MajiangPai> getPublicPaiList() {
		return publicPaiList;
	}

	public List<MajiangPai> getCaishenList() {
		return caishenList;
	}

	public List<List<ResultShoupaiVO>> getResultShoupaiZuList() {
		return resultShoupaiZuList;
	}

	public List<Shunzi> getShunziList() {
		return shunziList;
	}

	public List<MajiangPai> getKeziTypeList() {
		return keziTypeList;
	}

	public List<GangchuPaiZuVO> getGangchuList() {
		return gangchuList;
	}

	public int getPao() {
		return pao;
	}

	public int getHushu() {
		return hushu;
	}

	public int getScore() {
		return score;
	}

}
