package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.ju.finish.FixedPanNumbersJuFinishiDeterminer;
import com.dml.majiang.ju.result.JuResult;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.pan.guipai.RandomGuipaiDeterminer;
import com.dml.majiang.pan.publicwaitingplayer.WaitDaPlayerPanPublicWaitingPlayerDeterminer;
import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.player.action.chi.PengganghuFirstChiActionProcessor;
import com.dml.majiang.player.action.gang.HuFirstGangActionProcessor;
import com.dml.majiang.player.action.guo.DoNothingGuoActionProcessor;
import com.dml.majiang.player.action.hu.PlayerHuAndClearAllActionHuActionUpdater;
import com.dml.majiang.player.action.hu.PlayerSetHuHuActionProcessor;
import com.dml.majiang.player.action.initial.ZhuangMoPaiInitialActionUpdater;
import com.dml.majiang.player.action.listener.comprehensive.DianpaoDihuOpportunityDetector;
import com.dml.majiang.player.action.listener.comprehensive.GuoPengBuPengStatisticsListener;
import com.dml.majiang.player.action.listener.comprehensive.JuezhangStatisticsListener;
import com.dml.majiang.player.action.listener.mo.MoGuipaiCounter;
import com.dml.majiang.player.action.peng.HuFirstPengActionProcessor;
import com.dml.majiang.player.menfeng.RandomMustHasDongPlayersMenFengDeterminer;
import com.dml.majiang.player.menfeng.ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer;
import com.dml.majiang.player.shoupaisort.BaibanDangGuipaiBenpaiShoupaiSortComparator;
import com.dml.majiang.player.zhuang.MenFengDongZhuangDeterminer;
import com.dml.majiang.position.MajiangPosition;
import com.dml.majiang.position.MajiangPositionUtil;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.extend.fpmpv.FixedPlayersMultipanAndVotetofinishGame;
import com.dml.mpgame.game.extend.multipan.WaitingNextPan;
import com.dml.mpgame.game.player.GamePlayer;
import com.dml.mpgame.game.player.PlayerPlaying;

public class MajiangGame extends FixedPlayersMultipanAndVotetofinishGame {
	private String gameId;
	private int panshu;
	private int renshu;
	private boolean jinjie1;
	private boolean jinjie2;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean lazila;
	private boolean gangsuanfen;
	private Ju ju;
	private int currentPanNo;
	private Map<String, Integer> playerLianZhuangCountMap = new HashMap<>();
	private Map<String, Integer> playeTotalScoreMap = new HashMap<>();

	public MaidiResult maidi(String playerId, boolean maidiState) throws Exception {
		MaidiResult maidiResult = new MaidiResult();
		WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = (WenzhouMajiangPanResultBuilder) ju
				.getCurrentPanResultBuilder();
		Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = wenzhouMajiangPanResultBuilder
				.getPlayerMaidiStateMap();
		String zhuangPlayerId = ju.getCurrentPan().getZhuangPlayerId();
		List<String> playerIdList = new ArrayList<>(playerMaidiStateMap.keySet());
		if (maidiState) {
			if (zhuangPlayerId.equals(playerId)) {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.maidi);
				for (String pid : playerIdList) {
					if (!zhuangPlayerId.equals(pid)) {
						playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.startDingdi);
					}
				}
			} else {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.dingdi);
			}
		} else {
			if (zhuangPlayerId.equals(playerId)) {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.bumai);
				for (String pid : playerIdList) {
					if (!zhuangPlayerId.equals(pid)) {
						playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.startDingdi);
					}
				}
			} else {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.buding);
			}
		}
		boolean start = true;
		for (String pid : playerIdList) {
			if (MajiangGamePlayerMaidiState.startDingdi.equals(playerMaidiStateMap.get(pid))
					|| MajiangGamePlayerMaidiState.startMaidi.equals(playerMaidiStateMap.get(pid))
					|| MajiangGamePlayerMaidiState.waitForMaidi.equals(playerMaidiStateMap.get(pid))) {
				start = false;
			}
		}
		if (start) {
			PanActionFrame firstActionFrame = startPan(playerIdList, System.currentTimeMillis());
			state = new Playing();
			maidiResult.setFirstActionFrame(firstActionFrame);
		}
		MajiangGameValueObject majiangGame = new MajiangGameValueObject(this);
		majiangGame.setPlayerMaidiStateMap(playerMaidiStateMap);
		maidiResult.setMajiangGame(majiangGame);
		return maidiResult;
	}

	public Map<String, MajiangGamePlayerMaidiState> createJuAndReadyFirstPan(long currentTime) throws Exception {
		ju = new Ju();
		// 因为买底动作将开始阶段分为两部分
		// ju.setStartFirstPanProcess(new ClassicStartFirstPanProcess());
		// ju.setStartNextPanProcess(new ClassicStartNextPanProcess());
		ju.setPlayersMenFengDeterminerForFirstPan(new RandomMustHasDongPlayersMenFengDeterminer(currentTime));
		ju.setPlayersMenFengDeterminerForNextPan(new ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer());
		ju.setZhuangDeterminerForFirstPan(new MenFengDongZhuangDeterminer());
		ju.setZhuangDeterminerForNextPan(new MenFengDongZhuangDeterminer());
		ju.setAvaliablePaiFiller(new WenzhouMajiangRandomAvaliablePaiFiller(currentTime + 1, shaozhongfa));
		ju.setGuipaiDeterminer(new RandomGuipaiDeterminer(currentTime + 2));
		ju.setFaPaiStrategy(new WenzhouMajiangFaPaiStrategy(16));
		ju.setCurrentPanFinishiDeterminer(new WenzhouMajiangPanFinishDeterminer());
		ju.setGouXingPanHu(new WenzhouMajiangGouXingPanHu());
		ju.setCurrentPanPublicWaitingPlayerDeterminer(new WaitDaPlayerPanPublicWaitingPlayerDeterminer());

		WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = new WenzhouMajiangPanResultBuilder();
		Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = new HashMap<>();
		wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(playerMaidiStateMap);
		wenzhouMajiangPanResultBuilder.setJinjie1(jinjie1);
		wenzhouMajiangPanResultBuilder.setJinjie2(jinjie2);
		wenzhouMajiangPanResultBuilder.setTeshushuangfan(teshushuangfan);
		wenzhouMajiangPanResultBuilder.setCaishenqian(caishenqian);
		wenzhouMajiangPanResultBuilder.setShaozhongfa(shaozhongfa);
		wenzhouMajiangPanResultBuilder.setLazila(lazila);
		wenzhouMajiangPanResultBuilder.setGangsuanfen(gangsuanfen);
		ju.setCurrentPanResultBuilder(wenzhouMajiangPanResultBuilder);

		ju.setJuFinishiDeterminer(new FixedPanNumbersJuFinishiDeterminer(panshu));

		ju.setJuResultBuilder(new WenzhouMajiangJuResultBuilder());

		ju.setInitialActionUpdater(new ZhuangMoPaiInitialActionUpdater());
		ju.setMoActionProcessor(new WenzhouMajiangMoActionProcessor());
		ju.setMoActionUpdater(new WenzhouMajiangMoActionUpdater());
		ju.setDaActionProcessor(new WenzhouMajiangDaActionProcessor());
		ju.setDaActionUpdater(new WenzhouMajiangDaActionUpdater());
		ju.setChiActionProcessor(new PengganghuFirstChiActionProcessor());
		ju.setChiActionUpdater(new WenzhouMajiangChiActionUpdater());
		ju.setPengActionProcessor(new HuFirstPengActionProcessor());
		ju.setPengActionUpdater(new WenzhouMajiangPengActionUpdater());
		ju.setGangActionProcessor(new HuFirstGangActionProcessor());
		ju.setGangActionUpdater(new WenzhouMajiangGangActionUpdater());
		ju.setGuoActionProcessor(new DoNothingGuoActionProcessor());
		ju.setGuoActionUpdater(new WenzhouMajiangGuoActionUpdater());
		ju.setHuActionProcessor(new PlayerSetHuHuActionProcessor());
		ju.setHuActionUpdater(new PlayerHuAndClearAllActionHuActionUpdater());

		ju.addActionStatisticsListener(new JuezhangStatisticsListener());
		ju.addActionStatisticsListener(new MoGuipaiCounter());
		ju.addActionStatisticsListener(new DianpaoDihuOpportunityDetector());
		ju.addActionStatisticsListener(new GuoPengBuPengStatisticsListener());

		Pan firstPan = new Pan();
		firstPan.setNo(1);
		allPlayerIds().forEach((pid) -> firstPan.addPlayer(pid));
		ju.setCurrentPan(firstPan);

		// 开始定第一盘的门风
		ju.determinePlayersMenFengForFirstPan();

		// 开始定第一盘庄家
		ju.determineZhuangForFirstPan();
		String zhuangPlayerId = firstPan.getZhuangPlayerId();
		playerLianZhuangCountMap.put(zhuangPlayerId, 1);
		allPlayerIds().forEach((pid) -> playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.waitForMaidi));
		playerMaidiStateMap.put(zhuangPlayerId, MajiangGamePlayerMaidiState.startMaidi);
		this.currentPanNo = firstPan.getNo();
		return playerMaidiStateMap;
	}

	public PanActionFrame startPan(List<String> playerIdList, long currentTime) throws Exception {
		// 开始填充可用的牌
		ju.fillAvaliablePai();

		// 开始定财神
		ju.determineGuipai();

		// 设置手牌排序器
		Pan currenPan = ju.getCurrentPan();
		Set<MajiangPai> publicGuipaiSet = currenPan.getPublicGuipaiSet();
		MajiangPai[] guipaiTypes = new MajiangPai[publicGuipaiSet.size()];
		publicGuipaiSet.toArray(guipaiTypes);
		MajiangPai guipai = guipaiTypes[0];
		ju.updateShoupaiListSortComparatorForAllPlayersInCurrentPan(
				new BaibanDangGuipaiBenpaiShoupaiSortComparator(guipai));
		// 开始发牌
		ju.faPai();

		// 庄家可以摸第一张牌
		ju.updateInitialAction();

		// 庄家摸第一张牌,进入正式行牌流程
		ju.action(ju.getCurrentPan().getZhuangPlayerId(), 1, System.currentTimeMillis());
		// 必然庄家已经先摸了一张牌了
		return ju.getCurrentPan().findLatestActionFrame();
	}

	public MajiangActionResult action(String playerId, int actionId, long actionTime) throws Exception {
		PanActionFrame panActionFrame = ju.action(playerId, actionId, actionTime);
		MajiangActionResult result = new MajiangActionResult();
		result.setPanActionFrame(panActionFrame);

		checkAndFinishPan();

		if (state.name().equals(WaitingNextPan.name) || state.name().equals(Finished.name)) {// 盘结束了
			this.currentPanNo = ju.countFinishedPan();
			WenzhouMajiangPanResult panResult = (WenzhouMajiangPanResult) ju.findLatestFinishedPanResult();
			panResult.getPlayerResultList()
					.forEach((pr) -> playeTotalScoreMap.put(pr.getPlayerId(), pr.getTotalScore()));
			result.setPanResult(panResult);
			if (state.name().equals(Finished.name)) {// 局结束了
				result.setJuResult((WenzhouMajiangJuResult) ju.getJuResult());
			}
		}
		result.setMajiangGame(new MajiangGameValueObject(this));
		return result;
	}

	public JuResult finishJu() {
		ju.finish();
		return ju.getJuResult();
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
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

	public boolean isGangsuanfen() {
		return gangsuanfen;
	}

	public void setGangsuanfen(boolean gangsuanfen) {
		this.gangsuanfen = gangsuanfen;
	}

	public Ju getJu() {
		return ju;
	}

	public void setJu(Ju ju) {
		this.ju = ju;
	}

	public Map<String, Integer> getPlayeTotalScoreMap() {
		return playeTotalScoreMap;
	}

	public void setPlayeTotalScoreMap(Map<String, Integer> playeTotalScoreMap) {
		this.playeTotalScoreMap = playeTotalScoreMap;
	}

	public Map<String, Integer> getPlayerLianZhuangCountMap() {
		return playerLianZhuangCountMap;
	}

	public void setPlayerLianZhuangCountMap(Map<String, Integer> playerLianZhuangCountMap) {
		this.playerLianZhuangCountMap = playerLianZhuangCountMap;
	}

	public int getCurrentPanNo() {
		return currentPanNo;
	}

	public void setCurrentPanNo(int currentPanNo) {
		this.currentPanNo = currentPanNo;
	}

	@Override
	protected boolean checkToFinishGame() throws Exception {
		return ju.getJuResult() != null;
	}

	@Override
	protected boolean checkToFinishCurrentPan() throws Exception {
		return ju.getCurrentPan() == null;
	}

	@Override
	protected void startNextPan() throws Exception {
		WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = (WenzhouMajiangPanResultBuilder) ju
				.getCurrentPanResultBuilder();
		Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = wenzhouMajiangPanResultBuilder
				.getPlayerMaidiStateMap();
		Map<String, MajiangGamePlayerMaidiState> newMaidiStateMap = null;
		ju.getActionStatisticsListenerManager().updateListenersForNextPan();
		Pan nextPan = new Pan();
		nextPan.setNo(ju.countFinishedPan() + 1);
		this.currentPanNo = nextPan.getNo();
		PanResult latestFinishedPanResult = ju.findLatestFinishedPanResult();
		List<String> allPlayerIds = latestFinishedPanResult.allPlayerIds();
		allPlayerIds.forEach((pid) -> nextPan.addPlayer(pid));
		ju.setCurrentPan(nextPan);
		ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer menFengDeterminer = (ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer) ju
				.getPlayersMenFengDeterminerForNextPan();
		String lastPanZhangPlayerId = latestFinishedPanResult.findZhuangPlayerId();
		int lastPanlianZhuangCount = playerLianZhuangCountMap.get(lastPanZhangPlayerId);
		playerLianZhuangCountMap.clear();

		if (lastPanlianZhuangCount < 4) {// 连庄没有超过4次
			// 开始定下一盘的门风
			ju.determinePlayersMenFengForNextPan();
		} else {
			// 先找出庄的下家
			String zhuangXiajiaPlayerId = latestFinishedPanResult.findXiajiaPlayerId(lastPanZhangPlayerId);
			// 下家连庄次数为一
			menFengDeterminer.setZhuangPlayerId(zhuangXiajiaPlayerId);
			menFengDeterminer.setLianZhuangCount(1);
			// 再计算要顺时针移几步到东
			MajiangPosition p = latestFinishedPanResult.playerMenFeng(zhuangXiajiaPlayerId);
			int n = 0;
			while (true) {
				MajiangPosition np = MajiangPositionUtil.nextPositionClockwise(p);
				n++;
				if (np.equals(MajiangPosition.dong)) {
					break;
				} else {
					p = np;
				}
			}
			// 最后给所有玩家设置门风
			for (String pid : allPlayerIds) {
				MajiangPosition playerMenFeng = latestFinishedPanResult.playerMenFeng(pid);
				MajiangPosition newPlayerMenFeng = playerMenFeng;
				for (int i = 0; i < n; i++) {
					newPlayerMenFeng = MajiangPositionUtil.nextPositionClockwise(newPlayerMenFeng);
				}
				nextPan.updatePlayerMenFeng(pid, newPlayerMenFeng);
			}
		}

		// 开始定下一盘庄家
		ju.determineZhuangForNextPan();
		String lianZhuangPlayerId = menFengDeterminer.getZhuangPlayerId();
		int lianZhuangCount = menFengDeterminer.getLianZhuangCount();
		playerLianZhuangCountMap.put(lianZhuangPlayerId, lianZhuangCount);
		if (lianZhuangCount > 1) {// 连庄
			newMaidiStateMap = playerMaidiStateMap;
			wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(newMaidiStateMap);
			startPan(allPlayerIds, System.currentTimeMillis());
			state = new Playing();
			updateAllPlayersState(new PlayerPlaying());
		} else {// 重新买底
			newMaidiStateMap = new HashMap<>();
			for (String pid : allPlayerIds) {
				newMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.waitForMaidi);
			}
			wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(newMaidiStateMap);
			String zhuangPlayerId = menFengDeterminer.getZhuangPlayerId();
			newMaidiStateMap.put(zhuangPlayerId, MajiangGamePlayerMaidiState.startMaidi);
			state = new MaidiState();
			updateAllPlayersState(new PlayerMaidi());
		}
		MajiangGameValueObject majiangGame = new MajiangGameValueObject(this);
		majiangGame.setPlayerMaidiStateMap(newMaidiStateMap);
	}

	@Override
	protected void updatePlayerToExtendedVotingState(GamePlayer player) {
		if (player.getState().name().equals(PlayerMaidi.name)) {
			player.setState(new PlayerVotingWhenMaidi());
		} else if (player.getState().name().equals(PlayerAfterMaidi.name)) {
			player.setState(new PlayerVotingWhenAfterMaidi());
		}
	}

	@Override
	protected void updateToExtendedVotingState() {
		if (state.name().equals(MaidiState.name)) {
			state = new VotingWhenMaidi();
		}
	}

	@Override
	protected void updatePlayerToExtendedVotedState(GamePlayer player) {
		if (player.getState().name().equals(PlayerVotingWhenMaidi.name)) {
			player.setState(new PlayerVotedWhenMaidi());
		} else if (player.getState().name().equals(PlayerVotingWhenAfterMaidi.name)) {
			player.setState(new PlayerVotedWhenAfterMaidi());
		}
	}

	@Override
	protected void recoveryPlayersStateFromExtendedVoting() throws Exception {
		if (state.name().equals(VotingWhenMaidi.name)) {
			for (GamePlayer player : idPlayerMap.values()) {
				if (player.getState().name().equals(PlayerVotingWhenMaidi.name)
						|| player.getState().name().equals(PlayerVotedWhenMaidi.name)) {
					updatePlayerState(player.getId(), new PlayerMaidi());
				} else if (player.getState().name().equals(PlayerVotingWhenAfterMaidi.name)
						|| player.getState().name().equals(PlayerVotedWhenAfterMaidi.name)) {
					updatePlayerState(player.getId(), new PlayerAfterMaidi());
				}
			}
		}
	}

	@Override
	protected void recoveryStateFromExtendedVoting() throws Exception {
		if (state.name().equals(VotingWhenMaidi.name)) {
			state = new MaidiState();
		}
	}

	@Override
	public void start() throws Exception {
		state = new MaidiState();
		updateAllPlayersState(new PlayerMaidi());
	}

}
