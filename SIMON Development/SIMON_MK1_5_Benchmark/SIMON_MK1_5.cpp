/* 
 * Header of the SIMON MK1.5 protocol.
 * This version refactors the existing code from MK1.4 regarding the usage of the pins.
 * Instead of calling the existing functions of pinMode, digitalRead and digitalWrite,
 *  the registers are used directly.
 * 
 * Created on 2024-07-21.
 */

#include "SIMON_MK1_5.h"

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
  this->sreg = SREG;                                  // get current interrupt register state
  cli();                                              // pause interrupts
  *(this->dModeReg) |= this->dBit;                    // configure DATA pin as OUTPUT
  *(this->dOutReg) |= this->dBit;                     // Set data state to HIGH
  *(this->sModeReg) |= this->sBit;                    // configure SIGNAL pin as OUTPUT
  *(this->sOutReg) |= this->dBit;                     // Set signal state to HIGH
  SREG = this->sreg;                                  // resume interrupts
  bool signalState = 0;                               // initialize current state of the signal line
  delayMicroseconds(2*minimumSignalLength);
  // send confirmationByte
  for(byte i=8; i>0; --i) {                           // loop over all bits
    this->sreg = SREG;                                // get current interrupt register state
    cli();                                            // pause interrupts
    if(1 & (responseByte >> i-1)) {                   // set the current bit
      *(this->dOutReg) |= this->dBit;                 // write 1 on DATA pin
    } else {
      *(this->dOutReg) &= ~(this->dBit);              // write 0 on DATA pin
    }
    if(signalState) {                                 // set the current clock state
      *(this->sOutReg) |= this->sBit;                 // write 1 on SIGNAL pin
    } else {
      *(this->sOutReg) &= ~(this->sBit);              // write 0 on SIGNAL pin
    }
    SREG = this->sreg;                                // resume interrupts
    signalState = !signalState;                       // change clock state for next cycle
    delayMicroseconds(minimumSignalLength);           // wait minimum signal length                         
  }
  *(this->dOutReg) |= this->dBit;                     // write 1 on DATA pin
  *(this->sOutReg) |= this->sBit;                     // write 1 on SIGNAL pin
}

// send data contained in byte array via the defined pins
void SIMON::sendData(const byte& charCounter) {
  this->sreg = SREG;                                  // get current interrupt register state
  cli();                                              // pause interrupts
  *(this->dModeReg) |= this->dBit;                    // configure DATA pin as OUTPUT
  *(this->dOutReg) |= this->dBit;                     // Set data state to HIGH
  *(this->sModeReg) |= this->sBit;                    // configure SIGNAL pin as OUTPUT
  *(this->sOutReg) |= this->dBit;                     // Set signal state to HIGH
  SREG = this->sreg;                                  // resume interrupts
  delayMicroseconds(minimumSignalLength);
  this->sreg = SREG;                                  // get current interrupt register state
  cli();                                              // pause interrupts
  *(this->dOutReg) &= ~(this->dBit);                  // enable signal, set signal state to LOW
  SREG = this->sreg;                                  // resume interrupts
  delayMicroseconds(minimumSignalLength);
  this->sreg = SREG;                                  // get current interrupt register state
  cli();                                              // pause interrupts
  *(this->dOutReg) |= this->dBit;                     // Set signal state back to HIGH
  SREG = this->sreg;                                  // resume interrupts
  delayMicroseconds(minimumSignalLength);
  bool signalState = 0;                               // initialize current state of the signal line
  for(byte i=0; i<charCounter+3; ++i) {               // loop over binary String
    for(byte j=8; j>0; --j) {                         // loop over bits of current char
      this->sreg = SREG;                              // get current interrupt register state
      cli();                                          // pause interrupts
      if(1 & (this->transferBytes[i] >> j-1)) {       // set the current bit
        *(this->dOutReg) |= this->dBit;               // write 1 on DATA pin
      } else {
        *(this->dOutReg) &= ~(this->dBit);            // write 0 on DATA pin
      }
      if(signalState) {                               // set the current clock state
        *(this->sOutReg) |= this->sBit;               // write 1 on SIGNAL pin
      } else {
        *(this->sOutReg) &= ~(this->sBit);            // write 0 on SIGNAL pin
      }
      SREG = this->sreg;                              // resume interrupts
      signalState = !signalState;                     // change clock state for next cycle
      delayMicroseconds(minimumSignalLength);         // Wait minimum signal length
    }
  }
  this->sreg = SREG;                                // get current interrupt register state
  cli();                                            // pause interrupts
  *(this->dOutReg) |= this->dBit;                   // set dataPin back to HIGH
  SREG = this->sreg;                                // resume interrupts
  if(!signalState) {
    this->sreg = SREG;                                // get current interrupt register state
    cli();                                            // pause interrupts
    *(this->sOutReg) |= this->sBit;                   // prepare disable signal
    SREG = this->sreg;                                // resume interrupts
	  delayMicroseconds(minimumSignalLength);
  }
  this->sreg = SREG;                                  // get current interrupt register state
  cli();                                              // pause interrupts
  //*(this->sOutReg) &= ~(this->dBit);                  // disable signal
  *(this->dOutReg) &= ~(this->dBit);                  // disable signal
  SREG = this->sreg;                                  // resume interrupts
  delayMicroseconds(minimumSignalLength);
  this->sreg = SREG;                                  // get current interrupt register state
  cli();                                              // pause interrupts
  //*(this->sOutReg) |= this->dBit;                     // set dataPin back to HIGH
  *(this->dOutReg) |= this->dBit;                     // set dataPin back to HIGH
  SREG = this->sreg;                                  // resume interrupts
}

// read incoming data from the SIMON data lines and store it in the static byte array
void SIMON::readData(const bool awaitResponse, byte& responseByte, byte& charCounter) {
  this->sreg = SREG;                                  // get current interrupt register state
  cli();                                              // pause interrupts
  *(this->dModeReg) &= ~(this->dBit);                 // configure DATA pin as INPUT_PULLUP
  *(this->dOutReg) |= this->dBit;
  *(this->sModeReg) &= ~(this->sBit);                 // configure SIGNAL pin as INPUT_PULLUP
  *(this->sOutReg) |= this->sBit;
  SREG = this->sreg;                                  // resume interrupts
  delayMicroseconds(minimumSignalLength);
  byte stateStack = 0;                                // byte to contain multiple single flags to save on memory
  if(*(this->sInReg) & this->sBit) {                  // get current state of SIGNAL pin, later used as past state
    stateStack = 1;
  }
  //stateStack &= ~((byte)1 << 1);
  if(*(this->dInReg) & this->dBit) {                  // get current state of DATA pin, later used as past state
    stateStack |= (byte)1 << 1;
  }
  stateStack |= (1 << 2) & (stateStack << 2);         // initialize second state of SIGNAL pin with same value
  stateStack |= (1 << 3) & ((stateStack >> 1) << 3);  // initialize second state of DATA pin with same value
  if(awaitResponse) {                                 // response handling is different than rest
	responseByte = 0;                                   // re-initialize response byte to default value
	for(byte i=8; i> 0; --i) {                          // loop over all bits of the response byte
	  while((1 & stateStack) == (1 & (stateStack >> 2))) { // lock until the next signal clock comes in
      stateStack &= ~((byte)1 << 2);
      if(*(this->sInReg) & this->sBit) {
        stateStack |= (byte)1 << 2;
      }
	  }
    stateStack = (stateStack & ~(byte)1) | (1 & (stateStack >> 2));  // save current state for next bit
    responseByte &= ~((byte)1 << i-1);
    if(*(this->dInReg) & this->dBit) {                // set current bit according to state on DATA pin
      responseByte |= (byte)1 << i-1;
    }
	}
  } else {
	stateStack |= (stateStack & 1) << 2;                // get current state of SIGNAL pin, later used as past state
	stateStack |= ((stateStack >> 1) & 1) << 3;         // get current state of DATA pin, later used as past state
	while(((stateStack >> 1) & 1) == ((stateStack >> 3) & 1)) {   // lock until the next bit comes in; activation signal
    if(*(this->sInReg) & this->sBit) {                // get current state of SIGNAL pin
      stateStack |= 1;
    } else {
      stateStack &= ~((byte)1);
    }
    if(*(this->dInReg) & this->dBit) {                // get current state of DATA pin
      stateStack |= 1 << 1;
    } else {
      stateStack &= ~((byte)1 << 1);
    }
	}
	while((stateStack & 1) == ((stateStack >> 2) & 1)) {   // lock until the next bit comes in or exit condition is met
    if(*(this->sInReg) & this->sBit) {                // get new SIGNAL state
      stateStack |= 1 << 2;
    } else {
      stateStack &= ~((byte)1 << 2);
    }
	}
	byte charIndex = 0;                                 // index in the static byte array where data is written to
	byte bitIndex = 7;                                  // current bit index in the active byte
	while(!((stateStack >> 4) & 1)) {                   // main loop, get every data signal from data line
	  while(((stateStack & 1) == ((stateStack >> 2) & 1)) && (((stateStack >> 1) & 1) == ((stateStack >> 3) & 1))) {  // lock until the next change on either pin comes
      if(*(this->sInReg) & this->sBit) {              // get new SIGNAL state
        stateStack |= 1 << 2;
      } else {
        stateStack &= ~((byte)1 << 2);
      }
      if(*(this->dInReg) & this->dBit) {              // get new DATA state
        stateStack |= 1 << 3;
      } else {
        stateStack &= ~((byte)1 << 3);
      }
	  }
	  delayMicroseconds(minimumSignalLength/3); // wait until both pins have new state
    if(*(this->sInReg) & this->sBit) {                // get new SIGNAL state
      stateStack |= 1 << 2;
    } else {
      stateStack &= ~((byte)1 << 2);
    }
    if(*(this->dInReg) & this->dBit) {                // get new DATA state
      stateStack |= 1 << 3;
    } else {
      stateStack &= ~((byte)1 << 3);
    }
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
	this->transferBytes[i] = 0;                         // set the current byte to zero
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
    uint8_t dPort = digitalPinToPort(dataPin);
    this->dBit = digitalPinToBitMask(dataPin);        // get register bit of DATA pin
    this->dModeReg = portModeRegister(dPort);         // get the pointer to the DATA port mode register
    this->dOutReg = portOutputRegister(dPort);        // get the pointer to the DATA port output register
    this->dInReg = portInputRegister(dPort);          // get the pointer to the DATA port input register
    uint8_t sPort = digitalPinToPort(signalPin);
    this->sBit = digitalPinToBitMask(signalPin);      // get register bit of SIGNAL pin
    this->sModeReg = portModeRegister(sPort);         // get the pointer to the SIGNAL port mode register
    this->sOutReg = portOutputRegister(sPort);        // get the pointer to the SIGNAL port output register
    this->sInReg = portInputRegister(sPort);          // get the pointer to the SIGNAL port input register
    // set pins as input
    this->sreg = SREG;                                // get current interrupt register state
    cli();                                            // pause interrupts
    *(this->dModeReg) &= ~(this->dBit);               // configure DATA pin as INPUT_PULLUP
    *(this->dOutReg) |= this->dBit;
    *(this->sModeReg) &= ~(this->sBit);               // configure SIGNAL pin as INPUT_PULLUP
    *(this->sOutReg) |= this->sBit;
    SREG = this->sreg;                                // resume interrupts
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
	sendData(charCounter);                              // send the actual binary data via the bus
	// 3. read response
	readData(1, responseByte, charCounter);             // read the response byte from the receiving device
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
	emptyByteArray(charCounter);                        // empty the byte array
	// 2. read data
	readData(0, responseByte, charCounter);             // read the transmission from the sender
	if(this->ownNumber == this->transferBytes[charCounter+2]) {  // verify if received signal is targeted to this device
	  // 3. verify Fletcher-16 sums
	  getFletcher16Checksums(checksum1, checksum2, charCounter);  // calculate the Fletcher-16 checksums for the received signal
	  // 4. send response and exit if successful
	  if((checksum1 == this->transferBytes[charCounter]) && (checksum2 == this->transferBytes[charCounter+1])) { // verify checksums
		confirmation = 1;                                 // set the flag, exiting the loop
		sendResponse(this->confirmationByte);             // send response to sender
	  } else {
		sendResponse(this->errorByte);                    // send response to sender
	  }
	}
  }
  return retreivePayload(charCounter);                // return the received transmission
}
