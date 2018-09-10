package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.result.CurrentPanResultBuilder;
import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.menfeng.ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer;
import com.dml.majiang.player.shoupai.ShoupaiPaiXing;

public class WenzhouMajiangPanResultBuilder implements CurrentPanResultBuilder {
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap;
	private boolean jinjie;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean lazila;

	@Override
	public PanResult buildCurrentPanResult(Ju ju, long panFinishTime) {
		Pan currentPan = ju.getCurrentPan();
		WenzhouMajiangPanResult latestFinishedPanResult = (WenzhouMajiangPanResult) ju.findLatestFinishedPanResult();
		Map<String, Integer> playerTotalScoreMap = new HashMap<>();
		if (latestFinishedPanResult != null) {
			for (WenzhouMajiangPanPlayerResult panPlayerResult : latestFinishedPanResult.getPlayerResultList()) {
				playerTotalScoreMap.put(panPlayerResult.getPlayerId(), panPlayerResult.getTotalScore());
			}
		}
		ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer menfengDeterminer = (ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer) ju
				.getPlayersMenFengDeterminerForNextPan();
		int difen = 2;
		if (menfengDeterminer.getLianZhunagCount() == 2) {
			difen = 4;
		}
		if (menfengDeterminer.getLianZhunagCount() == 3) {
			if (jinjie) {
				difen = 8;
			} else {
				difen = 4;
			}
		}
		if (menfengDeterminer.getLianZhunagCount() >= 4) {
			if (jinjie) {
				difen = 16;
			} else {
				difen = 8;
			}
		}
		MajiangPlayer huPlayer = currentPan.findHuPlayer();
		List<String> playerIdList = currentPan.sortedPlayerIdList();
		List<WenzhouMajiangPanPlayerResult> playerResultList = new ArrayList<>();
		if (huPlayer != null) {// 正常有人胡
			WenzhouMajiangHu hu = (WenzhouMajiangHu) huPlayer.getHu();
			WenzhouMajiangPanPlayerHufan huPlayerHufan = hu.getHufan();
			ShoupaiPaiXing huShoupaiPaiXing = hu.getShoupaiPaiXing();
			int paixingbeishu = huPlayerHufan.getValue();
			playerIdList.forEach((playerId) -> {
				MajiangPlayer player = currentPan.findPlayerById(playerId);
				WenzhouMajiangPanPlayerResult playerResult = new WenzhouMajiangPanPlayerResult();
				playerResult.setPlayerId(playerId);
				if (playerId.equals(huPlayer.getId())) {
					playerResult.setHufan(huPlayerHufan);
					WenzhouMajiangPanPlayerCaishenqian caishenqian = new WenzhouMajiangPanPlayerCaishenqian(player);
					caishenqian.calculate(true, this.caishenqian);
					playerResult.setCaishenqian(caishenqian);
				} else {
					// 计算非胡玩家分数
					playerResult.setHufan(new WenzhouMajiangPanPlayerHufan());
					WenzhouMajiangPanPlayerCaishenqian caishenqian = new WenzhouMajiangPanPlayerCaishenqian(player);
					caishenqian.calculate(false, this.caishenqian);
					playerResult.setCaishenqian(caishenqian);
				}
				playerResultList.add(playerResult);
			});

			for (int i = 0; i < playerResultList.size(); i++) {
				WenzhouMajiangPanPlayerResult playerResult1 = playerResultList.get(i);
				WenzhouMajiangPanPlayerCaishenqian caishenqian1 = playerResult1.getCaishenqian();
				String playerId1 = playerResult1.getPlayerId();
				for (int j = (i + 1); j < playerResultList.size(); j++) {
					WenzhouMajiangPanPlayerResult playerResult2 = playerResultList.get(j);
					WenzhouMajiangPanPlayerCaishenqian caishenqian2 = playerResult2.getCaishenqian();
					String playerId2 = playerResult2.getPlayerId();
					// 结算财神钱
					int qian1 = caishenqian1.getValue();
					int qian2 = caishenqian2.getValue();
					caishenqian1.jiesuan(-qian2);
					caishenqian2.jiesuan(-qian1);
					if (playerId1.equals(huPlayer.getId())) {// 1胡2不胡
						// 是不是庄家胡
						boolean zhuangHu = currentPan.getZhuangPlayerId().equals(playerId1);
						if (zhuangHu) {// 闲家输庄家
							int maidiCount = 1;// 底分倍数
							if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)) {
								maidiCount += 1;
							}
							if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)) {
								maidiCount += 1;
							}
							int score1 = playerResult1.getScore();
							int score2 = playerResult2.getScore();
							score1 += difen * maidiCount * paixingbeishu;
							score2 -= difen * maidiCount * paixingbeishu;
							playerResult1.setScore(score1);
							playerResult2.setScore(score2);
						} else {
							// 是不是庄家输
							boolean zhuangShu = currentPan.getZhuangPlayerId().equals(playerId2);
							if (zhuangShu) {// 庄家输闲家
								int maidiCount = 1;// 底分倍数
								if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)) {
									maidiCount += 1;
								}
								if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)) {
									maidiCount += 1;
								}
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 += difen * maidiCount * paixingbeishu;
								score2 -= difen * maidiCount * paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							} else {// 闲家输闲家
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 += paixingbeishu;
								score2 -= paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							}
						}
					} else if (playerId2.equals(huPlayer.getId())) {// 2胡1不胡
						// 是不是庄家胡
						boolean zhuangHu = currentPan.getZhuangPlayerId().equals(playerId2);
						if (zhuangHu) {// 闲家输庄家
							int maidiCount = 1;// 底分倍数
							if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)) {
								maidiCount += 1;
							}
							if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)) {
								maidiCount += 1;
							}
							int score1 = playerResult1.getScore();
							int score2 = playerResult2.getScore();
							score1 -= difen * maidiCount * paixingbeishu;
							score2 += difen * maidiCount * paixingbeishu;
							playerResult1.setScore(score1);
							playerResult2.setScore(score2);
						} else {
							// 是不是庄家输
							boolean zhuangShu = currentPan.getZhuangPlayerId().equals(playerId1);
							if (zhuangShu) {// 庄家输闲家
								int maidiCount = 1;// 底分倍数
								if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)) {
									maidiCount += 1;
								}
								if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)) {
									maidiCount += 1;
								}
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 -= difen * maidiCount * paixingbeishu;
								score2 += difen * maidiCount * paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							} else {// 闲家输闲家
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 -= paixingbeishu;
								score2 += paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							}
						}
					} else {// 不胡之间

					}
				}
			}

			// 胡的那家shoupaixing放入结果，其余不胡的shoupailist放入结果
			playerResultList.forEach((playerResult) -> {
				MajiangPlayer player = currentPan.findPlayerById(playerResult.getPlayerId());
				// 计算当盘总分
				playerResult.setScore(playerResult.getScore() + playerResult.getCaishenqian().getValue());
				// 计算累计总分
				if (latestFinishedPanResult != null) {
					playerResult.setTotalScore(
							playerTotalScoreMap.get(playerResult.getPlayerId()) + playerResult.getScore());
				} else {
					playerResult.setTotalScore(playerResult.getScore());
				}
				playerResult.setMenFeng(player.getMenFeng());
				// 吃碰杠出去的要加到结果
				playerResult.setPublicPaiList(new ArrayList<>(player.getPublicPaiList()));
				playerResult.setChichupaiZuList(new ArrayList<>(player.getChichupaiZuList()));
				playerResult.setPengchupaiZuList(new ArrayList<>(player.getPengchupaiZuList()));
				playerResult.setGangchupaiZuList(new ArrayList<>(player.getGangchupaiZuList()));
				playerResult.setGuipaiTypeSet(new HashSet<>(player.getGuipaiTypeSet()));
				playerResult.setShoupaiList(new ArrayList<>(player.getFangruShoupaiList()));
				if (playerResult.getPlayerId().equals(huPlayer.getId())) {
					playerResult.setHu(true);
					playerResult.setBestShoupaiPaiXing(huShoupaiPaiXing);
				} else {
					playerResult.setHu(false);
				}
			});

			WenzhouMajiangPanResult wenzhouMajiangPanResult = new WenzhouMajiangPanResult();
			wenzhouMajiangPanResult.setPanNo(currentPan.getNo());
			wenzhouMajiangPanResult.setPanFinishTime(panFinishTime);
			wenzhouMajiangPanResult.setZhuangPlayerId(currentPan.getZhuangPlayerId());
			wenzhouMajiangPanResult.setPlayerResultList(playerResultList);
			wenzhouMajiangPanResult.setHu(true);
			wenzhouMajiangPanResult.setZimo(hu.isZimo());
			wenzhouMajiangPanResult.setDianpaoPlayerId(hu.getDianpaoPlayerId());
			return wenzhouMajiangPanResult;
		} else {// 流局
			playerIdList.forEach((playerId) -> {
				MajiangPlayer player = currentPan.findPlayerById(playerId);
				WenzhouMajiangPanPlayerResult playerResult = new WenzhouMajiangPanPlayerResult();
				playerResult.setPlayerId(playerId);
				playerResult.setHufan(new WenzhouMajiangPanPlayerHufan());
				WenzhouMajiangPanPlayerCaishenqian caishenqian = new WenzhouMajiangPanPlayerCaishenqian(player);
				caishenqian.calculate(false, this.caishenqian);
				playerResult.setCaishenqian(caishenqian);
				playerResultList.add(playerResult);
			});

			for (int i = 0; i < playerResultList.size(); i++) {
				WenzhouMajiangPanPlayerResult playerResult1 = playerResultList.get(i);
				WenzhouMajiangPanPlayerCaishenqian caishenqian1 = playerResult1.getCaishenqian();
				for (int j = (i + 1); j < playerResultList.size(); j++) {
					WenzhouMajiangPanPlayerResult playerResult2 = playerResultList.get(j);
					WenzhouMajiangPanPlayerCaishenqian caishenqian2 = playerResult2.getCaishenqian();
					// 结算财神钱
					caishenqian1.jiesuan(-caishenqian2.getValue());
					caishenqian2.jiesuan(-caishenqian1.getValue());
				}
			}
			// shoupailist放入结果
			playerResultList.forEach((playerResult) -> {
				MajiangPlayer player = currentPan.findPlayerById(playerResult.getPlayerId());
				// 计算当盘总分
				playerResult.setScore(playerResult.getScore() + playerResult.getCaishenqian().getValue());
				// 计算累计总分
				if (latestFinishedPanResult != null) {
					playerResult.setTotalScore(
							playerTotalScoreMap.get(playerResult.getPlayerId()) + playerResult.getScore());
				} else {
					playerResult.setTotalScore(playerResult.getScore());
				}
				playerResult.setMenFeng(player.getMenFeng());
				// 吃碰杠出去的要加到结果
				playerResult.setPublicPaiList(new ArrayList<>(player.getPublicPaiList()));
				playerResult.setChichupaiZuList(new ArrayList<>(player.getChichupaiZuList()));
				playerResult.setPengchupaiZuList(new ArrayList<>(player.getPengchupaiZuList()));
				playerResult.setGangchupaiZuList(new ArrayList<>(player.getGangchupaiZuList()));
				playerResult.setGuipaiTypeSet(new HashSet<>(player.getGuipaiTypeSet()));
				playerResult.setShoupaiList(new ArrayList<>(player.getFangruShoupaiList()));
				playerResult.setHu(false);
			});

			WenzhouMajiangPanResult wenzhouMajiangPanResult = new WenzhouMajiangPanResult();
			wenzhouMajiangPanResult.setPanNo(currentPan.getNo());
			wenzhouMajiangPanResult.setPanFinishTime(panFinishTime);
			wenzhouMajiangPanResult.setZhuangPlayerId(currentPan.getZhuangPlayerId());
			wenzhouMajiangPanResult.setPlayerResultList(playerResultList);
			wenzhouMajiangPanResult.setHu(false);

			return wenzhouMajiangPanResult;
		}
	}

	public boolean isJinjie() {
		return jinjie;
	}

	public void setJinjie(boolean jinjie) {
		this.jinjie = jinjie;
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

	public Map<String, MajiangGamePlayerMaidiState> getPlayerMaidiStateMap() {
		return playerMaidiStateMap;
	}

	public void setPlayerMaidiStateMap(Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap) {
		this.playerMaidiStateMap = playerMaidiStateMap;
	}

}
