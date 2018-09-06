package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.shoupai.GuipaiDangPai;
import com.dml.majiang.player.shoupai.PaiXing;
import com.dml.majiang.player.shoupai.ShoupaiCalculator;
import com.dml.majiang.player.shoupai.ShoupaiGangziZu;
import com.dml.majiang.player.shoupai.ShoupaiKeziZu;
import com.dml.majiang.player.shoupai.ShoupaiPaiXing;
import com.dml.majiang.player.shoupai.ShoupaiWithGuipaiDangGouXingZu;
import com.dml.majiang.player.shoupai.gouxing.GouXing;
import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

public class WenzhouMajiangJiesuanCalculator {

	// 自摸胡
	public static WenzhouMajiangHu calculateBestZimoHu(boolean couldTianhu, GouXingPanHu gouXingPanHu,
			MajiangPlayer player, MajiangMoAction moAction, boolean shaozhongfa) {
		ShoupaiCalculator shoupaiCalculator = player.getShoupaiCalculator();
		List<MajiangPai> guipaiList = player.findGuipaiList();// TODO 也可以用统计器做
		Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		MajiangPai[] paiTypesForBaibanAct = calculatePaiTypesForBaibanAct(guipaiTypeSet);// 白板可以扮演的牌类
		int baibanCount = shoupaiCalculator.count(MajiangPai.baiban);
		// 移除白板,把白板当的牌加入计算器
		for (int i = 0; i < baibanCount; i++) {
			shoupaiCalculator.removePai(MajiangPai.baiban);
			shoupaiCalculator.addPai(paiTypesForBaibanAct[0]);
		}
		if (!player.gangmoGuipai()) {
			shoupaiCalculator.addPai(player.getGangmoShoupai());
		}
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = calculateZimoHuPaiShoupaiPaiXingList(guipaiList, shaozhongfa,
				shoupaiCalculator, player, gouXingPanHu, player.getGangmoShoupai());
		// 把白板当的牌移出计算器,加入白板
		for (int i = 0; i < baibanCount; i++) {
			shoupaiCalculator.removePai(paiTypesForBaibanAct[0]);
			shoupaiCalculator.addPai(MajiangPai.baiban);
		}
		if (!player.gangmoGuipai()) {
			shoupaiCalculator.removePai(player.getGangmoShoupai());
		}

		if (!huPaiShoupaiPaiXingList.isEmpty()) {// 有胡牌型
			// 要选出分数最高的牌型
			// 先计算和手牌型无关的参数
			ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu = new ShoupaixingWuguanJiesuancanshu(player);
			ShoupaiPaiXing bestHuShoupaiPaiXing = null;
			for (ShoupaiPaiXing shoupaiPaiXing : huPaiShoupaiPaiXingList) {

			}
			return new WenzhouMajiangHu();
		} else {// 不成胡
			return null;
		}
	}

	// 抢杠胡
	public static WenzhouMajiangHu calculateBestQianggangHu(MajiangPai gangPai, GouXingPanHu gouXingPanHu,
			MajiangPlayer player, boolean shaozhongfa) {
		ShoupaiCalculator shoupaiCalculator = player.getShoupaiCalculator();
		List<MajiangPai> guipaiList = player.findGuipaiList();// TODO 也可以用统计器做
		Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		MajiangPai[] paiTypesForBaibanAct = calculatePaiTypesForBaibanAct(guipaiTypeSet);// 白板可以扮演的牌类
		int baibanCount = shoupaiCalculator.count(MajiangPai.baiban);
		// 移除白板,把白板当的牌加入计算器
		for (int i = 0; i < baibanCount; i++) {
			shoupaiCalculator.removePai(MajiangPai.baiban);
			shoupaiCalculator.addPai(paiTypesForBaibanAct[0]);
		}
		shoupaiCalculator.addPai(gangPai);
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = calculateZimoHuPaiShoupaiPaiXingList(guipaiList, shaozhongfa,
				shoupaiCalculator, player, gouXingPanHu, player.getGangmoShoupai());
		// 把白板当的牌移出计算器,加入白板
		for (int i = 0; i < baibanCount; i++) {
			shoupaiCalculator.removePai(paiTypesForBaibanAct[0]);
			shoupaiCalculator.addPai(MajiangPai.baiban);
		}
		shoupaiCalculator.removePai(gangPai);
		if (!huPaiShoupaiPaiXingList.isEmpty()) {// 有胡牌型

			// 要选出分数最高的牌型
			// 先计算和手牌型无关的参数
			ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu = new ShoupaixingWuguanJiesuancanshu(player);
			ShoupaiPaiXing bestHuShoupaiPaiXing = null;
			for (ShoupaiPaiXing shoupaiPaiXing : huPaiShoupaiPaiXingList) {

			}
			return new WenzhouMajiangHu();
		} else {// 不成胡
			return null;
		}
	}

	// 点炮胡
	public static WenzhouMajiangHu calculateBestDianpaoHu(boolean couldDihu, GouXingPanHu gouXingPanHu,
			MajiangPlayer player, boolean shaozhongfa, MajiangPai hupai) {
		ShoupaiCalculator shoupaiCalculator = player.getShoupaiCalculator();
		List<MajiangPai> guipaiList = player.findGuipaiList();// TODO 也可以用统计器做
		Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
		MajiangPai[] paiTypesForBaibanAct = calculatePaiTypesForBaibanAct(guipaiTypeSet);// 白板可以扮演的牌类
		int baibanCount = shoupaiCalculator.count(MajiangPai.baiban);
		// 移除白板,把白板当的牌加入计算器
		for (int i = 0; i < baibanCount; i++) {
			shoupaiCalculator.removePai(MajiangPai.baiban);
			shoupaiCalculator.addPai(paiTypesForBaibanAct[0]);
		}
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = calculateZimoHuPaiShoupaiPaiXingList(guipaiList, shaozhongfa,
				shoupaiCalculator, player, gouXingPanHu, hupai);
		// 把白板当的牌移出计算器,加入白板
		for (int i = 0; i < baibanCount; i++) {
			shoupaiCalculator.removePai(paiTypesForBaibanAct[0]);
			shoupaiCalculator.addPai(MajiangPai.baiban);
		}
		if (!huPaiShoupaiPaiXingList.isEmpty()) {// 有胡牌型

			// 要选出分数最高的牌型
			// 先计算和手牌型无关的参数
			ShoupaixingWuguanJiesuancanshu shoupaixingWuguanJiesuancanshu = new ShoupaixingWuguanJiesuancanshu(player);
			ShoupaiPaiXing bestHuShoupaiPaiXing = null;
			for (ShoupaiPaiXing shoupaiPaiXing : huPaiShoupaiPaiXingList) {

			}
			return new WenzhouMajiangHu();
		} else {// 不成胡
			return null;
		}
	}

	// 其实点炮,抢杠胡,也包含自摸的意思，也调用这个
	private static List<ShoupaiPaiXing> calculateZimoHuPaiShoupaiPaiXingList(List<MajiangPai> guipaiList,
			boolean shaozhongfa, ShoupaiCalculator shoupaiCalculator, MajiangPlayer player, GouXingPanHu gouXingPanHu,
			MajiangPai huPai) {
		if (!guipaiList.isEmpty()) {// 有财神
			return calculateHuPaiShoupaiPaiXingListWithCaishen(guipaiList, shaozhongfa, shoupaiCalculator, player,
					gouXingPanHu, huPai);
		} else {// 没财神
			return calculateHuPaiShoupaiPaiXingListWithoutCaishen(shoupaiCalculator, player, gouXingPanHu, huPai);
		}
	}

	private static List<ShoupaiPaiXing> calculateHuPaiShoupaiPaiXingListWithoutCaishen(
			ShoupaiCalculator shoupaiCalculator, MajiangPlayer player, GouXingPanHu gouXingPanHu, MajiangPai huPai) {
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = new ArrayList<>();
		// 计算构型
		List<GouXing> gouXingList = shoupaiCalculator.calculateAllGouXing();
		int chichuShunziCount = player.countChichupaiZu();
		int pengchuKeziCount = player.countPengchupaiZu();
		int gangchuGangziCount = player.countGangchupaiZu();
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
		return huPaiShoupaiPaiXingList;
	}

	private static List<ShoupaiPaiXing> calculateHuPaiShoupaiPaiXingListWithCaishen(List<MajiangPai> guipaiList,
			boolean shaozhongfa, ShoupaiCalculator shoupaiCalculator, MajiangPlayer player, GouXingPanHu gouXingPanHu,
			MajiangPai huPai) {
		int chichuShunziCount = player.countChichupaiZu();
		int pengchuKeziCount = player.countPengchupaiZu();
		int gangchuGangziCount = player.countGangchupaiZu();
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = new ArrayList<>();
		MajiangPai[] paiTypesForGuipaiAct = calculatePaiTypesForGuipaiAct(shaozhongfa);// 鬼牌可以扮演的牌类
		// 开始循环财神各种当法，算构型
		List<ShoupaiWithGuipaiDangGouXingZu> shoupaiWithGuipaiDangGouXingZuList = calculateShoupaiWithGuipaiDangGouXingZuList(
				guipaiList, paiTypesForGuipaiAct, shoupaiCalculator);
		// 对于可胡的构型，计算出所有牌型
		for (ShoupaiWithGuipaiDangGouXingZu shoupaiWithGuipaiDangGouXingZu : shoupaiWithGuipaiDangGouXingZuList) {
			GuipaiDangPai[] guipaiDangPaiArray = shoupaiWithGuipaiDangGouXingZu.getGuipaiDangPaiArray();
			List<GouXing> gouXingList = shoupaiWithGuipaiDangGouXingZu.getGouXingList();
			for (GouXing gouXing : gouXingList) {
				boolean hu = gouXingPanHu.panHu(gouXing.getGouXingCode(), chichuShunziCount, pengchuKeziCount,
						gangchuGangziCount);
				if (hu) {
					// 先把所有当的鬼牌加入计算器
					for (int i = 0; i < guipaiDangPaiArray.length; i++) {
						shoupaiCalculator.addPai(guipaiDangPaiArray[i].getDangpai());
					}
					// 计算牌型
					huPaiShoupaiPaiXingList.addAll(calculateAllShoupaiPaiXingForGouXingWithHupai(gouXing,
							shoupaiCalculator, guipaiDangPaiArray, huPai));
					// 再把所有当的鬼牌移出计算器
					for (int i = 0; i < guipaiDangPaiArray.length; i++) {
						shoupaiCalculator.removePai(guipaiDangPaiArray[i].getDangpai());
					}
				}

			}
		}
		return huPaiShoupaiPaiXingList;
	}

	private static List<ShoupaiPaiXing> calculateAllShoupaiPaiXingForGouXingWithHupai(GouXing gouXing,
			ShoupaiCalculator shoupaiCalculator, GuipaiDangPai[] guipaiDangPaiArray, MajiangPai huPai) {
		boolean sancaishen = (guipaiDangPaiArray.length == 3);
		List<ShoupaiPaiXing> huPaiShoupaiPaiXingList = new ArrayList<>();
		// 计算牌型
		List<PaiXing> paiXingList = shoupaiCalculator.calculateAllPaiXingFromGouXing(gouXing);
		for (PaiXing paiXing : paiXingList) {
			List<ShoupaiPaiXing> shoupaiPaiXingList = paiXing.generateShoupaiPaiXingByGuipaiDangPai(guipaiDangPaiArray);
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

	private static List<ShoupaiWithGuipaiDangGouXingZu> calculateShoupaiWithGuipaiDangGouXingZuList(
			List<MajiangPai> guipaiList, MajiangPai[] paiTypesForGuipaiAct, ShoupaiCalculator shoupaiCalculator) {
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
				// 计算构型
				List<GouXing> gouXingList = shoupaiCalculator.calculateAllGouXing();
				// 再把所有当的鬼牌移出计算器
				for (int i = 0; i < guipaiDangPaiArray.length; i++) {
					shoupaiCalculator.removePai(guipaiDangPaiArray[i].getDangpai());
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
