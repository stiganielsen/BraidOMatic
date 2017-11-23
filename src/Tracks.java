/*
public class Tracks {
}




//LOAD into arraylists instead

    void LoadTracks(){  //LOAD all tracks initialized from textfile
        String[] lines = loadStrings("TracksDrawn1.txt");
        //int remainTrack=numberOfTracks-lines.length;
        for (int i=0; i<lines.length; i++) {
            String[] pieces = split(lines[i], ',');

            tracks[i][0]=-3; //first is a 0 -used for change of direction
            for (int j=0; j<pieces.length; j++) {
                tracks[i][j+1]= int(pieces[j]);
            }
            for (int j=pieces.length; j<modulesInTracks-1; j++) {
                tracks[i][j+1]= -3; //the rest of the array is filled with 0
            }
            for (int j =0; j<modulesInTracks; j++) print(tracks[i][j]+",");
            println();
        }
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