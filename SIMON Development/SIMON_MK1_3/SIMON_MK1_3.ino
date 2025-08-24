/*
 * Code for implementing the reworked version of the SIMON protocol.
 * This version doesn't use a dynamic String object for the messages, instead it uses a fixed byte array.
 * Generally, all usages of the String classes are removed in this version to improve stability and reduce storage footprint.
 * This version of the protocol also assumes that there are 3 connections between the devices: GND, CLOCK, SIGNAL
 * The speed of this version is limited to a clock cycle time of 1ms for initial testing.
 * Higher speeds on Arduino and Raspberry Pi were already tested, maybe they will be added later on.
 */
// SIMON constants
#define DATA_PIN 7                                    // Data line for the SIMON protocol
#define SIGNAL_PIN 6                                  // Signal line for the SIMON protocol
#define OWN_NUMBER 1  // 1 for receiver, 0 for sender // device number of the device this code should run on
#define DEBUG_FLAG 1                                  // flag to indicate to use printing
//-------------------------------------------Variables for protocol----------------------------------------
// allocate memory for send/receive data + initialize counter for internal usage
const byte characterLimit = 100;                      // maximum number of bytes to allocate for the data array
char transferBytes[characterLimit+3] = {0};           // initialize array used during the protocol. The offset of +3 is required for the protocol.
uint8_t dataPin = 0;			              						  // pin to transmit data
uint8_t signalPin = 0;			            						  // pin to transmit signal
const int minimumSignalLength = 50;                   // time in us that is the minimum signal length of the SIMON protocol in microseconds
const byte confirmationByte = 109;                    // data to send to indicate a confirmation, binary is "01101101"
const byte errorByte = 146;                           // data to send to indicate an error, binary is "010010010"
//-------------------------------------------Variables for protocol----------------------------------------

//--------------------------------------------------SIMON--------------------------------------------------
// Setup of initial pins and own device number on bus
void beginSIMON(uint8_t newDataPin, uint8_t newSignalPin) {
  dataPin = newDataPin;
  signalPin = newSignalPin;
  pinMode(dataPin, INPUT_PULLUP);                     // set DATA line as output
  pinMode(signalPin, INPUT_PULLUP);                   // set SIGNAL line as output
  Serial.begin(57600);                                // temp, for debugging
}

// empties the byte array, resets every byte to zero
void emptyByteArray(const byte &characterLimit2, byte &charCounter, byte* transferBytes2) {
  for(byte i=0; i < characterLimit2; ++i) {           // loop over all bytes in the array
    transferBytes2[i] = 0;                            // set the current byte to zero
  }
  charCounter = 0;                                    // reset the global char counter
}

// copy a given char array to the static byte array of SIMON
void loadPayload(char* text, const byte& characterLimit2, byte& charCounter, byte* transferBytes2) {
  emptyByteArray(characterLimit2, charCounter, transferBytes2);  // clean up remaining content of the static byte array
  if(strlen(text) > characterLimit2) {			          // handle larger String than buffer, cut off rest
    charCounter = characterLimit2;
  } else {
    charCounter = strlen(text);                       // determine length of given command
  }
  for(byte i=0; i < charCounter; ++i) {               // loop over all characters of the given text
    transferBytes2[i] = text[i];                      // set the bytes in the static byte array
  }
}

// copy the active content in the static byte array of SIMON to a new char array
String retreivePayload(const byte& characterLimit2, byte& charCounter, byte* transferBytes2) {
  char text[charCounter+1] = {0};                     // allocate memory for the resulting char array
  for(byte i=0; i < charCounter; ++i) {               // loop over all active bytes in the static byte array
    text[i] = transferBytes2[i];                      // copy single chars to new char array
  }
  text[charCounter] = 0;			                        // set null to complete String
  return String(text);                                // return the char array without any leading zeros
}

// get the checksums from Fletcher-16 for a given binary data String
void getFletcher16Checksums(byte& sum1, byte& sum2, byte& charCounter, byte* transferBytes2) {
  sum1 = 0;                                           // initialize first sum
  sum2 = 0;                                           // intialize second sum
  // loop does NOT use Fletcher-16 sums and device number, only actual payload!
  for(byte i=0; i < charCounter; ++i) {               // loop over byte array for each char and calculate the sums
    sum1 = (sum1 + transferBytes2[i]) % 255;          // calculate checksum1
    sum2 = (sum1 + sum2) % 255;                       // calculate checksum2
  }
}

// function to change a single bit in a byte based on https://stackoverflow.com/questions/47981/how-to-set-clear-and-toggle-a-single-bit
void changeBit(byte& number, byte bit, bool newState) {
  //number = (number & ~(1 << bit)) | (newState << bit);
  number = (number & ~((byte)1 << bit)) | ((byte)newState << bit);
}

// send a given char array via the SIMON protocol
void sendSIMONData(byte receiver_number, char* command) {
  // 1. copy command to byte array + set up remaining values
  byte charCounter = 0;								  // initialize helper variable to keep track of active data in static byte array
  loadPayload(command, characterLimit, charCounter, transferBytes);  // load the given command into byte array
  byte checksum1 = 0;								                  // initialize first Fletcher-16 checksum
  byte checksum2 = 0;								                  // initialize second Fletcher-16 checksum
  getFletcher16Checksums(checksum1, checksum2, charCounter, transferBytes);  // determine Fletcher-16 sums for command
  transferBytes[charCounter] = checksum1;             // assign first sum to byte array
  transferBytes[charCounter+1] = checksum2;           // assign second sum to byte array
  transferBytes[charCounter+2] = receiver_number;     // assign receiver number to byte array
  byte responseByte = errorByte;                      // initialize response byte as error byte for loop
  while(!(responseByte == confirmationByte)) {        // loop until the transfer was successful 
    // 2. send data
    sendData(charCounter);                            // send the actual binary data via the bus
    // 3. read response
    readData(true, responseByte, charCounter);        // read the response byte from the receiving device
  }
}

// read a message sent by another device via the SIMON protocol
String readSIMONData(){
  bool confirmation = 0;                              // initialize flag for loop
  byte checksum1 = 0;								                  // initialize first Fletcher-16 checksum
  byte checksum2 = 0;								                  // initialize second Fletcher-16 checksum
  byte responseByte = 0;                              // initialize response byte
  byte charCounter = 0;					              			  // initialize helper variable to keep track of active data in static byte array
  while(!confirmation) {                              // loop until the transfer was successful
    // 1. setup
    emptyByteArray(characterLimit, charCounter, transferBytes);  // empty the byte array
    // 2. read data
    readData(false, responseByte, charCounter);       // read the transmission from the sender
    if(OWN_NUMBER == transferBytes[charCounter+2]) {  // verify if received signal is targeted to this device
      // 3. verify Fletcher-16 sums
      getFletcher16Checksums(checksum1, checksum2, charCounter, transferBytes);  // calculate the Fletcher-16 checksums for the received signal
      // 4. send response and exit if successful
      if((checksum1 == transferBytes[charCounter]) && (checksum2 == transferBytes[charCounter+1])) { // verify checksums
        confirmation = 1;                             // set the flag, exiting the loop
        sendResponse(confirmationByte);               // send response to sender
      } else {
        sendResponse(errorByte);                      // send response to sender
      }
    }
  }
  return retreivePayload(characterLimit, charCounter, transferBytes);  // return the received transmission
}

// send a response during the transmission
void sendResponse(const byte& responseByte) {
  pinMode(dataPin, OUTPUT);                           // set the DATA pin as an output
  pinMode(signalPin, OUTPUT);                         // set the SIGNAL pin as an output
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set signal state back to HIGH
  bool signalState = 0;                               // initialize current state of the signal line
  delayMicroseconds(2*minimumSignalLength);
  // send confirmationByte
  for(byte i=8; i>0; --i) {                           // loop over all bits
    digitalWrite(dataPin, 1 & (responseByte >> i-1)); // set the current bit
    digitalWrite(signalPin, signalState);             // set the current clock state
    signalState = !signalState;                       // change clock state for next cycle
    delayMicroseconds(minimumSignalLength);           // wait minimum signal length                         
  }
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set signal state back to HIGH
}

// send data contained in byte array via the defined pins
void sendData(byte& charCounter) {
  pinMode(dataPin, OUTPUT);                           // set the DATA pin as an output
  pinMode(signalPin, OUTPUT);                         // set the SIGNAL pin as an output
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set signal state back to HIGH
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 0);                           // enable signal
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  delayMicroseconds(minimumSignalLength);
  bool signalState = 0;                               // initialize current state of the signal line
  for(byte i=0; i<charCounter+3; ++i) {               // loop over binary String
    char current = transferBytes[i];                  // get current char from binary string
    for(byte j=8; j>0; --j) {                         // loop over bits of current char
      digitalWrite(dataPin, 1 & (current >> j-1));    // set the current bit
      digitalWrite(signalPin, signalState);           // set the current clock state
      signalState = !signalState;                     // change clock state for next cycle
      delayMicroseconds(minimumSignalLength);         // Wait minimum signal length
    }
  }
  if(!signalState) {
    digitalWrite(dataPin, 1);                         // prepare disable signal
    delayMicroseconds(minimumSignalLength);
  }
  digitalWrite(dataPin, 0);                           // disable signal
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
}

// read incoming data from the SIMON data lines and store it in the static byte array
void readData(bool awaitResponse, byte &responseByte, byte &charCounter) {
  pinMode(dataPin, INPUT_PULLUP);                     // set the DATA pin as an input
  pinMode(signalPin, INPUT_PULLUP);                   // set the SIGNAL pin as an input
  delayMicroseconds(minimumSignalLength);
  byte stateStack = 0;								                // byte to contain multiple single flags to save on memory
  stateStack |= (byte)digitalRead(signalPin);         // get current state of SIGNAL pin, later used as past state
  stateStack = (stateStack & ~((byte)1 << 1)) | ((byte)digitalRead(dataPin) << 1);  // get current state of SIGNAL pin, later used as past state
  if(awaitResponse) {                                 // response handling is different than rest
    responseByte = 0;                                 // re-initialize response byte to default value
    for(byte i=8; i> 0; --i) {                        // loop over all bits of the response byte
      while((stateStack & 1) == ((stateStack >> 2) & 1)) { // lock until the next signal clock comes in
		    stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);
      }
	    stateStack = (stateStack & ~(byte)1) | (((stateStack >> 2) & 1) << 2);  // save current state for next bit
      responseByte |= digitalRead(dataPin) << i-1;    // set current bit according to state on DATA pin
    }
  } else {
    stateStack |= (stateStack & 1) << 2;              // get current state of SIGNAL pin, later used as past state
    stateStack |= ((stateStack >> 1) & 1) << 3;       // get current state of DATA pin, later used as past state
    while(((stateStack >> 1) & 1) == ((stateStack >> 3) & 1)) {   // lock until the next bit comes in; activation signal
      stateStack = (stateStack & ~(byte)1) | ((byte)digitalRead(signalPin) << 0);  // get current state of SIGNAL pin
      stateStack = (stateStack & ~((byte)1 << 1)) | ((byte)digitalRead(dataPin) << 1);  // get current state of DATA pin
    }
    while((stateStack & 1) == ((stateStack >> 2) & 1)) {   // lock until the next bit comes in or exit condition is met
      stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);  // get new SIGNAL state
    }
    byte charIndex = 0;                               // index in the static byte array where data is written to
    byte bitIndex = 7;                                // current bit index in the active byte
    while(!((stateStack >> 4) & 1)) {                 // main loop, get every data signal from data line
      while(((stateStack & 1) == ((stateStack >> 2) & 1)) && (((stateStack >> 1) & 1) == ((stateStack >> 3) & 1))) {  // lock until the next change on either pin comes
        stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);  // get new SIGNAL state
        stateStack = (stateStack & ~((byte)1 << 3)) | ((byte)digitalRead(dataPin) << 3);  // get new DATA state
      }
      delayMicroseconds(minimumSignalLength/3);       // wait until both pins have new state
	    stateStack = (stateStack & ~((byte)1 << 2)) | ((byte)digitalRead(signalPin) << 2);  // get new SIGNAL state
	    stateStack = (stateStack & ~((byte)1 << 3)) | ((byte)digitalRead(dataPin) << 3);  // get new DATA state
	    stateStack = (stateStack & ~((byte)1 << 4)) | ((byte)(!((stateStack >> 3) & 1) && ((stateStack >> 1) & 1) && (stateStack & 1) && ((stateStack >> 2) & 1)) << 4);  // handle exit condition
      if(!((stateStack >> 4) & 1)) {                        // make sure that end signal was not yet encountered
	      stateStack = (stateStack & ~(byte)1) | ((stateStack >> 2) & 1);  // save current SIGNAL state for next bit
		    stateStack = (stateStack & ~((byte)1 << 1)) | (((stateStack >> 3) & 1) << 1);  // save current DATA state for next bit
        transferBytes[charIndex] |= ((stateStack >> 1) & 1) << bitIndex;  // set current bit
        if(bitIndex > 0) {                            // check if bit index has not yet reached position 0
          --bitIndex;                                 // decrement bit index position to next position
        } else {                                      // bit index has reached position 0
          bitIndex = 7;                               // reset bit index to highest position
          ++charIndex;                                // increment char index to go to next char
		      stateStack = (stateStack & ~((byte)1 << 4)) | ((byte)(charIndex > (characterLimit+3)) << 4);  // increment char index and check if data is longer than defined limit
        }
      }
    }
    charCounter = charIndex - 3;                      // save the number of received bytes, adjust for positions of checksums and ID
  }
}
//--------------------------------------------------SIMON--------------------------------------------------

//-----------------------------------------------MAIN PROGRAM----------------------------------------------
void setup() {
  //initialize SIMON protocol
  beginSIMON(DATA_PIN, SIGNAL_PIN);
  if(DEBUG_FLAG) {
    if(OWN_NUMBER) {
      Serial.println("I am a receiver with number "+ String(OWN_NUMBER) + ".");
    } else {
      Serial.println("I am a sender with number "+ String(OWN_NUMBER) + ".");
    }
  }
}

void loop() {
  if(DEBUG_FLAG) {
    if(OWN_NUMBER) {
      // program is receiver
      long t0 = micros();
      String input = readSIMONData();                 // get the command via SIMON, blocks until command is received
      long t1 = micros();
      Serial.println("receive transmission took " + String(round(t1-t0)) + " us");
      Serial.print("received: ");
      Serial.println(input);                          // print received data to Serial
      delay(900);
    } else {
      //program is sender
      long t0 = micros();
      sendSIMONData(1, "Hello World 2024!");          // send command to receiver
      long t1 = micros();
      Serial.println("send transmission took " + String(round(t1-t0)) + " us");
      delay(1000);
    }
    Serial.println("-------");
  } else {
    if(OWN_NUMBER) {
      // program is receiver
      String input = readSIMONData();                 // get the command via SIMON, blocks until command is received
      delay(900);                                     // wait 2 seconds for next cycle
    } else {
      // program is sender
      sendSIMONData(1, "Hello World 2024!");          // send command to receiver
      delay(1000);                                    // wait 2 seconds for next cycle
    }
  }
}
