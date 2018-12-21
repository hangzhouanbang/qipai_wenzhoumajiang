package com.anbang.qipai.wenzhoumajiang.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.disruptor.CoreSnapshotService;
import com.anbang.qipai.wenzhoumajiang.web.vo.CommonVO;

@RestController
@RequestMapping("/snapshot")
public class SnapshotController {

	@Autowired
	private CoreSnapshotService coreSnapshotService;

	@RequestMapping("/save")
	@ResponseBody
	public CommonVO saveSnapshot() {
		final CommonVO commonVO = new CommonVO();
		commonVO.setSuccess(true);
		try {
			this.coreSnapshotService.makeSnapshot();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			commonVO.setSuccess(false);
		}
		return commonVO;
	}

}
