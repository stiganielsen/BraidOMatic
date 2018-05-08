import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class NewStart2017_46 extends PApplet {


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
int modulesInTracks=35;
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
int[] trackColors=new int[numberOfTracks];
boolean switchesUnmapped=true;
//////////////////////////////////////CONNECTED TO THE SERIAL ??
boolean connected=false;
//////////////////////////////////////////////////////////////////////////
public void setup() {
  
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
public void draw() {
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
class driver {
  int mod; //number 
  boolean dir;
  float posx;//position
  float posy;
public  int [] att=new int[8];
  float [] pox=new float[8];
  float [] poy=new float[8];


  driver(int mo, boolean di, float px, float py, int []at,float []ppx,float []ppy) {
    mod=mo;
    dir=di;
    posx=px;
    posy=py;
    att = at;
    pox = ppx;
    poy = ppy;
  }
  ArrayList positList = new ArrayList();


}

  class posit{
    float posx;
    float posy;
   posit(float pox,float poy){
    posx=pox;
    posy=poy;
   }
  }
/////////////////////////////////////////////////////////
class optPos {
  float posx; //number 
  float posy;
  boolean taken; //is the location occupied? true: free location / false: occupied loc
  boolean viable;
  optPos(float px, float py, boolean ta, boolean vi) {
    posx=px;
    posy=py;
    taken=ta;
    viable=vi;
  }
}
//////////////////////////////////////////
public void SuggestDriverLocation(int current ) { //keeps a list of optional locations, starting from x,0 counting clockwise
  optPosList.clear();
  //get the location of the driver in question -the last driver - and later on any driver
  driver drLook = (driver) driverList.get(current); //later get a specific clicked one...
  float CCdist= sqrt ( (driverSize/2)*(driverSize/2)+(driverSize/2)*(driverSize/2)); //just finding the radial distance to surrounding center points.

  optPosList.add(new optPos(drLook.posx+driverSize, drLook.posy, true, true)); //0
  optPosList.add(new optPos(drLook.posx+CCdist, drLook.posy+CCdist, true, true)); //1
  optPosList.add(new optPos(drLook.posx, drLook.posy+driverSize, true, true));//2
  optPosList.add(new optPos(drLook.posx-CCdist, drLook.posy+CCdist, true, true));//3
  optPosList.add(new optPos(drLook.posx-driverSize, drLook.posy, true, true));//4
  optPosList.add(new optPos(drLook.posx-CCdist, drLook.posy-CCdist, true, true));//5
  optPosList.add(new optPos(drLook.posx, drLook.posy-driverSize, true, true)); //6
  optPosList.add(new optPos(drLook.posx+CCdist, drLook.posy-CCdist, true, true));//7
}

/////////////////////////////////////
public void RotateOne() {
  rotation++;
  if (rotation>7) rotation=0;
}
//////////////////////////////////////////
public void DrawNearestOptionalModule() { //Highlight nearest optional place in GREEN color or RED if not viable location/////////////////////////////////////////////////////////////////////////
  float distance=10000;
  float distanceNear=10000;
  strokeWeight(3);
  stroke(255);
  for (int i = 0; i<optPosList.size (); i++) {
    optPos opLook = (optPos) optPosList.get(i);

    for (int j = 0; j<driverList.size (); j++) {
      driver drLook = (driver) driverList.get(j);
      float distClose = dist(opLook.posx, opLook.posy, drLook.posx, drLook.posy);
      if (distClose<1) { 
        opLook.taken=false;
      }
      if (distClose<driverSize-.5f) opLook.viable=false; //if another module is too close, the place is not viable
    }

    distance= dist(mouseX, mouseY, opLook.posx, opLook.posy);
    if (distanceNear>distance) {
      distanceNear=distance; 
      closestOptpos=i;
    }
  }
  optPos opNear = (optPos) optPosList.get(closestOptpos);
  if (opNear.taken && opNear.viable) stroke(0, 200, 0); 
  else stroke(222, 0, 0);
  ellipse(opNear.posx, opNear.posy, driverSize, driverSize);
}
//////////////////////////////////////////
public void DropInDriverModule() {//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  optPos opPlace = (optPos) optPosList.get(closestOptpos);
  int []at={0, 0, 0, 0, 0, 0, 0, 0}; //setup an array for ording the positions on the driver wheel being dropped
  float []ppx={0, 0, 0, 0, 0, 0, 0, 0};
  float []ppy={0, 0, 0, 0, 0, 0, 0, 0};

  if (opPlace.viable) {
    driver drSele = (driver) driverList.get(selectedModule); //fetch the selected/'building upon' module
    boolean directionThisMod=true; 
    int starta= drSele.att[closestOptpos];  //this is the previous drivers position that we attach to.
    println("starta: "+starta);
    if (drSele.dir) directionThisMod=false; //switch direction of the next module if the drSele is opposite.
    if (directionThisMod) {  //turning CV so the next module must be numbered oppositely direction CCV
      println("direction of this module is TRUE therefore going CV");

      for (int a=0; a<8; a++) {
        at[(closestOptpos+a+4)%8]=(a+starta)%8;
      }
    } else { 
      println("direction of This module is FALSE going CCV now");
      int cnt=0;
      for (int a=8; a>0; a--) {
        at[(closestOptpos+a+4) % 8] = (((starta-a) % 8)+8)%8 ;//(starta-cnt)%8 ;
        cnt++;
      }
    } // CCV

    //println(at);
    driverList.add(new driver (1, directionThisMod, opPlace.posx, opPlace.posy, at, ppx, ppy));
    driver drTjek = (driver) driverList.get(driverList.size()-1); //fetch the selected/'building upon' module
    for (int i =0; i<8; i++) {
      println("abspos: "+i+" pos:"+drTjek.att[i]);
    }
    FindNeighbourModules(driverList.size()-1);  //calling the switch placer on the latest module
  } else { 
    println("This location is not allowed");
    println("selected Module is now: "+altModule);
  }
}



//////////////////////////////////////////
public void FindNeighbourModules(int lookFrom) { //lists and prints the modules that are neighbours to the input 'lookFrom' module/////////////////////////////////////////////////////////////////////////
  driver drFrom = (driver) driverList.get(lookFrom); 
  int place=8;
  for (int i=0; i<driverList.size (); i++) {

    driver drLook = (driver) driverList.get(i); //later get a specific clicked one...
    float distance = dist(drFrom.posx, drFrom.posy, drLook.posx, drLook.posy);
    if (driverSize-3<distance&&distance<driverSize+3) { //means it is within range .. now find which position

      float swiposx=drFrom.posx;//(drFrom.posx+drLook.posx)/2;
      float swiposy=drFrom.posy;//(drFrom.posy+drLook.posy)/2;
      float lgtDia=sqrt((driverSize*driverSize)*2);

      if (drFrom.posx+1<drLook.posx) { //this tells us the drLook is to the right somewhere 
        if (drFrom.posy+1<drLook.posy) { //down right
          println("down right");
          place=1;
        } else if (drFrom.posy-1>drLook.posy) {  //up right
          println("up right");
          place=7;
        } else { 
          println("straight right"); //straight right
          place=0;
        }
      } else if (drFrom.posx-1>drLook.posx) { 
        if (drFrom.posy+1<drLook.posy) {
          println("down left"); 
          place=3;
        } else if (drFrom.posy-1>drLook.posy) { 
          println("up left");  
          place=5;
        } else {
          println("straight left");
          place=4;
        }
      } else if (drFrom.posy<drLook.posy) {
        println("straight below");
        place= 2;
      } else {
        println("straight above");
        place= 6;
      }
      int swiPos=drLook.att[(place+4)%8];
      println("module "+lookFrom+" connects to "+i+" at its "+swiPos+" pos");
      //find location between lookFrom and drLook
      switch (place) {  //depending where the neigbour module is connected (placed) and what direction the driver is turning, then a switch is assigned to posx posy of the driver plus offset.
      case 0:
        if (drFrom.dir) {
          swiposx+=driverSize/2;
          swiposy+=driverSize/2;
        } else {
          swiposx+=driverSize/2;
          swiposy-=driverSize/2;
        }
        break;
      case 1: //other is down right
        if (drFrom.dir) {
          //swiposx+=lgtDia/2;
          swiposy+=lgtDia/2;
        } else {
          swiposx+=lgtDia/2;
          //  swiposy-=driverSize/2;
        }
        break;
      case 2: //Other drv is straight below
        if (drFrom.dir) { //CV
          swiposx-=driverSize/2;
          swiposy+=driverSize/2;
        } else {
          swiposx+=driverSize/2;
          swiposy+=driverSize/2;
        }
        break;
      case 3: //other drv is down left

        if (drFrom.dir) {
          swiposx-=lgtDia/2;
          // swiposy+=driverSize/2;
        } else {
          //  swiposx+=driverSize/2;
          swiposy+=lgtDia/2;
        }
        break;
      case 4: //other is straight to the LEFT
        if (drFrom.dir) {
          swiposx-=driverSize/2;
          swiposy-=driverSize/2;
        } else {
          swiposx-=driverSize/2;
          swiposy+=driverSize/2;
        }
        break;
      case 5: //other is UP LEFT
        if (drFrom.dir) {
          //swiposx-=lgtDia/2;
          swiposy-=lgtDia/2;
        } else {
          swiposx-=lgtDia/2;
          //swiposy+=lgtDia/2;
        }
        break;
      case 6: //other is Straight UP 
        if (drFrom.dir) {
          swiposx+=driverSize/2;
          swiposy-=driverSize/2;
        } else {
          swiposx-=driverSize/2;
          swiposy-=driverSize/2;
        }
        break;
      case 7: //other is UP right
        if (drFrom.dir) {
          swiposx+=lgtDia/2;
          //swiposy-=lgtDia/2;
        } else {
          //swiposx-=lgtDia/2;
          swiposy-=lgtDia/2;
        }
        break;
      }
      boolean add=true; //checks whether to add the switch later or if its already added to another switch
      for (int s=0; s<swiList.size(); s++) {
        swi swiLook = (swi) swiList.get(s);
        if (dist(swiLook.posx, swiLook.posy, swiposx, swiposy)<5) { //if the difference in location for x and y is smaller than 1 -means there is already a switch at that location
          add=false;

          if (drFrom.dir) {
            swiLook.bmoR=lookFrom;
            swiLook.bmoL=i ;
          } else {
            swiLook.bmoR=i;
            swiLook.bmoL=lookFrom ;
          }

          println("COMBINING to swi: "+s+" bmod(RL): "+i+","+lookFrom);
          { 
          }
        }
      }
      if (add) {
        if (drFrom.dir)
        {
          swiList.add(new swi(lookFrom, i, -1, -1, swiPos, true, swiposx, swiposy)); //swi ( int amr, int aml,int bmr, int bml, int po, boolean r,float px,float py) {
        } else {
          swiList.add(new swi(i, lookFrom, -1, -1, swiPos, true, swiposx, swiposy));
        }
      }
    }
  }
}
//////////////////////////////////////////
public void DrawCurrentSelectedModule() {  //highlights the module that is selected to build upon/////////////////////////////////////////////////////////////////////////
  driver drLook = (driver) driverList.get(selectedModule); //later get a specific clicked one...
  strokeWeight(12);
  stroke(0, 122, 0);
  ellipse(drLook.posx, drLook.posy, driverSize, driverSize);
}



//////////////////////////////////////////
public void DrawDrivers() { //drawing the drivers as white ellipses///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  for (int d=0; d<driverList.size (); d++) {
    driver drLook = (driver) driverList.get(d);
    noFill();
    if (drLook.dir) stroke(255);
    else stroke(200);

    strokeWeight(PApplet.parseInt(driverSize/25));
    ellipse(drLook.posx, drLook.posy, driverSize, driverSize); //the white driver main circle
    fill(0);
    noStroke();
    ellipse(drLook.posx, drLook.posy, 20, 20);
    fill(110, 110, 255);
    if (d<10) text(d, drLook.posx-5, drLook.posy+5);
    else text(d, drLook.posx-9, drLook.posy+5);
    for (int p=0; p<8; p++) {
      float slot=p;
      float xx =drLook.posx+(  ((driverSize/2)-5) * cos(radians(360*(slot/8))));
      float yy =drLook.posy+(  ((driverSize/2)-5) * sin(radians(360*(slot/8))));     


      if (drLook.dir) { 
        fill(222, (255-(drLook.att[(p-rotation+8)%8]*30)), 0);
        stroke(drLook.att[(p-rotation+8)%8]*10);
      } else {
        fill(222, (255-(drLook.att[(p+rotation+8)%8]*30)), 0);
        stroke(drLook.att[(p+rotation+8)%8]*10);
      }

      strokeWeight(PApplet.parseInt(driverSize/4));
      point (xx, yy);
      text(drLook.att[p], xx-5, yy+5);
      noFill();
      drLook.pox[drLook.att[p]]=xx;  
      drLook.poy[drLook.att[p]]=yy;
      // print(" in:"+p+ " at:"+ drLook.att[p] +" x:"+ round(drLook.pox[p]));
    }
  }
}


///////////////////////////////////////////////////////////////////////////////
public int SelectClosestPosition() { //from the positions available in the selected module
  int closestModule=10;
  driver drLook = (driver) driverList.get(selectedModule);
  float distMin=10000;
  for (int i=0; i<8; i++) {
    float distMM=dist(mouseX, mouseY, drLook.pox[i], drLook.poy[i]);  
    if (distMM<distMin) {
      distMin=distMM;
      closestModule=i;
    }
  }
  //println("the closest position: "+ drLook.att[closestModule]) ;
  fill(trackColors[curDrawTrack]);
  noStroke();

  ellipse(drLook.pox[closestModule], drLook.poy[closestModule], 29, 29);

  if (millis()%800<400) { 
    ellipse(mouseX, mouseY, 26, 26);
    stroke(trackColors[curDrawTrack]);
    strokeWeight(2);
    line (mouseX, mouseY, drLook.pox[closestModule], drLook.poy[closestModule]);
  }
  return closestModule;
}

////////////////////////////////////////////////////////////////////
public void SelectClosestModule() { //Find the closest driver module and make it the public 'SelectedModule'/////////////////////////////////////////////////////////////////////////
  float distance=10000;
  float distanceNear=10000;
  int closestMod=0;
  for (int j =0; j<driverList.size (); j++) {
    driver drLook = (driver) driverList.get(j);
    distance= dist(mouseX, mouseY, drLook.posx, drLook.posy);
    if (distanceNear>distance) {
      distanceNear=distance; 
      closestMod=j;
    }
    selectedModule=closestMod;
  }
  DrawCurrentSelectedModule();
}
//////////////////////////////////////////////////////////////
public void SaveModulePositions() {
  //save the MODULE LAYOUT
  //write values from the agent positions to file.
  String[] lines = new String[driverList.size()];
  for (int i = 0; i < driverList.size (); i++) {
    driver drLook=(driver) driverList.get(i);
    String stratt="";
    String strppx="";
    String strppy="";

    for (int j=0; j<8; j++) {
      stratt= stratt.concat(";"+ Integer.toString(drLook.att[j]));
      strppx= strppx.concat(";"+ Float.toString(drLook.pox[j]));
      strppy= strppy.concat(";"+ Float.toString(drLook.poy[j]));
    }
    lines[i] = PApplet.parseInt(i)+","+PApplet.parseInt(drLook.dir)+","+PApplet.parseInt(drLook.posx) + "," + PApplet.parseInt(drLook.posy) + "," + stratt  + strppx  + strppy ;
  }
  saveStrings(dataPath("ModulePositions"+reload+".txt"), lines);
  SaveTracks();
  reconfigModules=false; //Switching to draw the tracks
  traceTracks=true;     //switching to draw the tracks
}
//////////////////////////////////////////////////
public void ReloadModules() { //reload the module XY-positions .
  driverList.clear();
  String[] lines = loadStrings("ModulePositions"+reload+".txt");

  String[] mappings = loadStrings("MappingSwitches"+reload+".txt");

  String[] mapPieces = split(mappings[0], ',');

  for (int i=0; i<mapPieces.length; i++) {
    swiMap[i]=PApplet.parseInt(mapPieces[i]);
    print(PApplet.parseInt(mapPieces[i])+",");
  }     

  for (int i=0; i<lines.length; i++) {
    println(lines[i]);
    String[] pieces = split(lines[i], ',');
    String[] arrPieces = split(lines[i], ';');

    int []aat={0, 0, 0, 0, 0, 0, 0, 0}; //setup an array for ording the positions on the driver wheel being dropped
    float []pppx={0, 0, 0, 0, 0, 0, 0, 0};
    float []pppy={0, 0, 0, 0, 0, 0, 0, 0};

    if (pieces.length == 5) {
      int n = PApplet.parseInt(pieces[0]);
      int d = PApplet.parseInt(pieces[1]);
      int x = PApplet.parseInt(pieces[2]) ;
      int y = PApplet.parseInt(pieces[3]) ;
      print( "direction: "+d);
      for (int j=0; j<8; j++)
      {
        aat[j]= PApplet.parseInt(arrPieces[j+1]);
        print(aat[j]+",");

        pppx[j]=PApplet.parseFloat(arrPieces[j+8]);
        pppy[j]=PApplet.parseFloat(arrPieces[j+16]);
        //   }
      }


      println("mod-" + n +" - "+aat[0]+" ;"+ pppx[0] +";"+ pppy[0] );

      if (d==1) driverList.add(new driver (n, true, x, y, aat, pppx, pppy));
      else  driverList.add(new driver (n, false, x, y, aat, pppx, pppy));

      // driverList.add(new driver (1,true, 300, 300,a,ppx,ppy));
    }
  }
  RecreateSwitches();
  reconfigModules=false;
  traceTracks=true;
}
public void mouseReleased() {
  if (mouseButton==RIGHT && driverList.size()!=selectedModule && driverList.size()!=1) { //-1  if its a right mousebutton then the selected module is deleted
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
    saveStrings(dataPath("MappingSwitches"+reload+".txt"), lines); 
    
  }
  }
}
//////////////////////////////////////////////
public void printTextCommands() {
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
public void keyPressed() {
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
public void mouseWheel(MouseEvent event) {
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
public void textOnScreen(String printThis) {
  text(printThis, 10, newLine);
  newLine+=20;
}

public void serialEvent (Serial myPort) {
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

public void sendValues() {

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

public void sendValuesMap() {
 if (connected) {  
      myPort.write("m"+servoString); 
      myPort.write('\n'); 
      myPort.clear();
    }
    println("pos: "+goToPos+"s"+servoString); 
    oldServoString=servoString;
}

int swiTest=0;
int once=0;
////////////////////////////////////////////////////////////
class swi {
  int amoL;
  int amoR;
  int bmoL;
  int bmoR;
  int pos;
  boolean rl;
  float posx;
  float posy;
  swi ( int amr, int aml, int bmr, int bml, int po, boolean r, float px, float py) {
    amoR=amr;
    amoL=aml;
    bmoR=bmr;
    bmoL=bml;
    pos=po;
    rl=r;
    posx=px;
    posy=py;
  }
}
//////////////////////////////////////////////////////////////////////////////////////////////////
public void FindLoneDrivers() {  //'the third lonely switch exception' - checking if there are other drivers within range of the swiLook (instead of driver connected to driver finding pairs) - then check if any of them are already known to the switch (amoR amoL bmoR bmorL)
  println("FindLoneDrivers");
  float lgtDia=sqrt((driverSize*driverSize)*2);
  for (int s=0; s<swiList.size(); s++) {
    swi swiLook = (swi) swiList.get(s);
    //println("s");
    for (int d=0; d<driverList.size(); d++) {
      driver drLook = (driver) driverList.get(d);
     // println(" d: ");
      float distance = dist(drLook.posx, drLook.posy, swiLook.posx, swiLook.posy);
     // print(distance);
      
      if ((lgtDia/2)-1 < distance && distance < (lgtDia/2)+1) { //the driverlook is within distance 
        //print("w");
        if (swiLook.amoR == drLook.mod || swiLook.amoL == drLook.mod || swiLook.bmoR == drLook.mod || swiLook.bmoL == drLook.mod) //the driver IS part of the switch already
        {
       //   println("finding some within distance");//do nothing
        } else //the driver IS NOT part of the switch - that means it is lone, and must be given the drLook as either bmoR or bmoL
        {
          println("finding exception loner driver: "+ d+ "swiLook.bmoL: " +swiLook.bmoL);
          //finding out which of the amoL or amoR are closest : this must be the opposite L/R as the closest away
          
          driver driAmoL = (driver) driverList.get(swiLook.amoL);
          driver driAmoR = (driver) driverList.get(swiLook.amoR);
          
          float distAmoL = dist(drLook.posx, drLook.posy, driAmoL.posx, driAmoL.posy);
          float distAmoR = dist(drLook.posx, drLook.posy, driAmoR.posx, driAmoR.posy);
          
          println("dist from mod:"+drLook.mod+ " to " +swiLook.amoL + " is: " +distAmoL); //printing the distances for check .. they should be one driver size (2xr) for the closest : 100
          println("dist from mod:"+drLook.mod+ " to " +swiLook.amoR + " is: " + distAmoR);
          
          if (distAmoL<distAmoR){ swiLook.bmoR=d ; swiLook.bmoL=-1;}//if the opposite module closest is a'left' then this must be to the'right' respectively of the switch
          else {swiLook.bmoL=d ; swiLook.bmoR=-1;}
        }
      }
    }
  }
}
////////////////////////////////////////////////////////////////////
public void mapSwitches() {
  /*for all switches possible turn one at the time from 1-0
   as one is turning, either click on the switch turning
   (choose the nearest to cursor and select)
   else click next or previous
   
   an array is saved which sets the offset of each switch. ie. number 3 switch in the computer is in fact attached to slot number 8 on the machine, then the array is +5. if 9th switch in program is 4th slot the mapping array is -5
   This mapping array is then introduced at the switch-string
   */
  textOnScreen("MAPPING SWITCHES");
  textOnScreen("Click on the twitching switch");
  textOnScreen("make sure the red mark matches the twitching switch");
  char data[] = new char[34];
  for (int i =0; i<34; i++) {
    if (once==0 && millis()%1000>501) {
      sendValuesMap(); 
      once=1;
    }//to just send once every 500ms
    if (i==swiTest && millis()%1000<499) {
      if (once==1) { 
        sendValuesMap();
        once=0;
      } //to just send once every 500ms
      data[i] = '1'; //every half second, the chosen 'tester' is set to '1'
      for (int m=0; m<swiList.size(); m++) {
        if (   m+swiMap[m] == swiTest) {
          swi swiLook = (swi) swiList.get(swiTest-swiMap[m]);
          fill(255, 0, 0);
          ellipse(swiLook.posx, swiLook.posy, 48, 48);
        }
      }
    } else data[i] = '0';
  }
  getClosestSwitch();
  servoString= new String(data);
}

////////////////////////////////////////////////////////////////////////////////////////////////////
public void ListSwitches () {
  for (int s=0; s<swiList.size(); s++) {
    swi swiLook = (swi) swiList.get(s);
    println("swi:"+s+" amod(RL): "+swiLook.amoR+","+swiLook.amoL+" bmod(RL): "+swiLook.bmoR+","+swiLook.bmoL+" pos: "+swiLook.pos+"");
  }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public void RecreateSwitches() {
  swiList.clear();
  println("CLEARING: "+swiList.size());
  for (int d=0; d<driverList.size(); d++) {
    FindNeighbourModules(d);
  }
   FindLoneDrivers();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////
public void DrawSwitches () {
  for (int s=0; s<swiList.size (); s++) {
    swi swiLook = (swi) swiList.get(s);
    noFill();
    if (swiLook.rl) stroke(10, 255, 10); 
    else stroke(255, 10, 10);

    strokeWeight(driverSize/5);
    point(swiLook.posx, swiLook.posy); //the white driver main circle
    stroke(0);
    fill(0);
    if (9<s) text(s, swiLook.posx-9, swiLook.posy+6);
    else text(s, swiLook.posx-5, swiLook.posy+6);
  }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
public void saveSwitches() {
  char[] swiAsChars = new char[32]; //myName.toCharArray();
  for (int i =0; i<32; i++) { 
    swiAsChars[i]=0;
  }
  for (int i = 0; i < swiList.size (); i++) {
    if (swiMap[i]!=100) {
      swi swiLook= (swi) swiList.get(i);
      if (swiLook.rl) {
        swiAsChars[i+swiMap[i]] = '1';
      } else {
        swiAsChars[i+swiMap[i]] = '0';
      }
      //  swiAsChars[i+swiMap[i]] = char(byte(swiLook.rl));
      ///print(char(byte(swiLook.rl))+"here");
      swiLines[goToPos%1533] = String.valueOf(swiAsChars);
      servoString =  String.valueOf(swiAsChars);
      // println(servoString);
    } else { 
      println("THERE ARE UNMAPPED SWITCHES: " + i);
      swiTest=i;
    }
  }
}
//////////////////////////////////////////////////////////////////////////////////////////////
public void getClosestSwitch() {

  float distMin=10000;
  for (int i=0; i<swiList.size(); i++) {
    swi swiLook = (swi) swiList.get(i);  

    float distCS=dist(mouseX, mouseY, swiLook.posx, swiLook.posy);
    if (distCS<distMin) {
      distMin=distCS; 
      closestSwitch=i;
    }
  }
  swi swiClose = (swi) swiList.get(closestSwitch);
  noFill();
  strokeWeight(3);
  stroke(0, 200, 0);
  ellipse(swiClose.posx, swiClose.posy, 28, 28);
  //return closestSwitch;
}
//this file is to design tracks

public void SelectTrackModule() {
  //Find the closest driver module and make it the public 'SelectedModule'/////////////////////////////////////////////////////////////////////////
  float distance=10000;
  float distanceNear=10000;
  int closestMod=0;
  for (int j =0; j<driverList.size (); j++) {
    driver drLook = (driver) driverList.get(j);
    distance= dist(mouseX, mouseY, drLook.posx, drLook.posy-driverSize);
    if (distanceNear>distance) {
      distanceNear=distance; 
      closestMod=j;
    }
    selectedModule=closestMod;
  }
}
/////////////////////////////////////////////////
public void TrackModuleAdd() {//adds a track number to the active instance[n] of the double array of tracks [][]
  int nextSpot=2;
  for (int i=2; i<modulesInTracks; i++) { //look throught the track array
    if (tracks[curDrawTrack][i]==-1&&tracks[curDrawTrack][i-1]!=-3) {    //Now moved up to the end of the specified track
      nextSpot=i;  //And the next spot to insert the selected module number is over that -1 value
    }
  }
  if (tracks[curDrawTrack][nextSpot-1]!=-1 && connectionTest(selectedModule, tracks[curDrawTrack][nextSpot-1])) {

    tracks[curDrawTrack][nextSpot]=selectedModule;
    tracks[curDrawTrack][nextSpot+1]=-1; //moving up the 
    tracks[curDrawTrack][nextSpot+2]=-3;
    SaveTracks();
    printTracks();
  } else if (tracks[curDrawTrack][3]==-1 && !connectionTest(selectedModule, tracks[curDrawTrack][nextSpot-1])) {
    println("Start of track moved to module "+selectedModule );
    tracks[curDrawTrack][2]=selectedModule;
    tracks[curDrawTrack][3]=-1;
    println(tracks[curDrawTrack][2]);
    printTracks();
  } else { 
    println("This module is not connected to the previous, plz connect the inbetween");
  }
}
////////////////////////////////////////////////////
public void TrackModuleRemove() {//adds a track number to the active instance[n] of the double array of tracks [][]
  int nextSpot=0;
  for (int i=2; i<modulesInTracks; i++) { //look throught the track array
    if (tracks[curDrawTrack][i]==-1) {    //Now moved up to the end of the specified track
      nextSpot=i;  //And the next spot to insert the selected module number is over that -1 value
    }
  }
  if (tracks[curDrawTrack][nextSpot-2]!=-1) {
    tracks[curDrawTrack][nextSpot-1]=-1; //moving up the 
    tracks[curDrawTrack][nextSpot]=-3;
    SaveTracks();
    printTracks();
  }
}
//////////////////////////////////////////////////
public void SaveTracks() {
  String[] lines = new String[numberOfTracks];

  for (int i=0; i<numberOfTracks; i++) {
    lines[i]="-1";
    for (int j=2; j<modulesInTracks; j++) {
      if (tracks[i][j]!=-3) lines [i]=lines[i]+','+tracks[i][j];
      else {
        tracks[i][1]=-1;
      }
    }
  }
  saveStrings(dataPath("TracksDrawn"+reload+".txt"), lines);
}
//////////////////////////////////////////////////
public boolean connectionTest(int driA, int driB) { //check if two drivers are connected
  boolean foundConnection=false;

  for (int s=0; s<swiList.size(); s++) {//find the swi(s) connected to driA
    swi swiLook = (swi) swiList.get(s);
    if (swiLook.amoR==driA||swiLook.amoL==driA) { //if the switch connects to driA
      if (swiLook.amoR==driB||swiLook.amoL==driB) { //AND if the switch also connects to driB
        foundConnection=true;
      }
    }
    if (swiLook.bmoR==driA||swiLook.bmoL==driA) { //if the switch connects to driA
      if (swiLook.bmoR==driB||swiLook.bmoL==driB) { //AND if the switch also connects to driB
        if (driA!=driB) {//if its not the same module itself)
          foundConnection=true;
        }
      }
    }
  }
  if (foundConnection) return true;
  else return false;
}

//////////////////////////////////////////////////
public void  DrawTracks() {  //draws the lines from module center to module center (active instance ends b ya line to ,mouse x,y) 
  if (true) {
    for (int j =0; j<numberOfTracks; j++) {//8tracks int j =curDrawTrack;
      for (int i=2; i<modulesInTracks-2; i++) { //
        if (tracks[j][i]>-1 && tracks[j][i-1]!=-1 && driverList.size()>tracks[j][i]&& driverList.size()>tracks[j][i-1]) {
          //  println(tracks[j][i-1]+",");
          driver drLook = (driver) driverList.get(tracks[j][i]);
          driver drLookBack = (driver) driverList.get(tracks[j][i-1]);  // this line calls an object in a -1

          if (driverList.size()>tracks[j][i]) { //if exists the drivers we are drawing tracks on top of -as to be sure to avoid crash
            fill (233);
            float offset=80*(j-curDrawTrack);
            if (j==curDrawTrack) { 
              // stroke(255);
              stroke( trackColors[j]);
            //  stroke((150+j*69*20)%255, j*69*20%255, 255-j*69%255);
              strokeWeight(12);
              line( drLookBack.posx, drLookBack.posy+offset, drLook.posx, drLook.posy+offset); 

              if (tracks[j][i+1]==-1&&connectionTest(tracks[j][i], tracks[j][2])) {
                ;
                textOnScreen("Track "+j+" CLOSED");
                driver drLookFirst = (driver) driverList.get(tracks[j][2]);
                stroke(0, 110, 0);
                line(drLook.posx, drLook.posy, drLookFirst.posx, drLookFirst.posy);
                 stroke( trackColors[j]);
              } else if (tracks[j][i+1]==-1) textOnScreen("Track "+j+" NOT closed");
            } else { 
              stroke(130); 
              strokeWeight(3);
            }

            float factor= 0.1f;
            int movex=width-100;
            int movey=2+j*100;
           
            strokeWeight(2);
            line( drLookBack.posx*factor+movex, drLookBack.posy*factor+movey, drLook.posx*factor+movex, drLook.posy*factor+movey); // drLookBack.posx, drLookBack.posy
            noFill();
            rect(movex, movey, 95, 95);
            if ( tracks[j][i]==tracks[j][2]) {
              driver drLookFirst = (driver) driverList.get(tracks[j][2]);
              line( drLookFirst.posx, drLookFirst.posy, drLook.posx, drLook.posy);
            }
          }
        }
        if (tracks[j][i+1]==-1 && j==curDrawTrack && tracks[j][i]>-1 && driverList.size()>tracks[j][i]) { //draw to the mouse or to the first start of the track
          driver drLook = (driver) driverList.get(tracks[j][i]);
          if (mode==2) line( drLook.posx, drLook.posy, mouseX, mouseY);
        }
      }
    }
  }
}

//LOAD into arraylists instead

public void LoadTracks() {  //LOAD all tracks initialized from textfile
  //first clear the tracks
  for (int j=0; j<numberOfTracks; j++) {
    for (int i=0; i<modulesInTracks; i++) { 
      tracks[j][i]=-3;
    }
  }
  String[] lines = loadStrings("TracksDrawn"+reload+".txt");
  //int remainTrack=numberOfTracks-lines.length;
  for (int i=0; i<numberOfTracks; i++) { //lines.length
    if (i<lines.length) {
      String[] pieces = split(lines[i], ',');

      tracks[i][0]=-3; //first is a 0 -used for change of direction
      for (int j=0; j<pieces.length; j++) {
        tracks[i][j+1]= PApplet.parseInt(pieces[j]);
      }

      for (int j=pieces.length; j<modulesInTracks-1; j++) {
        tracks[i][j+1]= -3; //the rest of the array is filled with 0
      }
    } else {
      for (int j=0; j<modulesInTracks; j++) {
        tracks[i][j]= -3; //the rest of the array is filled with 0
      }
    }
    for (int j =0; j<modulesInTracks; j++) 
      print(tracks[i][j]+",");
    println();
  }
}
///////////////////////////////////////////
public void  printTracks() {
  for (int i=0; i<numberOfTracks; i++) {
    for (int j=0; j<modulesInTracks; j++) {
      print(tracks[i][j]+",");
    }
    println();
  }
  println();
}






//tracks are found in a textfile
//and look like this:
//-1 means circular
//-2 means bounce and change direction //this seems dangerous though...
/*

 -1,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,-1
 -1,1,2,3,4,5,6,7,8,-1
 -1,9,10,11,12,13,14,15,16,-1
 -1,8,1,2,3,4,5,6,7,8,9,16,15,14,13,12,11,10,9,-1
 -1,16,1,2,3,4,5,6,7,8,1,16,15,14,13,12,11,10,9,-1
 
 */
//a car's purpose is to move about the configuration according to its path. Any one driver will at all times stay in the position it starts in. so a car starting in a position 2 will remain in a dynamic position two.. //<>// //<>// //<>// //<>// //<>// //<>// //<>// //<>// //<>//

//all drivers have three types of 'positions' absolute or static, that is starting from most right (+x,0),
// and rotated which is the ((static+offset)*direction) given by the connections to other drivers, 
//meaning they always meet adjacent drivers at the same dynamic position, and their sprocket like connection giving the change of direction, 
//and finally the dynamic which is dynamic=(static+offset)+rotation*direction..

//heuristics for a driver is to move it along a 'track'. 
//Any one or more connected modules can make up a track. 
//(out of 3 driver modules A,B,C tracks can be ABC AB BC A B C. From ABCD we can make tracks ABCD ABC BCD CDA DAB AB BC CD DA A B C D. All tracks can be traversed in both directions.

class car {
  // states
  int name;
  int module; //can be one of the 16 modules (or more)
  int pos; //can be 1-8 in the module
  float posx;
  float posy;
  int track;
  int dir; //this is +1 or -1 for easy use
  int nxtmod;
  int col;  ///
  int trpos;
  // constructor
  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, int co) {
    name=na;
    module = mo;
    pos = po;
    posx=pox;
    posy=poy;
    track=tra;
    dir=di;
    nxtmod=nxtm;
    col=co;
    trpos=trp;
  }
}//end CLASS
//////////////////////////////////////////////////////////////////////////////////////////////////////
public boolean trackContainsModule(int module) {
  boolean check=false;
  for (int i =0; i<modulesInTracks; i++) {
    if (tracks[curDrawTrack][i]==module) check=true;
  }
  return check;
}
/////////////////////////////////////////////////////////////////////
public void carAdd(int dir) {
  if (trackContainsModule(selectedModule)) {
    int selPos=SelectClosestPosition();
    boolean doRemove=false;
    int carToRemove=-1;
    for (int i =0; i<carList.size(); i++) { //running through cars to find out if there is a car in the position already
      car carLook = (car) carList.get(i);
      if (carLook.module==selectedModule&&carLook.pos==selPos)
      { 
       doRemove=true;
       carToRemove=i;
       println("Car: "+i+" in mod: "+selectedModule+" pos: "+selPos);
      }
      }
    if(doRemove) carList.remove(carToRemove);
     carList.add(new car (carList.size()+1, selectedModule, selPos, 0, 0, curDrawTrack, dir, 1, 2, trackColors[curDrawTrack]));
     
    //print("calling addtrace");
    addTraceToCar(carList.size());
    //  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
    IniCars();
  } else {
    println("The module is not part of the track, try another");
    driver drLook = (driver) driverList.get(selectedModule);
    fill(255, 0, 0);
    noStroke();
    ellipse(drLook.pox[SelectClosestPosition()], drLook.poy[SelectClosestPosition()], 12, 12);
  }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////
public void InsertCars() {
  for (int i=2; i<modulesInTracks; i+=2) //going up to the 25 as max numbers of a track definition
  {

    if (tracks[curDrawTrack][i]!=-3 && tracks[curDrawTrack][i]!=-1) {
      print("placing into track: "+ tracks[curDrawTrack][i]);
      int j=curDrawTrack;

      carList.add(new car (carList.size()+1, tracks[curDrawTrack][i], curDrawTrack%8, 0, 0, j, 1, 1, 2, trackColors[curDrawTrack]));//  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
      addTraceToCar(carList.size());

      carList.add(new car (carList.size()+1, tracks[curDrawTrack][i], (curDrawTrack+4)%8, 0, 0, j, 1, 1, 2, trackColors[curDrawTrack]));//  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
      addTraceToCar(carList.size());

      carList.add(new car (carList.size()+1, tracks[curDrawTrack][i], (curDrawTrack+2)%8, 0, 0, j, -1, 1, 2, trackColors[curDrawTrack]));//  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
      addTraceToCar(carList.size());

      carList.add(new car (carList.size()+1, tracks[curDrawTrack][i], (curDrawTrack+6)%8, 0, 0, j, -1, 1, 2, trackColors[curDrawTrack]));//  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
      addTraceToCar(carList.size());
    }
  }
  IniCars();
}
/////////////////////////////////////////////////////////////////////
public void ClearCars() {
  for (int j=0; j<20; j++) {
    for (int i =0; i<carList.size(); i++) {
      car carLook = (car) carList.get(i);
      if (curDrawTrack==carLook.track) {
        carList.remove(i);
      }
    }
  }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////
public void IniCars() {  //this is run once from the Setup()
  for (int c = 0; c < carList.size (); c++) { //take a car
    car carLook = (car) carList.get(c);
    for (int i=modulesInTracks-1; i>1; i--) { //not going so far as to look at the last one
      if (tracks[carLook.track][i]==carLook.module) { 
        carLook.trpos=i;
        carLook.nxtmod=tracks[carLook.track][i+carLook.dir];
      }
    }
    // println("cMod "+ carLook.module + " pos: "+ carLook.pos + " dir: "+ carLook.dir + " trpos: " +carLook.trpos+" nxtModule: "+ carLook.nxtmod);
  }
}
///////////////////////////////////////////////////////
public void moveCarsToTrack(int fromTrack) { //cars are moved from their current track to the selected track by the keypress '0'...'7' 
  for (int i=0; i<carList.size(); i++) {
    car carLook=(car) carList.get(i);
    boolean carIsInTrack = false;
    for (int m=0; m<modulesInTracks; m++) {
      if (tracks[curDrawTrack][m]==carLook.module) carIsInTrack=true;
    }
    if (carLook.track==fromTrack && carIsInTrack) 
    {
      carLook.track=curDrawTrack;
      carLook.col= trackColors[curDrawTrack];
    }
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
public void EndTrackHandle () { //we have aligned the current location with track of the driver already. carLook.trpos is correct
  boolean doPrint=false;
  for (int c = 0; c < carList.size (); c++) { //take every car
    car carLook = (car) carList.get(c);


    //THIS WORKS FOR CYCLIC TRACKS ONLY
    if (carLook.nxtmod==-1 && carLook.dir==-1) { //means its the start of the track (as noted in the file) 
      //find the last module in that track looking from the back and up
      int i=23;
      while (tracks[carLook.track][i] != -1) { 
        i--; 
        if (doPrint) println(i);
      } //not going so far as to look at the last one
      carLook.trpos=i;//+carLook.dir;
      carLook.nxtmod=tracks[carLook.track][carLook.trpos+carLook.dir];
      if (doPrint)print("StartOfTrack: "+ carLook.name );
      if (doPrint)println(" cMod "+ carLook.module + " pos: "+ carLook.pos + " dir: "+ carLook.dir + " trpos: " +carLook.trpos+" nxtModule: "+ carLook.nxtmod);
    } else if (carLook.nxtmod==-1 && carLook.dir==1) { //means its the end of the track (as noted in the file) 
      //carLook.dir=-1; //switch direction
      //and take the second (or third value in the tracks array
      carLook.nxtmod=tracks[carLook.track][2];  /////////////////////////////////Start from the beginning of the track (in this case that is index 2 cause [0]:-3 and [1]:)
      carLook.trpos=1;                                     ////////////////////////////////////////////////HERE MAY CAUSE A BUG
      if (doPrint)println("EndOfTrack: "+"nxt: "+carLook.nxtmod);
    } else { 
      if (doPrint)println("NotAtEnd: "+ carLook.name + " cMod "+ carLook.module + " pos: "+ carLook.pos + " dir: "+ carLook.dir + " trpos: " +carLook.trpos+" nxtModule: "+ carLook.nxtmod);
    }
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
public void StepCarInModulesAndSetSwitches () {/////////////////////////////////////////////////////////////////////////////////////////////////////
  for (int c = 0; c < carList.size (); c++) { //take a car
    car carLook = (car) carList.get(c);
    int cPos=carLook.pos;
    int cMod=carLook.module;
    int nMod=carLook.nxtmod;
    for (int s=0; s<swiList.size (); s++) { //go through all switches
      swi swiLook = (swi) swiList.get(s);
      if  (swiLook.pos==cPos) { //If a switch is at that position

        boolean hasSwitched=false; 
        if (swiLook.amoL==cMod || swiLook.amoR == cMod) { //to avoid an ipossible jump across the switch

          if (nMod==swiLook.amoR) {
            swiLook.rl=true; //turning the switch to go left.
            carLook.module=swiLook.amoR; 
            hasSwitched=true;
          } else if (nMod==swiLook.amoL) {
            swiLook.rl=false; //turning the switch to go left.
            carLook.module=swiLook.amoL;
            hasSwitched=true;
          }
          if (hasSwitched==false) { //to make sure we stay in the module
            if (swiLook.amoL==cMod) swiLook.rl=false;
            else if (swiLook.amoR==cMod) swiLook.rl=true;

            //  println("inside 'hasSwitched for the a side");
          }
        } //amol
        if (swiLook.bmoL==cMod || swiLook.bmoR == cMod) { //to avoid an ipossible jump across the switch

          if (nMod==swiLook.bmoR) {
            swiLook.rl=true; //turning the switch to go left.
            carLook.module=swiLook.bmoR;  
            hasSwitched=true;
          } else if (nMod==swiLook.bmoL) {
            swiLook.rl=false; //turning the switch to go left.
            carLook.module=swiLook.bmoL;          
            hasSwitched=true;
          }
          if (hasSwitched==false) {  //to make sure we stay in the module
            if (swiLook.bmoL==cMod) swiLook.rl=false;
            else if (swiLook.bmoR==cMod) swiLook.rl=true;
            // println("inside 'hasSwitched for the b side");
          }
        }//bmol

        if (hasSwitched && carLook.dir==1) {
          print("stepping and has switched. nxtmod: "+ carLook.nxtmod +" trpos: "+carLook.trpos );
          carLook.trpos++;
          carLook.nxtmod = tracks[carLook.track][carLook.trpos+carLook.dir];
          println("+ Updated nxtmod: "+ carLook.nxtmod +" trpos: "+carLook.trpos );
        } 
        if (hasSwitched && carLook.dir==-1) 
        {   
          carLook.trpos--;
          carLook.nxtmod = tracks[carLook.track][carLook.trpos+carLook.dir];
        }
      } //end correct position
    } //all swi
  } //all cars
}
//////////////////////////////////////////////////////////////////////////////////////////////////////
public void StepCarInDriver() { //moves through the positions 1-7 on drivers
  for (int c = 0; c < carList.size (); c++) { //take a car
    car carLook = (car) carList.get(c);

    if (carLook.pos==7) carLook.pos=0; //rotate overlap on the modules
    else carLook.pos++;
  }
}
/////////////////////////////////////////////////////////////////////////////////////
public void DrawCars() {/////////////////////////////////////////////////////////////////////////////////////////////////////
  for (int i=0; i<carList.size (); i++) {
    car carLook = (car) carList.get(i);
    fill(carLook.col);
    noStroke();
    ellipse(carLook.posx, carLook.posy, 33, 33);
    fill(0);
    if (carLook.dir>0) text(carLook.track+"+", carLook.posx-5, carLook.posy+5);
    else  text(carLook.track+"-", carLook.posx-5, carLook.posy+5);
  }
}
/////////////////////////////////////////////////////////////////////////////////////
public void LocateCars() {/////////////////////////////////////////////////////////////////////////////////////////////////////

  for (int c = 0; c < carList.size (); c++) { //take a car
    car carLook = (car) carList.get(c);
    driver driverLook = (driver) driverList.get(carLook.module);
    carLook.posx = driverLook.pox[carLook.pos] ;
    carLook.posy = driverLook.poy[carLook.pos] ;
  }
}

/////////////////////////////////////////////////////////////////////////////////////
public void Collision() {/////////////////////////////////////////////////////////////////////////////////////////////////////
  for (int i=0; i<carList.size (); i++) {
    car carLookI = (car) carList.get(i);
    // println("checking these: "+i+"Mod"+carLookI.module+"Pos"+carLookI.pos+"AND:"+j+"Mod"+carLookJ.module+"Pos"+carLookJ.pos);
    for (int s=0; s<swiList.size (); s++) { //go through all switches
      swi swiLook = (swi) swiList.get(s);
      if  (swiLook.pos==carLookI.pos) { //at the position of some switches
        if (swiLook.amoL==carLookI.module || swiLook.amoR == carLookI.module ) //now the car is at a switch
        { 
          for (int j=i+1; j<carList.size (); j++) {
            car carLookJ = (car) carList.get(j); //going through all cars(except this) to see if any are in same location.
            if  (swiLook.pos==carLookJ.pos) { //if Jcar is at the position of a switch
              if (swiLook.amoL==carLookJ.module || swiLook.amoR == carLookJ.module) { //if Jcar is at the same module too its a collision
                totalCollisions++;
                //println("COLLISSION on A-side");
                carList.remove(j);
                println("COLLISION, car: "+carLookI.name+" and: "+carLookJ.name+"crashed on module: "+carLookJ.module+" pos: "+carLookJ.pos+" totalCollisions:"+totalCollisions+" totalLeft: "+carList.size()+".....................................C");
              }
            }
          }
        }
        if (swiLook.bmoL==carLookI.module || swiLook.bmoR == carLookI.module ) //now the car is at a switch
        { 
          for (int j=i+1; j<carList.size (); j++) {
            car carLookJ = (car) carList.get(j); //going through all cars(except this) to see if any are in same location.
            if  (swiLook.pos==carLookJ.pos) { //if Jcar is at the position of a switch
              if (swiLook.bmoL==carLookJ.module || swiLook.bmoR == carLookJ.module) {
                totalCollisions++;
                println("COLLISSION on A-side");
                carList.remove(j);
                println("COLLISION, car: "+carLookI.name+" and: "+carLookJ.name+"crashed on module: "+carLookJ.module+" pos: "+carLookJ.pos+" totalCollisions:"+totalCollisions+" totalLeft: "+carList.size()+".....................................C");
              }
            }
          }
        }
      }
    }
  }
}
//this is a feature to look ahead and see if anything will collide after a shift.
//the idea is to make placeholders for the carriers -  a set of ghost carriers, that are then moved ahead without actually sending any commands to the machine..
//

class trace {

  int carrier;
  int [] pox=new int[200];
  int [] poy=new int[200];
  int [] tra=new int[200];
 

  trace(int ca, int []px, int []py, int[] tr) {
    carrier=ca;
    pox=px;
    poy=py;
    tra=tr; //the track the car was associated with at the traced point
   
  }
}


//every time we .add a trace we initiate the three poxpoypoz with 0values, then these can be overwritten as the car is moving.
public void addTraceToCar(int addtoCar) {
print("trying to add trace");
  int insert=0;
  int []poix=new int[200];
  int []poiy=new int[200];
  int [] trai=new int[200];

  for (int i=0; i<200; i++) {
    poix[i]=insert;
    poiy[i]=insert;
    trai[i]=curDrawTrack;
  }

  tracesList.add(new trace(addtoCar, poix, poiy, trai));
}


public void updateTraces() {
  //go through all traces in the list and look for the carrier with the same name,
  //then use the position of that carrier to assign values to the poxpoypoz[goToPos] of the trace
  //z values are redundant as the instance of the array is also giving the progression in height
  for (int t=0; t<tracesList.size(); t++) {
//       optPos opLook = (optPos) optPosList.get(i);
       trace traLook=(trace) tracesList.get(t);
    for (int i=0; i<carList.size(); i++) {
        car carLook = (car) carList.get(i);
        if(carLook.name==traLook.carrier){
          traLook.pox[goToPos%200]=PApplet.parseInt(carLook.posx);
          traLook.poy[goToPos%200]=PApplet.parseInt(carLook.posy);
        }
    }
  }
}
public void printTracesToFile(){

  String[] lines = new String[tracesList.size()];
print(tracesList.size()+" traces");
 for (int t=0; t<tracesList.size(); t++) {
     trace traLook=(trace) tracesList.get(t);
     String strposx="";
     String strposy="";
     String strposz="";
    // String strtra="";
     for(int i=0;i<200;i++){
        strposx=strposx.concat(";"+ Integer.toString(traLook.pox[i]));
        strposy=strposy.concat(";"+ Integer.toString(traLook.poy[i]));
        strposz=strposz.concat(";"+ Integer.toString(i));
       
       // strtra=strtra.concat(";"+ Integer.toString(traLook.tra[i]));
     }
      lines[t]=PApplet.parseInt(t)+","+strposx+","+strposy+","+strposz;//+","+strtra;
      }
       saveStrings(dataPath("TracesOutput.txt"), lines);
}
  public void settings() {  size(900, 900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "NewStart2017_46" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
