void mouseReleased() {
  if (mouseButton==RIGHT && driverList.size()!=selectedModule && driverList.size()!=1) { //-1  if its a right mousebutton then the selected module is deleted
    if (mode==1) {driverList.remove(selectedModule);RecreateSwitches();}
    else if (mode==2) TrackModuleRemove();
    else if (mode==3) carAdd(-1);
    else if (mode==0); //mapping switches
  }
  if (mouseButton==LEFT) {
    if (mode==1){ DropInDriverModule();RecreateSwitches();}
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
    saveStrings(dataPath("MappingSwitches"+reload+".txt"), lines); 
    
  }
  }
}
//////////////////////////////////////////////
void printTextCommands() {
  if (!connected) textOnScreen("Machine NOT connected");
  String textTemp="MODE: " +mode+"";
  textTemp+=" - LAYOUT NR: "+reload;
 // if (mode==1) textTemp+=+reload;
   if(mode==1) textTemp+=" - MODIFY LAYOUT";
  if (mode==2) textTemp+=" - MODIFY TRACK: "+curDrawTrack;
  if (mode==3) textTemp+=" - CARRIER PLACE/REMOVE";
  if (mode==4) textTemp+=" - MAPPING";
  textOnScreen(textTemp);

  textOnScreen("'O' change MODE ");
  textOnScreen("'R' change LAYOUT");
  if (mode==2||mode==3) textOnScreen("'N' Next track or 'scroll'");
  textOnScreen("'M' Move machine");
  textOnScreen("'S' Save configuration");
 
  if (mode==1) textOnScreen("Click R/L to build/remove modules ");
  if (mode==2) textOnScreen("Click R/L to draw/delete track ");
  if (mode==3) textOnScreen("Click R/L to add dir +/- carriers ");



  if (mode==2||mode==3) textOnScreen("'I' Insert carriers");
  if (mode==2||mode==3) textOnScreen("'C' Clear carriers");

  //textTemp="Drawing track: " +curDrawTrack;
  //if (mode==2||mode==3) textOnScreen(textTemp);
  if (mode==3) textOnScreen("n-'0..7' move carriers from n-track to selected track ");
 textOnScreen("total: "+totalCollisions+" collisions");
  splashText(lastKeypress);
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

  if (key=='m'||key=='M') { //move
      Splash("(M) move");
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
 
  if (key=='i'|| key=='I') {
    InsertCars();
    Splash("I");
  }
  if (key=='c'|| key=='C') {
    if(mode!=3){Splash("MODE 3 -place carriers"); mode=3;}
    else{
    ClearCars();
    Splash("C clear carriers from track " +curDrawTrack);
  }
  }

  if (key=='d'|| key=='D') { //SAVE MODULE CONFIGURATION
    RecreateSwitches();
    Splash("D");
  }
   if (key=='e'||key=='E') {
     Splash("(E) Exported traces of carriers to text file");
    print("Traces printed to file");
    printTracesToFile();
  } 
    if (key=='o'||key=='O') {
    mode++;
     Splash("(O) MODE: "+ mode);
    if (mode>numberOfModes) mode=1;
  }
   if (key=='p'|| key=='P') { // print switches AND Place carriers on every second element of the track. 
    Splash("P");
    ListSwitches();
  }

  if (key=='q'||key=='Q') {
    Splash("Q");
    print("Reconfiguration Mode");
    reconfigModules=true;
    traceTracks=false;
  }
  if (key=='r'||key=='R') {
     if(reload<4) reload++;
    else reload =1;
    ReloadModules();
     LoadTracks();
     RecreateSwitches();
    Splash("(R) LAYOUT "+reload);
    
  }
  if (key=='s'|| key=='S') { //SAVE MODULE CONFIGURATION
    RecreateSwitches();
    Splash("S");
    FindLoneDrivers() ;
    SaveModulePositions();
  }
   if (key=='t'||key=='T') { //Clear and update traces to carriers and drive more to trace
     tracesList.clear();
     for (int i =0;i<carList.size();i++)
     {
     addTraceToCar(i);
     }

}

  if (key=='w'||key=='W') {
    Splash("W");
    print("Track Mode");
    reconfigModules=false;
    traceTracks=true;
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
    textSize(16);
  text(printThis, 10, newLine);
  newLine+=20;
}

void splashText(String printThis){
  fill(185);
    textSize(splashVisible);
    text(lastKeypress,10,height-22);
    textSize(16);
    fill(255);
    if(splashVisible>30) {splashVisible=splashVisible-5;}
   else splashVisible=14 ;
}
void Splash(String inputString){
  splashVisible=50;
  lastKeypress=inputString;
}