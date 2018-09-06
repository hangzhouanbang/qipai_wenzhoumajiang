package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.MajiangPlayerAction;
import com.dml.majiang.player.action.MajiangPlayerActionType;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.guo.MajiangGuoAction;
import com.dml.majiang.player.action.guo.MajiangPlayerGuoActionUpdater;
import com.dml.majiang.player.action.listener.comprehensive.JuezhangStatisticsListener;
import com.dml.majiang.player.action.mo.LundaoMopai;
import com.dml.majiang.player.action.mo.MajiangMoAction;

public class WenzhouMajiangGuoActionUpdater implements MajiangPlayerGuoActionUpdater {

	@Override
	public void updateActions(MajiangGuoAction guoAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		currentPan.playerClearActionCandidates(guoAction.getActionPlayerId());

		MajiangPlayer player = currentPan.findPlayerById(guoAction.getActionPlayerId());

		// 首先看一下,我过的是什么? 是我摸牌之后的胡,杠? 还是别人打出牌之后我可以吃碰杠胡
		PanActionFrame latestPanActionFrame = currentPan.findLatestActionFrame();
		MajiangPlayerAction action = latestPanActionFrame.getAction();
		if (action.getType().equals(MajiangPlayerActionType.mo)) {// 过的是我摸牌之后的胡,杠
			// 那要我打牌
			List<MajiangPai> fangruShoupaiList = player.getFangruShoupaiList();
			JuezhangStatisticsListener juezhangStatisticsListener = ju.getActionStatisticsListenerManager()
					.findListener(JuezhangStatisticsListener.class);
			for (MajiangPai pai : fangruShoupaiList) {
				if (MajiangPai.isZipai(pai) && juezhangStatisticsListener.ifJuezhang(pai)) {
					player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
				}
			}
			if (player.getActionCandidates().isEmpty()) {
				player.generateDaActions();
			}
		} else if (action.getType().equals(MajiangPlayerActionType.da)) {// 过的是别人打出牌之后我可以吃碰杠胡
			if (currentPan.allPlayerHasNoActionCandidates()) {// 如果所有玩家啥也干不了
				// 打牌那家的下家摸牌
				MajiangPlayer xiajiaPlayer = currentPan
						.findXiajia(currentPan.findPlayerById(action.getActionPlayerId()));
				xiajiaPlayer.addActionCandidate(new MajiangMoAction(xiajiaPlayer.getId(), new LundaoMopai()));
			}
		} else {
		}
	}

}
