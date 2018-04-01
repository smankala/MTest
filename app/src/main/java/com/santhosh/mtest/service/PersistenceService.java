package com.santhosh.mtest.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.santhosh.mtest.IPersistenceInterface;
import com.santhosh.mtest.IPersistenceInterfaceCallBack;
import com.santhosh.mtest.LoginUser;
import com.santhosh.mtest.MTestApplication;
import com.santhosh.mtest.User;
import com.santhosh.mtest.network.LoginService;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class PersistenceService extends Service{

    LoginService loginService;
    /**
     * Hack Alert
     * Login call doesnt return id of user
     * only create user returns id  and common thread among both is username
     *
     * In order to proceed to editUser screen user below maps to find username -> id map for a login call
     *
     */
    Map<String,Integer> userNameToIdMap = new HashMap<String, Integer>();
    Map<String,String> userNameToTokenMap = new HashMap<String, String>();

    @Override
    public void onCreate() {
        super.onCreate();
        loginService = ((MTestApplication)getApplication()).getRetroFit().create(LoginService.class);
    }

    User user;
    String token;

    IPersistenceInterface.Stub persistenceStub = new IPersistenceInterface.Stub() {

        @Override
        public void getCurrentAuthedUser(String userName, IPersistenceInterfaceCallBack callBack) throws RemoteException {
            String token = userNameToTokenMap.get(userName);
            Integer id = userNameToIdMap.get(userName);
            loginService.getUser("JWT "+token, id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((user) -> {
                        userNameToTokenMap.put(userName,token);
                        handleAuthResponse(callBack, token);
                    },(ignored)->
                            handleAuthResponse(callBack,null));
        }

        @Override
        public void clearSession() throws RemoteException {
            user = null;
        }

        @Override
        public int getUserId(String username) throws RemoteException {
            if(userNameToIdMap.containsKey(username)){
                return userNameToIdMap.get(username);
            }
            return -1;
        }


        @Override
        public void loginUser(String userName, String password, IPersistenceInterfaceCallBack callBack) throws
                RemoteException {
            LoginUser user = new LoginUser();
            user.username = userName;
            user.password = password;
            loginService.login(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((authtoken) -> {
                        token = authtoken.getAccessToken();
                        userNameToTokenMap.put(userName,token);
                        handleAuthResponse(callBack, token);

            },(ignored)->
                    handleAuthResponse(callBack,null));
        }

        @Override
        public void createUser(User user, IPersistenceInterfaceCallBack callBack) throws RemoteException {
            loginService.createUser(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((createduser)->handleCreateUserResponse(callBack,createduser),(ignored)
                            ->handleCreateUserResponse(callBack,null));

        }

        @Override
        public void updateUser(User user, IPersistenceInterfaceCallBack callBack) throws RemoteException {
            String token = userNameToTokenMap.get(user.username);
            loginService.update("JWT "+token,user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((createduser)->handleCreateUserResponse(callBack,createduser),(ignored)
                            ->handleCreateUserResponse(callBack,null));
        }
    };

    private void handleAuthResponse(IPersistenceInterfaceCallBack callBack, String token) {
        try {
            callBack.handleLoginResponse(token);
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    private void handleCreateUserResponse(IPersistenceInterfaceCallBack callBack, User user) {
        try {
            if(user != null){
                userNameToIdMap.put(user.username,user.id);
            }
            callBack.handleUserUpdate(user);
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return persistenceStub;
    }
}
