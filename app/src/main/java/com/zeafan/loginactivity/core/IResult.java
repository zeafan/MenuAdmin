package com.zeafan.loginactivity.core;

/**
 * Created by mahmoudam on 8/8/2018.
 */

public interface IResult {

     void notifySuccess(Object response);
     void notifyError(Object error);

}

