This is the second revision of the program for Aperture Automated Science (AAS) and implements the voice recognition for program control.

You will need the following libraries in order to run it:
CMU
jSApi
jSerialComm
jUnit
Pi4J

Adding the libraries is done by copying the above mentioned folders from the root library folder in the GitHub repository into the "lib" folder of this program revision.
This program revision really just needs the CMU and jSApi libraries, the rest can be removed in the program as they are not required for the implemented functionality.

You will also need to copy over the folder 'CMUSphinx' from the 'resources' folder from root into this program revisions folder. Please keep the files in the program revisions folder if any conflicts arise.
(this way, I can save some storage space because the language model for the voice recognition is quite large).
