package org.opensignature.opensignpdf.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.lowagie.text.DocumentException;



/**
 * Utility class with useful methods to handle Files. 
 * 
 * @author <a href="mailto:japaricio@accv.es">Javier Aparicio</a>
 *
 */
public class FileUtils {
  
  
  /**
   * Loggger de clase
   */
  private static Logger logger = Logger.getLogger(FileUtils.class);
  
  
  
  
  
  
  /**
   * Adds a suffix to a file.
   * 
   * @param file
   * @param suffix
   * @return
   */
  public static File addSuffix( File file, String suffix, boolean overwrite){
    
    logger.info("[addSuffix.entrada]:: " + Arrays.asList(new Object[] { file, suffix }));
    
    if ( file == null ){
      logger.warn("[addSuffix]:: File is null.");
      return null;
    }
    
    int lastDot =  file.getName().lastIndexOf('.');
    if ( lastDot < 0 ){ lastDot = file.getName().length()-1; }
    
    String fileName   = file.getName().substring(0, lastDot);
    String extension  = file.getName().substring(lastDot);
    logger.debug("[addSuffix]:: fileName: " + fileName);
    logger.debug("[addSuffix]:: extension: " + extension);
    
    File fRet = new File( file.getParentFile(), fileName + suffix + extension);
    if ( !overwrite ){
      fRet = getNextFileName(fRet);
    }
    
    logger.info("[addSuffix.retorna]:: fRet: " + fRet);
    return fRet;
    
  }
  
  
  /**
   * Obtains the next file for a File following the pattern:
   *  original file: a.pdf
   *    next file: a[1].pdf
   *    next file: a[2].pdf
   *    ...
   * 
   * @param file
   * @return
   */
  public static File getNextFileName(File file){
    
    logger.info("[getNextFileName.in]:: " + Arrays.asList(new Object[] { file }));
    
    int lastDot =  file.getName().lastIndexOf('.');
    if ( lastDot < 0 ){ lastDot = file.getName().length()-1; }
    
    String fileName   = file.getName().substring(0, lastDot);
    int lastBracket = fileName.lastIndexOf('[');
    fileName = fileName.substring( 0, (lastBracket>0 ? lastBracket:fileName.length()) );
    
    String extension  = file.getName().substring( lastDot );
    logger.debug("[getNextFileName]:: fileName: " + fileName);
    logger.debug("[getNextFileName]:: extension: " + extension);
    
    File nextFile = null;
    int i = 1;
    while (true) {
      nextFile = new File(file.getParentFile(), fileName + "[" + i + "]" + extension);
      if ( !nextFile.exists() ){ break; }
      i++;
    }
    
    logger.info("[getNextFileName.out]:: " + nextFile.getAbsolutePath());
    return nextFile;
    
  }
  
  
  
  /**
   * 
   * @param fOutDir
   * @param file
   * @return
   * @throws DocumentException
   * @throws IOException
   */
  public static File getLastFileName(File file) throws DocumentException, IOException{
    
    logger.info("[getLastFileName.in]:: " + Arrays.asList(new Object[] { file }));
    
    int lastDot =  file.getName().lastIndexOf('.');
    if ( lastDot < 0 ){ lastDot = file.getName().length()-1; }
    
    String fileName   = file.getName().substring(0, lastDot);
    int lastBracket = fileName.lastIndexOf('[');
    fileName = fileName.substring( 0, (lastBracket>0 ? lastBracket : fileName.length()) );
    
    String extension  = file.getName().substring(lastDot);
    logger.debug("[getLastFileName]:: fileName: " + fileName);
    logger.debug("[getLastFileName]:: extension: " + extension);
    
    File nextFile = null;
    int i = 0;
    while (true) {
      
      logger.debug("[getLastFileName]:: Iteration...");
      if ( i==0 && !file.exists() ){
        nextFile = file;
        logger.debug("[getLastFileName]:: File '" + nextFile.getAbsolutePath() + "' created");
        break;
      
      }else if ( i > 0 ){
        
        nextFile = new File(file.getParentFile(), fileName+"["+i+"]"+extension);
        
        if ( !nextFile.exists() ){
          nextFile = new File(file.getParentFile(), fileName+"["+(i-1)+"]"+extension);
          break;
        }
        
      }
      
      i++;
      
    }
    
    logger.info("[getLastFileName.out]:: " + nextFile.getAbsolutePath());
    return nextFile;
    
  }
  

}
