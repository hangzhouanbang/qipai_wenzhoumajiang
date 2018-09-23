package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.listener.WenzhouMajiangChiPengGangActionStatisticsListener;
import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.MajiangPlayerAction;
import com.dml.majiang.player.action.chi.MajiangChiAction;
import com.dml.majiang.player.action.chi.MajiangPlayerChiActionUpdater;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.gang.MajiangGangAction;
import com.dml.majiang.player.action.peng.MajiangPengAction;

public class WenzhouMajiangChiActionUpdater implements MajiangPlayerChiActionUpdater {

	@Override
	public void updateActions(MajiangChiAction chiAction, Ju ju) throws Exception {
		WenzhouMajiangChiPengGangActionStatisticsListener juezhangStatisticsListener = ju
				.getActionStatisticsListenerManager()
				.findListener(WenzhouMajiangChiPengGangActionStatisticsListener.class);
		Pan currentPan = ju.getCurrentPan();

		if (!juezhangStatisticsListener.getPlayerActionMap().isEmpty()) {// 如果有其他动作
			MajiangPlayerAction finallyDoneAction = juezhangStatisticsListener.findPlayerFinallyDoneAction();
			if (finallyDoneAction != null) {// 有其他碰杠动作，先执行碰杠
				MajiangPlayer actionPlayer = currentPan.findPlayerById(finallyDoneAction.getActionPlayerId());
				if (finallyDoneAction instanceof MajiangPengAction) {
					actionPlayer.addActionCandidate((MajiangPengAction) finallyDoneAction);
				} else if (finallyDoneAction instanceof MajiangGangAction) {
					actionPlayer.addActionCandidate((MajiangGangAction) finallyDoneAction);
				}
				juezhangStatisticsListener.updateForNextLun();
			} else {

			}
		} else {
			MajiangPlayer player = currentPan.findPlayerById(chiAction.getActionPlayerId());
			currentPan.clearAllPlayersActionCandidates();
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
	}

}
