package com.dlz.test.tools;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * 查找重复的文件
 */
public class DulplicateFiles {
	@Test
	public void getFiles() throws Exception {
		getFileInfo(new File("E:\\资料\\5工作资料\\新建文件夹"));
		Set<Long> keySet=m.keySet();
		int i=0;
		for(Long k:keySet){
			if(m.get(k).size()>1){
				List<String> fs=m.get(k);
				for(String fp:fs){
					String scr=getFileCRCCode(new File(fp));
					if(m2.containsKey(scr)){
						m2.get(scr).add(fp);
					}else{
						List<String> l=new ArrayList<String>();
						l.add(fp);
						m2.put(scr, l);
					}
				}
				i+=fs.size();
				System.out.println(i);
			}
		}
		System.out.println(i);
		
		i=0;
		Set<String> keySet2=m2.keySet();
		for(String k:keySet2){
			if(m2.get(k).size()>1){
				List<String> fs=m2.get(k);
				i+=fs.size();
				System.out.println(fs);
			}
		}
		System.out.println(i);
	}
	
	
	private void getFileInfo(File f) throws Exception{
		if(f.isDirectory()){
			File[] files=f.listFiles();
			for(File fi:files){
				getFileInfo(fi);
			}
		}else{
			long scr=f.length();
			String fp=f.getAbsolutePath();
			if(m.containsKey(scr)){
				m.get(scr).add(fp);
			}else{
				List<String> l=new ArrayList<String>();
				l.add(fp);
				m.put(scr, l);
			}
		}
	}
	
	int i=0;
	private static Map<Long,List<String>> m=new HashMap<Long,List<String>>();
	private static Map<String,List<String>> m2=new HashMap<String,List<String>>();
	
    @SuppressWarnings("resource")
	public static String getFileCRCCode(File file) throws Exception {
        FileInputStream fileinputstream = new FileInputStream(file);
        CRC32 crc32 = new CRC32();
        for (CheckedInputStream checkedinputstream = new CheckedInputStream(fileinputstream, crc32);checkedinputstream.read() != -1;) {
        }
        return Long.toHexString(crc32.getValue());
    }
}

