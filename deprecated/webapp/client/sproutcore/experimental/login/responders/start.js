Login.START = SC.Responder.create({

    didBecomeFirstResponder: function() {
        var loginPane = Login.getPath('mainPage.loginPane');
        loginPane.append();
		loginPane.makeFirstResponder(loginPane.contentView.username.field); // focus first field
    },

    login: function() {
        Login.loginController.login(this, 'loginResponseReceived');
        Login.loginController.set('loggingIn', YES);
    },

    loginResponseReceived: function(success, user) {
        var pane;
        Login.loginController.set('loggingIn', NO);
        if (success) {
            pane = Login.getPath('mainPage.loginPane');
            pane.remove();
            /*var cook = SC.Cookie.create({
                name: "cookieLogin",
                value: sessionId,
				path: '/',
				secure: YES,
            });
            cook.write();*/
			//redirect
			location.href="../webadmin";
        } else {
            Login.loginController.set('errorMessage', Login.loginController.BAD_CREDENTIALS_ERROR.loc());
        }
    }
});
