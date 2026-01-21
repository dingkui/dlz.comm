package com.dlz.test.tools;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 删除指定目录下的所有空文件夹
 *
 * @author zdw
 *
 */
public class EmputyFolderFinder {
	List<File> list = new ArrayList<File>();

	// 得到某一目录下的所有文件夹
	public List<File> visitAll(File root) {
		File[] dirs = root.listFiles();
		if (dirs != null) {
			for (int i = 0; i < dirs.length; i++) {
				if (dirs[i].isDirectory()) {
					list.add(dirs[i]);
				}
				visitAll(dirs[i]);
			}
		}
		return list;
	}

	/**
	 * 删除空的文件夹
	 * 
	 * @param list
	 */
	public void removeNullFile(List<File> list) {
		for (int i = 0; i < list.size(); i++) {
			File temp = list.get(i);
			// 是目录且为空
			if (temp.isDirectory() && temp.listFiles().length <= 0) {
				System.out.println(temp.getAbsolutePath());
				temp.delete();
			}
		}
	}

	/**
	 * @param args
	 */
	@Test
	public void find() {
		EmputyFolderFinder m = new EmputyFolderFinder();
		List<File> list = m.visitAll(new File("D:\\java\\svns\\erp\\erpgls"));
		System.out.println(list.size());
		m.removeNullFile(list);
		System.out.println("ok");
	}
}