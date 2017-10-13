/*
 * The MIT License
 *
 * Copyright (c) 2016, AbsInt Angewandte Informatik GmbH
 * Author: Dr.-Ing. Joerg Herter
 * Email: herter@absint.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.absint.astree;

import hudson.model.TaskListener;
import java.io.*;


/**
 * Helper thread to periodically update the Jenkins console output w.r.t.
 * a given (text) file.
 *  
 * @author AbsInt Angewandte Informatik GmbH
 */
class StatusPoller extends Thread {

   static private long INITIAL_DELAY = 1 * 1000;

   private long    interval;
   private boolean    alive;

   private TaskListener listener;
   private File log;


  /**
   * Constructor.
   *
   * @param interval    update interval in milliseconds 
   * @param listener    TaskListener to provide access to Jenkins console via getLogger()
   * @param log         input file (log to copy to console)
   */
   StatusPoller(long interval, TaskListener listener, File log) {
       this.alive    = true;
       this.interval = interval;
       this.listener = listener;
       this.log      = log;
   }

 
   public void run() {
       boolean active               = true;
       RandomAccessFile fileHandler = null;
       StringBuilder             sb = null;
       long lpos = 0, cpos;
       try { this.sleep(INITIAL_DELAY); }
       catch(InterruptedException e) {}
       while(active) {
         if(!alive) // last update 
            active = false;
         try {
          this.sleep(interval);
          fileHandler = new RandomAccessFile(log, "r");
          if(fileHandler.length() == 0) continue; // no output there, yet
          cpos        = fileHandler.length() - 1;
          sb = new StringBuilder();
          for(long filePointer = lpos; filePointer <= cpos; filePointer++){
              fileHandler.seek( filePointer );
              sb.append((char)fileHandler.readByte());
          }
          lpos = cpos;
          this.listener.getLogger().print(sb.toString());
         } 
         catch(InterruptedException ie) {
         }
         catch( FileNotFoundException e ) {
            e.printStackTrace();
         } 
         catch( IOException e ) {
            e.printStackTrace();
         }
         finally {
            if (fileHandler != null )
                try {
                   fileHandler.close();
                } 
                catch (IOException e) {
                }
         }

       }    
   }

  /**
   * Tells the thread to stop after a final update.
   */
   public void kill() {
      this.alive = false;
   }
}

