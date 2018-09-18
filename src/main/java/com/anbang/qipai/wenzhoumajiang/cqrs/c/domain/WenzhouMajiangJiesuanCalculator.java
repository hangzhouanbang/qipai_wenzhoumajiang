package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.mo.GanghouBupai;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.shoupai.BaibanDangPai;
import com.dml.majiang.player.shoupai.GuipaiDangPai;
import com.dml.majiang.player.shoupai.PaiXing;
import com.dml.majiang.player.shoupai.ShoupaiCalculator;
import com.dml.majiang.player.shoupai.ShoupaiDuiziZu;
import com.dml.majiang.player.shoupai.ShoupaiGangziZu;
import com.dml.majiang.player.shoupai.ShoupaiJiesuanPai;
import com.dml.majiang.player.shoupai.ShoupaiKeziZu;
import com.dml.majiang.player.shoupai.ShoupaiPaiXing;
import com.dml.majiang.player.shoupai.ShoupaiShunziZu;
import com.dml.majiang.player.shoupai.ShoupaiWithGuipaiDangGouXingZu;
import com.dml.majiang.player.shoupai.gouxing.GouXing;
import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

public class WenzhouMajiangJiesuanCalculator {

	// 自摸胡
	public static WenzhouMajiangHu calculateBestZimoHu(boolean couldTianhu, GouXingPanHu gouXingPanHu,
			MajiangPlayer player, MajiangMoAction moAction, boolean shaozhongfa, boolean teshushuangfan,
			boolean lazila) {
		ShoupaiCalculator shoupaiCalculator = player.getShoupaiCalculator();
		List<MajiangPai> guipaiList = player.findGuipaiList();// TODO 也可以用统计器做

		if (!player.gangmoGuipai()) {
			shoupaiCalculator.addPai(player.getGangmoShoupai());
		}
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = calculateZimoHuPaiShoupaiPaiXingList(guipaiList, shaozhongfa,
				shoupaiCalculator, player, gouXingPanHu, player.getGangmoShoupai());
		if (!player.gangmoGuipai()) {
			shoupaiCalculator.removePai(player.getGangmoShoupai());
		}

		if (!huPaiShoupaiPaiXingList.isEmpty()) {// 有胡牌型
			// 要选出分数最高的牌型
			// 先计算和手牌型无关的参数
			ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu = new ShoupaixingWuguanJiesuancanshu(player);
			WenzhouMajiangPanPlayerHufan bestHufan = null;
			ShoupaiPaiXing bestHuShoupaiPaiXing = null;
			for (ShoupaiPaiXing shoupaiPaiXing : huPaiShoupaiPaiXingList) {
				WenzhouMajiangPanPlayerHufan hufan = calculateHufanForShoupaiPaiXing(couldTianhu, false,
						shoupaixingWuguanJiesuancanshu, shoupaiPaiXing, true,
						moAction.getReason().getName().equals(GanghouBupai.name), true, false, teshushuangfan, lazila);
				if (bestHufan == null || bestHufan.getValue() < hufan.getValue()) {
					bestHufan = hufan;
					bestHuShoupaiPaiXing = shoupaiPaiXing;
				}
			}
			return new WenzhouMajiangHu(bestHuShoupaiPaiXing, bestHufan);
		} else {// 不成胡
			return null;
		}
	}

	// 抢杠胡
	public static WenzhouMajiangHu calculateBestQianggangHu(MajiangPai gangPai, GouXingPanHu gouXingPanHu,
			MajiangPlayer player, boolean shaozhongfa, boolean teshushuangfan, boolean lazila) {
		ShoupaiCalculator shoupaiCalculator = player.getShoupaiCalculator();
		List<MajiangPai> guipaiList = player.findGuipaiList();// TODO 也可以用统计器做

		shoupaiCalculator.addPai(gangPai);
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = calculateZimoHuPaiShoupaiPaiXingList(guipaiList, shaozhongfa,
				shoupaiCalculator, player, gouXingPanHu, gangPai);

		shoupaiCalculator.removePai(gangPai);
		if (!huPaiShoupaiPaiXingList.isEmpty()) {// 有胡牌型

			// 要选出分数最高的牌型
			// 先计算和手牌型无关的参数
			ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu = new ShoupaixingWuguanJiesuancanshu(player);
			WenzhouMajiangPanPlayerHufan bestHufan = null;
			ShoupaiPaiXing bestHuShoupaiPaiXing = null;
			for (ShoupaiPaiXing shoupaiPaiXing : huPaiShoupaiPaiXingList) {
				WenzhouMajiangPanPlayerHufan hufan = calculateHufanForShoupaiPaiXing(false, false,
						shoupaixingWuguanJiesuancanshu, shoupaiPaiXing, true, false, false, true, teshushuangfan,
						lazila);
				if (bestHufan == null || bestHufan.getValue() < hufan.getValue()) {
					bestHufan = hufan;
					bestHuShoupaiPaiXing = shoupaiPaiXing;
				}
			}
			return new WenzhouMajiangHu(bestHuShoupaiPaiXing, bestHufan);
		} else {// 不成胡
			return null;
		}
	}

	// 点炮胡
	public static WenzhouMajiangHu calculateBestDianpaoHu(boolean couldDihu, GouXingPanHu gouXingPanHu,
			MajiangPlayer player, MajiangPai hupai, boolean shaozhongfa, boolean teshushuangfan, boolean lazila) {
		ShoupaiCalculator shoupaiCalculator = player.getShoupaiCalculator();
		List<MajiangPai> guipaiList = player.findGuipaiList();// TODO 也可以用统计器做

		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = calculateZimoHuPaiShoupaiPaiXingList(guipaiList, shaozhongfa,
				shoupaiCalculator, player, gouXingPanHu, hupai);

		if (!huPaiShoupaiPaiXingList.isEmpty()) {// 有胡牌型

			// 要选出分数最高的牌型
			// 先计算和手牌型无关的参数
			ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu = new ShoupaixingWuguanJiesuancanshu(player);
			WenzhouMajiangPanPlayerHufan bestHufan = null;
			ShoupaiPaiXing bestHuShoupaiPaiXing = null;
			for (ShoupaiPaiXing shoupaiPaiXing : huPaiShoupaiPaiXingList) {
				WenzhouMajiangPanPlayerHufan hufan = calculateHufanForShoupaiPaiXing(false, false,
						shoupaixingWuguanJiesuancanshu, shoupaiPaiXing, true, false, false, false, teshushuangfan,
						lazila);
				if (bestHufan == null || bestHufan.getValue() < hufan.getValue()) {
					bestHufan = hufan;
					bestHuShoupaiPaiXing = shoupaiPaiXing;
				}
			}
			return new WenzhouMajiangHu(bestHuShoupaiPaiXing, bestHufan);
		} else {// 不成胡
			return null;
		}
	}

	private static WenzhouMajiangPanPlayerHufan calculateHufanForShoupaiPaiXing(boolean couldTianhu, boolean couldDihu,
			ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu, ShoupaiPaiXing shoupaiPaiXing, boolean hu,
			boolean gangkaiHu, boolean zimoHu, boolean qianggangHu, boolean teshushuangfan, boolean lazila) {
		WenzhouMajiangPanPlayerHufan hufan = new WenzhouMajiangPanPlayerHufan();
		WenzhouMajiangPanPlayerHuxing huxing = calculateHuxingForShoupaiPaiXing(couldTianhu, couldDihu,
				shoupaixingWuguanJiesuancanshu, shoupaiPaiXing, teshushuangfan, gangkaiHu, zimoHu, qianggangHu);
		MajiangPai guipaiType = shoupaixingWuguanJiesuancanshu.getGuipaiType();// 鬼牌
		/* 中发白：手上包含中、發和白三者的对子或刻子且符合胡牌基本牌型。（若财神为中發白时，手上需包含财神和除财神外另外两者的刻子或杠牌且符合胡牌基本牌型） */
		if (shoupaixingWuguanJiesuancanshu.isGuipaiIsZhongFaBai()) {
			if (guipaiType.equals(MajiangPai.hongzhong) && shoupaixingWuguanJiesuancanshu.getFacaiCount() > 2
					&& shoupaixingWuguanJiesuancanshu.getBaibanCount() > 2) {
				huxing.setZhongfabai(true);
			}
			if (guipaiType.equals(MajiangPai.facai) && shoupaixingWuguanJiesuancanshu.getHongzhongCount() > 2
					&& shoupaixingWuguanJiesuancanshu.getBaibanCount() > 2) {
				huxing.setZhongfabai(true);
			}
			if (guipaiType.equals(MajiangPai.baiban) && shoupaixingWuguanJiesuancanshu.getFacaiCount() > 2
					&& shoupaixingWuguanJiesuancanshu.getHongzhongCount() > 2) {
				huxing.setZhongfabai(true);
			}
		} else {
			if (shoupaixingWuguanJiesuancanshu.getHongzhongCount() > 1
					&& shoupaixingWuguanJiesuancanshu.getFacaiCount() > 1
					&& shoupaixingWuguanJiesuancanshu.getBaibanCount() > 1) {
				huxing.setZhongfabai(true);
			}
		}
		// 软牌、硬牌判断
		boolean ruan = false;
		// 财神当牌的次数
		int caishenDangPai = 0;
		// 对子组中的财神对子数
		int caishenDuiziZu = 0;
		List<ShoupaiDuiziZu> duiziList = shoupaiPaiXing.getDuiziList();
		for (ShoupaiDuiziZu duiziZu : duiziList) {
			ShoupaiJiesuanPai pai1 = duiziZu.getPai1();
			ShoupaiJiesuanPai pai2 = duiziZu.getPai2();
			if (duiziZu.getDuiziType().equals(guipaiType) && duiziZu.yuanPaiFenZu()) {
				caishenDuiziZu += 1;
			}
			if (pai1.getYuanPaiType().equals(guipaiType) && !pai1.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
			if (pai2.getYuanPaiType().equals(guipaiType) && !pai2.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
		}
		List<ShoupaiKeziZu> keziList = shoupaiPaiXing.getKeziList();
		for (ShoupaiKeziZu keziZu : keziList) {
			ShoupaiJiesuanPai pai1 = keziZu.getPai1();
			ShoupaiJiesuanPai pai2 = keziZu.getPai2();
			ShoupaiJiesuanPai pai3 = keziZu.getPai3();
			if (pai1.getYuanPaiType().equals(guipaiType) && !pai1.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
			if (pai2.getYuanPaiType().equals(guipaiType) && !pai2.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
			if (pai3.getYuanPaiType().equals(guipaiType) && !pai3.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
		}
		List<ShoupaiShunziZu> shunziList = shoupaiPaiXing.getShunziList();
		for (ShoupaiShunziZu shunziZu : shunziList) {
			ShoupaiJiesuanPai pai1 = shunziZu.getPai1();
			ShoupaiJiesuanPai pai2 = shunziZu.getPai2();
			ShoupaiJiesuanPai pai3 = shunziZu.getPai3();
			if (pai1.getYuanPaiType().equals(guipaiType) && !pai1.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
			if (pai2.getYuanPaiType().equals(guipaiType) && !pai2.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
			if (pai3.getYuanPaiType().equals(guipaiType) && !pai3.dangBenPai()) {
				caishenDangPai += 1;
				ruan = true;
			}
		}
		if (zimoHu || qianggangHu) {
			ruan = false;
		}
		hufan.setRuan(ruan);
		if ((shoupaixingWuguanJiesuancanshu.getCaishenShu() == 2 && caishenDangPai == 0)
				|| (shoupaixingWuguanJiesuancanshu.getCaishenShu() == 3 && caishenDangPai == 1)) {
			huxing.setShuangcaiguiwei(true);// 双财神归位
		}
		if (shoupaixingWuguanJiesuancanshu.getCaishenShu() == 3 && caishenDangPai == 0) {
			huxing.setSancaiguiwei(true);// 三财神归位（辣子辣）
		}
		if (caishenDuiziZu == 1) {
			huxing.setCaishenniudui(true);// 财神牛对
		}
		hufan.setHuxing(huxing);
		hufan.calculate(teshushuangfan, lazila);
		return hufan;
	}

	private static WenzhouMajiangPanPlayerHuxing calculateHuxingForShoupaiPaiXing(boolean couldTianhu,
			boolean couldDihu, ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu,
			ShoupaiPaiXing shoupaiPaiXing, boolean hu, boolean gangkaiHu, boolean zimoHu, boolean qianggangHu) {
		WenzhouMajiangPanPlayerHuxing huxing = new WenzhouMajiangPanPlayerHuxing();
		huxing.setSancaishen(shoupaixingWuguanJiesuancanshu.getCaishenShu() == 3);
		huxing.setTianhu(couldTianhu);// 天胡
		huxing.setDihu(couldDihu);// 地胡
		huxing.setHu(hu);// 胡
		huxing.setGangkai(gangkaiHu);// 杠上开花
		huxing.setZimohu(zimoHu);// 自摸胡
		huxing.setQiangganghu(qianggangHu);// 抢杠胡
		huxing.setQingyise(shoupaixingWuguanJiesuancanshu.isQingyise());// 清一色
		huxing.setHunyise(shoupaixingWuguanJiesuancanshu.isHunyise());// 混一色
		huxing.setBadui(shoupaiPaiXing.getDuiziList().size() == 8);// 八对
		if (shoupaiPaiXing.getDanpaiList().size() == 1) {
			huxing.setDanzhangdiao(true);// 单张吊
			if (shoupaixingWuguanJiesuancanshu.getCaishenShu() == 1) {
				huxing.setQuanqiushen(true);// 全求神
			}
		}
		if (shoupaiPaiXing.getDuiziList().size() == 1 && shoupaixingWuguanJiesuancanshu.getChichupaiZuCount() == 0
				&& shoupaiPaiXing.countShunzi() == 0) {// 碰碰胡
			huxing.setPengpenghu(true);
		}
		return huxing;
	}

	// 其实点炮,抢杠胡,也包含自摸的意思，也调用这个
	private static List<ShoupaiPaiXing> calculateZimoHuPaiShoupaiPaiXingList(List<MajiangPai> guipaiList,
			boolean shaozhongfa, ShoupaiCalculator shoupaiCalculator, MajiangPlayer player, GouXingPanHu gouXingPanHu,
			MajiangPai huPai) {
		Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		MajiangPai[] paiTypesForBaibanAct = calculatePaiTypesForBaibanAct(guipaiTypeSet);// 白板可以扮演的牌类
		int baibanCount = shoupaiCalculator.count(MajiangPai.baiban);
		BaibanDangPai[] baibanDangPaiArray = new BaibanDangPai[baibanCount];
		if (baibanCount > 0) {
			// 移除白板
			for (int i = 0; i < baibanCount; i++) {
				shoupaiCalculator.removePai(MajiangPai.baiban);
				baibanDangPaiArray[i] = new BaibanDangPai(paiTypesForBaibanAct[0]);
			}
		}

		if (!guipaiList.isEmpty()) {// 有财神
			List<ShoupaiPaiXing> shoupaiPaiXingList = calculateHuPaiShoupaiPaiXingListWithCaishen(baibanDangPaiArray,
					guipaiList, shaozhongfa, shoupaiCalculator, player, gouXingPanHu, huPai);
			if (baibanCount > 0) {
				// 加入白板
				for (int i = 0; i < baibanCount; i++) {
					shoupaiCalculator.addPai(MajiangPai.baiban);
				}
			}
			return shoupaiPaiXingList;
		} else {// 没财神
			List<ShoupaiPaiXing> shoupaiPaiXingList = calculateHuPaiShoupaiPaiXingListWithoutCaishen(baibanDangPaiArray,
					shoupaiCalculator, player, gouXingPanHu, huPai);
			if (baibanCount > 0) {
				// 加入白板
				for (int i = 0; i < baibanCount; i++) {
					shoupaiCalculator.addPai(MajiangPai.baiban);
				}
			}
			return shoupaiPaiXingList;
		}
	}

	private static List<ShoupaiPaiXing> calculateHuPaiShoupaiPaiXingListWithoutCaishen(
			BaibanDangPai[] baibanDangPaiArray, ShoupaiCalculator shoupaiCalculator, MajiangPlayer player,
			GouXingPanHu gouXingPanHu, MajiangPai huPai) {
		int chichuShunziCount = player.countChichupaiZu();
		int pengchuKeziCount = player.countPengchupaiZu();
		int gangchuGangziCount = player.countGangchupaiZu();
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = new ArrayList<>();
		if (baibanDangPaiArray.length > 0) {
			List<ShoupaiWithGuipaiDangGouXingZu> shoupaiWithGuipaiDangGouXingZuList = calculateShoupaiWithBaibanDangGouXingZuList(
					baibanDangPaiArray, shoupaiCalculator);
			// 对于可胡的构型，计算出所有牌型
			for (ShoupaiWithGuipaiDangGouXingZu shoupaiWithGuipaiDangGouXingZu : shoupaiWithGuipaiDangGouXingZuList) {
				GuipaiDangPai[] guipaiDangPaiArray = shoupaiWithGuipaiDangGouXingZu.getGuipaiDangPaiArray();
				ShoupaiJiesuanPai[] dangPaiArray = new ShoupaiJiesuanPai[baibanDangPaiArray.length];
				System.arraycopy(baibanDangPaiArray, 0, dangPaiArray, 0, baibanDangPaiArray.length);
				List<GouXing> gouXingList = shoupaiWithGuipaiDangGouXingZu.getGouXingList();
				for (GouXing gouXing : gouXingList) {
					boolean hu = gouXingPanHu.panHu(gouXing.getGouXingCode(), chichuShunziCount, pengchuKeziCount,
							gangchuGangziCount);
					if (hu) {
						if (baibanDangPaiArray.length > 0) {
							// 先把所有当的白板加入计算器
							for (int i = 0; i < baibanDangPaiArray.length; i++) {
								shoupaiCalculator.addPai(baibanDangPaiArray[i].getDangpai());
							}
						}
						// 计算牌型
						huPaiShoupaiPaiXingList.addAll(calculateAllShoupaiPaiXingForGouXingWithHupai(gouXing,
								shoupaiCalculator, dangPaiArray, guipaiDangPaiArray, huPai));
						if (baibanDangPaiArray.length > 0) {
							// 再把所有当的白板移出计算器
							for (int i = 0; i < baibanDangPaiArray.length; i++) {
								shoupaiCalculator.removePai(baibanDangPaiArray[i].getDangpai());
							}
						}
					}
				}
			}
		} else {
			// 计算构型
			List<GouXing> gouXingList = shoupaiCalculator.calculateAllGouXing();
			for (GouXing gouXing : gouXingList) {
				boolean hu = gouXingPanHu.panHu(gouXing.getGouXingCode(), chichuShunziCount, pengchuKeziCount,
						gangchuGangziCount);
				if (hu) {
					// 计算牌型
					List<PaiXing> paiXingList = shoupaiCalculator.calculateAllPaiXingFromGouXing(gouXing);
					for (PaiXing paiXing : paiXingList) {
						ShoupaiPaiXing shoupaiPaiXing = paiXing.generateAllBenPaiShoupaiPaiXing();
						// 对ShoupaiPaiXing还要变换最后弄进的牌
						List<ShoupaiPaiXing> shoupaiPaiXingListWithDifftentLastActionPaiInZu = shoupaiPaiXing
								.differentiateShoupaiPaiXingByLastActionPai(huPai);
						huPaiShoupaiPaiXingList.addAll(shoupaiPaiXingListWithDifftentLastActionPaiInZu);
					}
				}
			}
		}
		return huPaiShoupaiPaiXingList;
	}

	private static List<ShoupaiPaiXing> calculateHuPaiShoupaiPaiXingListWithCaishen(BaibanDangPai[] baibanDangPaiArray,
			List<MajiangPai> guipaiList, boolean shaozhongfa, ShoupaiCalculator shoupaiCalculator, MajiangPlayer player,
			GouXingPanHu gouXingPanHu, MajiangPai huPai) {
		int chichuShunziCount = player.countChichupaiZu();
		int pengchuKeziCount = player.countPengchupaiZu();
		int gangchuGangziCount = player.countGangchupaiZu();
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = new ArrayList<>();
		MajiangPai[] paiTypesForGuipaiAct = calculatePaiTypesForGuipaiAct(shaozhongfa);// 鬼牌可以扮演的牌类
		// 开始循环财神各种当法，算构型
		List<ShoupaiWithGuipaiDangGouXingZu> shoupaiWithGuipaiDangGouXingZuList = calculateShoupaiWithGuipaiDangGouXingZuList(
				baibanDangPaiArray, guipaiList, paiTypesForGuipaiAct, shoupaiCalculator);
		// 对于可胡的构型，计算出所有牌型
		for (ShoupaiWithGuipaiDangGouXingZu shoupaiWithGuipaiDangGouXingZu : shoupaiWithGuipaiDangGouXingZuList) {
			GuipaiDangPai[] guipaiDangPaiArray = shoupaiWithGuipaiDangGouXingZu.getGuipaiDangPaiArray();
			ShoupaiJiesuanPai[] dangPaiArray = new ShoupaiJiesuanPai[baibanDangPaiArray.length
					+ guipaiDangPaiArray.length];
			System.arraycopy(baibanDangPaiArray, 0, dangPaiArray, 0, baibanDangPaiArray.length);
			System.arraycopy(guipaiDangPaiArray, 0, dangPaiArray, baibanDangPaiArray.length, guipaiDangPaiArray.length);
			List<GouXing> gouXingList = shoupaiWithGuipaiDangGouXingZu.getGouXingList();
			for (GouXing gouXing : gouXingList) {
				boolean hu = gouXingPanHu.panHu(gouXing.getGouXingCode(), chichuShunziCount, pengchuKeziCount,
						gangchuGangziCount);
				if (hu) {
					// 先把所有当的鬼牌加入计算器
					for (int i = 0; i < guipaiDangPaiArray.length; i++) {
						shoupaiCalculator.addPai(guipaiDangPaiArray[i].getDangpai());
					}
					if (baibanDangPaiArray.length > 0) {
						// 先把所有当的白板加入计算器
						for (int i = 0; i < baibanDangPaiArray.length; i++) {
							shoupaiCalculator.addPai(baibanDangPaiArray[i].getDangpai());
						}
					}
					// 计算牌型
					huPaiShoupaiPaiXingList.addAll(calculateAllShoupaiPaiXingForGouXingWithHupai(gouXing,
							shoupaiCalculator, dangPaiArray, guipaiDangPaiArray, huPai));
					// 再把所有当的鬼牌移出计算器
					for (int i = 0; i < guipaiDangPaiArray.length; i++) {
						shoupaiCalculator.removePai(guipaiDangPaiArray[i].getDangpai());
					}
					if (baibanDangPaiArray.length > 0) {
						// 再把所有当的白板移出计算器
						for (int i = 0; i < baibanDangPaiArray.length; i++) {
							shoupaiCalculator.removePai(baibanDangPaiArray[i].getDangpai());
						}
					}
				}

			}
		}
		return huPaiShoupaiPaiXingList;
	}

	private static List<ShoupaiPaiXing> calculateAllShoupaiPaiXingForGouXingWithHupai(GouXing gouXing,
			ShoupaiCalculator shoupaiCalculator, ShoupaiJiesuanPai[] dangPaiArray, GuipaiDangPai[] guipaiDangPaiArray,
			MajiangPai huPai) {
		boolean sancaishen = (guipaiDangPaiArray.length == 3);
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = new ArrayList<>();
		// 计算牌型
		List<PaiXing> paiXingList = shoupaiCalculator.calculateAllPaiXingFromGouXing(gouXing);
		for (PaiXing paiXing : paiXingList) {
			List<ShoupaiPaiXing> shoupaiPaiXingList = paiXing.generateShoupaiPaiXingByDangPai(dangPaiArray);
			// 过滤暗杠或暗刻有两个财神当的
			Iterator<ShoupaiPaiXing> i = shoupaiPaiXingList.iterator();
			while (i.hasNext()) {
				ShoupaiPaiXing shoupaiPaiXing = i.next();
				for (ShoupaiKeziZu shoupaiKeziZu : shoupaiPaiXing.getKeziList()) {
					if (shoupaiKeziZu.countGuipaiDangQitapai() > (sancaishen ? 2 : 1)) {
						i.remove();
						break;
					}
				}
				for (ShoupaiGangziZu shoupaiGangziZu : shoupaiPaiXing.getGangziList()) {
					if (shoupaiGangziZu.countGuipaiDangQitapai() > (sancaishen ? 2 : 1)) {
						i.remove();
						break;
					}
				}
			}

			// 对于每一个ShoupaiPaiXing还要变换最后弄进的牌
			for (ShoupaiPaiXing shoupaiPaiXing : shoupaiPaiXingList) {
				List<ShoupaiPaiXing> shoupaiPaiXingListWithDifftentLastActionPaiInZu = shoupaiPaiXing
						.differentiateShoupaiPaiXingByLastActionPai(huPai);
				huPaiShoupaiPaiXingList.addAll(shoupaiPaiXingListWithDifftentLastActionPaiInZu);
			}

		}
		return huPaiShoupaiPaiXingList;
	}

	private static List<ShoupaiWithGuipaiDangGouXingZu> calculateShoupaiWithBaibanDangGouXingZuList(
			BaibanDangPai[] baibanDangPaiArray, ShoupaiCalculator shoupaiCalculator) {
		List<ShoupaiWithGuipaiDangGouXingZu> shoupaiWithGuipaiDangGouXingZuList = new ArrayList<>();
		GuipaiDangPai[] guipaiDangPaiArray = new GuipaiDangPai[0];
		if (baibanDangPaiArray.length > 0) {
			// 先把所有当的白板加入计算器
			for (int i = 0; i < baibanDangPaiArray.length; i++) {
				shoupaiCalculator.addPai(baibanDangPaiArray[i].getDangpai());
			}
		}
		// 计算构型
		List<GouXing> gouXingList = shoupaiCalculator.calculateAllGouXing();

		if (baibanDangPaiArray.length > 0) {
			// 再把所有当的白板移出计算器
			for (int i = 0; i < baibanDangPaiArray.length; i++) {
				shoupaiCalculator.removePai(baibanDangPaiArray[i].getDangpai());
			}
		}
		ShoupaiWithGuipaiDangGouXingZu shoupaiWithGuipaiDangGouXingZu = new ShoupaiWithGuipaiDangGouXingZu();
		shoupaiWithGuipaiDangGouXingZu.setGouXingList(gouXingList);
		shoupaiWithGuipaiDangGouXingZu.setGuipaiDangPaiArray(guipaiDangPaiArray);
		shoupaiWithGuipaiDangGouXingZuList.add(shoupaiWithGuipaiDangGouXingZu);
		return shoupaiWithGuipaiDangGouXingZuList;
	}

	private static List<ShoupaiWithGuipaiDangGouXingZu> calculateShoupaiWithGuipaiDangGouXingZuList(
			BaibanDangPai[] baibanDangPaiArray, List<MajiangPai> guipaiList, MajiangPai[] paiTypesForGuipaiAct,
			ShoupaiCalculator shoupaiCalculator) {
		List<ShoupaiWithGuipaiDangGouXingZu> shoupaiWithGuipaiDangGouXingZuList = new ArrayList<>();

		int guipaiCount = guipaiList.size();
		int maxZuheCode = (int) Math.pow(paiTypesForGuipaiAct.length, guipaiCount);
		int[] modArray = new int[guipaiCount];
		for (int i = 0; i < guipaiCount; i++) {
			modArray[i] = (int) Math.pow(paiTypesForGuipaiAct.length, guipaiCount - 1 - i);
		}
		for (int zuheCode = 0; zuheCode < maxZuheCode; zuheCode++) {
			GuipaiDangPai[] guipaiDangPaiArray = new GuipaiDangPai[guipaiCount];
			int temp = zuheCode;
			int previousGuipaiDangIdx = 0;
			for (int i = 0; i < guipaiCount; i++) {
				int mod = modArray[i];
				int shang = temp / mod;
				if (shang >= previousGuipaiDangIdx) {
					int yu = temp % mod;
					guipaiDangPaiArray[i] = new GuipaiDangPai(guipaiList.get(i), paiTypesForGuipaiAct[shang]);
					temp = yu;
					previousGuipaiDangIdx = shang;
				} else {
					guipaiDangPaiArray = null;
					break;
				}
			}
			if (guipaiDangPaiArray != null) {
				// 先把所有当的鬼牌加入计算器
				for (int i = 0; i < guipaiDangPaiArray.length; i++) {
					shoupaiCalculator.addPai(guipaiDangPaiArray[i].getDangpai());
				}
				if (baibanDangPaiArray.length > 0) {
					// 先把所有当的白板加入计算器
					for (int i = 0; i < baibanDangPaiArray.length; i++) {
						shoupaiCalculator.addPai(baibanDangPaiArray[i].getDangpai());
					}
				}
				// 计算构型
				List<GouXing> gouXingList = shoupaiCalculator.calculateAllGouXing();
				// 再把所有当的鬼牌移出计算器
				for (int i = 0; i < guipaiDangPaiArray.length; i++) {
					shoupaiCalculator.removePai(guipaiDangPaiArray[i].getDangpai());
				}
				if (baibanDangPaiArray.length > 0) {
					// 再把所有当的白板移出计算器
					for (int i = 0; i < baibanDangPaiArray.length; i++) {
						shoupaiCalculator.removePai(baibanDangPaiArray[i].getDangpai());
					}
				}
				ShoupaiWithGuipaiDangGouXingZu shoupaiWithGuipaiDangGouXingZu = new ShoupaiWithGuipaiDangGouXingZu();
				shoupaiWithGuipaiDangGouXingZu.setGouXingList(gouXingList);
				shoupaiWithGuipaiDangGouXingZu.setGuipaiDangPaiArray(guipaiDangPaiArray);
				shoupaiWithGuipaiDangGouXingZuList.add(shoupaiWithGuipaiDangGouXingZu);
			}
		}
		return shoupaiWithGuipaiDangGouXingZuList;
	}

	private static MajiangPai[] calculatePaiTypesForGuipaiAct(boolean shaozhongfa) {
		MajiangPai[] xushupaiArray = MajiangPai.xushupaiArray();
		MajiangPai[] fengpaiArray = MajiangPai.fengpaiArray();
		MajiangPai[] paiTypesForGuipaiAct;
		if (shaozhongfa) {// 少中发
			paiTypesForGuipaiAct = new MajiangPai[xushupaiArray.length + fengpaiArray.length];
			System.arraycopy(xushupaiArray, 0, paiTypesForGuipaiAct, 0, xushupaiArray.length);
			System.arraycopy(fengpaiArray, 0, paiTypesForGuipaiAct, xushupaiArray.length, fengpaiArray.length);
		} else {
			paiTypesForGuipaiAct = new MajiangPai[xushupaiArray.length + fengpaiArray.length + 2];
			System.arraycopy(xushupaiArray, 0, paiTypesForGuipaiAct, 0, xushupaiArray.length);
			System.arraycopy(fengpaiArray, 0, paiTypesForGuipaiAct, xushupaiArray.length, fengpaiArray.length);
			paiTypesForGuipaiAct[31] = MajiangPai.hongzhong;
			paiTypesForGuipaiAct[32] = MajiangPai.facai;
		}
		return paiTypesForGuipaiAct;
	}

	private static MajiangPai[] calculatePaiTypesForBaibanAct(Set<MajiangPai> guipaiTypeSet) {
		MajiangPai[] paiTypesForBaibanAct = new MajiangPai[guipaiTypeSet.size()];
		guipaiTypeSet.toArray(paiTypesForBaibanAct);
		return paiTypesForBaibanAct;
	}
}
