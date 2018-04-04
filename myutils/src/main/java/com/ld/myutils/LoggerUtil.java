package com.ld.myutils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 日志工具
 * @author Seven
 * @date 2017年10月16日
 */
public class LoggerUtil {
	
	private static Logger jdkLog ;
	public static final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
	public static Logger getJdkLog() {
		if(jdkLog==null) {
			try {
				jdkLog = Logger.getLogger(LoggerUtil.class.getName());
				jdkLog.setLevel(Level.ALL);
				String logPath = System.getProperty("user.dir")+File.separator+"log.log";
				FileHandler fileHandler;
				fileHandler = new FileHandler(logPath);
				fileHandler.setLevel(Level.ALL); 
				Formatter formatter = new Formatter() {
					
					@Override
					public String format(LogRecord record) {
						// TODO Auto-generated method stub
						 // 设置文件输出格式
	                    return "[ " + getCurrentDateStr(DATE_PATTERN_FULL) + " - Level:"
	                            + record.getLevel().getName().substring(0, 1) + " ]-" + "[" + record.getSourceClassName()
	                            + " -> " + record.getSourceMethodName() + "()] " + record.getMessage() + "\n";
					}
				};
				fileHandler.setFormatter(formatter);
				jdkLog.addHandler(fileHandler);  
				jdkLog.setUseParentHandlers(false);
				jdkLog.info("Log Begin -------------------");
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		return jdkLog;
		
		
	}
	 /**
     * 获取当前时间
     * 
     * @return
     */
    public static String getCurrentDateStr(String pattern) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
