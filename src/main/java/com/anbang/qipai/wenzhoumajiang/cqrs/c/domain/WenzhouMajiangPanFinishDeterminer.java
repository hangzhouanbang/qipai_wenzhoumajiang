package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.finish.CurrentPanFinishiDeterminer;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.MajiangPlayerAction;
import com.dml.majiang.player.action.MajiangPlayerActionType;
import com.dml.majiang.player.action.hu.MajiangHuAction;

public class WenzhouMajiangPanFinishDeterminer implements CurrentPanFinishiDeterminer {

	@Override
	public boolean determineToFinishCurrentPan(Ju ju) {
		Pan currentPan = ju.getCurrentPan();
		boolean hu = currentPan.anyPlayerHu();
		// 如果有人胡
		if (hu) {
			List<MajiangPlayer> huPlayers = currentPan.findAllHuPlayers();
			Map<String, MajiangPlayer> huPlayerMap = new HashMap<>();
			Set<String> huPlayerIdSet = new HashSet<>();
			for (MajiangPlayer huPlayer : huPlayers) {
				huPlayerIdSet.add(huPlayer.getId());
				huPlayerMap.put(huPlayer.getId(), huPlayer);
			}
			MajiangPlayer bestHuPlayer = huPlayers.get(0);
			WenzhouMajiangHu bestHu = (WenzhouMajiangHu) bestHuPlayer.getHu();
			String dianpaoPlayerId = bestHu.getDianpaoPlayerId();
			if (dianpaoPlayerId != null) {// 如果是全求神或者点炮胡有可能有多个胡家
				MajiangPlayer dianpaoPlayer = currentPan.findPlayerById(dianpaoPlayerId);
				MajiangPlayer xiajiaPlayer = currentPan.findXiajia(dianpaoPlayer);
				MajiangPlayer betterHuPlayer = null;
				WenzhouMajiangHu betterHu = null;

				// 按点炮者下家开始遍历出最佳胡，软牌硬牌没有优先级
				while (true) {
					if (!xiajiaPlayer.getId().equals(dianpaoPlayerId)) {
						MajiangPlayer huPlayer = huPlayerMap.get(xiajiaPlayer.getId());
						if (huPlayer != null) {// 已经点了"胡"
							WenzhouMajiangHu majiangHu = (WenzhouMajiangHu) huPlayer.getHu();
							if (betterHu == null || (majiangHu.getHufan().getValue() > 2
									&& betterHu.getHufan().getValue() < majiangHu.getHufan().getValue())) {
								betterHuPlayer = xiajiaPlayer;
								betterHu = majiangHu;
							}
						} else {// 没有点"胡"或者选了"过"
							for (MajiangPlayerAction action : xiajiaPlayer.getActionCandidates().values()) {
								if (action.getType().equals(MajiangPlayerActionType.hu)) {
									MajiangHuAction huAction = (MajiangHuAction) action;
									WenzhouMajiangHu majiangHu = (WenzhouMajiangHu) huAction.getHu();
									if (betterHu == null || (majiangHu.getHufan().getValue() > 2
											&& betterHu.getHufan().getValue() < majiangHu.getHufan().getValue())) {
										betterHuPlayer = xiajiaPlayer;
										betterHu = majiangHu;
									}
								}
							}
						}
					} else {
						break;
					}
					xiajiaPlayer = currentPan.findXiajia(xiajiaPlayer);
				}
				if (betterHuPlayer != null) {
					bestHuPlayer = betterHuPlayer;
					bestHu = betterHu;
				}
				if (huPlayerIdSet.contains(bestHuPlayer.getId())) {
					return true;
				}
			} else {
				return true;
			}
			return false;
		} else {
			int liupai = 0;
			int avaliablePaiLeft = currentPan.countAvaliablePai();
			if (avaliablePaiLeft < liupai) {
				return true;
			} else {
				return false;
			}
		}
	}

}
