package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.GangType;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.MajiangPlayerAction;
import com.dml.majiang.player.action.MajiangPlayerActionType;
import com.dml.majiang.player.action.chi.MajiangChiAction;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.gang.MajiangGangAction;
import com.dml.majiang.player.action.guo.MajiangGuoAction;
import com.dml.majiang.player.action.guo.MajiangPlayerGuoActionUpdater;
import com.dml.majiang.player.action.hu.MajiangHuAction;
import com.dml.majiang.player.action.listener.comprehensive.ChiPengGangRecordListener;
import com.dml.majiang.player.action.listener.comprehensive.JuezhangStatisticsListener;
import com.dml.majiang.player.action.mo.GanghouBupai;
import com.dml.majiang.player.action.mo.LundaoMopai;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.action.peng.MajiangPengAction;
import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

public class WenzhouMajiangGuoActionUpdater implements MajiangPlayerGuoActionUpdater {

	@Override
	public void updateActions(MajiangGuoAction guoAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		currentPan.playerClearActionCandidates(guoAction.getActionPlayerId());

		MajiangPlayer player = currentPan.findPlayerById(guoAction.getActionPlayerId());

		// 首先看一下,我过的是什么? 是我摸牌之后的胡,杠? 还是别人打出牌之后我可以吃碰杠胡
		PanActionFrame latestPanActionFrame = currentPan.findNotGuoLatestActionFrame();
		MajiangPlayerAction action = latestPanActionFrame.getAction();
		if (action.getType().equals(MajiangPlayerActionType.mo)) {// 过的是我摸牌之后的胡,杠
			// 那要我打牌
			if (player.getActionCandidates().isEmpty()) {
				List<MajiangDaAction> juefengList = new ArrayList<>();
				List<MajiangDaAction> genfengList = new ArrayList<>();
				List<MajiangDaAction> toufengList = new ArrayList<>();
				// 啥也不能干，那只能打出牌
				/*
				 * 绝风：抓牌后，手牌有绝张风牌字牌，需优先打出，其他牌颜色变灰无法点击
				 * 跟风：抓牌后，手牌有不成对、暗刻的风牌字牌，且该风牌字牌在已打的牌堆里也有，则该张牌需要优先打出 头风：抓牌后，手牌中单独一张的风牌字牌需要优先打出
				 */
				List<MajiangPai> fangruShoupaiList = player.getFangruShoupaiList();
				JuezhangStatisticsListener juezhangStatisticsListener = ju.getActionStatisticsListenerManager()
						.findListener(JuezhangStatisticsListener.class);
				Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
				MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
				guipaiTypeSet.toArray(guipaiTypes);
				MajiangPai guipaiType = guipaiTypes[0];

				for (MajiangPai pai : fangruShoupaiList) {
					if (!MajiangPai.baiban.equals(pai)) {
						if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(pai)) {
							if (juezhangStatisticsListener.ifJuezhang(pai)) {
								juefengList.add(new MajiangDaAction(player.getId(), pai));
							} else if (juezhangStatisticsListener.ifMingPai(pai)
									&& player.getShoupaiCalculator().count(pai) == 1) {
								genfengList.add(new MajiangDaAction(player.getId(), pai));
							} else if (player.getShoupaiCalculator().count(pai) == 1) {
								toufengList.add(new MajiangDaAction(player.getId(), pai));
							}
						}
					} else if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(guipaiType)) {
						if (juezhangStatisticsListener.ifJuezhang(pai)) {
							juefengList.add(new MajiangDaAction(player.getId(), pai));
						} else if (juezhangStatisticsListener.ifMingPai(pai)
								&& player.getShoupaiCalculator().count(pai) == 1) {
							genfengList.add(new MajiangDaAction(player.getId(), pai));
						} else if (player.getShoupaiCalculator().count(pai) == 1) {
							toufengList.add(new MajiangDaAction(player.getId(), pai));
						}
					} else {

					}
				}

				if (!juefengList.isEmpty()) {
					for (MajiangDaAction daAction : juefengList) {
						player.addActionCandidate(daAction);
					}
				} else if (!genfengList.isEmpty()) {
					for (MajiangDaAction daAction : genfengList) {
						player.addActionCandidate(daAction);
					}
				} else if (!toufengList.isEmpty()) {
					for (MajiangDaAction daAction : toufengList) {
						player.addActionCandidate(daAction);
					}
				}
			}
			if (player.getActionCandidates().isEmpty()) {
				player.generateDaActions();
			}
		} else if (action.getType().equals(MajiangPlayerActionType.da)) {// 过的是别人打出牌之后我可以吃碰杠胡
			if (currentPan.allPlayerHasNoActionCandidates()) {// 如果所有玩家啥也干不了
				ChiPengGangRecordListener chiPengGangRecordListener = ju.getActionStatisticsListenerManager()
						.findListener(ChiPengGangRecordListener.class);
				MajiangPlayerAction finallyDoneAction = chiPengGangRecordListener.findPlayerFinallyDoneAction();
				if (finallyDoneAction != null) {// 有其他吃碰杠动作，先执行吃碰杠
					if (finallyDoneAction instanceof MajiangChiAction) {
						chiProcessorAndUpdater((MajiangChiAction) finallyDoneAction, ju);
					} else if (finallyDoneAction instanceof MajiangPengAction) {
						pengProcessorAndUpdater((MajiangPengAction) finallyDoneAction, ju);
					} else if (finallyDoneAction instanceof MajiangGangAction) {
						gangProcessorAndUpdater((MajiangGangAction) finallyDoneAction, ju);
					}
				} else {
					// 打牌那家的下家摸牌
					MajiangPlayer xiajiaPlayer = currentPan
							.findXiajia(currentPan.findPlayerById(action.getActionPlayerId()));
					xiajiaPlayer.addActionCandidate(new MajiangMoAction(xiajiaPlayer.getId(), new LundaoMopai()));
				}
			}
		} else if (action.getType().equals(MajiangPlayerActionType.gang)) {// 过的是别人杠牌之后我可以胡
			if (currentPan.allPlayerHasNoActionCandidates()) {// 如果所有玩家啥也干不了
				// 杠牌那家摸牌
				MajiangPlayer gangPlayer = currentPan.findPlayerById(action.getActionPlayerId());
				gangPlayer.addActionCandidate(new MajiangMoAction(gangPlayer.getId(), new LundaoMopai()));
			}
		} else {
		}
	}

	private void chiProcessorAndUpdater(MajiangChiAction chiAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		currentPan.playerChiPai(chiAction.getActionPlayerId(), chiAction.getDachupaiPlayerId(),
				chiAction.getChijinPai(), chiAction.getShunzi());

		currentPan.clearAllPlayersActionCandidates();

		MajiangPlayer player = currentPan.findPlayerById(chiAction.getActionPlayerId());
		if (player.getActionCandidates().isEmpty()) {
			List<MajiangDaAction> juefengList = new ArrayList<>();
			List<MajiangDaAction> genfengList = new ArrayList<>();
			List<MajiangDaAction> toufengList = new ArrayList<>();
			// 啥也不能干，那只能打出牌
			/*
			 * 绝风：抓牌后，手牌有绝张风牌字牌，需优先打出，其他牌颜色变灰无法点击
			 * 跟风：抓牌后，手牌有不成对、暗刻的风牌字牌，且该风牌字牌在已打的牌堆里也有，则该张牌需要优先打出 头风：抓牌后，手牌中单独一张的风牌字牌需要优先打出
			 */
			List<MajiangPai> fangruShoupaiList = player.getFangruShoupaiList();
			JuezhangStatisticsListener juezhangStatisticsListener = ju.getActionStatisticsListenerManager()
					.findListener(JuezhangStatisticsListener.class);
			Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
			MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
			guipaiTypeSet.toArray(guipaiTypes);
			MajiangPai guipaiType = guipaiTypes[0];

			for (MajiangPai pai : fangruShoupaiList) {
				if (!MajiangPai.baiban.equals(pai)) {
					if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(pai)) {
						if (juezhangStatisticsListener.ifJuezhang(pai)) {
							juefengList.add(new MajiangDaAction(player.getId(), pai));
						} else if (juezhangStatisticsListener.ifMingPai(pai)
								&& player.getShoupaiCalculator().count(pai) == 1) {
							genfengList.add(new MajiangDaAction(player.getId(), pai));
						} else if (player.getShoupaiCalculator().count(pai) == 1) {
							toufengList.add(new MajiangDaAction(player.getId(), pai));
						}
					}
				} else if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(guipaiType)) {
					if (juezhangStatisticsListener.ifJuezhang(pai)) {
						juefengList.add(new MajiangDaAction(player.getId(), pai));
					} else if (juezhangStatisticsListener.ifMingPai(pai)
							&& player.getShoupaiCalculator().count(pai) == 1) {
						genfengList.add(new MajiangDaAction(player.getId(), pai));
					} else if (player.getShoupaiCalculator().count(pai) == 1) {
						toufengList.add(new MajiangDaAction(player.getId(), pai));
					}
				} else {

				}
			}

			if (!juefengList.isEmpty()) {
				for (MajiangDaAction daAction : juefengList) {
					player.addActionCandidate(daAction);
				}
			} else if (!genfengList.isEmpty()) {
				for (MajiangDaAction daAction : genfengList) {
					player.addActionCandidate(daAction);
				}
			} else if (!toufengList.isEmpty()) {
				for (MajiangDaAction daAction : toufengList) {
					player.addActionCandidate(daAction);
				}
			}
		}
		if (player.getActionCandidates().isEmpty()) {
			player.generateDaActions();
		}
	}

	private void pengProcessorAndUpdater(MajiangPengAction pengAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		currentPan.playerPengPai(pengAction.getActionPlayerId(), pengAction.getDachupaiPlayerId(), pengAction.getPai());

		currentPan.clearAllPlayersActionCandidates();
		MajiangPlayer player = currentPan.findPlayerById(pengAction.getActionPlayerId());
		List<MajiangDaAction> juefengList = new ArrayList<>();
		List<MajiangDaAction> genfengList = new ArrayList<>();
		List<MajiangDaAction> toufengList = new ArrayList<>();
		// 啥也不能干，那只能打出牌
		/*
		 * 绝风：抓牌后，手牌有绝张风牌字牌，需优先打出，其他牌颜色变灰无法点击
		 * 跟风：抓牌后，手牌有不成对、暗刻的风牌字牌，且该风牌字牌在已打的牌堆里也有，则该张牌需要优先打出 头风：抓牌后，手牌中单独一张的风牌字牌需要优先打出
		 */
		List<MajiangPai> fangruShoupaiList = player.getFangruShoupaiList();
		JuezhangStatisticsListener juezhangStatisticsListener = ju.getActionStatisticsListenerManager()
				.findListener(JuezhangStatisticsListener.class);
		Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
		guipaiTypeSet.toArray(guipaiTypes);
		MajiangPai guipaiType = guipaiTypes[0];

		for (MajiangPai pai : fangruShoupaiList) {
			if (!MajiangPai.baiban.equals(pai)) {
				if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(pai)) {
					if (juezhangStatisticsListener.ifJuezhang(pai)) {
						juefengList.add(new MajiangDaAction(player.getId(), pai));
					} else if (juezhangStatisticsListener.ifMingPai(pai)
							&& player.getShoupaiCalculator().count(pai) == 1) {
						genfengList.add(new MajiangDaAction(player.getId(), pai));
					} else if (player.getShoupaiCalculator().count(pai) == 1) {
						toufengList.add(new MajiangDaAction(player.getId(), pai));
					}
				}
			} else if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(guipaiType)) {
				if (juezhangStatisticsListener.ifJuezhang(pai)) {
					juefengList.add(new MajiangDaAction(player.getId(), pai));
				} else if (juezhangStatisticsListener.ifMingPai(pai) && player.getShoupaiCalculator().count(pai) == 1) {
					genfengList.add(new MajiangDaAction(player.getId(), pai));
				} else if (player.getShoupaiCalculator().count(pai) == 1) {
					toufengList.add(new MajiangDaAction(player.getId(), pai));
				}
			} else {

			}
		}

		if (!juefengList.isEmpty()) {
			for (MajiangDaAction daAction : juefengList) {
				player.addActionCandidate(daAction);
			}
		} else if (!genfengList.isEmpty()) {
			for (MajiangDaAction daAction : genfengList) {
				player.addActionCandidate(daAction);
			}
		} else if (!toufengList.isEmpty()) {
			for (MajiangDaAction daAction : toufengList) {
				player.addActionCandidate(daAction);
			}
		}
		if (player.getActionCandidates().isEmpty()) {
			player.generateDaActions();
		}
	}

	private void gangProcessorAndUpdater(MajiangGangAction gangAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		GangType gangType = gangAction.getGangType();
		if (gangType.equals(GangType.gangdachu)) {
			currentPan.playerGangDachupai(gangAction.getActionPlayerId(), gangAction.getDachupaiPlayerId(),
					gangAction.getPai());
		} else if (gangType.equals(GangType.shoupaigangmo)) {
			currentPan.playerShoupaiGangMo(gangAction.getActionPlayerId(), gangAction.getPai());
		} else if (gangType.equals(GangType.gangsigeshoupai)) {
			currentPan.playerGangSigeshoupai(gangAction.getActionPlayerId(), gangAction.getPai());
		} else if (gangType.equals(GangType.kezigangmo)) {
			currentPan.playerKeziGangMo(gangAction.getActionPlayerId(), gangAction.getPai());
		} else if (gangType.equals(GangType.kezigangshoupai)) {
			currentPan.playerKeziGangShoupai(gangAction.getActionPlayerId(), gangAction.getPai());
		} else {
		}

		currentPan.clearAllPlayersActionCandidates();
		MajiangPlayer player = currentPan.findPlayerById(gangAction.getActionPlayerId());

		// 看看是不是有其他玩家可以抢杠胡
		boolean qiangganghu = false;
		if (gangAction.getGangType().equals(GangType.kezigangmo)
				|| gangAction.getGangType().equals(GangType.kezigangshoupai)) {
			WenzhouMajiangPanResultBuilder wenzhouMajiangJuResultBuilder = (WenzhouMajiangPanResultBuilder) ju
					.getCurrentPanResultBuilder();
			boolean teshushuangfan = wenzhouMajiangJuResultBuilder.isTeshushuangfan();
			boolean shaozhongfa = wenzhouMajiangJuResultBuilder.isShaozhongfa();
			boolean lazila = wenzhouMajiangJuResultBuilder.isLazila();
			GouXingPanHu gouXingPanHu = ju.getGouXingPanHu();
			MajiangPlayer currentPlayer = player;
			while (true) {
				MajiangPlayer xiajia = currentPan.findXiajia(currentPlayer);
				if (xiajia.getId().equals(player.getId())) {
					break;
				}
				WenzhouMajiangHu bestHu = WenzhouMajiangJiesuanCalculator.calculateBestQianggangHu(gangAction.getPai(),
						gouXingPanHu, xiajia, shaozhongfa, teshushuangfan, lazila);
				if (bestHu != null) {
					bestHu.setQianggang(true);
					bestHu.setDianpaoPlayerId(player.getId());
					xiajia.addActionCandidate(new MajiangHuAction(xiajia.getId(), bestHu));
					xiajia.checkAndGenerateGuoCandidateAction();
					qiangganghu = true;
					break;
				}

				currentPlayer = xiajia;
			}
		}

		// 没有抢杠胡，杠完之后要摸牌
		if (!qiangganghu) {
			player.addActionCandidate(new MajiangMoAction(player.getId(),
					new GanghouBupai(gangAction.getPai(), gangAction.getGangType())));
		}
	}
}
