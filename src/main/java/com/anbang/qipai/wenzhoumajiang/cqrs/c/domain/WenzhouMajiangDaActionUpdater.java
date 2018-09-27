package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.Shunzi;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.chi.MajiangChiAction;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.da.MajiangPlayerDaActionUpdater;
import com.dml.majiang.player.action.hu.MajiangHuAction;
import com.dml.majiang.player.action.listener.comprehensive.DianpaoDihuOpportunityDetector;
import com.dml.majiang.player.action.listener.comprehensive.GuoPengBuPengStatisticsListener;
import com.dml.majiang.player.action.mo.LundaoMopai;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.shoupai.ShoupaiCalculator;
import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

/**
 * 胡牌：一盘只能有一位胡牌者。如有多人同时表示胡牌时，从打牌者按逆时针方向顺序，靠前者被定为胡牌者。双翻牌型优先胡牌，如同时有个双翻牌型胡牌，则按打牌者逆时针方向顺序，靠前者定为双翻胡牌者。
 * 
 * @author lsc
 *
 */
public class WenzhouMajiangDaActionUpdater implements MajiangPlayerDaActionUpdater {

	@Override
	public void updateActions(MajiangDaAction daAction, Ju ju) {
		Pan currentPan = ju.getCurrentPan();
		MajiangPlayer daPlayer = currentPan.findPlayerById(daAction.getActionPlayerId());
		List<MajiangPai> daplayerFangruShoupaiList = daPlayer.getFangruShoupaiList();
		// 是否是地胡
		DianpaoDihuOpportunityDetector dianpaoDihuOpportunityDetector = ju.getActionStatisticsListenerManager()
				.findListener(DianpaoDihuOpportunityDetector.class);
		boolean couldDihu = dianpaoDihuOpportunityDetector.ifDihuOpportunity();
		daPlayer.clearActionCandidates();

		MajiangPlayer xiajiaPlayer = currentPan.findXiajia(daPlayer);
		xiajiaPlayer.clearActionCandidates();
		Set<MajiangPai> guipaiTypeSet = xiajiaPlayer.getGuipaiTypeSet();
		MajiangPai[] guipaiType = new MajiangPai[guipaiTypeSet.size()];
		guipaiTypeSet.toArray(guipaiType);
		MajiangPai guipai = guipaiType[0];
		MajiangPai daPai = daAction.getPai();
		// 下家可以吃，代码需要改进
		List<MajiangPai> fangruShoupaiList = xiajiaPlayer.getFangruShoupaiList();
		if (fangruShoupaiList.size() != 2) {// 下家只有两张手牌时不能吃
			ShoupaiCalculator shoupaiCalculator = xiajiaPlayer.getShoupaiCalculator();
			Shunzi shunzi1 = tryAndMakeShunziWithPai1(shoupaiCalculator, guipai, daPai);
			if (shunzi1 != null) {
				shunzi1.setPai1(daPai);
				xiajiaPlayer.addActionCandidate(
						new MajiangChiAction(xiajiaPlayer.getId(), daAction.getActionPlayerId(), daPai, shunzi1));
			}

			Shunzi shunzi2 = tryAndMakeShunziWithPai2(shoupaiCalculator, guipai, daPai);
			if (shunzi2 != null) {
				shunzi2.setPai2(daPai);
				xiajiaPlayer.addActionCandidate(
						new MajiangChiAction(xiajiaPlayer.getId(), daAction.getActionPlayerId(), daPai, shunzi2));
			}

			Shunzi shunzi3 = tryAndMakeShunziWithPai3(shoupaiCalculator, guipai, daPai);
			if (shunzi3 != null) {
				shunzi3.setPai3(daPai);
				xiajiaPlayer.addActionCandidate(
						new MajiangChiAction(xiajiaPlayer.getId(), daAction.getActionPlayerId(), daPai, shunzi3));
			}
		}
		GuoPengBuPengStatisticsListener guoPengBuPengStatisticsListener = ju.getActionStatisticsListenerManager()
				.findListener(GuoPengBuPengStatisticsListener.class);
		Map<String, MajiangPai> canNotPengPlayersPaiMap = guoPengBuPengStatisticsListener.getCanNotPengPlayersPaiMap();
		while (true) {
			if (!xiajiaPlayer.getId().equals(daAction.getActionPlayerId())) {
				// 其他的可以碰杠胡
				List<MajiangPai> fangruShoupaiList1 = xiajiaPlayer.getFangruShoupaiList();
				if (fangruShoupaiList1.size() != 2) {// 下家只有两张手牌时
					if (!canNotPengPlayersPaiMap.containsKey(xiajiaPlayer.getId())
							|| !canNotPengPlayersPaiMap.get(xiajiaPlayer.getId()).equals(daAction.getPai())) {
						xiajiaPlayer.tryPengAndGenerateCandidateAction(daAction.getActionPlayerId(), daAction.getPai());
					}
				}
				xiajiaPlayer.tryGangdachuAndGenerateCandidateAction(daAction.getActionPlayerId(), daAction.getPai());
				// 点炮胡
				WenzhouMajiangPanResultBuilder wenzhouMajiangJuResultBuilder = (WenzhouMajiangPanResultBuilder) ju
						.getCurrentPanResultBuilder();
				boolean teshushuangfan = wenzhouMajiangJuResultBuilder.isTeshushuangfan();
				boolean shaozhongfa = wenzhouMajiangJuResultBuilder.isShaozhongfa();
				boolean lazila = wenzhouMajiangJuResultBuilder.isLazila();
				GouXingPanHu gouXingPanHu = ju.getGouXingPanHu();
				// 先把这张牌放入计算器
				xiajiaPlayer.getShoupaiCalculator().addPai(daAction.getPai());
				WenzhouMajiangHu bestHu = WenzhouMajiangJiesuanCalculator.calculateBestDianpaoHu(couldDihu,
						gouXingPanHu, xiajiaPlayer, daAction.getPai(), shaozhongfa, teshushuangfan, lazila);
				// 再把这张牌拿出计算器
				xiajiaPlayer.getShoupaiCalculator().removePai(daAction.getPai());
				if (bestHu != null) {
					bestHu.setDianpao(true);
					bestHu.setDianpaoPlayerId(daPlayer.getId());
					xiajiaPlayer.addActionCandidate(new MajiangHuAction(xiajiaPlayer.getId(), bestHu));
				}

				xiajiaPlayer.checkAndGenerateGuoCandidateAction();
			} else {
				break;
			}
			xiajiaPlayer = currentPan.findXiajia(xiajiaPlayer);
			xiajiaPlayer.clearActionCandidates();
		}

		if (daplayerFangruShoupaiList.size() == 0) {// 全求神时必须胡，胡自己打出去的那张牌
			// 胡
			WenzhouMajiangPanResultBuilder wenzhouMajiangJuResultBuilder = (WenzhouMajiangPanResultBuilder) ju
					.getCurrentPanResultBuilder();
			boolean shaozhongfa = wenzhouMajiangJuResultBuilder.isShaozhongfa();
			boolean teshushuangfan = wenzhouMajiangJuResultBuilder.isTeshushuangfan();
			boolean lazila = wenzhouMajiangJuResultBuilder.isLazila();
			GouXingPanHu gouXingPanHu = ju.getGouXingPanHu();
			daPlayer.setGangmoShoupai(daAction.getPai());
			WenzhouMajiangHu bestHu = WenzhouMajiangJiesuanCalculator.calculateBestZimoHu(false, gouXingPanHu, daPlayer,
					new MajiangMoAction(daPlayer.getId(), new LundaoMopai()), shaozhongfa, teshushuangfan, lazila);
			daPlayer.setGangmoShoupai(null);
			daPlayer.addActionCandidate(new MajiangHuAction(daPlayer.getId(), bestHu));
		}
		currentPan.disablePlayerActionsByHuPengGangChiPriority();// 吃碰杠胡优先级判断
		// 如果所有玩家啥也做不了,那就下家摸牌
		if (currentPan.allPlayerHasNoActionCandidates()) {
			xiajiaPlayer = currentPan.findXiajia(daPlayer);
			xiajiaPlayer.addActionCandidate(new MajiangMoAction(xiajiaPlayer.getId(), new LundaoMopai()));
		}

		// TODO 接着做

	}

	private Shunzi tryAndMakeShunziWithPai1(ShoupaiCalculator shoupaiCalculator, MajiangPai guipai, MajiangPai pai1) {
		int[] paiQuantityArray = shoupaiCalculator.getPaiQuantityArray();
		int guipaiOrdinal = guipai.ordinal();
		int paiOrdinal = pai1.ordinal();
		if (paiOrdinal == 33) {
			paiOrdinal = guipaiOrdinal;
		}
		int pai2 = paiOrdinal + 1;
		int pai3 = paiOrdinal + 2;
		if (guipaiOrdinal == pai2) {
			pai2 = MajiangPai.baiban.ordinal();
		}
		if (guipaiOrdinal == pai3) {
			pai3 = MajiangPai.baiban.ordinal();
		}
		if (paiOrdinal >= 0 && paiOrdinal <= 8) {// 万
			if (paiOrdinal <= 6) {
				if (paiQuantityArray[pai2] > 0 && paiQuantityArray[pai3] > 0) {
					Shunzi shunzi = new Shunzi(pai1, MajiangPai.valueOf(pai2), MajiangPai.valueOf(pai3));
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (paiOrdinal >= 9 && paiOrdinal <= 17) {// 筒
			if (paiOrdinal <= 15) {
				if (paiQuantityArray[pai2] > 0 && paiQuantityArray[pai3] > 0) {
					Shunzi shunzi = new Shunzi(pai1, MajiangPai.valueOf(pai2), MajiangPai.valueOf(pai3));
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (paiOrdinal >= 18 && paiOrdinal <= 26) {// 条
			if (paiOrdinal <= 24) {
				if (paiQuantityArray[pai2] > 0 && paiQuantityArray[pai3] > 0) {
					Shunzi shunzi = new Shunzi(pai1, MajiangPai.valueOf(pai2), MajiangPai.valueOf(pai3));
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private Shunzi tryAndMakeShunziWithPai2(ShoupaiCalculator shoupaiCalculator, MajiangPai guipai, MajiangPai pai2) {
		int[] paiQuantityArray = shoupaiCalculator.getPaiQuantityArray();
		int guipaiOrdinal = guipai.ordinal();
		int paiOrdinal = pai2.ordinal();
		if (paiOrdinal == 33) {
			paiOrdinal = guipaiOrdinal;
		}
		int pai1 = paiOrdinal - 1;
		int pai3 = paiOrdinal + 1;
		if (guipaiOrdinal == pai1) {
			pai1 = MajiangPai.baiban.ordinal();
		}
		if (guipaiOrdinal == pai3) {
			pai3 = MajiangPai.baiban.ordinal();
		}
		if (paiOrdinal >= 0 && paiOrdinal <= 8) {// 万
			if (paiOrdinal >= 1 && paiOrdinal <= 7) {
				if (paiQuantityArray[pai1] > 0 && paiQuantityArray[pai3] > 0) {
					Shunzi shunzi = new Shunzi(MajiangPai.valueOf(pai1), pai2, MajiangPai.valueOf(pai3));
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (paiOrdinal >= 9 && paiOrdinal <= 17) {// 筒
			if (paiOrdinal >= 10 && paiOrdinal <= 16) {
				if (paiQuantityArray[pai1] > 0 && paiQuantityArray[pai3] > 0) {
					Shunzi shunzi = new Shunzi(MajiangPai.valueOf(pai1), pai2, MajiangPai.valueOf(pai3));
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (paiOrdinal >= 18 && paiOrdinal <= 26) {// 条
			if (paiOrdinal >= 19 && paiOrdinal <= 25) {
				if (paiQuantityArray[pai1] > 0 && paiQuantityArray[pai3] > 0) {
					Shunzi shunzi = new Shunzi(MajiangPai.valueOf(pai1), pai2, MajiangPai.valueOf(pai3));
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private Shunzi tryAndMakeShunziWithPai3(ShoupaiCalculator shoupaiCalculator, MajiangPai guipai, MajiangPai pai3) {
		int[] paiQuantityArray = shoupaiCalculator.getPaiQuantityArray();
		int guipaiOrdinal = guipai.ordinal();
		int paiOrdinal = pai3.ordinal();
		if (paiOrdinal == 33) {
			paiOrdinal = guipaiOrdinal;
		}
		int pai1 = paiOrdinal - 2;
		int pai2 = paiOrdinal - 1;
		if (guipaiOrdinal == pai1) {
			pai1 = MajiangPai.baiban.ordinal();
		}
		if (guipaiOrdinal == pai2) {
			pai2 = MajiangPai.baiban.ordinal();
		}
		if (paiOrdinal >= 0 && paiOrdinal <= 8) {// 万
			if (paiOrdinal >= 2) {
				if (paiQuantityArray[pai1] > 0 && paiQuantityArray[pai2] > 0) {
					Shunzi shunzi = new Shunzi(MajiangPai.valueOf(pai1), MajiangPai.valueOf(pai2), pai3);
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (paiOrdinal >= 9 && paiOrdinal <= 17) {// 筒
			if (paiOrdinal >= 11) {
				if (paiQuantityArray[pai1] > 0 && paiQuantityArray[pai2] > 0) {
					Shunzi shunzi = new Shunzi(MajiangPai.valueOf(pai1), MajiangPai.valueOf(pai2), pai3);
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (paiOrdinal >= 18 && paiOrdinal <= 26) {// 条
			if (paiOrdinal >= 20) {
				if (paiQuantityArray[pai1] > 0 && paiQuantityArray[pai2] > 0) {
					Shunzi shunzi = new Shunzi(MajiangPai.valueOf(pai1), MajiangPai.valueOf(pai2), pai3);
					return shunzi;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
