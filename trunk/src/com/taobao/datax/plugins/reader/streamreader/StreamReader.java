/**
 * (C) 2010-2011 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 
 * version 2 as published by the Free Software Foundation. 
 * 
 */


package com.taobao.datax.plugins.reader.streamreader;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.ReadOnlyFileSystemException;

import org.apache.log4j.Logger;

import com.taobao.datax.common.exception.ExceptionTracker;
import com.taobao.datax.common.exception.DataExchangeException;
import com.taobao.datax.common.plugin.Line;
import com.taobao.datax.common.plugin.LineReceiver;
import com.taobao.datax.common.plugin.LineSender;
import com.taobao.datax.common.plugin.PluginStatus;
import com.taobao.datax.common.plugin.Reader;

import sun.org.mozilla.javascript.internal.ast.ThrowStatement;



/**
 * @author bazhen.csy
 *
 */
public class StreamReader extends Reader {	
	private char FIELD_SPLIT = '\t';
	
	private String src="src";
	
	private String ENCODING = "UTF-8";
	
	private String nullString = "";
	
	private static Logger logger = Logger.getLogger(StreamReader.class.getCanonicalName());
	
	private BufferedReader reader=null;
	
	private LineSender resultWriter=null;
	@Override
	public int init() {
		this.src=param.getValue(src);
		this.FIELD_SPLIT = param.getCharValue(ParamKey.fieldSplit, '\t');
		this.ENCODING = param.getValue(ParamKey.encoding, "UTF-8");
		this.nullString = param.getValue(ParamKey.nullString, "");
		
		return PluginStatus.SUCCESS.value();
	}


	@Override
	public int connect() {
		return 0;
	}

	private String changeNull(final String item) {
		if (nullString != null && nullString.equals(item)) {
			return null;
		}
		return item;
	}
	
	public void readFile(File file) {
		int previous;
		String fetch;
		try {
			reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),this.ENCODING));
			while ((fetch=reader.readLine())!=null) {
				previous=0;
				Line line=resultWriter.createLine();
				for(int i=0;i<fetch.length();i++) {
					if(fetch.charAt(i)==this.FIELD_SPLIT){
						line.addField(fetch.substring(previous,i));
					    previous=i+1;
					}
				}
				line.addField(fetch.substring(previous));
				resultWriter.sendToWriter(line);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(ExceptionTracker.trace(e));
			throw new DataExchangeException(e.getCause());
		}
	}


	@Override
	public int startRead(LineSender resultWriter){
		int ret = PluginStatus.SUCCESS.value();
		this.resultWriter=resultWriter;
		File file=new File(this.src);
		if(!file.isDirectory()){
			
			readFile(file);
		}
		else {
			File[] files=file.listFiles();
			int i=1;
			for(File f:files){
				Line line=resultWriter.createLine();
				line.addField("No."+i+" is From:"+f.getName());
				resultWriter.sendToWriter(line);
				i++;
				readFile(f);
			}
		}
		resultWriter.flush();
		
		return ret;
	}


	@Override
	public int finish(){
		return PluginStatus.SUCCESS.value();
	}
}
