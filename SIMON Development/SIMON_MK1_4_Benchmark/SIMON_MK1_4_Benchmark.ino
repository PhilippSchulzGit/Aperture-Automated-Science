/*
 * Sketch to benchmark performance of the SIMON protocol.
 * Also acts as an example on how to use it.
 * SIMON MK1.4 is used here.
 * See the header and cpp file for more information about SIMON MK1.4.
 * 
 * Created on 2024-07-17.
 */
#define DATA_PIN 7                                    // Data line for the SIMON protocol
#define SIGNAL_PIN 6                                  // Signal line for the SIMON protocol
#define OWN_NUMBER 0  // 1 for receiver, 0 for sender // device number of the device this code should run on
#define DEBUG_FLAG 0                                  // flag to indicate to use printing

// import and create SIMON object
#include "SIMON_MK1_4.h"
SIMON simon(OWN_NUMBER, 75, 100);  // own ID on bus, us length of a single bit, maximum number of bytes to reserve
//SIMON simon(OWN_NUMBER);  // also possible, since both parameters have default values

void setup() {
  simon.begin(DATA_PIN, SIGNAL_PIN);                  // initialize SIMON protocol
  if(DEBUG_FLAG) {
	  Serial.begin(57600);                              // initialize Serial connection
    if(OWN_NUMBER) {
      Serial.println("I am a receiver with number "+ String(OWN_NUMBER) + ".");
    } else {
      Serial.println("I am a sender with number "+ String(OWN_NUMBER) + ".");
    }
  }
}

void loop() {
  if(DEBUG_FLAG) {
	// verbose version, prints to console
    if(OWN_NUMBER) {
      // program is receiver
      long t0 = micros();
      String input = simon.readSIMONData();           // get the command via SIMON, blocks until command is received
      long t1 = micros();
      Serial.println("receive transmission took " + String(round(t1-t0)) + " us");
      Serial.print("received: ");
      Serial.println(input);                          // print received data to Serial
      delay(900);
    } else {
      //program is sender
      long t0 = micros();
      simon.sendSIMONData(1, "Hello World 2024!");    // send command to receiver
      long t1 = micros();
      Serial.println("send transmission took " + String(round(t1-t0)) + " us");
      delay(1000);
    }
    Serial.println("-------");
  } else {
	// silent version, does not print to console
    if(OWN_NUMBER) {
      // program is receiver
      String input = simon.readSIMONData();           // get the command via SIMON, blocks until command is received
      delay(900);                                     // wait 2 seconds for next cycle
    } else {
      // program is sender
      simon.sendSIMONData(1, "Hello World 2024!");    // send command to receiver
      delay(1000);                                    // wait 2 seconds for next cycle
    }
  }
}
