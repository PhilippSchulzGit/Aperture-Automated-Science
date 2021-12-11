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
    public String numberToString(int number)
    {
        return numberToString(number+EMPTY_STRING);
    }

    /**
     * Method for converting a given Double into a String of the English language (e.g. 1,32->"one point three two")
     * @param number Double that should be converted into a String
     * @return Given number as a String of the English language
     * @author Philipp Schulz
     */
    public String numberToString(double number)
    {
        return numberToString(number+EMPTY_STRING);
    }

    /**
     * Method for converting a given String of a number into a String of the English language
     * @param number Number that should be converted into a String
     * @return Given number as a String of the English language
     * @author Philipp Schulz
     */
    public String numberToString(String number)
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for removing all last zeros in the given String
     * @param string String of which all last zeros should be removed
     * @return String without any last zeros at the end
     * @author Philipp Schulz
     */
    public String removeLastZeros(String string)
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for converting a given number as a String into a String containing all single digits
     * @param number String that contains the number to convert (supports "." too)
     * @return All single digits of the given number in a String
     * @author Philipp Schulz
     */
    public String numberToSingleDigits(String number)
    {
        //TODO: IMPLEMENT METHOD
        return "";
    }

    /**
     * Method for converting a given String into an Arraylist of the individual words
     * @param text Text that contains all words to be split into an ArrayList
     * @return ArrayList<String> containing all single words of the given text
     * @author Philipp Schulz
     */
    public ArrayList<String> extractSingleWordsFromText(String text)
    {
        //TODO: IMPLEMENT METHOD
        return new ArrayList<>();
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
