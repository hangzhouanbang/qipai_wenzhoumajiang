package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.chi.MajiangChiAction;
import com.dml.majiang.player.action.chi.MajiangPlayerChiActionUpdater;

public class WenzhouMajiangChiActionUpdater implements MajiangPlayerChiActionUpdater {

	@Override
	public void updateActions(MajiangChiAction chiAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		currentPan.clearAllPlayersActionCandidates();

		MajiangPlayer player = currentPan.findPlayerById(chiAction.getActionPlayerId());

		if (player.getActionCandidates().isEmpty()) {
			player.generateDaActions();
		}
	}

}
