/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springPlugin.exception;

/**
 *
 * @author Sandipan
 */
public class CustomException extends Exception {
    
    String message;
    
  public  CustomException(String msg) {
    message=msg;   
   }
   @Override
   public String getMessage(){
      return message ;       
   }
   
}
