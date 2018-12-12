package com.anbang.qipai.wenzhoumajiang.msg.msjobj;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangJuResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.JuResultDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;

public class MajiangHistoricalJuResult {
	private String gameId;
	private String dayingjiaId;
	private String datuhaoId;
	private List<WenzhouMajiangJuPlayerResultMO> playerResultList;
	private int lastPanNo;
	private int panshu;
	private long finishTime;

    public MajiangHistoricalJuResult() {
    }

    public MajiangHistoricalJuResult(JuResultDbo juResultDbo, MajiangGameDbo majiangGameDbo) {
		gameId = juResultDbo.getGameId();
		WenzhouMajiangJuResult wenzhouMajiangJuResult = juResultDbo.getJuResult();
		dayingjiaId = wenzhouMajiangJuResult.getDayingjiaId();
		datuhaoId = wenzhouMajiangJuResult.getDatuhaoId();

		finishTime = juResultDbo.getFinishTime();
		this.panshu = majiangGameDbo.getPanshu();
		lastPanNo = wenzhouMajiangJuResult.getFinishedPanCount();
		playerResultList = new ArrayList<>();
		if (wenzhouMajiangJuResult.getPlayerResultList() != null) {
			wenzhouMajiangJuResult.getPlayerResultList()
					.forEach((juPlayerResult) -> playerResultList.add(new WenzhouMajiangJuPlayerResultMO(juPlayerResult,
							majiangGameDbo.findPlayer(juPlayerResult.getPlayerId()))));
		} else {
			majiangGameDbo.getPlayers().forEach((majiangGamePlayerDbo) -> playerResultList
					.add(new WenzhouMajiangJuPlayerResultMO(majiangGamePlayerDbo)));
		}
	}

	public String getDayingjiaId() {
		return dayingjiaId;
	}

	public void setDayingjiaId(String dayingjiaId) {
		this.dayingjiaId = dayingjiaId;
	}

	public String getDatuhaoId() {
		return datuhaoId;
	}

	public void setDatuhaoId(String datuhaoId) {
		this.datuhaoId = datuhaoId;
	}

	public List<WenzhouMajiangJuPlayerResultMO> getPlayerResultList() {
		return playerResultList;
	}

	public void setPlayerResultList(List<WenzhouMajiangJuPlayerResultMO> playerResultList) {
		this.playerResultList = playerResultList;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public int getLastPanNo() {
		return lastPanNo;
	}

	public void setLastPanNo(int lastPanNo) {
		this.lastPanNo = lastPanNo;
	}

	public int getPanshu() {
		return panshu;
	}

	public void setPanshu(int panshu) {
		this.panshu = panshu;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

}
