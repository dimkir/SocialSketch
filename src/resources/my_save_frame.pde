@Override
void keyReleased(KeyEvent evt){
   println("*******************keyEvent: " + evt.getAction() );
   if ( evt.getAction() == KeyEvent.RELEASE ){
      // release is triggered only once
      // PRESS ( despite it's name is triggered 
      // on first key press and then repeated
      // every half a second (despite it's the
      // behaviour more suitable for TYPE event.
      saveFrame(getFrameName());
   }
   super.keyPressed(evt); 
}

/**
* Generates frame names for the 
* screenshots.
* We will probably use System.time because
* in typical usage scenario, for cretive coder would look 
* like this:
*  
*/
String getFrameName(){
    return "frame_" + System.currentTimeMillis() + ".png";
}