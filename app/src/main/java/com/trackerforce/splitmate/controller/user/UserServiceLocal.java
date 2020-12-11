package com.trackerforce.splitmate.controller.user;

import android.content.Context;
import android.util.Log;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.dao.LoginRepository;
import com.trackerforce.splitmate.model.Login;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.utils.Config;

import java.util.List;

public class UserServiceLocal {

    private static final String TAG = UserServiceLocal.class.getSimpleName();

    private final LoginRepository loginRepository;

    public UserServiceLocal(Context context) {
        loginRepository = new LoginRepository(context);
    }

    public void getUser(ServiceCallback<User> callback) {
        try {
            List<Login> loginList = loginRepository.findAll();

            if (loginList.size() > 0)
                callback.onSuccess(loginList.get(0).getUser());
            else
                callback.onError("User not found");

        } catch (Exception e) {
            callback.onError(e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

    public void syncUser(User user) throws Exception {
        Login loggedUser = loginRepository.findById(user.getId());
        loggedUser.setUser(user);
        loginRepository.update(user.getId(), loggedUser);
    }

    public void syncJwt(Login login) throws Exception {
        if (login != null && login.getJwt() != null && !login.getJwt().getToken().isEmpty()) {
            Config.getInstance().setLoggedUser(login);

            Login loggedUser = loginRepository.findById(login.getUser().getId());
            if (loggedUser == null) {
                loginRepository.save(login);
            } else {
                loginRepository.update(login.getUser().getId(), login);
            }
        } else {
            Config.getInstance().getLoggedUser().getJwt().setToken("");
        }
    }

    public void cleanLocalStorage() {
        loginRepository.cleanLocalStorage();
    }

    public Login getLoggedUser() throws Exception {
        List<Login> login = loginRepository.findAll();
        if (login.size() > 0) {
            return login.get(0);
        }
        return null;
    }

}
