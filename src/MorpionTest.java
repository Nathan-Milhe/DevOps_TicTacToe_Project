
import org.junit.Test;
import static org.junit.Assert.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author remy
 */


public class MorpionTest {
    
    public Morpion testmorpion;
    
    public MorpionTest()
    {
    	testmorpion = new Morpion();
    }
    
    
    @Test
    public void testAssertthatEqualsDraw()
    {
    	assertFalse(testmorpion.plateau.isDraw());
    }
    
    
    @Test
    public void testAssertthatEqualsWon()
    {
       assertFalse(testmorpion.plateau.won('X'));
    }
    
}
