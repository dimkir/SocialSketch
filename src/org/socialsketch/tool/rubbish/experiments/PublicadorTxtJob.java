package org.socialsketch.tool.rubbish.experiments;

//imports

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import javax.annotation.PostConstruct;



public class PublicadorTxtJob 
//extends PublicadorJob 
{
//
//    /**
//     * Why do you use 2 ExecutorService's ? 
//     * Isn't it enough with just having one?
//     */
//    private ExecutorService nfeExecutorService;
//    
//    
//    private ExecutorService nfeThread = Executors.newSingleThreadExecutor(); // Single Thread contains pool ExecutorService nfeExecutorService.
//    
//    protected Set<File> processingFiles = Collections.newSetFromMap(new ConcurrentHashMap<File, Boolean>());
//
//    // getters and setters
//
//    @PostConstruct
//    public void setup() { // Initialized executor with ThreadPoolExecutor
//        final Long qtdeMinThread = (configuration.getQtdThreadEnvioToNumber() / 2);
//        final Long qtdeMaxThead = configuration.getQtdThreadEnvioToNumber();
//        nfeExecutorService = new ThreadPoolExecutor(qtdeMinThread.intValue(), qtdeMaxThead.intValue(), 2, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
//    }
//    
//    /**
//     * Who's calling this? The one who's creating PublicadorTxtJob?
//     */
//    @Override
//    public void execute() { // The main method.
//        
//        final FilesBroker filesBroker = new FilesBroker(verificaTipoArquivo, processingFiles, configuration, arquivoService);
//
//        SortedSet<File> listaArquivos = arquivoService.listaArquivos();
//        final TreeMultimap<String, File> result = filesBroker.map(listaArquivos);
//
//        Runnable nfeRunnable = new Runnable() { //An anonymous class or your Thread
//            @Override
//            public void run() {
//                List<Callable<Void>> nfeTasks = makeTasks(result.get("nfe"));
//                performTasks(nfeExecutorService, nfeTasks);
//            }
//        };
//
//        nfeThread.submit(nfeRunnable); // Submit the Thread to nfeThread
//    }
//
//    /**
//     * Converts collection of files into collection of tasks.
//     * 
//     * @param files
//     * @return 
//     */
//    private List<Callable<Void>> makeTasks(final SortedSet<File> files) { // Return list of classes that implements Callable interface
//        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
//        int maxFilesToProcess = Integer.parseInt(configuration.getQtdRegistros());
//        for (final File file : files) {
//            if (--maxFilesToProcess == 0) {
//                break;
//            }
//            TxtPublicadorBPM txtPublicadorBPM = (TxtPublicadorBPM) publicadorFactory.getObject(); // This class implements Callable interface that contains the rules/business.
//            txtPublicadorBPM.setFile(file);
//            txtPublicadorBPM.setProcessingFiles(processingFiles);
//            
//            boolean inserted = processingFiles.add(file);
//            if (inserted) {
//                tasks.add(txtPublicadorBPM);
//            }
//        }
//        return tasks;
//    }
//
//    private void performTasks(final ExecutorService executorService, final List<Callable<Void>> tasks) { // Submit list of task(Callable) to nfeExecutorService
//        for (Callable<Void> c : tasks){
//            executorService.submit(c);
//        }
//    }
}