package firsttool.tweetqueue;

/**
 * This interface functions provide points of accessing
 * queue of messages.
 */
public interface IQueueAccessPoint 
{
     /**
      * Returns next element in the queue if available.
      * @return 
      */
      AbstractTweet getNextOrNull();
}