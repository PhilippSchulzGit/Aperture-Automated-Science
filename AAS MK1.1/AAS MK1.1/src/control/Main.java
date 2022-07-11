package control;

import model.Constants.MainConstants;

/**
 * Highest level class of the program
 * @author Philipp Schulz
 */
public class Main implements MainConstants
{
    /**
     * Main method, entry point of the program
     * @param args Arguments given during call of the program
     * @author Philipp Schulz
     */
    public static void main(String[] args)
    {
        // create new object of the AAS class
        Aas aas = new Aas();
        // start the AAS program
        aas.startAAS();
    }
}
