
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
void FindLoneDrivers() {  //'the third lonely switch exception' - checking if there are other drivers within range of the swiLook (instead of driver connected to driver finding pairs) - then check if any of them are already known to the switch (amoR amoL bmoR bmorL)
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
void mapSwitches() {
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
void ListSwitches () {
  for (int s=0; s<swiList.size(); s++) {
    swi swiLook = (swi) swiList.get(s);
    println("swi:"+s+" amod(RL): "+swiLook.amoR+","+swiLook.amoL+" bmod(RL): "+swiLook.bmoR+","+swiLook.bmoL+" pos: "+swiLook.pos+"");
  }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void RecreateSwitches() {
  swiList.clear();
  println("CLEARING: "+swiList.size());
  for (int d=0; d<driverList.size(); d++) {
    FindNeighbourModules(d);
  }
   FindLoneDrivers();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////
void DrawSwitches () {
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
void saveSwitches() {
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
void getClosestSwitch() {

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