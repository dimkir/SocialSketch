package components;

/**
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public interface IImageEnvelopeReceiver {

    /**
     * Submits envelope with processed images.
     * SHOULD BE RUN ON EDT THREAD!
     * @param imageEnvelope 
     */
    public void submitImageEnvelope(ImageFileLoadWorker.ImageEnvelope imageEnvelope);
    
}
