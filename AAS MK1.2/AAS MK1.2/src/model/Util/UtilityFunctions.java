package model.Util;

import model.Constants.Util.UtilityFunctionsConstants;

import java.util.ArrayList;

/**
 * Class that contains utility functions that do not belong in any other class
 * Can exist multiple times
 * @author Philipp Schulz
 */
public class UtilityFunctions implements UtilityFunctionsConstants
{
    /**
     * Method for converting a given Integer into a String of the English language (e.g. 210->"two hundred ten")
     * @param number Integer that should be converted into a String
     * @return Given number as a String of the English language
     * @author Philipp Schulz
     */
    public static String numberToString(int number)
    {
        return numberToString(number+EMPTY_STRING);
    }

    /**
     * Method for converting a given Double into a String of the English language (e.g. 1,32->"one point three two")
     * @param number Double that should be converted into a String
     * @return Given number as a String of the English language
     * @author Philipp Schulz
     */
    public static String numberToString(double number)
    {
        return numberToString(number+EMPTY_STRING);
    }

    /**
     * Method for converting a given String of a number into a String of the English language
     * @param number Number that should be converted into a String
     * @return Given number as a String of the English language
     * @author Philipp Schulz
     */
    public static String numberToString(String number)
    {
        // initialize return value
        StringBuilder numberString = new StringBuilder(EMPTY_STRING);
        // initialize floating point and integer parts of number
        long numberDouble = ZERO;
        long numberLong;
        // parse string as numbers for conversion handling
        if(number.contains(POINT))  // if a floating point number is given
        {
            numberDouble = Long.parseLong(number.substring(number.indexOf(POINT)+INCREMENT));
            numberLong = Long.parseLong(number.substring(ZERO,number.indexOf(POINT)));
        }
        else    // if a pure integer is given
        {
            numberLong = Long.parseLong(number);
        }
        // if the integer part of the number is zero
        if(numberLong==ZERO)
        {
            // add zero and space to return value
            numberString.append(UNDER_TWENTY[ZERO]).append(SPACE);
        }
        // if the number is negative
        if(numberLong<ZERO)
        {
            // add minus sign and space to return value
            numberString.append(MINUS_STRING + SPACE);
            // adjust numbers to avoid multiple minus sings
            numberLong *= INVERSION_FACTOR;
            numberDouble *= INVERSION_FACTOR;
        }
        // create new strings for adjusted number rest
        String newNumberInt = numberLong+EMPTY_STRING;
        String newNumberFloating = numberDouble+EMPTY_STRING;
        // determine how many triplets are in the integer part of the number
        int tripletCount = (int)(newNumberInt.length()/TRIPLET_SIZE + ROUND_CEILING_FACTOR + INCREMENT);
        // initialize ArrayList that will contain the triplets
        ArrayList<String> triplets = new ArrayList<>();
        // loop over the number of triplets in the integer part of the number
        for(int i = 0; i < tripletCount; i++)
        {
            // check if the integer part of the number is larger than a single triplet
            if((newNumberInt.length() - (i*TRIPLET_SIZE)) > TRIPLET_SIZE)
            {
                // extract current triplet from number String, starting from right side
                triplets.add(newNumberInt.substring(newNumberInt.length()-TRIPLET_SIZE));
                // adjust integer part of number, remove current triplet
                newNumberInt = newNumberInt.substring(ZERO_INDEX,newNumberInt.length()-TRIPLET_SIZE);
            }
            else    // if the integer part of the number is equal or smaller than a single triplet
            {
                // add triplet to ArrayList
                triplets.add(newNumberInt);
            }
        }
        // loop backwards over extracted triplets, starting with the "largest" triplet
        for(int i = tripletCount-INCREMENT; i >= 0; i--)
        {
            numberString.append(convertTripletToWords(triplets.get(i), i + INCREMENT));
        }
        // check if there is a floating point remainder
        if(!newNumberFloating.equals(ZERO+EMPTY_STRING))
        {
            // convert the floating point part of the number to single digit words
            numberString.append(numberToSingleDigits(POINT+newNumberFloating));
        }
        // return the final String
        return numberString.toString();
    }

    /**
     * Method for converting a given triplet of digits into words
     * @param triplet String that contains a number with 3 digits
     * @param weight Weight of the decimal name for the triplet (e.g. a million, a billion)
     * @return Given number converted into English words
     */
    private static String convertTripletToWords(String triplet, int weight)
    {
        // initialize return String and helper Strings
        String tripletWords = EMPTY_STRING;
        String triplet1, triplet2, triplet3, triplet23, triplet123;
        // assign triplets based on length of the given triplet
        switch(triplet.length())
        {
            case TRIPLET_SIZE_1:    // if the String has length 0
                // assign all helper Strings to "0"
                triplet1 = ZERO_STRING;
                triplet2 = ZERO_STRING;
                triplet3 = ZERO_STRING;
                break;
            case TRIPLET_SIZE_2:    // if the String has length 1
                triplet1 = ZERO_STRING;
                triplet2 = ZERO_STRING;
                triplet3 = triplet;
                break;
            case TRIPLET_SIZE_3:   // if the String has length 2
                triplet1 = ZERO_STRING;
                triplet2 = triplet.substring(TRIPLET_SIZE_1,TRIPLET_SIZE_2);
                triplet3 = triplet.substring(TRIPLET_SIZE_2);
                break;
            default:               // if the String has length 3
                triplet1 = triplet.substring(ZERO,TRIPLET_SIZE_1);
                triplet2 = triplet.substring(TRIPLET_SIZE_1,TRIPLET_SIZE_2);
                triplet3 = triplet.substring(TRIPLET_SIZE_2,TRIPLET_SIZE_3);
                break;
        }
        // create triplet combinations required for next part
        triplet23 = triplet2+triplet3;
        triplet123 = triplet1 + triplet23;
        // check if total triplet is not zero, ignore if it is
        if(!triplet123.equals(TRIPLET_ZERO))
        {
            // handle triplet1 helper String if it is not zero
            if(Integer.parseInt(triplet1) != ZERO)
            {
                // add the hundred part to number
                tripletWords += UNDER_TWENTY[Integer.parseInt(triplet1)] + SPACE + HUNDRED + SPACE;
            }
            // check if triplet23 is less than twenty
            if(Integer.parseInt(triplet23) < TWENTY)
            {
                // add last two digits as one combination
                tripletWords += UNDER_TWENTY[Integer.parseInt(triplet23)] + SPACE;
            }
            else    // if the number part is larger than 20, split triplet2 and triplet3
            {
                // handle triplet2 if greater than zero
                if(!triplet2.equals(ZERO_STRING))
                {
                    // add the teens part to number
                    tripletWords += TEENS_DIGITS[Integer.parseInt(triplet2)] + SPACE;
                }
                // handle triplet3 if greater than zero
                if(!triplet3.equals(ZERO_STRING))
                {
                    // add the single digit part to number
                    tripletWords += UNDER_TWENTY[Integer.parseInt(triplet3)] + SPACE;
                }
            }
            // apply total weight to triplet (thousand, million, billion, ...)
            if(weight>TRIPLET_SIZE_1)
            {
                // add triplet weight as appended word
                tripletWords += TEN_STEPS[weight] + SPACE;
            }
        }
        // return final String
        return tripletWords;
    }

    /**
     * Method for converting a given number as a String into a String containing all single digits
     * @param number String that contains the number to convert (supports "." too)
     * @return All single digits of the given number in a String
     * @author Philipp Schulz
     */
    public static String numberToSingleDigits(String number)
    {
        // initialize return String
        StringBuilder singleDigits = new StringBuilder(EMPTY_STRING);
        // loop over all characters of the given number String
        for(int i = 0; i < number.length();i++)
        {
            // extract the current character
            String currentChar = number.substring(i,i+ONE_INDEX);
            // if current character is not a point
            if(!currentChar.equals(POINT))
            {
                // append single digit to return String
                singleDigits.append(UNDER_TWENTY[Integer.parseInt(currentChar)]);
            }
            else    // if current character is a point
            {
                // append point to return String
                singleDigits.append(POINT_STRING);
            }
            // append space to return String
            singleDigits.append(SPACE);
        }
        // return
        return singleDigits.toString();
    }

    /**
     * Method for converting a given String into an Arraylist of the individual words
     * @param text Text that contains all words to be split into an ArrayList
     * @return ArrayList<String> containing all single words of the given text
     * @author Philipp Schulz
     */
    public static ArrayList<String> extractSingleWordsFromText(String text)
    {
        // initialize ArrayList for words
        ArrayList<String> words = new ArrayList<>();
        // save local copy of parameter text
        String message = text;
        // replace question mark and exclamation mark with empty strings
        message = message.replace(QUESTION_MARK,EMPTY_STRING);
        message = message.replace(EXCLAMATION_MARK,EMPTY_STRING);
        // split message into single words and store them in ArrayList
        if(message.contains(SPACE))    // if the text contains multiple words
        {
            // loop until no space is left in the text
            while(message.contains(SPACE))
            {
                // save word to ArrayList
                words.add(message.substring(ZERO_INDEX,message.indexOf(SPACE)));
                // adjust text for next loop
                message = message.substring(message.indexOf(SPACE)+INCREMENT);
            }
            // add last word if present
            if(message.length()>=MINIMUM_WORD_LENGTH)
            {
                words.add(message.trim());
            }
        }
        else                            // if the text only contains one word
        {
            words.add(message.trim());  // add single word to ArrayList
        }
        // go over all words and convert numbers to text if required
        for(int i=0;i<words.size();i++)
        {
            // try to parse current word to Double
            try
            {
                // parse word to number
                double doubleWord = Double.parseDouble(words.get(i));
                // check if number is an integer
                if((doubleWord*NUMBER_PARSE_FACTOR)%NUMBER_PARSE_FACTOR == NUMBER_PARSE_RESULT)
                {
                    // parse integer to text
                    words.set(i,numberToString(Integer.parseInt(words.get(i))).trim());
                }
                else // if number is floating point
                {
                    // parse double to text
                    words.set(i,numberToString(doubleWord).trim());
                }
            }
            catch(Exception e)
            {
                // word is not a number, remove point
                words.set(i,words.get(i).replace(POINT,EMPTY_STRING));
                // replace common characters with appropriate words
                words.set(i,words.get(i).replace(MINUS,MINUS_STRING));
                words.set(i,words.get(i).replace(PLUS,PLUS_STRING));
            }
        }
        // initialize return value
        ArrayList<String> newWords = new ArrayList<>();
        // final loop over words, split new words from number conversion
        for(String word : words)
        {
            // if the current word contains a space
            if(word.contains(SPACE))
            {
                // split current word into all sub words
                String[] multipleWords = word.split(SPACE);
                // loop over sub words
                for(String newWord : multipleWords)
                {
                    // if the current sub word is not just a space or an empty String
                    if(!newWord.equals(SPACE) && !newWord.equals(EMPTY_STRING))
                    {
                        // add current sub word to new word list
                        newWords.add(newWord);
                    }
                }
            }
            else
            {
                // just add the word
                newWords.add(word);
            }
        }
        // return words
        return newWords;
    }

    /**
     * Method for converting a String containing binary into a String containing ASCII text
     * @param binaryString String with binary data to convert
     * @return ASCII-formatted String based on the given binary input
     * @author Philipp Schulz
     */
    public String binaryToString(String binaryString)
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for converting an ASCII-formatted String into its binary representation
     * @param string ASCII-formatted String that should be converted
     * @return String containing the binary representation of the input String
     * @author Philipp Schulz
     */
    public String stringToBinary(String string)
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for converting a String with binary data into an Integer
     * @param binaryString String with binary data to convert
     * @return Integer of the binary input data
     * @author Philipp Schulz
     */
    public int binaryToInt(String binaryString)
    {
        return Integer.parseInt(binaryToString(binaryString));
    }

    /**
     * Method for converting an Integer into its binary representation in form of a String
     * @param integer Integer that should be converted
     * @return String with binary data based on the given Integer
     * @author Philipp Schulz
     */
    public String intToBinary(int integer)
    {
        return binaryToString(integer+EMPTY_STRING);
    }

    /**
     * Method for extracting the Fletcher-16 checksums from a given binary data String
     * @param binaryString String containing binary data
     * @return Fletcher-16 checksums of the given binary input (formatted like "number1number2")
     * @author Philipp Schulz
     */
    public String getFletcher16Checksums(String binaryString)
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for determining if the given binary String contains more ones or zeros
     * @param binaryString String with binary data that should be checked
     * @return True if more ones are in the given binary data
     */
    public boolean zerosOrOnes(String binaryString)
    {
        //TODO: IMPLEMENT METHOD
        return false;
    }
}
