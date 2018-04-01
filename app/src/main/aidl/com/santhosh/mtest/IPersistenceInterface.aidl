package com.santhosh.mtest;
import com.santhosh.mtest.User;
import com.santhosh.mtest.IPersistenceInterfaceCallBack;

interface IPersistenceInterface {
        void getCurrentAuthedUser(String userName, IPersistenceInterfaceCallBack callBack);
        void clearSession();
        void loginUser(in String userName, in String password, IPersistenceInterfaceCallBack callBack);
        void createUser(in User user,IPersistenceInterfaceCallBack callBack);
        void updateUser(in User user,IPersistenceInterfaceCallBack callBack);
        int getUserId(String username);
}
