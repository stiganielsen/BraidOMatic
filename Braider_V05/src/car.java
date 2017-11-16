public class car {
}


//a car's purpose is to move about the configuration according to its path. Any one driver will at all times stay in the position it starts in. so a car starting in a position 2 will remain in a dynamic position two.. //<>// //<>// //<>//

//all drivers have three types of 'positions' absolute or static, that is starting from most right (+x,0),
// and rotated which is the ((static+offset)*direction) given by the connections to other drivers,
//meaning they always meet adjacent drivers at the same dynamic position, and their sprocket like connection giving the change of direction,
//and finally the dynamic which is dynamic=(static+offset)+rotation*direction..

//heuristics for a driver is to move it along a 'track'.
//Any one or more connected modules can make up a track.
//(out of 3 driver modules A,B,C tracks can be ABC AB BC A B C. From ABCD we can make tracks ABCD ABC BCD CDA DAB AB BC CD DA A B C D. All tracks can be traversed in both directions.

class car { //<>//
    // states
    int name;
    int module; //can be one of the 16 modules (or more)
    int pos; //can be 1-8 in the module
    float posx;
    float posy;
    int track;
    int dir; //this is +1 or -1 for easy use
    int nxtmod;
    color col;  ///
    int trpos;
    // constructor
    car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
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
        // trpos=-1;
        // collide = co;
    }
}//end CLASS
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    void InsertCars() {
        // carList.add(new car (0,0,0,0,0,0,-1,100,2,color(255,0,0)));//  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
        carList.add(new car (1, 3, 0, 0, 0, 0, 1, 1, 2, color(255, 0, 0)));//  car(int na, int mo, int po, float pox, float poy, int tra, int di, int nxtm, int trp, color co) {
        IniCars();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    void IniCars() {  //this is run once from the Setup()
        for (int c = 0; c < carList.size (); c++) { //take a car
            car carLook = (car) carList.get(c);
            for (int i=modulesInTracks-1; i>1; i--) { //not going so far as to look at the last one
                if (tracks[carLook.track][i]==carLook.module) {
                    carLook.trpos=i;
                    carLook.nxtmod=tracks[carLook.track][i+carLook.dir];
                }
            }
            println("cMod "+ carLook.module + " pos: "+ carLook.pos + " dir: "+ carLook.dir + " trpos: " +carLook.trpos+" nxtModule: "+ carLook.nxtmod);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    void StepCarInDriver() { //moves through the positions 1-7 on drivers
        for (int c = 0; c < carList.size (); c++) { //take a car
            car carLook = (car) carList.get(c);

            if (carLook.pos==7) carLook.pos=0; //rotate overlap on the modules
            else carLook.pos++;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    void EndTrackHandle () { //we have aligned the current location with track of the driver already. carLook.trpos is correct
        for (int c = 0; c < carList.size (); c++) { //take every car
            car carLook = (car) carList.get(c);


            //THIS WORKS FOR CYCLIC TRACKS ONLY
            if (carLook.nxtmod==-1 && carLook.dir==-1) { //means its the start of the track (as noted in the file)
                //find the last module in that track looking from the back and up
                int i=23;
                while (tracks[carLook.track][i] != -1) { //not going so far as to look at the last one
                    i--;
                    if (tracks[carLook.track][i]==-1) {
                        carLook.nxtmod=tracks[carLook.track][carLook.trpos+carLook.dir];
                    }
                    carLook.trpos=i+carLook.dir;
                    print("SOT: ");
                    println("cMod "+ carLook.module + " pos: "+ carLook.pos + " dir: "+ carLook.dir + " trpos: " +carLook.trpos+" nxtModule: "+ carLook.nxtmod);
                }
            } else if (carLook.nxtmod==-1 && carLook.dir==1) { //means its the end of the track (as noted in the file)
                //carLook.dir=-1; //switch direction
                //and take the second (or third value in the tracks array
                carLook.nxtmod=tracks[carLook.track][2];  /////////////////////////////////HERE MAY BE WRONG  [x]
                carLook.trpos=2;                                     ////////////////////////////////////////////////HERE MAY CAUSE A BUG
                print("EOT: "+"nxt: "+carLook.nxtmod);
            } else {
                println("ELSE: cMod "+ carLook.module + " pos: "+ carLook.pos + " dir: "+ carLook.dir + " trpos: " +carLook.trpos+" nxtModule: "+ carLook.nxtmod);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    void StepCarInModulesAndSetSwitches () {/////////////////////////////////////////////////////////////////////////////////////////////////////
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
                            if (swiLook.amoL==cMod) swiLook.rl=false;
                            else if (swiLook.amoR==cMod) swiLook.rl=true;
                        }
                    }//bmol

                    if (hasSwitched && carLook.dir==1) {
                        carLook.trpos++;
                        carLook.nxtmod = tracks[carLook.track][carLook.trpos+carLook.dir];
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

    /////////////////////////////////////////////////////////////////////////////////////
    void DrawCars() {/////////////////////////////////////////////////////////////////////////////////////////////////////
        for (int i=0; i<carList.size (); i++) {
            car carLook = (car) carList.get(i);
            fill(255, 255, 5);
            noStroke();
            ellipse(carLook.posx, carLook.posy, 33, 33);
            fill(0);
            text(carLook.name, carLook.posx-5, carLook.posy+5);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////
    void LocateCars() {/////////////////////////////////////////////////////////////////////////////////////////////////////

        for (int c = 0; c < carList.size (); c++) { //take a car
            car carLook = (car) carList.get(c);
            //print("this cars module: " + carLook.module);
            driver driverLook = (driver) driverList.get(carLook.module);
            //  println("locate cars from module "+carLook.module);
            //    println("this cars module: " + carLook.module);
            carLook.posx = driverLook.pox[carLook.pos] ;
            carLook.posy = driverLook.poy[carLook.pos] ;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    void Collision() {/////////////////////////////////////////////////////////////////////////////////////////////////////
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