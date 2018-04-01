package com.santhosh.mtest;
import com.santhosh.mtest.User;

interface IPersistenceInterfaceCallBack {
   void handleLoginResponse(String token);
   void handleUserUpdate(out User user);
}
