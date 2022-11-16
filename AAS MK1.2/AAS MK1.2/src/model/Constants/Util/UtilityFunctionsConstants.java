package model.Constants.Util;

/**
 * Interface containing all constants used in the UtilityFunctions class
 * @author Philipp Schulz
 */
public interface UtilityFunctionsConstants
{
    int ZERO_INDEX = 0;                                             // index of position 0
    int ONE_INDEX = 1;                                              // index of position 1
    int INCREMENT = 1;                                              // factor to increment by 1
    int MINIMUM_WORD_LENGTH = 1;                                    // minimum length of a word to filter out empty words or spaces
    int NUMBER_PARSE_FACTOR = 10;                                   // factor to multiply number with during integer conversion
    int NUMBER_PARSE_RESULT = 0;                                    // result if number is an integer
    int ZERO = 0;                                                   // number 0
    int INVERSION_FACTOR = -1;                                      // factor to remove the negative sign of the number
    int FLOATING_SUBSTRING_START = 2;                               // offset to get the floating part from a double number
    int TRIPLET_SIZE = 3;                                           // maximum size of a triplet
    int TRIPLET_SIZE_1 = 0;                                         // size of triplet of 0 digits
    int TRIPLET_SIZE_2 = 1;                                         // size of triplet of 1 digits
    int TRIPLET_SIZE_3 = 2;                                         // size of triplet of 2 digits
    int TWENTY = 20;                                                // number 20 to distinguish during number to word conversion

    double ROUND_CEILING_FACTOR = 0.5;                              // factor for rounding for numbers >=0.5 correctly

    String SPACE = " ";                                             // String that contains one space character
    String EMPTY_STRING = "";                                       // empty String
    String MINUS = "-";                                             // String that contains one minus character
    String MINUS_STRING = " minus";                                 // String that contains the word minus plus a spacer
    String PLUS = "+";                                              // String that contains one plus character
    String PLUS_STRING = " plus ";                                  // String that contains the word plus with two spacers
    String COMMA = ",";                                             // String that contains one comma character
    String POINT = ".";                                             // String that contains one point character
    String POINT_STRING = "point";                                  // String that contains the word point
    String QUESTION_MARK = "?";                                     // String that contains one question mark character
    String EXCLAMATION_MARK = "!";                                  // String that contains one exclamation mark character
    String[] UNDER_TWENTY = {"zero","one","two","three","four","five","six","seven",
            "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen",
            "sixteen","seventeen","eighteen","nineteen"};           // String array that contains all numbers between 0 and 19
    String[] TEENS_DIGITS = {"","","twenty","thirty","forty","fifty","sixty","seventy",
            "eighty","ninety"};                                     // String array that contains all 10 steps between 20 and 90
    String[] TEN_STEPS = {"","","thousand","million","billion","trillion","quadrillion","quintillion",
            "sextillion","septillion","octillion","nonillion","decillion"}; // String array that contains all 1000 steps between a thousand and a decillion
    String ZERO_STRING = "0";                                       // String that contains one 0 character
    String TRIPLET_ZERO = "000";                                    // String that contains 3 0 characters
    String HUNDRED = "hundred";                                     // String that contains one word for 100
}
