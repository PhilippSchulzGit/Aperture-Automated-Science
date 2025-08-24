/*
 * Code for implementing the reworked version of the SIMON protocol.
 * This version doesn't use a dynamic String object for the messages, instead it uses a fixed byte array.
 ' Generally, all usages of the String classes are removed in this version to improve stability and reduce storage footprint.
 * This version of the protocol also assumes that there are 3 connections between the devices: GND, CLOCK, SIGNAL
 * The speed of this version is limited to a clock cycle time of 1ms for initial testing.
 * Higher speeds on Arduino and Raspberry Pi were already tested, maybe they will be added later on.
 */
//SIMON constants
#define DATA 7                                        // Data line for the SIMON protocol
#define SIGNAL 6                                      // Signal line for the SIMON protocol
#define OWN_NUMBER 1  // 1 for receiver, 0 for sender //device number of the device this code should run on
#define DEBUG_FLAG 1                                  // flag to indicate to use printing
//-------------------------------------------Variables for protocol----------------------------------------
// allocate memory for send/receive data + initialize counter for internal usage
const int characterLimit = 100;                       // maximum number of bytes to allocate for the data array
char transferBytes[characterLimit+3] = {0};           // initialize array used during the protocol. The offset of +3 is required for the protocol.
int charCounter = 0;                                  // counter for keeping track of how many chars are used in transferBytes
// timing constants
const unsigned int minimumSignalLength = 1000;        // time in us that is the minimum signal length of the SIMON protocol in microseconds
const int timeout = 100;                              // time in ms that will pass until the method getSignalLength() times out
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
void beginSIMON(int data_pin, int signal_pin) {
  pinMode(data_pin, INPUT_PULLUP);                    // set DATA line as output
  pinMode(signal_pin, OUTPUT);                        // set SIGNAL line as output
  digitalWrite(signal_pin,LOW);                       // signal line must stay as a low output for establishing a common ground
  Serial.begin(57600);                                //temp, for debugging
}

// emties the byte array, resets every byte to zero
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

// function to send a response during the transmission
void sendResponse() {
  pinMode(DATA,OUTPUT);                               // set the DATA pin as an output
  digitalWrite(DATA,HIGH);                            // end enable signal
  delayMicroseconds(minimumSignalLength);
  // send confirmationByte
  for(int i=7; i>=0; --i) {                           // loop over all bits
    digitalWrite(DATA, 0);                            
    if(1 & (responseByte >> i)) {                     // check if the current bit is 0 or 1
        delayMicroseconds(2*minimumSignalLength);     // if 1, wait for 2*minimum signal length
      } else {
        delayMicroseconds(minimumSignalLength);       // if 0, wait for minimum signal length
      }
      digitalWrite(DATA, 1);                          // end of single bit transfer
      delayMicroseconds(minimumSignalLength);         // wait minimum signal length                         
  }
}

// function to send data contained in byte array via the defined pins
void sendData() {
  pinMode(DATA,OUTPUT);                               // set the DATA pin as an output
  digitalWrite(DATA,HIGH);                            // end enable signal
  delayMicroseconds(2*minimumSignalLength);
  for(int i=0;i<8;i++){                               // instead of waiting for 16*minimumSignalLength, toggle DATA line
    digitalWrite(DATA,LOW);                           // change DATA pin to LOW
    delayMicroseconds(minimumSignalLength);           // delay for minimumSignalLength
    digitalWrite(DATA,HIGH);                          // change DATA pin to HIGH
    delayMicroseconds(minimumSignalLength);           // delay for minimumSignalLength
  }
  digitalWrite(DATA,LOW);                             // begin enable signal
  delayMicroseconds(3*minimumSignalLength);           // wait the time for enable signal
  digitalWrite(DATA,HIGH);                            // end enable signal
  delayMicroseconds(minimumSignalLength);
  for(int i=0; i<charCounter+3; ++i) {                // loop over binary String
    char current = transferBytes[i];                  // get current char from binary string
    for(int j=7; j>=0; --j) {                          // loop over bits of current char
      digitalWrite(DATA, LOW);
      if(1 & (current >> j)) {                        // check if the current bit is 0 or 1
        delayMicroseconds(2*minimumSignalLength);     // if 1, wait for 2*minimum signal length
      } else {
        delayMicroseconds(minimumSignalLength);       // if 0, wait for minimum signal length
      }
      digitalWrite(DATA,HIGH);                        // end of single binary String digit
      delayMicroseconds(minimumSignalLength);         // Wait minimum signal length
    }
  }
  digitalWrite(DATA,LOW);                             // begin disable signal
  delayMicroseconds(4*minimumSignalLength);           // wait the time for disable signal
  digitalWrite(DATA,HIGH);                            // end disable signal
}

byte getSignalLength() {                              //determines the length of the current signal on the signal line, function blocks when called
  bool loopStop=0;                                    //required boolean value for main loop
  byte signalLength=0;                                //declare value to return, used in later calculation
  bool timeoutFlag=0;
  unsigned long helperTime = 0;                       // helper variable inside the loop
  while(!loopStop) {                                  //main loop, determines signal length
    if(!digitalRead(DATA)) {                          //if signal is LOW
      unsigned long beginTime=micros();               //save current timestamp
      unsigned long endTime=0;                        //save current timestamp
      bool checkPoint=0;                              //variable to end while loop
      while(!checkPoint) {                            //when signal is still LOW
        helperTime = micros();
        if(digitalRead(DATA)){                        //if signal ends (e.g. goes from low to high)
          checkPoint=1;                               //break main loop
          endTime=helperTime;                           //save current time to determine signal length
        }
        if((helperTime-beginTime)/minimumSignalLength >= timeout){  //timeout for when the signal is not continued
          timeoutFlag=1;                                  //used for returning timeout
          checkPoint=1;                               //break loop
        }
      }
      signalLength=(endTime-beginTime)/minimumSignalLength;   //calculate signal length, middle factor based on micros() (1000) or millis() (1)
      loopStop=1;                                     //break the main loop
    }
  }
  if(!timeoutFlag){
    return signalLength;                              // return signal length, has to be between 1 and 4
  }else{
    return timeout;                                   // returns timeout as signal length
  }
}

void readData(bool awaitResponse) {                   // reads incoming data from the SIMON data lines and stores it in the static byte array
  pinMode(DATA,INPUT_PULLUP);                         // configure data line as an input to enable other communication
  delayMicroseconds(minimumSignalLength);
  if(awaitResponse) {                                 // response handling is different than rest
    responseByte = 0;                                 // re-initialize response byte to default value
    for(int i=7; i>= 0; --i) {                        // loop over all bits of the response byte
      responseByte |= (getSignalLength()==2) << i;    // compare current signal length and set bit accordingly
    }
    //delay(1000);
  } else {
    bool waitLoop = 0;                                // required boolean value to determine end of sent data
    while(!waitLoop) {                                // loop for waiting for the enable signal
      int currentSignalLength = getSignalLength();    // get the current signal length
      if(currentSignalLength==3) {                    // if current signal length is enable signal length
        waitLoop=1;                                   // break waiting loop
      }else if(currentSignalLength==timeout) {        // if current signal length is timed out
        waitLoop=1;
      }
    }
    bool mainLoop = 0;                                // required boolean value to gather data from line and end the transmission
    int charIndex = 0;                                // index in the static byte array where data is written to
    int bitIndex = 7;                                 // current bit index in the active byte
    while(!mainLoop) {                                // main loop, get every data signal from data line
      byte signalLength = getSignalLength();          // gets the signal length of the current signal sent over DATA line
      if (signalLength==4 || signalLength==timeout) { // check exit condition before handling rest
        mainLoop = 1;                                 // exit the main loop, indicating the end of the transmission
      } else {
        transferBytes[charIndex] |= (signalLength==2) << bitIndex;  // set current bit
        if(bitIndex > 0) {                            // check if bit index has not yet reached position 0
          --bitIndex;                                 // decrement bit index position to next position
        } else {                                      // bit index has reached position 0
          bitIndex = 7;                               // reset bit index to highest position
          ++charIndex;                                // increment char index to go to next char
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
  beginSIMON(DATA, SIGNAL);
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
      String input = readSIMONData();                 // get the command via SIMON, blocks until command is received
      delay(900);                                     // wait 2 seconds for next cycle
    } else {
      // program is sender
      sendSIMONData(1, "Hello World 2024!");          // send command to receiver
      delay(1000);                                    // wait 2 seconds for next cycle
    }
  }
}
