package com.anbang.qipai.wenzhoumajiang.web.vo;

import java.util.Map;
import java.util.Set;

import com.dml.mpgame.game.extend.vote.GameFinishVoteValueObject;
import com.dml.mpgame.game.extend.vote.VoteOption;
import com.dml.mpgame.game.extend.vote.VoteResult;

public class GameFinishVoteVO {
	private String sponsorId;

	private Set<String> votePlayerIds;

	private Map<String, VoteOption> playerIdVoteOptionMap;

	private VoteResult result;

	private long startTime;

	private long remainTime;

	public GameFinishVoteVO() {

	}

	public GameFinishVoteVO(GameFinishVoteValueObject vote) {
		sponsorId = vote.getSponsorId();
		votePlayerIds = vote.getVotePlayerIds();
		playerIdVoteOptionMap = vote.getPlayerIdVoteOptionMap();
		result = vote.getResult();
		startTime = vote.getStartTime();
		long endTime = vote.getEndTime();
		long currentTime = System.currentTimeMillis();
		remainTime = currentTime > endTime ? 0 : endTime - currentTime;
	}

	public String getSponsorId() {
		return sponsorId;
	}

	public void setSponsorId(String sponsorId) {
		this.sponsorId = sponsorId;
	}

	public Set<String> getVotePlayerIds() {
		return votePlayerIds;
	}

	public void setVotePlayerIds(Set<String> votePlayerIds) {
		this.votePlayerIds = votePlayerIds;
	}

	public Map<String, VoteOption> getPlayerIdVoteOptionMap() {
		return playerIdVoteOptionMap;
	}

	public void setPlayerIdVoteOptionMap(Map<String, VoteOption> playerIdVoteOptionMap) {
		this.playerIdVoteOptionMap = playerIdVoteOptionMap;
	}

	public VoteResult getResult() {
		return result;
	}

	public void setResult(VoteResult result) {
		this.result = result;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(long remainTime) {
		this.remainTime = remainTime;
	}

}
