package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.da.MajiangPlayerDaActionProcessor;

public class WenzhouMajiangDaActionProcessor implements MajiangPlayerDaActionProcessor {

	@Override
	public void process(MajiangDaAction action, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		currentPan.playerDaChuPai(action.getActionPlayerId(), action.getPai());

		// MajiangPlayer player = currentPan.findPlayerById(action.getActionPlayerId());
		// List<MajiangPai> fangruShoupaiList = player.getFangruShoupaiList();
		// Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		// MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
		// guipaiTypeSet.toArray(guipaiTypes);
		// if (fangruShoupaiList.size() == 0) {
		// // 胡
		// WenzhouMajiangPanResultBuilder wenzhouMajiangJuResultBuilder =
		// (WenzhouMajiangPanResultBuilder) ju
		// .getCurrentPanResultBuilder();
		// boolean teshushuangfan = wenzhouMajiangJuResultBuilder.isTeshushuangfan();
		// boolean shaozhongfa = wenzhouMajiangJuResultBuilder.isShaozhongfa();
		// boolean lazila = wenzhouMajiangJuResultBuilder.isLazila();
		// WenzhouMajiangHu bestHu = new WenzhouMajiangHu();
		// WenzhouMajiangPanPlayerHufan hufan = new WenzhouMajiangPanPlayerHufan();
		// WenzhouMajiangPanPlayerHuxing huxing = new WenzhouMajiangPanPlayerHuxing();
		// huxing.setDanzhangdiao(true);
		// huxing.setQuanqiushen(true);
		// huxing.setZimohu(true);
		// hufan.setHuxing(huxing);
		// hufan.calculate(teshushuangfan, lazila);
		// bestHu.setHuxingHu(true);
		// bestHu.setZimo(true);
		// bestHu.setHufan(hufan);
		// ShoupaiPaiXing shoupaiPaiXing = new ShoupaiPaiXing();
		// List<ShoupaiDuiziZu> duiziList = new ArrayList<>();
		// ShoupaiDuiziZu duiziZu = new ShoupaiDuiziZu();
		// duiziZu.setDuiziType(action.getPai());
		// MajiangPai guipaiType = guipaiTypes[0];
		// GuipaiDangPai pai1 = new GuipaiDangPai();
		// pai1.setGuipai(guipaiType);
		// pai1.setDangpai(action.getPai());
		// duiziZu.setPai1(pai1);
		// duiziZu.fillAllBlankPaiWithBenPai();
		// duiziList.add(duiziZu);
		// shoupaiPaiXing.setDuiziList(duiziList);
		// List<ShoupaiDanpai> danpaiList = new ArrayList<>();
		// shoupaiPaiXing.setDanpaiList(danpaiList);
		// List<ShoupaiKeziZu> keziList = new ArrayList<>();
		// shoupaiPaiXing.setKeziList(keziList);
		// List<ShoupaiGangziZu> gangziList = new ArrayList<>();
		// shoupaiPaiXing.setGangziList(gangziList);
		// List<ShoupaiShunziZu> shunziList = new ArrayList<>();
		// shoupaiPaiXing.setShunziList(shunziList);
		// bestHu.setShoupaiPaiXing(shoupaiPaiXing);
		//
		// MajiangPlayer xiajiaPlayer = currentPan.findXiajia(player);
		// boolean couldHu = true;
		// while (true) {
		// if (!xiajiaPlayer.getId().equals(action.getActionPlayerId())) {
		// // 点炮胡
		// GouXingPanHu gouXingPanHu = ju.getGouXingPanHu();
		// // 先把这张牌放入计算器
		// xiajiaPlayer.getShoupaiCalculator().addPai(action.getPai());
		// WenzhouMajiangHu hu =
		// WenzhouMajiangJiesuanCalculator.calculateBestDianpaoHu(false, gouXingPanHu,
		// xiajiaPlayer, action.getPai(), shaozhongfa, teshushuangfan, lazila);
		// // 再把这张牌拿出计算器
		// xiajiaPlayer.getShoupaiCalculator().removePai(action.getPai());
		// if (hu != null) {
		// hu.setZimo(false);
		// hu.setDianpaoPlayerId(player.getId());
		// if (bestHu.getHufan().getValue() < hu.getHufan().getValue()) {
		// couldHu = false;
		// }
		// }
		// } else {
		// break;
		// }
		// xiajiaPlayer = currentPan.findXiajia(xiajiaPlayer);
		// xiajiaPlayer.clearActionCandidates();
		// }
		// if (couldHu) {
		// player.setHu(bestHu);
		// }
		// }
	}

}
