package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.player.action.MajiangPlayerAction;

public class PanActionFrameVO {

	private MajiangPlayerAction action;
	private PanValueObjectVO panAfterAction;
	private long actionTime;

	public PanActionFrameVO(PanActionFrame panActionFrame) {
		action = panActionFrame.getAction();
		actionTime = panActionFrame.getActionTime();
		panAfterAction = new PanValueObjectVO(panActionFrame.getPanAfterAction());
	}

	public MajiangPlayerAction getAction() {
		return action;
	}

	public PanValueObjectVO getPanAfterAction() {
		return panAfterAction;
	}

	public long getActionTime() {
		return actionTime;
	}

}
