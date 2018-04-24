import processing.serial.*;
Serial myPort;  
PFont metaBold;
ArrayList driverList;
ArrayList optPosList;
ArrayList swiList;
ArrayList carList;
ArrayList tracesList;
int driverSize=100;  //the diameter of a driver
int closestOptpos=0;
int closestSwitch=0;
int selectedModule=0;
int altModule=0;
int [] ppa={0,1,2,3,4,5,6,7};
float [] ppx={0,1,2,3,4,5,6,7};
float [] ppy={0,1,2,3,4,5,6,7};
int rotation = 0;
int numberOfTracks=9;
int modulesInTracks=25;
int [][] tracks = new int [numberOfTracks][modulesInTracks]; //twelve tracks of up to 12 modules (these are used bidirectionally)
int totalCollisions;
boolean reconfigModules=true;
boolean traceTracks=false;
int curDrawTrack=0;
int curDrawInTrack=2;
int newLine=10;
int goToPos, oldGoToPos;
int numberOfModes=4;
int mode=1;
int reload=1;
String servoString="1111111111111111111111111111111111"; //extended if more servos are needed now 26
String oldServoString="1111111111111111111111111111111111";
String myString = null;
String servoSetTo="000,1111111111111111111111111111111111";
int[]swiMap=new int[34];
String[] swiLines = new String[1533];
color[] trackColors=new color[numberOfTracks];
boolean switchesUnmapped=true;
//////////////////////////////////////CONNECTED TO THE SERIAL ??
boolean connected=false;
//////////////////////////////////////////////////////////////////////////
void setup() {
  size(900, 900);
  metaBold = loadFont("fontMS.vlw");
  textFont(metaBold, 40);
if(connected){
  myPort = new Serial(this, "COM18", 9600);
  myPort.clear();
  myPort.bufferUntil('\n');
}
  driverList    = new ArrayList();
  optPosList    = new ArrayList();
  swiList       = new ArrayList();
  carList       = new ArrayList();
  tracesList    = new ArrayList();
  //add one driver to start from
     
  driverList.add(new driver (1,true, 300, 300,ppa,ppx,ppy));
  //  swiList.add(new swi(5, 9, 10, 0, 0, 5, true, 0, 0));
  
  textSize(16);
   LoadTracks();
  ellipseMode(CENTER);
 for(int i=0;i<numberOfTracks;i++){
 trackColors[i]= color((150+i*69*20)%255, i*69*20%255, 255-i*69%255);
 

 }
 for(int i=0;i<swiMap.length;i++) swiMap[i]=100; 
 ReloadModules();
    ///////////////////to do: -but for now manually defined in text file..
    //Build all tracks: a recursive function that steps module to module(via switches) every step is compared to all previous steps. 
    //when the start module is reached it becomes a circular step -1,x,x,x,x,-1 
    //if it can not go other ways than back at the previous module it ends there as a linear track.
///////////////////    

    //INITIALIZE cars to tracks -finding the start module
  
    //
}
void draw() {
   // general operation
  background(0);
  fill(0);
  newLine=20;
  fill(255);
    printTextCommands(); 
if (mode==1){

  noFill();
  SelectClosestModule();
  SuggestDriverLocation(selectedModule);
  DrawNearestOptionalModule();
}
else if(mode==2){// draw tracks
   fill(255);
    noFill();
  SelectClosestModule();
  DrawTracks();
  //SelectTrackModule();
}
else if(mode==3){
    noFill();
  SelectClosestModule();
  SelectClosestPosition();
  DrawTracks();
}
else if(mode==4){
  mapSwitches(); 
}

  DrawDrivers();
  DrawSwitches();
  LocateCars();
  DrawCars();
  Collision();
}