/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yodcserver;

/**
 *
 * @author satyam
 */
public class YoDCServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here 
        IncomingConnectionHandler incomingConnectionHandler = new IncomingConnectionHandler();
        new Thread (incomingConnectionHandler).start();
        
        SharedFileListener sfl = new SharedFileListener();
        new Thread (sfl).start();
        
        SearchQueryHandler searcher = new SearchQueryHandler();
        new Thread (searcher).start();
        
        new MessageHandler().start();
    }
    
}
