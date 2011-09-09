SC.mixin(Useradmin, {

    checkLogin: function() {
        this.fetchLoggedUser(this, this.didFetchLogin);
    },

    didFetchLogin: function(userDescriptor) {
        Useradmin.statechart.sendEvent('loginDidCheck', this, userDescriptor);
    },

    fetchLoggedUser: function(callback, callbackMethod) {
        SC.Request.getUrl(Useradmin.context_server + "/resources/protected/login/user").json().notify(this, this.didFetchLoggedUser, callback, callbackMethod).send();
    },

    didFetchLoggedUser: function(response, callback, callbackMethod) {
        SC.Logger.warn(response.get('body'));
        if (SC.ok(response)) {
            var body = response.get('body');
            if (SC.kindOf(body, SC.Error)) {
                Useradmin.logout();
            } else {
                if (SC.none(body) || body == '0' || body === '') {
                    callbackMethod.call(callback, null);
                } else {
                    var hash = response.get('body');
                    var user = Useradmin.User.create(hash);
                    callbackMethod.call(callback, user);
                }
            }
        } else {
            callbackMethod.call(callback, 'ServerFailed');
        }
    },

    restartApp: function() {
        this.doGotoLoginPage(NO);
    },

    logout: function() {
        SC.Request.getUrl(Useradmin.context_server + "/resources/unprotected/login/logout").json().notify(this, this.didLogout).send();
    },

    didLogout: function(response, ignoreApp) {
        if (SC.ok(response)) {
            this.doGotoLoginPage(YES);
        } else {
            SC.Logger.warn('error logging out:' + response);
        }
    },

    doGotoLoginPage: function(ignoreApp) {
        SC.Logger.info('To login page');
        var app;
        if (ignoreApp) {
            app = "?app=" + Useradmin.context_client;
        } else {
            app = '';
        }
        if (SC.buildMode === 'debug') {
            window.location = 'http://localhost:8080' + Useradmin.context_server + '/protected/welcome_dev.html' + app;
        } else {
            window.location.href = Useradmin.context_server + '/protected/welcome.html' + app;
        }
    }
});
