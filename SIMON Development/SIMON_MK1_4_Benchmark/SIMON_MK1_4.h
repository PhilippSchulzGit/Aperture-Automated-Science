/* 
 * Header of the SIMON MK1.4 protocol.
 * This version refactors the existing code from MK1.3 into a single class.
 * The class makes it much easier and more comfortable to use it.
 * 
 * Created on 2024-07-14.
 */
#ifdef ARDUINO
#include <Arduino.h>
#endif
#ifndef SIMON_H
#define SIMON_H

class SIMON {
  private:
    const byte characterLimit;                        // maximum number of bytes to allocate for the data array
    char* transferBytes;                              // array used during the protocol
    uint8_t dataPin;                                  // pin to transmit data
    uint8_t signalPin;                                // pin to transmit signal
    const byte ownNumber;                             // own number on the bus
    const int minimumSignalLength;                    // time in us that is the minimum signal length of the SIMON protocol in microseconds
    const byte confirmationByte = 109;                // data to send to indicate a confirmation, binary is "01101101"
    const byte errorByte = 170;                       // data to send to indicate an error, binary is "10101010"
	  
    void emptyByteArray(byte& charCounter);           // empties the byte array, resets every byte to zero
	  void loadPayload(char* text, byte& charCounter);  // copy a given char array to the static byte array of SIMON
	  String retreivePayload(const byte& charCounter);  // copy the active content in the static byte array of SIMON to a new char array
	  void getFletcher16Checksums(byte& sum1, byte& sum2, const byte& charCounter);   // get the checksums from Fletcher-16 for a given binary data String
	  void sendResponse(const byte& responseByte);      // send a response during the transmission
	  void sendData(const byte& charCounter);           // send data contained in byte array via the defined pins
	  void readData(const bool awaitResponse, byte& responseByte, byte& charCounter);  // read incoming data from the SIMON data lines and store it in the static byte array
	
  public:
    SIMON(const byte number, const int minimumSignalLength=50, const byte characterLimit=100);  // Constructor
	  ~SIMON();                                         // Destructor
	  void begin(const uint8_t newDataPin, const uint8_t newSignalPin);  // sets up the pins for transmission
	  void sendSIMONData(const byte receiver_number, const char* command);  // send a given char array via the SIMON protocol
	  String readSIMONData();                           // read a message sent by another device via the SIMON protocol
};
#endif
