package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.player.menfeng.PlayersMenFengDeterminer;
import com.dml.majiang.position.MajiangPosition;
import com.dml.majiang.position.MajiangPositionUtil;

public class WenzhouMajiangPlayersMenFengDeterminer implements PlayersMenFengDeterminer {

	private String zhuangPlayerId;
	private int lianZhuangCount = 1;

	@Override
	public void determinePlayersMenFeng(Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		PanResult latestFinishedPanResult = ju.findLatestFinishedPanResult();
		String zhuangPlayerId = latestFinishedPanResult.findZhuangPlayerId();
		this.zhuangPlayerId = zhuangPlayerId;
		if (lianZhuangCount < 4) {// 连庄没有超过4次
			if (!latestFinishedPanResult.ifPlayerHu(zhuangPlayerId)) {// 庄没有胡
				// 先找出庄的下家
				String zhuangXiajiaPlayerId = latestFinishedPanResult.findXiajiaPlayerId(zhuangPlayerId);
				// 下家连庄次数为一
				this.zhuangPlayerId = zhuangXiajiaPlayerId;
				lianZhuangCount = 1;
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
				List<String> allPlayerIds = latestFinishedPanResult.allPlayerIds();
				for (String playerId : allPlayerIds) {
					MajiangPosition playerMenFeng = latestFinishedPanResult.playerMenFeng(playerId);
					MajiangPosition newPlayerMenFeng = playerMenFeng;
					for (int i = 0; i < n; i++) {
						newPlayerMenFeng = MajiangPositionUtil.nextPositionClockwise(newPlayerMenFeng);
					}
					currentPan.updatePlayerMenFeng(playerId, newPlayerMenFeng);
				}
			} else {
				// 连庄次数加一
				lianZhuangCount += 1;
				List<String> allPlayerIds = latestFinishedPanResult.allPlayerIds();
				for (String playerId : allPlayerIds) {
					MajiangPosition playerMenFeng = latestFinishedPanResult.playerMenFeng(playerId);
					currentPan.updatePlayerMenFeng(playerId, playerMenFeng);
				}
			}
		} else {// 强制换庄
			// 先找出庄的下家
			String zhuangXiajiaPlayerId = latestFinishedPanResult.findXiajiaPlayerId(zhuangPlayerId);
			// 下家连庄次数为一
			this.zhuangPlayerId = zhuangXiajiaPlayerId;
			lianZhuangCount = 1;
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
			List<String> allPlayerIds = latestFinishedPanResult.allPlayerIds();
			for (String playerId : allPlayerIds) {
				MajiangPosition playerMenFeng = latestFinishedPanResult.playerMenFeng(playerId);
				MajiangPosition newPlayerMenFeng = playerMenFeng;
				for (int i = 0; i < n; i++) {
					newPlayerMenFeng = MajiangPositionUtil.nextPositionClockwise(newPlayerMenFeng);
				}
				currentPan.updatePlayerMenFeng(playerId, newPlayerMenFeng);
			}
		}
	}

	public String getZhuangPlayerId() {
		return zhuangPlayerId;
	}

	public void setZhuangPlayerId(String zhuangPlayerId) {
		this.zhuangPlayerId = zhuangPlayerId;
	}

	public int getLianZhuangCount() {
		return lianZhuangCount;
	}

	public void setLianZhuangCount(int lianZhuangCount) {
		this.lianZhuangCount = lianZhuangCount;
	}

}
