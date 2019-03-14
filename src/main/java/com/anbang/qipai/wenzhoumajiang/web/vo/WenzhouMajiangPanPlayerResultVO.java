package com.anbang.qipai.wenzhoumajiang.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.WenzhouMajiangPanPlayerResultDbo;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.Shunzi;
import com.dml.majiang.player.chupaizu.ChichuPaiZu;
import com.dml.majiang.player.chupaizu.GangchuPaiZu;
import com.dml.majiang.player.chupaizu.PengchuPaiZu;
import com.dml.majiang.player.shoupai.ShoupaiDanpai;
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

	private WenzhouMajiangPanPlayerHuxingVO huxing;

	private WenzhouMajiangPanPlayerHufanVO hufanVo;

	/**
	 * 这个是胡翻
	 */
	private int hufan;

	/**
	 * 这个是财神钱
	 */
	private int caishenqian;

	/**
	 * 这个是杠分
	 */
	private int gangfen;

	/**
	 * 这个是结算分
	 */
	private int score;

	private boolean tongpei;// 通赔

	public WenzhouMajiangPanPlayerResultVO(MajiangGamePlayerDbo gamePlayerDbo, String zhuangPlayerId, boolean zimo,
			String dianpaoPlayerId, WenzhouMajiangPanPlayerResultDbo panPlayerResult) {
		playerId = gamePlayerDbo.getPlayerId();
		nickname = gamePlayerDbo.getNickname();
		headimgurl = gamePlayerDbo.getHeadimgurl();
		if (playerId.equals(zhuangPlayerId)) {
			zhuang = true;
		}
		hu = panPlayerResult.getPlayer().getHu() != null;
		publicPaiList = new ArrayList<>(panPlayerResult.getPlayer().getPublicPaiList());
		tongpei = panPlayerResult.getPlayerResult().isTongpei();
		huxing = new WenzhouMajiangPanPlayerHuxingVO(panPlayerResult.getPlayerResult().getHufan().getHuxing());
		if (hu) {
			hufanVo = new WenzhouMajiangPanPlayerHufanVO(panPlayerResult.getPlayerResult().getHufan());
		} else {
			hufanVo = new WenzhouMajiangPanPlayerHufanVO();
		}
		this.hufan = panPlayerResult.getPlayerResult().getHufan().getValue();
		this.caishenqian = panPlayerResult.getPlayerResult().getCaishenqian().getTotalscore();
		this.gangfen = panPlayerResult.getPlayerResult().getGang().getTotalscore();
		this.score = panPlayerResult.getPlayerResult().getScore();
		List<ChichuPaiZu> chichuPaiZuList = panPlayerResult.getPlayer().getChichupaiZuList();
		for (ChichuPaiZu chichuPaiZu : chichuPaiZuList) {
			shunziList.add(chichuPaiZu.getShunzi());
		}

		List<PengchuPaiZu> pengchupaiZuList = panPlayerResult.getPlayer().getPengchupaiZuList();
		for (PengchuPaiZu pengchuPaiZu : pengchupaiZuList) {
			keziTypeList.add(pengchuPaiZu.getKezi().getPaiType());
		}

		List<GangchuPaiZu> gangchupaiZuList = panPlayerResult.getPlayer().getGangchupaiZuList();
		for (GangchuPaiZu gangchuPaiZu : gangchupaiZuList) {
			gangchuList.add(new GangchuPaiZuVO(gangchuPaiZu));
		}

		if (hu) {
			this.zimo = zimo;
			ShoupaiPaiXing shoupaiPaiXing = panPlayerResult.getPlayer().getHu().getShoupaiPaiXing();
			if (shoupaiPaiXing == null) {// 三财神胡没有牌型
				caishenList = new ArrayList<>(panPlayerResult.getPlayer().getFangruGuipaiList());
				List<MajiangPai> shoupaiList = panPlayerResult.getPlayer().getFangruShoupaiList();
				List<ResultShoupaiVO> list = new ArrayList<>();
				resultShoupaiZuList.add(list);
				for (MajiangPai pai : shoupaiList) {
					list.add(new ResultShoupaiVO(pai));
				}
				MajiangPai gangmoShoupai = panPlayerResult.getPlayer().getGangmoShoupai().getPai();
				ResultShoupaiVO lastPai = new ResultShoupaiVO(gangmoShoupai);
				lastPai.setHupai(true);
				if (caishenList.contains(gangmoShoupai)) {
					lastPai.setCaishen(true);
				}
				list.add(lastPai);
			} else {
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

				List<ShoupaiDanpai> danpaiList = shoupaiPaiXing.getDanpaiList();
				for (ShoupaiDanpai shoupaiDanpai : danpaiList) {
					List<ResultShoupaiVO> shoupaiList = new ArrayList<>();
					resultShoupaiZuList.add(shoupaiList);
					shoupaiList.add(new ResultShoupaiVO(shoupaiDanpai.getPai()));
				}
			}
		} else {
			if (!zimo) {
				if (playerId.equals(dianpaoPlayerId)) {
					dianpao = true;
				}
			}
			List<MajiangPai> shoupaiList = panPlayerResult.getPlayer().getFangruShoupaiList();
			if (panPlayerResult.getPlayer().getGangmoShoupai() != null) {
				shoupaiList.add(panPlayerResult.getPlayer().getGangmoShoupai().getPai());
			}
			caishenList = new ArrayList<>(panPlayerResult.getPlayer().getFangruGuipaiList());
			List<ResultShoupaiVO> list = new ArrayList<>();
			resultShoupaiZuList.add(list);
			for (MajiangPai pai : shoupaiList) {
				list.add(new ResultShoupaiVO(pai));
			}
		}
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public boolean isZhuang() {
		return zhuang;
	}

	public void setZhuang(boolean zhuang) {
		this.zhuang = zhuang;
	}

	public boolean isHu() {
		return hu;
	}

	public void setHu(boolean hu) {
		this.hu = hu;
	}

	public boolean isZimo() {
		return zimo;
	}

	public void setZimo(boolean zimo) {
		this.zimo = zimo;
	}

	public boolean isDianpao() {
		return dianpao;
	}

	public void setDianpao(boolean dianpao) {
		this.dianpao = dianpao;
	}

	public List<MajiangPai> getPublicPaiList() {
		return publicPaiList;
	}

	public void setPublicPaiList(List<MajiangPai> publicPaiList) {
		this.publicPaiList = publicPaiList;
	}

	public List<MajiangPai> getCaishenList() {
		return caishenList;
	}

	public void setCaishenList(List<MajiangPai> caishenList) {
		this.caishenList = caishenList;
	}

	public List<List<ResultShoupaiVO>> getResultShoupaiZuList() {
		return resultShoupaiZuList;
	}

	public void setResultShoupaiZuList(List<List<ResultShoupaiVO>> resultShoupaiZuList) {
		this.resultShoupaiZuList = resultShoupaiZuList;
	}

	public List<Shunzi> getShunziList() {
		return shunziList;
	}

	public void setShunziList(List<Shunzi> shunziList) {
		this.shunziList = shunziList;
	}

	public List<MajiangPai> getKeziTypeList() {
		return keziTypeList;
	}

	public void setKeziTypeList(List<MajiangPai> keziTypeList) {
		this.keziTypeList = keziTypeList;
	}

	public List<GangchuPaiZuVO> getGangchuList() {
		return gangchuList;
	}

	public void setGangchuList(List<GangchuPaiZuVO> gangchuList) {
		this.gangchuList = gangchuList;
	}

	public WenzhouMajiangPanPlayerHuxingVO getHuxing() {
		return huxing;
	}

	public void setHuxing(WenzhouMajiangPanPlayerHuxingVO huxing) {
		this.huxing = huxing;
	}

	public int getHufan() {
		return hufan;
	}

	public void setHufan(int hufan) {
		this.hufan = hufan;
	}

	public int getCaishenqian() {
		return caishenqian;
	}

	public void setCaishenqian(int caishenqian) {
		this.caishenqian = caishenqian;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getGangfen() {
		return gangfen;
	}

	public void setGangfen(int gangfen) {
		this.gangfen = gangfen;
	}

	public WenzhouMajiangPanPlayerHufanVO getHufanVo() {
		return hufanVo;
	}

	public void setHufanVo(WenzhouMajiangPanPlayerHufanVO hufanVo) {
		this.hufanVo = hufanVo;
	}

	public boolean isTongpei() {
		return tongpei;
	}

	public void setTongpei(boolean tongpei) {
		this.tongpei = tongpei;
	}

}
