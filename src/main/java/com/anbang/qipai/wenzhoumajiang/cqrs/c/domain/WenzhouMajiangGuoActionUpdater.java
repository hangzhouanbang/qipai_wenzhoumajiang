package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.listener.WenzhouMajiangChiPengGangActionStatisticsListener;
import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
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
import com.dml.majiang.player.action.mo.LundaoMopai;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.action.peng.MajiangPengAction;

public class WenzhouMajiangGuoActionUpdater implements MajiangPlayerGuoActionUpdater {

	@Override
	public void updateActions(MajiangGuoAction guoAction, Ju ju) {
		Pan currentPan = ju.getCurrentPan();
		currentPan.playerClearActionCandidates(guoAction.getActionPlayerId());

		MajiangPlayer player = currentPan.findPlayerById(guoAction.getActionPlayerId());
		// 首先看一下,我过的是什么? 是我摸牌之后的胡,杠? 还是别人打出牌之后我可以吃碰杠胡
		PanActionFrame latestPanActionFrame = currentPan.findNotGuoLatestActionFrame();
		MajiangPlayerAction action = latestPanActionFrame.getAction();
		if (action.getType().equals(MajiangPlayerActionType.mo)) {// 过的是我摸牌之后的胡,杠
			MajiangPai gangmoShoupai = player.getGangmoShoupai();
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
				WenzhouMajiangChiPengGangActionStatisticsListener juezhangStatisticsListener = ju
						.getActionStatisticsListenerManager()
						.findListener(WenzhouMajiangChiPengGangActionStatisticsListener.class);
				Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
				MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
				guipaiTypeSet.toArray(guipaiTypes);
				MajiangPai guipaiType = guipaiTypes[0];

				for (MajiangPai pai : fangruShoupaiList) {
					if (!MajiangPai.baiban.equals(pai)) {
						if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(pai)) {
							if (juezhangStatisticsListener.ifJuezhang(pai)) {
								juefengList.add(new MajiangDaAction(player.getId(), pai));
							} else if (!gangmoShoupai.equals(pai) && juezhangStatisticsListener.ifMingPai(pai)
									&& player.getShoupaiCalculator().count(pai) == 1) {
								genfengList.add(new MajiangDaAction(player.getId(), pai));
							} else if (!gangmoShoupai.equals(pai) && player.getShoupaiCalculator().count(pai) == 1) {
								toufengList.add(new MajiangDaAction(player.getId(), pai));
							}
						}
					} else if (!guipaiTypeSet.contains(pai) && MajiangPai.isZipai(guipaiType)) {
						if (juezhangStatisticsListener.ifJuezhang(pai)) {
							juefengList.add(new MajiangDaAction(player.getId(), pai));
						} else if (!gangmoShoupai.equals(pai) && juezhangStatisticsListener.ifMingPai(pai)
								&& player.getShoupaiCalculator().count(pai) == 1) {
							genfengList.add(new MajiangDaAction(player.getId(), pai));
						} else if (!gangmoShoupai.equals(pai) && player.getShoupaiCalculator().count(pai) == 1) {
							toufengList.add(new MajiangDaAction(player.getId(), pai));
						}
					} else {

					}
				}
				if (!MajiangPai.baiban.equals(gangmoShoupai)) {
					if (!guipaiTypeSet.contains(gangmoShoupai) && MajiangPai.isZipai(gangmoShoupai)) {
						if (juezhangStatisticsListener.ifJuezhang(gangmoShoupai)) {
							juefengList.add(new MajiangDaAction(player.getId(), gangmoShoupai));
						} else if (juezhangStatisticsListener.ifMingPai(gangmoShoupai)
								&& player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
							genfengList.add(new MajiangDaAction(player.getId(), gangmoShoupai));
						} else if (player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
							toufengList.add(new MajiangDaAction(player.getId(), gangmoShoupai));
						}
					}
				} else if (!guipaiTypeSet.contains(gangmoShoupai) && MajiangPai.isZipai(guipaiType)) {
					if (juezhangStatisticsListener.ifJuezhang(gangmoShoupai)) {
						juefengList.add(new MajiangDaAction(player.getId(), gangmoShoupai));
					} else if (juezhangStatisticsListener.ifMingPai(gangmoShoupai)
							&& player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
						genfengList.add(new MajiangDaAction(player.getId(), gangmoShoupai));
					} else if (player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
						toufengList.add(new MajiangDaAction(player.getId(), gangmoShoupai));
					}
				} else {

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
			if (currentPan.allPlayerHasNoActionCandidates() && !currentPan.anyPlayerHu()) {// 如果所有玩家啥也干不了
				WenzhouMajiangChiPengGangActionStatisticsListener chiPengGangRecordListener = ju
						.getActionStatisticsListenerManager()
						.findListener(WenzhouMajiangChiPengGangActionStatisticsListener.class);
				MajiangPlayerAction finallyDoneAction = chiPengGangRecordListener.findPlayerFinallyDoneAction();
				if (finallyDoneAction != null) {// 有其他吃碰杠动作，先执行吃碰杠
					MajiangPlayer actionPlayer = currentPan.findPlayerById(finallyDoneAction.getActionPlayerId());
					if (finallyDoneAction instanceof MajiangPengAction) {// 如果是碰
						MajiangPengAction doAction = (MajiangPengAction) finallyDoneAction;
						actionPlayer.addActionCandidate(new MajiangPengAction(doAction.getActionPlayerId(),
								doAction.getDachupaiPlayerId(), doAction.getPai()));
					} else if (finallyDoneAction instanceof MajiangGangAction) {// 如果是杠
						MajiangGangAction doAction = (MajiangGangAction) finallyDoneAction;
						actionPlayer.addActionCandidate(new MajiangGangAction(doAction.getActionPlayerId(),
								doAction.getDachupaiPlayerId(), doAction.getPai(), doAction.getGangType()));
					} else if (finallyDoneAction instanceof MajiangChiAction) {// 如果是吃
						MajiangChiAction doAction = (MajiangChiAction) finallyDoneAction;
						actionPlayer.addActionCandidate(new MajiangChiAction(doAction.getActionPlayerId(),
								doAction.getDachupaiPlayerId(), doAction.getChijinPai(), doAction.getShunzi()));
					}
				} else {
					// 打牌那家的下家摸牌
					MajiangPlayer xiajiaPlayer = currentPan
							.findXiajia(currentPan.findPlayerById(action.getActionPlayerId()));
					xiajiaPlayer.addActionCandidate(new MajiangMoAction(xiajiaPlayer.getId(), new LundaoMopai()));
				}
				chiPengGangRecordListener.updateForNextLun();// 清空动作缓存
			}
		} else if (action.getType().equals(MajiangPlayerActionType.gang)) {// 过的是别人杠牌之后我可以胡
			if (currentPan.allPlayerHasNoActionCandidates() && !currentPan.anyPlayerHu()) {// 如果所有玩家啥也干不了
				// 杠牌那家摸牌
				MajiangPlayer gangPlayer = currentPan.findPlayerById(action.getActionPlayerId());
				gangPlayer.addActionCandidate(new MajiangMoAction(gangPlayer.getId(), new LundaoMopai()));
			}
		} else {
		}
	}
}
