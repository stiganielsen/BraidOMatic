//////////////////////////////////////////
void SuggestDriverLocation(int current ) { //keeps a list of optional locations, starting from x,0 counting clockwise
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
void RotateOne() {
  rotation++;
  if (rotation>7) rotation=0;
}
//////////////////////////////////////////
void DrawNearestOptionalModule() { //Highlight nearest optional place in GREEN color or RED if not viable location/////////////////////////////////////////////////////////////////////////
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
      if (distClose<driverSize-.5) opLook.viable=false; //if another module is too close, the place is not viable
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
void DropInDriverModule() {//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
void FindNeighbourModules(int lookFrom) { //lists and prints the modules that are neighbours to the input 'lookFrom' module/////////////////////////////////////////////////////////////////////////
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
void DrawCurrentSelectedModule() {  //highlights the module that is selected to build upon/////////////////////////////////////////////////////////////////////////
  driver drLook = (driver) driverList.get(selectedModule); //later get a specific clicked one...
  strokeWeight(12);
  stroke(0, 122, 0);
  ellipse(drLook.posx, drLook.posy, driverSize, driverSize);
}



//////////////////////////////////////////
void DrawDrivers() { //drawing the drivers as white ellipses///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  for (int d=0; d<driverList.size (); d++) {
    driver drLook = (driver) driverList.get(d);
    noFill();
    if (drLook.dir) stroke(255);
    else stroke(200);

    strokeWeight(int(driverSize/25));
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

      strokeWeight(int(driverSize/4));
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
int SelectClosestPosition() { //from the positions available in the selected module
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
void SelectClosestModule() { //Find the closest driver module and make it the public 'SelectedModule'/////////////////////////////////////////////////////////////////////////
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
void SaveModulePositions() {
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
    lines[i] = int(i)+","+int(drLook.dir)+","+int(drLook.posx) + "," + int(drLook.posy) + "," + stratt  + strppx  + strppy ;
  }
  saveStrings(dataPath("ModulePositions"+reload+".txt"), lines);
  SaveTracks();
  reconfigModules=false; //Switching to draw the tracks
  traceTracks=true;     //switching to draw the tracks
}
//////////////////////////////////////////////////
void ReloadModules() { //reload the module XY-positions .
  driverList.clear();
  String[] lines = loadStrings("ModulePositions"+reload+".txt");

  String[] mappings = loadStrings("MappingSwitches"+reload+".txt");

  String[] mapPieces = split(mappings[0], ',');

  for (int i=0; i<mapPieces.length; i++) {
    swiMap[i]=int(mapPieces[i]);
    print(int(mapPieces[i])+",");
  }     

  for (int i=0; i<lines.length; i++) {
    println(lines[i]);
    String[] pieces = split(lines[i], ',');
    String[] arrPieces = split(lines[i], ';');

    int []aat={0, 0, 0, 0, 0, 0, 0, 0}; //setup an array for ording the positions on the driver wheel being dropped
    float []pppx={0, 0, 0, 0, 0, 0, 0, 0};
    float []pppy={0, 0, 0, 0, 0, 0, 0, 0};

    if (pieces.length == 5) {
      int n = int(pieces[0]);
      int d = int(pieces[1]);
      int x = int(pieces[2]) ;
      int y = int(pieces[3]) ;
      print( "direction: "+d);
      for (int j=0; j<8; j++)
      {
        aat[j]= int(arrPieces[j+1]);
        print(aat[j]+",");

        pppx[j]=float(arrPieces[j+8]);
        pppy[j]=float(arrPieces[j+16]);
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