package net.sourceforge.tess4j;

import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.InputStreamReader;  
import java.util.ArrayList;  
import java.util.List;  
  
  
public class OCR {  
    private final String LANG_OPTION = "-l";  //Ӣ����ĸСдl����������1  
    private final String EOL = System.getProperty("line.separator");  
    private String tessPath = "D:\\Program Files (x86)\\Tesseract-OCR";//tesseract-ocr��װ��ַ  
    //private String tessPath = new File("tesseract").getAbsolutePath();  
      
    public String recognizeText(File imageFile,String imageFormat)throws Exception{  
        File outputFile = new File(imageFile.getParentFile(),"output");  
        StringBuffer strB = new StringBuffer();  
        List<String> cmd = new ArrayList<String>();  
        cmd.add(tessPath+"//tesseract");  
        /*if(OS.isWindowsXP()){  
            cmd.add(tessPath+"//tesseract");  
        }else if(OS.isLinux()){  
            cmd.add("tesseract");  
        }else{  
            cmd.add(tessPath+"//tesseract");  
        }  */
        cmd.add("");  
        cmd.add(outputFile.getName());  
        cmd.add("-l");
        cmd.add("chi_sim");
        //cmd.add(LANG_OPTION);  
        //cmd.add("chi_sim");  
        //cmd.add("eng");  
          
        ProcessBuilder pb = new ProcessBuilder();  
        pb.directory(imageFile.getParentFile());  
          
        cmd.set(1, imageFile.getName());  
        pb.command(cmd);  
        pb.redirectErrorStream(true);  
          
        Process process = pb.start();  
        //tesseract.exe 1.jpg 1 -l chi_sim  
        int w = process.waitFor();  

        if(w==0){  
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath()+".txt"),"UTF-8"));  
              
            String str;  
            while((str = in.readLine())!=null){
            	if(str == null || str.length()<=0 || str.trim().length()<=0) continue;
                strB.append(str).append(EOL);  
            }  
            in.close();  
        }else{  
            String msg;  
            switch(w){  
                case 1:  
                    msg = "Errors accessing files.There may be spaces in your image's filename.";  
                    break;  
                case 29:  
                    msg = "Cannot recongnize the image or its selected region.";  
                    break;  
                case 31:  
                    msg = "Unsupported image format.";  
                    break;  
                default:  
                    msg = "Errors occurred.";  
            }  
            //throw new RuntimeException(msg);  
        }  
        new File(outputFile.getAbsolutePath()+".txt").delete();  
        return strB.toString();  
    }  
}  
