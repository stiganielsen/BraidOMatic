
void serialEvent (Serial myPort) {
  // get the ASCII string:
  String inString = myPort.readStringUntil('\n');

  if (inString != null) {
    // trim off any whitespace:
    inString = trim(inString);
    // posFromEncoder = int(inString); 
    println("StringFromChip "+ inString); //printing to console here
    myPort.clear();
  }
}

void sendValues() {

  if (connected) {
    myPort.write('p'+str(goToPos)); 
    myPort.write('\n'); 
    myPort.clear();
  }
  
  if (connected) {  
      myPort.write("s"+servoString); 
      myPort.write('\n'); 
      myPort.clear();
    }
    println("pos:"+goToPos+"s"+servoString); 
    oldServoString=servoString;
}

void sendValuesMap() {
 if (connected) {  
      myPort.write("m"+servoString); 
      myPort.write('\n'); 
      myPort.clear();
    }
    println("pos: "+goToPos+"s"+servoString); 
    oldServoString=servoString;
}