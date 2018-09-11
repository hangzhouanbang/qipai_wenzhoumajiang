package com.anbang.qipai.wenzhoumajiang.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameState;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;

public class GameVO {
	private String id;// 就是gameid
	private int lianzhuangCount;
	private int panshu;
	private int renshu;
	private boolean jinjie1;
	private boolean jinjie2;
	private boolean gangsuanfen;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean lazila;
	private List<MajiangGamePlayerVO> playerList;
	private MajiangGameState state;

	public GameVO(MajiangGameDbo majiangGameDbo) {
		id = majiangGameDbo.getId();
		lianzhuangCount = majiangGameDbo.getLianzhuangCount();
		panshu = majiangGameDbo.getPanshu();
		renshu = majiangGameDbo.getRenshu();
		jinjie1 = majiangGameDbo.isJinjie1();
		jinjie2 = majiangGameDbo.isJinjie2();
		gangsuanfen = majiangGameDbo.isGangsuanfen();
		teshushuangfan = majiangGameDbo.isTeshushuangfan();
		caishenqian = majiangGameDbo.isCaishenqian();
		shaozhongfa = majiangGameDbo.isShaozhongfa();
		lazila = majiangGameDbo.isLazila();
		playerList = new ArrayList<>();
		majiangGameDbo.getPlayers().forEach((dbo) -> playerList.add(new MajiangGamePlayerVO(dbo)));
		state = majiangGameDbo.getState();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLianzhuangCount() {
		return lianzhuangCount;
	}

	public void setLianzhuangCount(int lianzhuangCount) {
		this.lianzhuangCount = lianzhuangCount;
	}

	public int getPanshu() {
		return panshu;
	}

	public void setPanshu(int panshu) {
		this.panshu = panshu;
	}

	public int getRenshu() {
		return renshu;
	}

	public void setRenshu(int renshu) {
		this.renshu = renshu;
	}

	public boolean isJinjie1() {
		return jinjie1;
	}

	public void setJinjie1(boolean jinjie1) {
		this.jinjie1 = jinjie1;
	}

	public boolean isJinjie2() {
		return jinjie2;
	}

	public void setJinjie2(boolean jinjie2) {
		this.jinjie2 = jinjie2;
	}

	public boolean isGangsuanfen() {
		return gangsuanfen;
	}

	public void setGangsuanfen(boolean gangsuanfen) {
		this.gangsuanfen = gangsuanfen;
	}

	public boolean isTeshushuangfan() {
		return teshushuangfan;
	}

	public void setTeshushuangfan(boolean teshushuangfan) {
		this.teshushuangfan = teshushuangfan;
	}

	public boolean isCaishenqian() {
		return caishenqian;
	}

	public void setCaishenqian(boolean caishenqian) {
		this.caishenqian = caishenqian;
	}

	public boolean isShaozhongfa() {
		return shaozhongfa;
	}

	public void setShaozhongfa(boolean shaozhongfa) {
		this.shaozhongfa = shaozhongfa;
	}

	public boolean isLazila() {
		return lazila;
	}

	public void setLazila(boolean lazila) {
		this.lazila = lazila;
	}

	public List<MajiangGamePlayerVO> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<MajiangGamePlayerVO> playerList) {
		this.playerList = playerList;
	}

	public MajiangGameState getState() {
		return state;
	}

	public void setState(MajiangGameState state) {
		this.state = state;
	}
}
