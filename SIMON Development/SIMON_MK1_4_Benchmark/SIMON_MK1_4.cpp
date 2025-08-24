/* 
 * Implementation of the SIMON MK1.4 protocol.
 * This version refactors the existing code from MK1.3 into a single class.
 * The class makes it much easier and more comfortable to use it.
 * 
 * Created on 2024-07-14.
 */

#include "SIMON_MK1_4.h"

// empties the byte array, resets every byte to zero
void SIMON::emptyByteArray(byte& charCounter) {
  for(byte i=0; i < this->characterLimit+3; ++i) {    // loop over all bytes in the array
	  this->transferBytes[i] = 0;                       // set the current byte to zero
  }
  charCounter = 0;                                    // reset the global char counter
}

// copy a given char array to the static byte array of SIMON
void SIMON::loadPayload(char* text, byte& charCounter) {
  emptyByteArray(charCounter);                        // clean up remaining content of the static byte array
  if(strlen(text) > this->characterLimit) {           // handle larger String than buffer, cut off rest
	  charCounter = this->characterLimit;
  } else {
	  charCounter = strlen(text);                       // determine length of given command
  }
  for(byte i=0; i < charCounter; ++i) {               // loop over all characters of the given text
	  this->transferBytes[i] = text[i];                 // set the bytes in the static byte array
  }
}

// copy the active content in the static byte array of SIMON to a new char array
String SIMON::retreivePayload(const byte& charCounter) {
  char text[charCounter+1] = {0};                     // allocate memory for the resulting char array
  for(byte i=0; i < charCounter; ++i) {               // loop over all active bytes in the static byte array
    text[i] = this->transferBytes[i];                 // copy single chars to new char array
  }
  text[charCounter] = 0;                              // set null to complete String
  return String(text);                                // return the char array without any leading zeros
}

// get the checksums from Fletcher-16 for a given binary data String
void SIMON::getFletcher16Checksums(byte& sum1, byte& sum2, const byte& charCounter) {
  sum1 = 0;                                           // initialize first sum
  sum2 = 0;                                           // intialize second sum
  // loop does NOT use Fletcher-16 sums and device number, only actual payload!
  for(byte i=0; i < charCounter; ++i) {               // loop over byte array for each char and calculate the sums
    sum1 = (sum1 + this->transferBytes[i]) % 255;     // calculate checksum1
    sum2 = (sum1 + sum2) % 255;                       // calculate checksum2
  }
}

// send a response during the transmission
void SIMON::sendResponse(const byte& responseByte) {
  pinMode(dataPin, OUTPUT);                           // set the DATA pin as an output
  pinMode(signalPin, OUTPUT);                         // set the SIGNAL pin as an output
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set data state back to HIGH
  bool signalState = 0;                               // initialize current state of the signal line
  delayMicroseconds(2*minimumSignalLength);
  // send confirmationByte
  for(byte i=8; i>0; --i) {                           // loop over all bits
    digitalWrite(dataPin, 1 & (responseByte >> (i-1)));  // set the current bit
    digitalWrite(signalPin, signalState);             // set the current clock state
    signalState = !signalState;                       // change clock state for next cycle
    delayMicroseconds(minimumSignalLength);           // wait minimum signal length                         
  }
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set data state back to HIGH
}

// send data contained in byte array via the defined pins
void SIMON::sendData(const byte& charCounter) {
  pinMode(dataPin, OUTPUT);                           // set the DATA pin as an output
  pinMode(signalPin, OUTPUT);                         // set the SIGNAL pin as an output
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set data state back to HIGH
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 0);                           // enable signal
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  delayMicroseconds(minimumSignalLength);
  bool signalState = 0;                               // initialize current state of the signal line
  for(byte i=0; i<charCounter+3; ++i) {               // loop over binary String
    for(byte j=8; j>0; --j) {                           // loop over bits of current char
      digitalWrite(dataPin, 1 & (this->transferBytes[i] >> (j-1)));  // set the current bit
      digitalWrite(signalPin, signalState);             // set the current clock state
      signalState = !signalState;                       // change clock state for next cycle
      delayMicroseconds(minimumSignalLength);           // Wait minimum signal length
    }
  }
	digitalWrite(dataPin, 1);                           // Set dataPin back to HIGH
  if(!signalState) {
	digitalWrite(signalPin, 1);                           // prepare disable signal
	delayMicroseconds(minimumSignalLength);
  }
  digitalWrite(dataPin, 0);                           // disable signal
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 1);                           // Set dataPin back to HIGH
}

// read incoming data from the SIMON data lines and store it in the static byte array
void SIMON::readData(const bool awaitResponse, byte& responseByte, byte& charCounter) {
  pinMode(dataPin, INPUT_PULLUP);                     // set the DATA pin as an input
  pinMode(signalPin, INPUT_PULLUP);                   // set the SIGNAL pin as an input
  delayMicroseconds(minimumSignalLength);
  // byte to contain multiple single flags to save on memory
  byte stateStack = digitalRead(signalPin);           // get current state of SIGNAL pin, later used as past state
  if(digitalRead(dataPin)) {                          // get current state of DATA pin, later used as past state
    stateStack |= (byte)1 << 1;
  }
  stateStack |= (1 << 2) & (stateStack << 2);         // initialize second state of SIGNAL pin with same value
  stateStack |= (1 << 3) & ((stateStack >> 1) << 3);  // initialize second state of DATA pin with same value
  if(awaitResponse) {                                 // response handling is different than rest
    responseByte = 0;                                 // re-initialize response byte to default value
    for(byte i = 8; i > 0; --i) {                     // loop over all bits of the response byte
      while((1 & stateStack) == (1 & (stateStack >> 2))) { // lock until the next signal clock comes in
		    stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);
      }
	    stateStack = (stateStack & ~(byte)1) | (1 & (stateStack >> 2));  // save current state for next bit
      responseByte |= digitalRead(dataPin) << (i-1);  // set current bit according to state on dataPin
    }
  } else {
	stateStack |= (stateStack & 1) << 2;                // get current state of SIGNAL pin, later used as past state
	stateStack |= ((stateStack >> 1) & 1) << 3;         // get current state of DATA pin, later used as past state
	while(((stateStack >> 1) & 1) == ((stateStack >> 3) & 1)) {   // lock until the next bit comes in; activation signal
	  stateStack = (stateStack & ~(byte)1) | (byte)digitalRead(signalPin);  // get current state of SIGNAL pin
	  stateStack = (stateStack & ~((byte)1 << 1)) | ((byte)digitalRead(dataPin) << 1);  // get current state of DATA pin
	}
	while((stateStack & 1) == ((stateStack >> 2) & 1)) {   // lock until the next bit comes in or exit condition is met
	  stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);  // get new SIGNAL state
	}
	byte charIndex = 0;                                 // index in the static byte array where data is written to
	byte bitIndex = 7;                                  // current bit index in the active byte
	while(!((stateStack >> 4) & 1)) {                   // main loop, get every data signal from data line
	  while(((stateStack & 1) == ((stateStack >> 2) & 1)) && (((stateStack >> 1) & 1) == ((stateStack >> 3) & 1))) {  // lock until the next change on either pin comes
		stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);  // get new SIGNAL state
		stateStack = (stateStack & ~((byte)1 << 3)) | ((byte)digitalRead(dataPin) << 3);  // get new DATA state
	  }
	  delayMicroseconds(minimumSignalLength/3);         // wait until both pins have new state
	  stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);  // get new SIGNAL state
	  stateStack = (stateStack & ~((byte)1 << 3)) | ((byte)digitalRead(dataPin) << 3);  // get new DATA state
	  stateStack = (stateStack & ~((byte)1 << 4)) | ((byte)(!((stateStack >> 3) & 1) && ((stateStack >> 1) & 1) && (stateStack & 1) && ((stateStack >> 2) & 1)) << 4);  // handle exit condition
	  if(!((stateStack >> 4) & 1)) {                    // make sure that end signal was not yet encountered
		stateStack = (stateStack & ~(byte)1) | ((stateStack >> 2) & 1);  // save current SIGNAL state for next bit
		stateStack = (stateStack & ~((byte)1 << 1)) | (((stateStack >> 3) & 1) << 1);  // save current DATA state for next bit
		this->transferBytes[charIndex] |= ((stateStack >> 1) & 1) << bitIndex;  // set current bit
		if(bitIndex > 0) {                                // check if bit index has not yet reached position 0
		  --bitIndex;                                     // decrement bit index position to next position
		} else {                                          // bit index has reached position 0
		  bitIndex = 7;                                   // reset bit index to highest position
		  ++charIndex;                                    // increment char index to go to next char
		  stateStack = (stateStack & ~((byte)1 << 4)) | ((byte)(charIndex > (characterLimit+3)) << 4);  // increment char index and check if data is longer than defined limit
		}
	  }
	}
	charCounter = charIndex - 3;                        // save the number of received bytes, adjust for positions of checksums and ID
  }
}

// constructor
SIMON::SIMON(const byte number, const int minimumSignalLength, const byte characterLimit) : ownNumber(number), characterLimit(characterLimit), minimumSignalLength(minimumSignalLength) {
  this->transferBytes = new char[characterLimit+3];   // allocate array used during the protocol. The offset of +3 is required for the protocol.
  for(byte i=0; i < this->characterLimit+3; ++i) {    // loop over all bytes in the array
	  this->transferBytes[i] = 0;                       // set the current byte to zero
  }
  this->dataPin = 0;                                  // initialize dataPin
  this->signalPin = 0;                                // initialize signalPin
}

// destructor
SIMON::~SIMON() {
  if(this->transferBytes != NULL) {
	  delete[] this->transferBytes;
  }
}

// sets up the pins for transmission
void SIMON::begin(const uint8_t newDataPin, const uint8_t newSignalPin) {
  if(0 == this->dataPin && 0 == this->signalPin) {    // check if pins were not yet set
    this->dataPin = newDataPin;
    this->signalPin = newSignalPin;
    pinMode(dataPin, INPUT_PULLUP);                   // set DATA line as output
    pinMode(signalPin, INPUT_PULLUP);                 // set SIGNAL line as output
  }
}

// send a given char array via the SIMON protocol
void SIMON::sendSIMONData(const byte receiver_number, const char* command) {
  // 1. copy command to byte array + set up remaining values
  byte charCounter = 0;							                  // initialize helper variable to keep track of active data in static byte array
  loadPayload(command, charCounter);                  // load the given command into byte array
  byte checksum1 = 0;				                          // initialize first Fletcher-16 checksum
  byte checksum2 = 0;							                    // initialize second Fletcher-16 checksum
  getFletcher16Checksums(checksum1, checksum2, charCounter);  // determine Fletcher-16 sums for command
  this->transferBytes[charCounter] = checksum1;       // assign first sum to byte array
  this->transferBytes[charCounter+1] = checksum2;     // assign second sum to byte array
  this->transferBytes[charCounter+2] = receiver_number; // assign receiver number to byte array
  byte responseByte = this->errorByte;                // initialize response byte as error byte for loop
  while(!(responseByte == this->confirmationByte)) {  // loop until the transfer was successful 
    // 2. send data
    sendData(charCounter);                            // send the actual binary data via the bus
    // 3. read response
    readData(1, responseByte, charCounter);           // read the response byte from the receiving device
  }
}

// read a message sent by another device via the SIMON protocol
String SIMON::readSIMONData() {
  bool confirmation = 0;                              // initialize flag for loop
  byte checksum1 = 0;                                 // initialize first Fletcher-16 checksum
  byte checksum2 = 0;                                 // initialize second Fletcher-16 checksum
  byte responseByte = 0;                              // initialize response byte
  byte charCounter = 0;                               // initialize helper variable to keep track of active data in static byte array
  while(!confirmation) {                              // loop until the transfer was successful
    // 1. setup
    emptyByteArray(charCounter);                      // empty the byte array
    // 2. read data
    readData(0, responseByte, charCounter);           // read the transmission from the sender
    if(this->ownNumber == this->transferBytes[charCounter+2]) {  // verify if received signal is targeted to this device
      // 3. verify Fletcher-16 sums
      getFletcher16Checksums(checksum1, checksum2, charCounter);  // calculate the Fletcher-16 checksums for the received signal
      // 4. send response and exit if successful
      if((checksum1 == this->transferBytes[charCounter]) && (checksum2 == this->transferBytes[charCounter+1])) { // verify checksums
      confirmation = 1;                               // set the flag, exiting the loop
      sendResponse(this->confirmationByte);           // send response to sender
      } else {
      sendResponse(this->errorByte);                  // send response to sender
      }
    }
  }
  return retreivePayload(charCounter);                // return the received transmission
}
