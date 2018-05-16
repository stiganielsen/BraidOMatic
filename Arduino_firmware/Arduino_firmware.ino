#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>
#include <string.h>

//Define Stepper pins on the ramps 1.4 board
#define X_STEP_PIN         A0
#define X_DIR_PIN          A1
#define X_ENABLE_PIN       38
#define Y_STEP_PIN         A6
#define Y_DIR_PIN          A7
#define Y_ENABLE_PIN       A2
#define Z_STEP_PIN         46
#define Z_DIR_PIN          48
#define Z_ENABLE_PIN       A8
//Pin for the buzzer
#define BEEP_PIN           42

//PVM board for servos
Adafruit_PWMServoDriver pwm1 = Adafruit_PWMServoDriver(0x40);
Adafruit_PWMServoDriver pwm2 = Adafruit_PWMServoDriver(0x41);

int spd = 800; //speed in milliseconds
int maxRev = 200; //? steps per revolution
int dirX = 1;
int testB = 0;
String incoming;
int gotoPos;

//interuptpins for the encoder
const byte interruptPinA = 3;   //channel a or b
const byte interruptPinB = 2; //unused
const byte interruptPinC = 18;  //channel a or b
const byte interruptPinD = 19;  //channel x
const byte buttonPin = 14;

int pulseperrevolution = 2048;//resulution of the encoder
long cntRevolution = 0; // how many times we have seen new value
long oldRev = 0;
int state = 5;
int rotPos = 0; //Number between 0 and max steps for a fullrevolution
int oldRotPos = 0;
long globalPos; //Number of positions passed since start(encoderresolution/8)
long oldGlobalPos;
int rotState = 0; //the position within module (1-8)

int oldPos = 0;
int programState = 0;
int servoMax = 310;// max pulse length for the HKSCM9-6
int servoMin = 210;//min pulse length for the HKSCM9-6
int servoTestMin = 310;//260; //Setting it to mid
int servoTestMax = 210;//260; //Setting it to mid

int inComing = 0;
String servoSetTo = "";
String servoSetOld = "";

//number of Servers boards
const int Servocount = 32;
//Buffer for the steps
const int Stepbuffer = 100;
char strs[Stepbuffer][Servocount];
char strsMap[Servocount];
String inStringPos = "";
String inStringSer = "";
String inStringSerMap = "";
String inStringSerOld = "";
int goToPos = 0;
boolean thisWasP = false;
boolean thisWasS = false;
boolean thisWasH = false;
boolean thisWasM = false;

int newPos = 0;

//Offset of the gears rotation position
//Negative number moves the gears forward. closer to 0 moves gears backwards.
int positionOffset = -1000; //-1400SETTING THE ENCODER POSITION IN RELATION TO POS 1

void setup() 
{
	tone(BEEP_PIN, 5000, 900);
	//Pin setup for the Encoder
	pinMode(interruptPinA, INPUT_PULLUP); //3
	pinMode(interruptPinB, INPUT_PULLUP); //2 UNUSED
	pinMode(interruptPinC, INPUT_PULLUP); //18
	pinMode(interruptPinD, INPUT_PULLUP); //19
	pinMode(buttonPin, INPUT_PULLUP);

	attachInterrupt(digitalPinToInterrupt(interruptPinA), interruptA, FALLING);
	attachInterrupt(digitalPinToInterrupt(interruptPinD), interruptD, FALLING);

	Serial.begin(9600);
	//Serilze the PVM comminication for board 1, 2 and setting the puls to 60Hz 
	pwm1.begin();
	pwm1.setPWMFreq(60);
	pwm2.begin();
	pwm2.setPWMFreq(60);

	//Sets the pins for the stepper and beeper
	pinMode(X_STEP_PIN, OUTPUT);
	pinMode(X_DIR_PIN, OUTPUT);
	pinMode(X_ENABLE_PIN, OUTPUT);
	pinMode(Y_STEP_PIN, OUTPUT);
	pinMode(Y_DIR_PIN, OUTPUT);
	pinMode(Y_ENABLE_PIN, OUTPUT);
	pinMode(Z_STEP_PIN, OUTPUT);
	pinMode(Z_DIR_PIN, OUTPUT);
	pinMode(Z_ENABLE_PIN, OUTPUT);
	pinMode(BEEP_PIN, OUTPUT);

	digitalWrite(X_DIR_PIN, LOW); //set direction opposite
	digitalWrite(Y_DIR_PIN, LOW); //set direction opposite
	digitalWrite(X_ENABLE_PIN, LOW);
	digitalWrite(Y_ENABLE_PIN, LOW);

	//Forloop may not be needed
	for (int i = 0; i < 680; i++) 
	{
		moveStepper();
		tone(BEEP_PIN, 2000, 9);
	}
	Serial.println(globalPos % 8);
	int resetpos = globalPos % 8;

	//test all SERVOES, flip them all at once, and then once at the time.
	testServos("00000000000000000000000000000000");
	servos();
	delay(500);
	testServos("11111111111111111111111111111111");
	servos(); delay(500);
	testServos("10101010101010101010101010101010");
	servos(); delay(500);
	testServos("01010101010101010101010101010101");
	servos();delay(500);
	testServos("111111111111111111111111111111");
	delay(100); tone(BEEP_PIN, 4000, 249);

	while (resetpos != 1) 
	{
		moveStepper();
		resetpos = (globalPos % 8);
		delayMicroseconds(400);
		Serial.println(globalPos % 8);
	}
	Serial.println(globalPos % 8);
	digitalWrite(X_ENABLE_PIN, HIGH);//motors off
	digitalWrite(Y_ENABLE_PIN, HIGH);//motors off
	cntRevolution = pulseperrevolution + positionOffset;
	goToPos = 0;	
}

void loop () 
{
	receiveData();
	if (globalPos < goToPos) 
	{	//move the steppers to position
		if (newPos != globalPos ) 
		{
			servos(); //set the servoes
			newPos = globalPos;
		}
		drive();
	}
	if (globalPos == goToPos) 
	{ //stop the steppers
		delay(100);
		digitalWrite(X_ENABLE_PIN, HIGH);
		digitalWrite(Y_ENABLE_PIN, HIGH);
	}
}

void drive() 
{
	digitalWrite(X_ENABLE_PIN, LOW);
	digitalWrite(Y_ENABLE_PIN, LOW);
	moveStepper();
}

//STEPPER DRIVE
unsigned long timer = micros();
boolean on = true;
void moveStepper() 
{
	if ( abs(micros() - timer) > spd)
	{ //if time has passed change the state
		timer = micros(); //reset timer
		if (on) 
		{ //turn off
			digitalWrite(X_STEP_PIN, LOW);
			digitalWrite(Y_STEP_PIN, HIGH);
			on = false;
		}
		else
		{ //turn on
			digitalWrite(X_STEP_PIN, HIGH);
			digitalWrite(Y_STEP_PIN, LOW);
			on = true;
		}
	}
}

//SERVO UPDATER
//going through every servo
void servos() 
{
	for (int i = 0; i < Servocount; i++) 
	{
		if(i<16)
		{
			if (strs[globalPos % Stepbuffer][i] == '1') 
				pwm1.setPWM(i , 0, servoMin);
			if (strs[globalPos % Stepbuffer][i] == '0') 
				pwm1.setPWM(i , 0, servoMax);
		}
		else
		{
			if (strs[globalPos % Stepbuffer][i] == '1') 
				pwm2.setPWM(i-16 , 0, servoMin);
			if (strs[globalPos % Stepbuffer][i] == '0') 
				pwm2.setPWM(i-16 , 0, servoMax);
		}
	}
}
//Mapping out the servos
void servoMap() 
{
	//going through every servo
	for (int i = 0; i < Servocount; i++) 
	{
		if(i<16)
		{
			if (strsMap[i] == '1') 
				pwm1.setPWM(i , 0, servoMin);
			if (strsMap[i] == '0') 
				pwm1.setPWM(i , 0, servoMax);
		}
		else
		{
			if (strsMap[i] == '1') 
				pwm2.setPWM(i-16 , 0, servoMin);
			if (strsMap[i] == '0') 
				pwm2.setPWM(i-16 , 0, servoMax);			
		}
	}
}
//Setting all servors to the mid position
void testServos(String tester) 
{
	for (int i = 0; i < Servocount; i++) 
	{
		if(i<16)
		{
			if (tester.substring(i, i + 1) == "1") 
				pwm1.setPWM(i , 0, servoTestMin);
			if (tester.substring(i, i + 1) == "0") 
				pwm1.setPWM(i , 0, servoTestMax);
		}
		else
		{
			if (tester.substring(i, i + 1) == "1") 
				pwm2.setPWM(i-16 , 0, servoTestMin);
			if (tester.substring(i, i + 1) == "0") 
				pwm2.setPWM(i-16 , 0, servoTestMax);			
		}
	}
}

//Serial read data from computer
void receiveData() 
{
	while (Serial.available() > 0) 
	{
		char inChar = Serial.read();
		if (inChar == 'p') 
			thisWasP = true;
		if (inChar == 's') 
			thisWasS = true;
		if (inChar == 'm') 
			thisWasM = true;

		if (isDigit(inChar)) 
		{
			if (thisWasS)
				inStringSer += (char)inChar; //put together to one long string of 1010101
			if (thisWasM)
				inStringSerMap += (char)inChar; //put together to one long string of 1010101
			if (thisWasP)
				inStringPos += (char)inChar; //put together the position
		}
		if (inChar == '\n') 
		{	//end the record and run the next block once
			if (thisWasP) 
			{
				tone(BEEP_PIN, 4000, 50);
				goToPos = inStringPos.toInt();
			}
			if (thisWasS) 
			{
				inStringSer.toCharArray(strs[goToPos % Stepbuffer], 18);//!!Stepbuffer may need to be 230 //copies the inString (as a char[] into the strs[n] on the position
				tone(BEEP_PIN, 2000, 50);
			}
			if (thisWasM) 
			{
				inStringSerMap.toCharArray(strsMap, 18); //copies the inString (as a char[] into the strs[n] on the position
				tone(BEEP_PIN, 1000, 50);
				servoMap();
			}
			thisWasP = false;
			thisWasS = false;
			thisWasM = false;
			inStringSer = "";
			inStringSerMap = "";
			inStringPos = "";
		}
	}
}

//Encoder function keeps track of steps in rotaion
void interruptA() 
{
	if (digitalRead(interruptPinA) == digitalRead(interruptPinC))
		cntRevolution++;
	else 
		cntRevolution--;
	rotPos = ((cntRevolution + positionOffset) % pulseperrevolution);
	//rember round
	globalPos = ((cntRevolution + positionOffset) / (pulseperrevolution/8));
	rotState = map(rotPos, 0, pulseperrevolution, 1, 9);
}
//Encoder function keeps track of full rotation
void interruptD() 
{
	int remainder = cntRevolution % pulseperrevolution;
	if (remainder < (pulseperrevolution/2) ) 
		cntRevolution = cntRevolution - (cntRevolution % pulseperrevolution); //going over //&& cntRevolution > 3500
	else 
		cntRevolution = cntRevolution + (pulseperrevolution - remainder); //going under
}

