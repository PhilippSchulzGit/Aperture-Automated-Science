/*
 * Sketch to benchmark performance of the SIMON protocol.
 * Also acts as an example on how to use it.
 * SIMON MK0.0 is used here.
 * This version is the original code that was used in the first usable state of GLaDOS as a whole system.
 * This version uses dynamic String objects to store the individual bits.
 * The minimumSignalLength is 1 ms.
 * 
 * Created on 2024-07-11.
 */
//--------------------------------------------------SIMON---------------------------------------------------------------------------------
//SIMON constants
#define DATA 7                                        //Data line for the SIMON protocol
#define SIGNAL 6                                      //Signal line for the SIMON protocol
#define OWN_NUMBER 1  // 1 for receiver, 0 for sender //device number of the device this code should run on
#define MINIMUM_SIGNAL_LENGTH 1                       //time in ms that is the minimum signal length of the SIMON protocol
#define TIMEOUT 200                                   //time in ms that will pass until the method getSignalLength() times out

#define DEBUG_FLAG 0                                  // flag to indicate to use printing
//----------------------------------------Variables for protocol---------------------------
String readBinaryData="0100100001100101011011000110110001101111001000000101011101101111011100100110110001100100001000000011001000110000001100100011010000100001";
String receivedBinaryNumber="00000010";
int receivedNumber=0;
String textData="001100010010000000110001";
String command="1 1";
String receivedChecksums="1000001000000101";
String ownChecksums="1000001000000101";
String confirmationString="11111111";
//----------------------------------------Variables for protocol---------------------------
//SIMON functions
void begin_SIMON(int data_pin, int signal_pin) {      //function to set up the pins for the SIMON protocol
  pinMode(data_pin, INPUT_PULLUP);                    //set DATA line as output
  pinMode(signal_pin, OUTPUT);                        //set SIGNAL line as output
  digitalWrite(signal_pin,LOW);                       //signal line must stay as a low output for establishing a common ground
  Serial.begin(57600);                                //temp, for debugging
}

String textToBinary(String text) {                    //function for converting a String containing text into a String containing binary
  String binaryText="";                               //String that wil contain the binary representation of the text
  for(int i=0; i<text.length(); i++){                 //main loop, go over every char and convert it into 8bit binary
   char currentChar = text.charAt(i);                 //get cuttent char
   for(int i=7; i>=0; i--){                           //iterate over all its of char, from msb to lsb (bit loop)
      byte bytes = bitRead(currentChar,i);            //read current bit
      if(bytes==0) {                                  //decide what to add to binary String
        binaryText+="0";
      } else {
        binaryText+="1";
      }
    }                                                 //end bit loop
  }                                                   //end main loop
  return binaryText;                                  //return binary representation of text as a String
}

String binaryToText(String binary) {                  //function for converting a String containing binary into a String containing text
  String text="";                                     //initialize the String containing the result text
  byte countBit=7;                                    //variable required to go over all individual bits of a byte
  byte countByte=0;                                   //variable required to keep track of which byte is currently written to
  int charSize=binary.length()/8;                     //get the required size for a byte array
  byte allChars[charSize];                            //initialize a new byte array with a fitting size
  for(int i=0;i<binary.length();i++) {                //main loop, going over every binary digit
      if(binary.charAt(i)=='1') {                     //check which digit is currently given in the specified position of the String
        bitSet(allChars[countByte],countBit);         //set the corresponding bit in the byte array to true
      } else {
        bitClear(allChars[countByte],countBit);       //set the corresponding bit in the byte array to false
      }
      if(countBit>0) {                                //required to change the count variables of the bit and byte
        countBit--;                                   //decrement countBit if greater than 0
      } else {
        countBit=7;                                   //set countBit to 7 if equal to or smaller than 0
        countByte++;                                  //increment countByte by 1 for the next byte
      }
  }
  for(int i=0;i<charSize;i++) {                       //loop over entire byte array to construct result String
    char singleChar = allChars[i];                    //get the current byte of the array and cast it to a char
    text+=String(singleChar);                         //add the current char to the result String
  }
  return text;                                        //return text converted from binary
}

String intToBinary(int receiver_number) {             //convert integer to binary String
  return textToBinary(String((char)receiver_number)); //use function textToBinary to convert the integer into a binary String
}

int binaryToInt(String binary) {                      //convert binary to integer
  int number = 0;
  for(int i=0; i < binary.length(); i++) {
    if('1' == binary.charAt(i)) {
      number += round(pow(2, binary.length()-i-1));
    }
  }
  return number;
  // OLD CODE
  /*
  char s[8];                                          //char array that will be used to convert the binary String to an integer
  binary.toCharArray(s, 9);                           //convert the String into a char array
  return strtol(s, (char**) NULL, 2);                 //return an integer got from the char array
  */
}
void binaryNumberConversion(){
  receivedNumber = binaryToInt(receivedBinaryNumber);
  // OLD CODE
  /*
  char s[8];                                          //char array that will be used to convert the binary String to an integer
  receivedBinaryNumber.toCharArray(s, 9);                           //convert the String into a char array
  receivedNumber= strtol(s, (char**) NULL, 2);                 //return an integer got from the char array
  */
}

void sendDataString(String binaryString) {            //sending the contents of a String containing binary data including begin and end signal
  pinMode(DATA,OUTPUT);                               //set the DATA pin as an output
  digitalWrite(DATA,HIGH);                            //end enable signal
  delay(2*MINIMUM_SIGNAL_LENGTH);
  for(int i=0;i<8;i++){                               //instead of waiting for 16*minimumSignalLength, toggle DATA line
    digitalWrite(DATA,LOW);                           //change DATA pin to LOW
    delay(MINIMUM_SIGNAL_LENGTH);                     //delay for minimumSignalLength
    digitalWrite(DATA,HIGH);                          //change DATA pin to HIGH
    delay(MINIMUM_SIGNAL_LENGTH);                     //delay for minimumSignalLength
  }
  digitalWrite(DATA,LOW);                             //begin enable signal
  delay(3*MINIMUM_SIGNAL_LENGTH);                     //wait the time for enable signal
  digitalWrite(DATA,HIGH);                            //end enable signal
  delay(MINIMUM_SIGNAL_LENGTH);
  for(int i=0;i<binaryString.length();i++) {          //loop over binary String
    char current=binaryString.charAt(i);              //get current char from binary string
    digitalWrite(DATA,LOW);                           //begin signal binary String digit
    if(current=='0') {                                //find out if current char is 0 or 1
      delay(MINIMUM_SIGNAL_LENGTH);                   //if 0, wait for minimum signal length
    } else {
      delay(2*MINIMUM_SIGNAL_LENGTH);                 //if 1, wait for 2*minimum signal length
    }
    digitalWrite(DATA,HIGH);                          //end of single binary String digit
    delay(MINIMUM_SIGNAL_LENGTH);                     //Wait minimum signal length
  }
  digitalWrite(DATA,LOW);                             //begin disable signal
  delay(4*MINIMUM_SIGNAL_LENGTH);                     //wait the time for disable signal
  digitalWrite(DATA,HIGH);                            //end disable signal
  delay(2);                                           //wait until receiver device notices last digital level
}

byte getSignalLength() {                              //determines the length of the current signal on the signal line, function blocks when called
  bool loopStop=0;                                    //required boolean value for main loop
  byte signalLength=0;                                //declare value to return, used in later calculation
  bool timeout=0;
  while(!loopStop) {                                  //main loop, determines signal length
    if(!digitalRead(DATA)) {                          //if signal is LOW
      unsigned long beginTime=micros();               //save current timestamp
      unsigned long endTime=0;                        //save current timestamp
      bool checkPoint=0;                              //variable to end while loop
      while(!checkPoint) {                            //when signal is still LOW
        if(digitalRead(DATA)){                        //if signal ends (e.g. goes from low to high)
          checkPoint=1;                               //break main loop
          endTime=micros();                           //save current time to determine signal length
        }
        if((micros()-beginTime)/(1000*MINIMUM_SIGNAL_LENGTH)>=TIMEOUT){  //timeout for when the signal is not continued
          timeout=1;                                  //used for returning timeout
          checkPoint=1;                               //break loop
        }
      }
      signalLength=(endTime-beginTime)/(1000*MINIMUM_SIGNAL_LENGTH);   //calculate signal length, middle factor based on micros() (1000) or millis() (1)
      loopStop=1;                                         //break the main loop
    }
  }
  if(!timeout){
    return signalLength;                              //return signal length, has to be between 1 and 4
  }else{
    return TIMEOUT;                                   //returns timeout as signal length
  }
}

void readData() {                                   //reads and returns incoming data from the SIMON data lines as a binary String (from enable signal to disable signal)
  pinMode(DATA,INPUT_PULLUP);                         //configure data line as an input to enable other communication
  delay(1);
  readBinaryData="";                                 //temporary set the received data
  bool waitLoop=0;                                    //required boolean value to determine end of sent data
  while(!waitLoop) {                                  //loop for waiting for the enable signal
    int currentSignalLength=getSignalLength();        //get the current signal length
    if(currentSignalLength==3) {                      //if current signal length is enable signal length
      waitLoop=1;                                     //break waiting loop
    }else if(currentSignalLength==TIMEOUT){           //if current signal length is timed out
      waitLoop=1;
    }
  }
  bool mainLoop=0;                                    //required boolean value to gather data from line and end the transmission
  while(!mainLoop) {                                  //main loop, get every data signal from data line
    byte signalLength=getSignalLength();              //gets the signal length of the current signal sent over DATA line
    if(signalLength==1) {                             //if the read signal length is 1
      readBinaryData+="0";                                  //add a 0 to the received data
    } else if(signalLength==2) {                      //if the read signal length is 2
      readBinaryData+="1";                                  //add a 1 to the received data
    } else if(signalLength==4||signalLength==TIMEOUT) {  //if the read signal length is 4
      mainLoop=1;                                     //exit the main loop, indicating the end of the transmission
    }
  }
  //return readData;                                    //return the binary String containing the read signals of the DATA line
}

void sendSIMONData(int receiver_number,String command){
  bool confirmation=0;
  do{
    String binaryCommand=textToBinary(command);
    // its the checksums that cause the protocol to not work!
    String binaryString=intToBinary(receiver_number)+binaryCommand+getFletcher16Checksums(binaryCommand);
    Serial.println("created binary string");
    sendDataString(binaryString);
    Serial.println("sent binary string");
    readData();
    Serial.println("received binary string: " + readBinaryData);
    if(confirmationString.equals(readBinaryData)){
      confirmation=1;
    }
  }while(!confirmation);
  Serial.println("transmission ok");
}

String readSIMONData(){
  bool confirmation=0;
  do{
    readData();
    receivedBinaryNumber = readBinaryData.substring(0,8);//binary receiver number
    binaryNumberConversion();
    textData=readBinaryData.substring(8,readBinaryData.length()-16); //get binary text
    command=binaryToText(textData);            //convert binary to text
    receivedChecksums = readBinaryData.substring(readBinaryData.length()-16); //convert binary to checksums
    ownChecksums=getFletcher16Checksums(textData);
    if(receivedChecksums.equals(ownChecksums) && (receivedNumber==OWN_NUMBER)){
      sendDataString(confirmationString);
      confirmation=1;
    }
  }while(!confirmation);
  return command;
}

// method for getting the checksums from Fletcher-16 for a given binary data String
// returns the Checksums in a binary string in the format: "sum1sum2"
String getFletcher16Checksums(String binaryData) {
  byte sumFletcher1=0;                                //reset sum1
  byte sumFletcher2=0;                                //reset sum2
  byte binLength=binaryData.length()/8;               //calculate number of blocks of 8
  for(byte i=0;i<binLength;i++) {                     //loop over data String for each block and calculate the sums
    sumFletcher1=(sumFletcher1+binaryToInt(binaryData.substring(i*8,i*8+8))) % 255; //calculate checksum1
    sumFletcher2=(sumFletcher1+sumFletcher2) % 255;   //calculate checksum2
  }
  return intToBinary(sumFletcher1)+intToBinary(sumFletcher2); //return checksums as binary String
}

bool zerosOrOnes(String binaryData){                  //method to determine if a given binary String contains more zeros or ones
  int ones=0;                                         //initialize counter for ones
  int zeros=0;                                        //initialize counter for zeros
  for(int i=0;i<binaryData.length();i++){             //loop over given binary String
    char currentChar = binaryData.charAt(i);          //get cuttent char
    if(currentChar=='1'){                             //if current char is 1
      ones++;                                         //increment counter for ones
    }else{                                            //if current char is 0
      zeros++;                                        //increment counter for zeros
    }
  }
  if(ones<=zeros){                                    //if the amount of ones is smaller than or equal to the number of zeros
    return 0;
  }else{                                              //if the amount of ones is greater than the number of zeros
    return 1;
  }
}
//--------------------------------------------------SIMON---------------------------------------------------------------------------------

//--------------------------------------------------MAIN PROGRAM--------------------------------------------------------------------------
void setup() {
  //initialize SIMON protocol
  begin_SIMON(DATA,SIGNAL);
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
	// verbose version, prints to console
    if(OWN_NUMBER) {
      // program is receiver
      long t0 = micros();
      String input = readSIMONData();                     //get the command via SIMON, blocks until command is received
      long t1 = micros();
      Serial.println("receive transmission took " + String(round((t1-t0)/1000.0f)) + " ms");
      Serial.println("received: " + input);               // print received data to Serial
      delay(900);                                        // wait 2 seconds for next cycle
    } else {
      // program is sender
      long t0 = micros();
      sendSIMONData(1, "Hello World 2024!");              // send command to receiver
      long t1 = micros();
      Serial.println("send transmission took " + String(round((t1-t0)/1000.0f)) + " ms");
      delay(1000);                                        // wait 2 seconds for next cycle
    }
    Serial.println("-------");
  } else {
	// silent version, does not print to console
    if(OWN_NUMBER) {
      // program is receiver
      String input = readSIMONData();                     //get the command via SIMON, blocks until command is received
      delay(900);                                        // wait 2 seconds for next cycle
    } else {
      // program is sender
      sendSIMONData(1, "Hello World 2024!");              // send command to receiver
      delay(1000);                                        // wait 2 seconds for next cycle
    }
  }
}
