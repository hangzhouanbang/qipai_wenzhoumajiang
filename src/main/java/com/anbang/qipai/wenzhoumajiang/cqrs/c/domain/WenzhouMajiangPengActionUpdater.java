package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.listener.WenzhouMajiangChiPengGangActionStatisticsListener;
import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.GangType;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.MajiangPlayerAction;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.gang.MajiangGangAction;
import com.dml.majiang.player.action.peng.MajiangPengAction;
import com.dml.majiang.player.action.peng.MajiangPlayerPengActionUpdater;
import com.dml.majiang.player.chupaizu.PengchuPaiZu;

public class WenzhouMajiangPengActionUpdater implements MajiangPlayerPengActionUpdater {

	@Override
	public void updateActions(MajiangPengAction pengAction, Ju ju) throws Exception {
		WenzhouMajiangChiPengGangActionStatisticsListener juezhangStatisticsListener = ju
				.getActionStatisticsListenerManager()
				.findListener(WenzhouMajiangChiPengGangActionStatisticsListener.class);
		Pan currentPan = ju.getCurrentPan();
		MajiangPlayer player = currentPan.findPlayerById(pengAction.getActionPlayerId());
		if (pengAction.isDisabledByHigherPriorityAction()) {// 如果动作被阻塞
			player.clearActionCandidates();// 玩家已经做了决定，要删除动作
			if (currentPan.allPlayerHasNoActionCandidates() && !currentPan.anyPlayerHu()) {// 所有玩家行牌结束，并且没人胡
				MajiangPlayerAction finallyDoneAction = juezhangStatisticsListener.findPlayerFinallyDoneAction();// 找出最终应该执行的动作
				MajiangPlayer actionPlayer = currentPan.findPlayerById(finallyDoneAction.getActionPlayerId());
				if (finallyDoneAction instanceof MajiangPengAction) {// 如果是碰，也只能是碰
					MajiangPengAction action = (MajiangPengAction) finallyDoneAction;
					actionPlayer.addActionCandidate(new MajiangPengAction(action.getActionPlayerId(),
							action.getDachupaiPlayerId(), action.getPai()));
				}
				juezhangStatisticsListener.updateForNextLun();// 清空动作缓存
			}
		} else {
			currentPan.clearAllPlayersActionCandidates();
			juezhangStatisticsListener.updateForNextLun();// 清空动作缓存

			// 刻子杠手牌
			List<PengchuPaiZu> pengchupaiZuList = player.getPengchupaiZuList();
			PengchuPaiZu pengchuPaiZu = pengchupaiZuList.get(pengchupaiZuList.size() - 1);
			for (MajiangPai fangruShoupai : player.getFangruShoupaiList()) {
				if (pengchuPaiZu.getKezi().getPaiType().equals(fangruShoupai)) {
					player.addActionCandidate(new MajiangGangAction(pengAction.getActionPlayerId(),
							pengAction.getDachupaiPlayerId(), fangruShoupai, GangType.kezigangshoupai));
					break;
				}
			}

			// 需要有“过”
			player.checkAndGenerateGuoCandidateAction();
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
