package com.anbang.qipai.wenzhoumajiang.cqrs.c.service.disruptor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.highto.framework.ddd.Command;
import com.highto.framework.nio.ByteBufferSerializer;

/**
 * Created by tan on 2016/8/30.
 */
public class FileUtil {
	public String getRecentFileName(String fileBasePath, String prefix) {
		File folder = new File(fileBasePath);
		// 获得folder文件夹下面所有文件
		String[] fileNames = folder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(prefix);
			}
		});
		String recentFileName = null;
		long recentCreateTime = 0;
		if (fileNames != null) {
			for (String fileName : fileNames) {
				long createTime = Long.parseLong(fileName.substring(prefix.length()));
				if (recentCreateTime < createTime) {
					recentFileName = fileName;
					recentCreateTime = createTime;
				}
			}
		}
		return recentFileName;
	}

	/**
	 * 注意 若cmd文件很大 。大小超过int最大值时 会有问题
	 *
	 * @param fileBasePath
	 * @param prefix
	 * @throws IOException
	 */
	public List<Command> read(String fileBasePath, String prefix) throws Throwable {
		String fileName = getRecentFileName(fileBasePath, prefix);
		List<Command> commands = new ArrayList<>();
		if (fileName != null) {
			RandomAccessFile file = new RandomAccessFile(fileBasePath + fileName, "r");
			FileChannel channel = file.getChannel();
			long size = channel.size();
			ByteBuffer buffer = ByteBuffer.allocate((int) size);
			channel.read(buffer);

			buffer.flip();
			while (buffer.hasRemaining()) {
				Command command = ByteBufferSerializer.byteBufferToObj(buffer);
				commands.add(command);
			}
		}
		return commands;
	}

}
