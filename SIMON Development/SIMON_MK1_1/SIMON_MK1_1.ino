/*
 * Code for implementing the reworked version of the SIMON protocol.
 * This version doesn't use a dynamic String object for the messages, instead it uses a fixed byte array.
 * Generally, all usages of the String classes are removed in this version to improve stability and reduce storage footprint.
 * This version of the protocol also assumes that there are 3 connections between the devices: GND, CLOCK, signalPin
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
const int characterLimit = 100;                       // maximum number of bytes to allocate for the data array
char transferBytes[characterLimit+3] = {0};           // initialize array used during the protocol. The offset of +3 is required for the protocol.
int charCounter = 0;                                  // counter for keeping track of how many chars are used in transferBytes
uint8_t dataPin = 0;								               	  // pin to transmit data
uint8_t signalPin = 0;								             	  // pin to transmit signal
// timing constants
const unsigned int minimumSignalLength = 1000;        // time in us that is the minimum signal length of the SIMON protocol in microseconds
// Fletcher-16 checksums
byte checksum1 = 0;                                   // first checksum from the Fletcher-16 algorithm
byte checksum2 = 0;                                   // second checksum from the Fletcher-16 algorithm
// transfer-specific constants
const byte confirmationByte = 109;                    // data to send to indicate a confirmation, binary is "1101101"
const byte errorByte = 18;                            // data to send to indicate an error, binary is "0010010"
byte responseByte = 0;                                // variable to store the response when sending data
//-------------------------------------------Variables for protocol----------------------------------------


//--------------------------------------------------SIMON--------------------------------------------------
// Setup of initial pins and own device number on bus
void beginSIMON(int newDataPin, int newSignalPin) {
  dataPin = newDataPin;
  signalPin = newSignalPin;
  pinMode(dataPin, INPUT_PULLUP);                     // set dataPin line as output
  pinMode(signalPin, INPUT_PULLUP);                   // set signalPin line as output
  Serial.begin(57600);                                // temp, for debugging
}

// empties the byte array, resets every byte to zero
void emptyByteArray() {
  for(int i=0; i < characterLimit; ++i) {             // loop over all bytes in the array
    transferBytes[i] = 0;                             // set the current byte to zero
  }
  charCounter = 0;                                    // reset the global char counter
}

// copy a given char array to the static byte array of SIMON
void loadPayload(char* text) {
  emptyByteArray();                                   // clean up remaining content of the static byte array
  charCounter = strlen(text);                         // determine length of given command
  for(int i=0; i < charCounter; ++i) {                // loop over all characters of the given text
    transferBytes[i] = text[i];                       // set the bytes in the static byte array
  }
}

// copy the active content in the static byte array of SIMON to a new char array
String retreivePayload() {
  char text[charCounter+1] = {0};                     // allocate memory for the resulting char array
  for(int i=0; i < charCounter; ++i) {                // loop over all active bytes in the static byte array
    text[i] = transferBytes[i];                       // copy single chars to new char array
  }
  text[charCounter] = 0;
  return String(text);                                // return the char array without any leading zeros
}

// get the checksums from Fletcher-16 for a given binary data String
void getFletcher16Checksums() {
  checksum1 = 0;                                      // initialize first sum
  checksum2 = 0;                                      // intialize second sum
  // loop does NOT use Fletcher-16 sums and device number, only actual payload!
  for(int i=0; i < charCounter; ++i) {                // loop over byte array for each char and calculate the sums
    checksum1=(checksum1+transferBytes[i]) % 255;     // calculate checksum1
    checksum2=(checksum1+checksum2) % 255;            // calculate checksum2
  }
}

// send a given char array via the SIMON protocol
void sendSIMONData(byte receiver_number, char* command){
  bool confirmation = 0;                              // initialize flag for loop
  // 1. copy command to byte array + set up remaining values
  loadPayload(command);                               // load the given command into byte array
  getFletcher16Checksums();                           // determine Fletcher-16 sums for command
  transferBytes[charCounter] = checksum1;             // assign first sum to byte array
  transferBytes[charCounter+1] = checksum2;           // assign second sum to byte array
  transferBytes[charCounter+2] = receiver_number;     // assign receiver number to byte array
  while(!confirmation) {                              // loop until the transfer was successful 
    // 2. send data
    sendData();                                       // send the actual binary data via the bus
    // 3. read response
    readData(true);                                   // read the response from the receiving device
    // 4. check if transfer was successful
    confirmation = responseByte == confirmationByte;  // set the flag, exiting the loop
  }
}

// read a message sent by another device via the SIMON protocol
String readSIMONData(){
  bool confirmation = 0;                              // initialize flag for loop
  while(!confirmation) {                              // loop until the transfer was successful
    // 1. setup
    emptyByteArray();                                 // empty the byte array
    // 2. read data
    readData(false);                                  // read the transmission from the sender
    if(OWN_NUMBER == transferBytes[charCounter+2]) {  // verify if received signal is targeted to this device
      // 3. verify Fletcher-16 sums
      getFletcher16Checksums();                       // calculate the Fletcher-16 checksums for the received signal
      // 4. send response and exit if successful
      if((checksum1 == transferBytes[charCounter]) && (checksum2 == transferBytes[charCounter+1])) { // verify checksums
        confirmation = 1;                             // set the flag, exiting the loop
        responseByte = confirmationByte;              // set the response byte to send
      } else {
        responseByte = errorByte;                     // set the response byte to send
      }
      sendResponse();                                 // send response to sender
    }
  }
  return retreivePayload();                           // return the received transmission
}

// send a response during the transmission
void sendResponse() {
  pinMode(dataPin, OUTPUT);                           // set the dataPin as an output
  pinMode(signalPin, OUTPUT);                         // set the signalPin as an output
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set signal state back to HIGH
  delayMicroseconds(2*minimumSignalLength);
  // send confirmationByte
  for(int i=7; i>=0; --i) {                           // loop over all bits
	digitalWrite(dataPin, 1 & (responseByte >> i));   // set the current bit
    digitalWrite(signalPin, 0);                       // set the current clock state
    delayMicroseconds(minimumSignalLength);           // wait minimum signal length   
	digitalWrite(dataPin, 1);                         // set the current bit   
    digitalWrite(signalPin, 1);                       // reset the current clock state
    delayMicroseconds(minimumSignalLength);           // wait minimum signal length                         
  }
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set signal state back to HIGH
}

// send data contained in byte array via the defined pins
void sendData() {
  pinMode(dataPin, OUTPUT);                           // set the dataPin as an output
  pinMode(signalPin, OUTPUT);                         // set the signalPin as an output
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  digitalWrite(signalPin, 1);                         // Set signal state back to HIGH
  delayMicroseconds(2*minimumSignalLength);
  digitalWrite(dataPin, 0);                           // enable signal
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
  delayMicroseconds(minimumSignalLength);
  for(int i=0; i<charCounter+3; ++i) {                // loop over binary String
    char current = transferBytes[i];                  // get current char from binary string
    for(int j=7; j>=0; --j) {                         // loop over bits of current char
	  digitalWrite(dataPin, 1 & (current >> j));        // set the current bit
      digitalWrite(signalPin, 0);                     // set the current clock state
      delayMicroseconds(minimumSignalLength);         // wait minimum signal length   
	  digitalWrite(dataPin, 1);                         // set the current bit   
      digitalWrite(signalPin, 1);                     // reset the current clock state
      delayMicroseconds(minimumSignalLength);         // wait minimum signal length
    }
  }
  digitalWrite(dataPin, 0);                           // disable signal
  delayMicroseconds(minimumSignalLength);
  digitalWrite(dataPin, 1);                           // Set signal state back to HIGH
}

// read incoming data from the SIMON data lines and store it in the static byte array
void readData(bool awaitResponse) {                   
  pinMode(dataPin, INPUT_PULLUP);                     // set the dataPin as an input
  pinMode(signalPin, INPUT_PULLUP);                   // set the signalPin as an input
  delayMicroseconds(minimumSignalLength);
  bool signalState = digitalRead(signalPin);          // get current state of signalPin, later used as past state
  bool dataState = digitalRead(dataPin);              // get current state of dataPin, later used as past state
  bool newSignalState = signalState;                  // helper variable to store new state of signalPin
  bool newDataState = dataState;                      // helper variable to store new state of dataPin
  bool helperToggle = 1;                              // ignores additional bits
  if(awaitResponse) {                                 // response handling is different than rest
    responseByte = 0;                                 // re-initialize response byte to default value
    for(int i=7; i>= 0; --i) {                        // loop over all bits of the response byte
      while(signalState == newSignalState) {          // lock until the next byte comes in
        newSignalState = digitalRead(signalPin);
      }
      signalState = newSignalState;                   // save current state for next bit
      responseByte |= digitalRead(dataPin) << i;      // set current bit according to state on dataPin
      if(i > 0) {
        while(signalState == newSignalState) {          // lock until the next byte comes in
          newSignalState = digitalRead(signalPin);
        }
        signalState = newSignalState;                   // save current state for next bit
      }
    }
  } else {
    bool waitLoop = dataState && !signalState;        // required boolean value to determine end of sent data
    while(dataState == newDataState) {                // lock until the next bit comes in; activation signal
        signalState = digitalRead(signalPin);         // get current state of signalPin
        dataState = digitalRead(dataPin);             // get current state of dataPin
    }
    while(signalState == newSignalState) {            // lock until the next byte comes in or exit condition is met
        newSignalState = digitalRead(signalPin);      // get new signalPin state
    }
    bool mainLoop = 0;                                // required boolean value to gather data from line and end the transmission
    int charIndex = 0;                                // index in the static byte array where data is written to
    int bitIndex = 7;                                 // current bit index in the active byte
    while(!mainLoop) {                                // main loop, get every data signal from data line
      while((signalState == newSignalState) && (dataState == newDataState)) {  // lock until the next change on either pin comes
        newSignalState = digitalRead(signalPin);      // get new signalPin state
        newDataState = digitalRead(dataPin);          // get new dataPin state
      }
      delayMicroseconds(minimumSignalLength/3);       // wait until both pins have new state
      newSignalState = digitalRead(signalPin);        // get new signalPin state
      newDataState = digitalRead(dataPin);            // get new dataPin state
      mainLoop = !newDataState && dataState && signalState && newSignalState;  // handle exit condition
      if(!mainLoop) {                                 // make sure that end signal was not yet encountered
        signalState = newSignalState;                 // save current signalPin state for next bit
        dataState = newDataState;                     // save current dataPin state for next bit
        if(helperToggle) {
          transferBytes[charIndex] |= dataState << bitIndex;  // set current bit
          if(bitIndex > 0) {                          // check if bit index has not yet reached position 0
            --bitIndex;                               // decrement bit index position to next position
          } else {                                    // bit index has reached position 0
            bitIndex = 7;                             // reset bit index to highest position
            ++charIndex;                              // increment char index to go to next char
          }
        }
        helperToggle = !helperToggle;
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
      String input = readSIMONData();                 //get the command via SIMON, blocks until command is received
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
      String input = readSIMONData();                 //get the command via SIMON, blocks until command is received
      delay(900);                                     // wait 2 seconds for next cycle
    } else {
      // program is sender
      sendSIMONData(1, "Hello World 2024!");          // send command to receiver
      delay(1000);                                    // wait 2 seconds for next cycle
    }
  }
}
