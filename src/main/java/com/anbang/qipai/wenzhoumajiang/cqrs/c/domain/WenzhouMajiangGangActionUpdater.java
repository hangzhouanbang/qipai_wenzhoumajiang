package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.listener.WenzhouMajiangChiPengGangActionStatisticsListener;
import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.fenzu.GangType;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.MajiangPlayerAction;
import com.dml.majiang.player.action.MajiangPlayerActionType;
import com.dml.majiang.player.action.gang.MajiangGangAction;
import com.dml.majiang.player.action.gang.MajiangPlayerGangActionUpdater;
import com.dml.majiang.player.action.hu.MajiangHuAction;
import com.dml.majiang.player.action.mo.GanghouBupai;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.action.peng.MajiangPengAction;
import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

public class WenzhouMajiangGangActionUpdater implements MajiangPlayerGangActionUpdater {

	@Override
	public void updateActions(MajiangGangAction gangAction, Ju ju) {
		Pan currentPan = ju.getCurrentPan();
		MajiangPlayer player = currentPan.findPlayerById(gangAction.getActionPlayerId());
		WenzhouMajiangChiPengGangActionStatisticsListener chiPengGangRecordListener = ju
				.getActionStatisticsListenerManager()
				.findListener(WenzhouMajiangChiPengGangActionStatisticsListener.class);
		if (gangAction.isDisabledByHigherPriorityAction()) {// 如果动作被阻塞
			player.clearActionCandidates();// 玩家已经做了决定，要删除动作
			if (currentPan.allPlayerHasNoActionCandidates() && !currentPan.anyPlayerHu()) {// 所有玩家行牌结束，并且没人胡
				MajiangPlayerAction finallyDoneAction = chiPengGangRecordListener.findPlayerFinallyDoneAction();// 找出最终应该执行的动作
				MajiangPlayer actionPlayer = currentPan.findPlayerById(finallyDoneAction.getActionPlayerId());
				if (finallyDoneAction instanceof MajiangGangAction) {// 如果是杠，也只能是杠
					MajiangGangAction action = (MajiangGangAction) finallyDoneAction;
					actionPlayer.addActionCandidate(new MajiangGangAction(action.getActionPlayerId(),
							action.getDachupaiPlayerId(), action.getPai(), action.getGangType()));
				}
				chiPengGangRecordListener.updateForNextLun();// 清空动作缓存
			}
		} else {
			currentPan.clearAllPlayersActionCandidates();
			chiPengGangRecordListener.updateForNextLun();// 清空动作缓存

			// 首先看一下,我过的是什么? 是我摸牌之后的胡,杠? 还是别人打出牌之后我可以吃碰杠胡
			PanActionFrame latestPanActionFrame = currentPan.findNotGuoLatestActionFrame();
			MajiangPlayerAction action = latestPanActionFrame.getAction();

			// 看看是不是有其他玩家可以抢杠胡
			boolean qiangganghu = false;
			if (gangAction.getGangType().equals(GangType.kezigangmo)
					|| gangAction.getGangType().equals(GangType.kezigangshoupai)) {
				if (action.getType().equals(MajiangPlayerActionType.peng)) {
					MajiangPengAction pengAction = (MajiangPengAction) action;
					if (!pengAction.getPai().equals(gangAction.getPai())) {
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
							WenzhouMajiangHu bestHu = WenzhouMajiangJiesuanCalculator.calculateBestQianggangHu(
									gangAction.getPai(), gouXingPanHu, xiajia, shaozhongfa, teshushuangfan, lazila);
							if (bestHu != null) {
								bestHu.setQianggang(true);
								bestHu.setDianpaoPlayerId(player.getId());
								xiajia.addActionCandidate(new MajiangHuAction(xiajia.getId(), bestHu));
								xiajia.checkAndGenerateGuoCandidateAction();
								qiangganghu = true;
							}

							currentPlayer = xiajia;
						}
					}
				} else {
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
						WenzhouMajiangHu bestHu = WenzhouMajiangJiesuanCalculator.calculateBestQianggangHu(
								gangAction.getPai(), gouXingPanHu, xiajia, shaozhongfa, teshushuangfan, lazila);
						if (bestHu != null) {
							bestHu.setQianggang(true);
							bestHu.setDianpaoPlayerId(player.getId());
							xiajia.addActionCandidate(new MajiangHuAction(xiajia.getId(), bestHu));
							xiajia.checkAndGenerateGuoCandidateAction();
							qiangganghu = true;
						}

						currentPlayer = xiajia;
					}
				}
			}
			// 没有抢杠胡，杠完之后要摸牌
			if (!qiangganghu) {
				player.addActionCandidate(new MajiangMoAction(player.getId(),
						new GanghouBupai(gangAction.getPai(), gangAction.getGangType())));
			}
		}
	}
}
