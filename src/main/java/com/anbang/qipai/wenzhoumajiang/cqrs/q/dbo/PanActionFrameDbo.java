package com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo;

public class PanActionFrameDbo {

	private String id;
	private String gameId;
	private int panNo;
	private int actionNo;
	private byte[] frameData;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getPanNo() {
		return panNo;
	}

	public void setPanNo(int panNo) {
		this.panNo = panNo;
	}

	public int getActionNo() {
		return actionNo;
	}

	public void setActionNo(int actionNo) {
		this.actionNo = actionNo;
	}

	public byte[] getFrameData() {
		return frameData;
	}

	public void setFrameData(byte[] frameData) {
		this.frameData = frameData;
	}

}
