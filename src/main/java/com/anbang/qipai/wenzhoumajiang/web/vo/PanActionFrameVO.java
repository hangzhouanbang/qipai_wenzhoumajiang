package com.anbang.qipai.wenzhoumajiang.web.vo;

import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.player.action.MajiangPlayerAction;

public class PanActionFrameVO {
	private int no;
	private MajiangPlayerAction action;
	private PanValueObjectVO panAfterAction;
	private long actionTime;

	public PanActionFrameVO(PanActionFrame panActionFrame) {
		no = panActionFrame.getNo();
		action = panActionFrame.getAction();
		actionTime = panActionFrame.getActionTime();
		panAfterAction = new PanValueObjectVO(panActionFrame.getPanAfterAction());
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public void setAction(MajiangPlayerAction action) {
		this.action = action;
	}

	public void setPanAfterAction(PanValueObjectVO panAfterAction) {
		this.panAfterAction = panAfterAction;
	}

	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
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
