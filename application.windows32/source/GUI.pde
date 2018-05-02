void mouseReleased() {
  if (mouseButton==RIGHT && driverList.size()-1!=selectedModule) { //if its a right mousebutton then the selected module is deleted
    if (mode==1) driverList.remove(selectedModule);
    else if (mode==2) TrackModuleRemove();
    else if (mode==3) carAdd(-1);
    else if (mode==0); //mapping switches
  }
  if (mouseButton==LEFT) {
    if (mode==1) DropInDriverModule();
    else if (mode==2) TrackModuleAdd();
    else if (mode==3) carAdd(+1);
    else if (mode==4) {
      swiMap[closestSwitch]=swiTest-closestSwitch; //mapping switches
      println("Encoding the map...................................................................");
      if (swiTest>swiList.size()-2) swiTest=0; 
      if (swiTest<0) swiTest=swiList.size()-2;
      else swiTest++;
   //SAVING THE MAPPING
    String[] lines = new String[1];
     lines[0]=""+swiMap[0];
      for(int i=1;i<swiMap.length;i++){
      lines[0]=lines[0]+","+swiMap[i];}
    saveStrings("MappingSwitches"+reload+".txt", lines); 
    
  }
  }
}
//////////////////////////////////////////////
void printTextCommands() {
  if (!connected) textOnScreen("Machine NOT connected");
  textOnScreen("'O' change MODE ");
  String textTemp="(" +mode+")";
  if (mode==1) textTemp+=" - RECONFIGURING NR: "+reload;
 // if (mode==1) textTemp+=+reload;
  if (mode==2) textTemp+=" - TRACK LAYOUT";
  if (mode==3) textTemp+=" - CARRIER PLACE/REMOVE";
  if (mode==4) textTemp+=" - MAPPING";

  textOnScreen(textTemp);
  textOnScreen("'M' Move machine");
  textOnScreen("'S' Save configuration");
  if (mode==1) textOnScreen("'R' reload existing configuration");
  if (mode==1) textOnScreen("Click R/L to build/remove modules ");
  if (mode==2) textOnScreen("Click R/L to draw/delete track ");
  if (mode==3) textOnScreen("Click R/L to add dir +/- carriers ");

  if (mode==2||mode==3) textOnScreen("'N' select next track");
  if (mode==1) textOnScreen("'S' save configuration");
  if (mode==2||mode==3) textOnScreen("'I' insert carriers");
  if (mode==2||mode==3) textOnScreen("'C' clear carriers");
  if (mode==2||mode==3) textOnScreen("'scroll' through tracks");
  textTemp="Drawing track: " +curDrawTrack;
  if (mode==2||mode==3) textOnScreen(textTemp);
  if (mode==3) textOnScreen("'0'..'7' moves carriers from current track to selected track");
}
/////////////////////////////////////////////////////////
void keyPressed() {
  if (key=='0') {
    moveCarsToTrack(0);
    IniCars();
    RecreateSwitches();
  }     
  if (key=='1') {
    moveCarsToTrack(1);
    IniCars();
  }     
  if (key=='2') {
    moveCarsToTrack(2);
    IniCars();
  }     
  if (key=='3') {
    moveCarsToTrack(3);
    IniCars();
  }     
  if (key=='4') {
    moveCarsToTrack(4);
    IniCars();
  }     
  if (key=='5') {
    moveCarsToTrack(5);
    IniCars();
  }     
  if (key=='6') {
    moveCarsToTrack(6);
    IniCars();
  }     
  if (key=='7') {
    moveCarsToTrack(7);
    IniCars();
  } 
  if (key=='o'||key=='O') {
    mode++;
    if (mode>numberOfModes) mode=1;
  }
  if (key=='n'||key=='N') { //move
    if (curDrawTrack<numberOfTracks-1) {
      curDrawTrack++;
    } else curDrawTrack=0;
  }
  if (key=='m'||key=='M') { //move
      
    // IniCars();
    goToPos++;
    RotateOne();
      EndTrackHandle();
    StepCarInModulesAndSetSwitches();
  
    updateTraces() ;
    StepCarInDriver();
    saveSwitches();
    sendValues(); //over serial to the physical controller -see Serialcom
  }
  if (key=='p'|| key=='P') { // print switches AND Place carriers on every second element of the track. 

    ListSwitches();
  }

  if (key=='i'|| key=='I') {
    InsertCars();
  }
  if (key=='c'|| key=='C') {
    ClearCars();
  }

  if (key=='d'|| key=='D') { //SAVE MODULE CONFIGURATION
    RecreateSwitches();
  }
  if (key=='s'|| key=='S') { //SAVE MODULE CONFIGURATION
    RecreateSwitches();
    FindLoneDrivers() ;
    SaveModulePositions();
  }
  if (key=='r'||key=='R') {
     if(reload<4) reload++;
    else reload =1;
    ReloadModules();
     LoadTracks();
     RecreateSwitches();
    
  }
  if (key=='q'||key=='Q') {
    print("Reconfiguration Mode");
    reconfigModules=true;
    traceTracks=false;
  }
  if (key=='w'||key=='W') {
    print("Track Mode");
    reconfigModules=false;
    traceTracks=true;
  }
   if (key=='t'||key=='T') {
    print("Traces printed to file");
    printTracesToFile();
  }
}
//////////////////////////////////////////////
void mouseWheel(MouseEvent event) {
  int e = event.getCount();
  if (mode==2||mode==3) { 
    curDrawTrack+=e;
    if (curDrawTrack<0)  curDrawTrack=numberOfTracks-1;
    if  (curDrawTrack>numberOfTracks-1) curDrawTrack=0;
  } else if (mode==4) { //if mode 'Switch Mapping'(4)
    if (swiTest>swiList.size()-2) swiTest=0; 
    else if (swiTest<0) swiTest=swiList.size()-2;
    else swiTest+=e;
  }
}
//////////////////////////////////////////////////
void textOnScreen(String printThis) {
  text(printThis, 10, newLine);
  newLine+=20;
}