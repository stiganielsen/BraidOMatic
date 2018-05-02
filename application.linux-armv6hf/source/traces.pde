
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
void addTraceToCar(int addtoCar) {
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


void updateTraces() {
  //go through all traces in the list and look for the carrier with the same name,
  //then use the position of that carrier to assign values to the poxpoypoz[goToPos] of the trace
  //z values are redundant as the instance of the array is also giving the progression in height
  for (int t=0; t<tracesList.size(); t++) {
//       optPos opLook = (optPos) optPosList.get(i);
       trace traLook=(trace) tracesList.get(t);
    for (int i=0; i<carList.size(); i++) {
        car carLook = (car) carList.get(i);
        if(carLook.name==traLook.carrier){
          traLook.pox[goToPos%200]=int(carLook.posx);
          traLook.poy[goToPos%200]=int(carLook.posy);
        }
    }
  }
}
void printTracesToFile(){

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
      lines[t]=int(t)+","+strposx+","+strposy+","+strposz;//+","+strtra;
      }
       saveStrings("TracesOutput.txt", lines);
}