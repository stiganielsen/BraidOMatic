//this file is to design tracks

void SelectTrackModule() {
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
void TrackModuleAdd() {//adds a track number to the active instance[n] of the double array of tracks [][]
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
void TrackModuleRemove() {//adds a track number to the active instance[n] of the double array of tracks [][]
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
void SaveTracks() {
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
  saveStrings("TracksDrawn"+reload+".txt", lines);
}
//////////////////////////////////////////////////
boolean connectionTest(int driA, int driB) { //check if two drivers are connected
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
void  DrawTracks() {  //draws the lines from module center to module center (active instance ends b ya line to ,mouse x,y) 
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

            float factor= 0.1;
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

void LoadTracks() {  //LOAD all tracks initialized from textfile
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
        tracks[i][j+1]= int(pieces[j]);
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
void  printTracks() {
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