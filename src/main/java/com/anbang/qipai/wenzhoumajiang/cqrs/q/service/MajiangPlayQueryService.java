package com.anbang.qipai.wenzhoumajiang.cqrs.q.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangActionResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyForGameResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyToNextPanResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.WenzhouMajiangPanResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.GameLatestPanActionFrameDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.JuResultDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.MajiangGameDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.MajiangGamePlayerMaidiDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dao.PanResultDboDao;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.GameLatestPanActionFrameDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.JuResultDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerMaidiDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.PanResultDbo;
import com.anbang.qipai.wenzhoumajiang.plan.bean.PlayerInfo;
import com.anbang.qipai.wenzhoumajiang.plan.dao.PlayerInfoDao;
import com.dml.majiang.pan.frame.LiangangangPanActionFramePlayerViewFilter;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.extend.vote.VotingWhenPlaying;

@Component
public class MajiangPlayQueryService {

	@Autowired
	private MajiangGameDboDao majiangGameDboDao;

	@Autowired
	private PanResultDboDao panResultDboDao;

	@Autowired
	private JuResultDboDao juResultDboDao;

	@Autowired
	private PlayerInfoDao playerInfoDao;

	@Autowired
	private MajiangGamePlayerMaidiDboDao majiangGamePlayerMaidiDboDao;

	@Autowired
	private GameLatestPanActionFrameDboDao gameLatestPanActionFrameDboDao;

	private LiangangangPanActionFramePlayerViewFilter pvFilter = new LiangangangPanActionFramePlayerViewFilter();

	public PanActionFrame findAndFilterCurrentPanValueObjectForPlayer(String gameId, String playerId) throws Exception {
		MajiangGameDbo majiangGameDbo = majiangGameDboDao.findById(gameId);
		if (!(majiangGameDbo.getState().name().equals(Playing.name)
				|| majiangGameDbo.getState().name().equals(VotingWhenPlaying.name))) {
			throw new Exception("game not playing");
		}

		GameLatestPanActionFrameDbo frame = gameLatestPanActionFrameDboDao.findById(gameId);
		byte[] frameData = frame.getData();
		PanActionFrame panActionFrame = PanActionFrame.fromByteArray(frameData);
		pvFilter.filter(panActionFrame, playerId);
		return panActionFrame;
	}

	public void readyForGame(ReadyForGameResult readyForGameResult) throws Throwable {
		MajiangGameValueObject majiangGame = readyForGameResult.getMajiangGame();
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		majiangGame.allPlayerIds().forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		MajiangGameDbo majiangGameDbo = new MajiangGameDbo(majiangGame, playerInfoMap);
		majiangGameDboDao.save(majiangGameDbo);

		if (majiangGame.getState().name().equals(MaidiState.name)) {// 开始买底
			MajiangGamePlayerMaidiDbo maidiDbo = new MajiangGamePlayerMaidiDbo(majiangGame);
			majiangGamePlayerMaidiDboDao.addMajiangGamePlayerMaidiDbo(maidiDbo);
		}
	}

	public void maidi(MaidiResult maidiResult) throws Throwable {
		MajiangGameValueObject majiangGame = maidiResult.getMajiangGame();
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		majiangGame.allPlayerIds().forEach((pid) -> playerInfoMap.put(pid, playerInfoDao.findById(pid)));
		MajiangGameDbo majiangGameDbo = new MajiangGameDbo(majiangGame, playerInfoMap);
		majiangGameDboDao.save(majiangGameDbo);

		MajiangGamePlayerMaidiDbo maidiDbo = new MajiangGamePlayerMaidiDbo(majiangGame);
		majiangGamePlayerMaidiDboDao.updateMajiangGamePlayerMaidiDbo(maidiDbo.getGameId(), maidiDbo.getPanNo(),
				maidiDbo.getPlayerMaidiStateMap());
		if (maidiResult.getFirstActionFrame() != null) {
			gameLatestPanActionFrameDboDao.save(majiangGame.getId(),
					maidiResult.getFirstActionFrame().toByteArray(1024 * 8));
			// TODO 记录一条Frame，回放的时候要做
		}
	}

	public void readyToNextPan(ReadyToNextPanResult readyToNextPanResult) throws Throwable {

		MajiangGameValueObject majiangGame = readyToNextPanResult.getMajiangGame();
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		majiangGame.allPlayerIds().forEach((pid) -> playerInfoMap.put(pid, playerInfoDao.findById(pid)));
		MajiangGameDbo majiangGameDbo = new MajiangGameDbo(majiangGame, playerInfoMap);
		majiangGameDboDao.save(majiangGameDbo);
		if (majiangGame.getState().name().equals(MaidiState.name)) {
			MajiangGamePlayerMaidiDbo maidiDbo = new MajiangGamePlayerMaidiDbo(majiangGame);
			majiangGamePlayerMaidiDboDao.addMajiangGamePlayerMaidiDbo(maidiDbo);
		}
		if (readyToNextPanResult.getFirstActionFrame() != null) {

			gameLatestPanActionFrameDboDao.save(majiangGame.getId(),
					readyToNextPanResult.getFirstActionFrame().toByteArray(1024 * 8));
			// TODO 记录一条Frame，回放的时候要做

		}

	}

	public void action(MajiangActionResult majiangActionResult) throws Throwable {

		MajiangGameValueObject majiangGame = majiangActionResult.getMajiangGame();
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		majiangGame.allPlayerIds().forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		MajiangGameDbo majiangGameDbo = new MajiangGameDbo(majiangGame, playerInfoMap);
		majiangGameDboDao.save(majiangGameDbo);

		String gameId = majiangActionResult.getMajiangGame().getId();
		PanActionFrame panActionFrame = majiangActionResult.getPanActionFrame();
		gameLatestPanActionFrameDboDao.save(gameId, panActionFrame.toByteArray(1024 * 8));
		// TODO 记录一条Frame，回放的时候要做

		// 盘出结果的话要记录结果
		WenzhouMajiangPanResult wenzhouMajiangPanResult = majiangActionResult.getPanResult();
		if (wenzhouMajiangPanResult != null) {
			PanResultDbo panResultDbo = new PanResultDbo(gameId, wenzhouMajiangPanResult);
			panResultDbo.setPanActionFrame(panActionFrame);
			panResultDboDao.save(panResultDbo);
			if (majiangActionResult.getJuResult() != null) {// 一切都结束了
				// 要记录局结果
				JuResultDbo juResultDbo = new JuResultDbo(gameId, panResultDbo, majiangActionResult.getJuResult());
				juResultDboDao.save(juResultDbo);
			}
		}

	}

	public PanResultDbo findPanResultDbo(String gameId, int panNo) {
		return panResultDboDao.findByGameIdAndPanNo(gameId, panNo);
	}

	public JuResultDbo findJuResultDbo(String gameId) {
		return juResultDboDao.findByGameId(gameId);
	}

	public MajiangGamePlayerMaidiDbo findLastPlayerMaidiDboByGameId(String gameId) {
		return majiangGamePlayerMaidiDboDao.findLastByGameId(gameId);
	}

}
