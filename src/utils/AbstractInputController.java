/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Fabian Rauscher
 */
public abstract class AbstractInputController
{
    /**
     * Initialize all listeners
     */
    public abstract void setUpInput();

    /**
     * Remove all listeners
     */
    public abstract void cleanUpInput();

}
