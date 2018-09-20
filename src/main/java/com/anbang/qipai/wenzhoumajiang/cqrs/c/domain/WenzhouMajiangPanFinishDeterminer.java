package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.finish.CurrentPanFinishiDeterminer;

public class WenzhouMajiangPanFinishDeterminer implements CurrentPanFinishiDeterminer {

	@Override
	public boolean determineToFinishCurrentPan(Ju ju) {
		Pan currentPan = ju.getCurrentPan();
		boolean hu = currentPan.anyPlayerHu();
		// 当有人胡并且所有人没有胡的动作游戏结束
		if (hu && currentPan.allPlayerHasNoHuActionCandidates()) {
			return true;
		} else {
			int liupai = 0;
			int avaliablePaiLeft = currentPan.countAvaliablePai();
			if (avaliablePaiLeft <= liupai) {
				return true;
			} else {
				return false;
			}
		}
	}

}
