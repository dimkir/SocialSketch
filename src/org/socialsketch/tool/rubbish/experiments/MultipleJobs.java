/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.socialsketch.tool.rubbish.experiments;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class MultipleJobs {
    private final ThreadPoolExecutor thPool;
    private BlockingQueue<Runnable> myQueue  =  new LinkedBlockingQueue<Runnable>();
    
    public MultipleJobs() {
        

        
        thPool = new ThreadPoolExecutor(3, 10, 2, TimeUnit.MINUTES, myQueue);
    }
    
    
    public static void main(String[] args) {
            new MultipleJobs();
    }
    
}
