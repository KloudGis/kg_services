// ==========================================================================
// Project:   Login.loginController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Login */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
Login.loginController = SC.Object.create(
/** @scope Login.loginController.prototype */
{
    loggingIn: NO,
    errorMessage: '',
    BAD_CREDENTIALS_ERROR: "_Sorry, wrong username or password.  Try again?",

    login: function(target, action) {
        SC.Request.getUrl('/webserver/resources/protected/opensession').json().notify(401, this, 'unauthorized').notify(this, 'verifyLogin', target, action).header('Authorization', this.get('authData')).send();
    },

    verifyLogin: function(response, target, action) {
        var sessionId;
        var success = NO;
		console.log('response received!');
        if (SC.ok(response)) {
            //var response = response.get('body');
            //if (response) {
            //    sessionId = response.sessionId;
				console.log(response.rawRequest.responseText);
				//var theForm = ""
				//SC.Request.postUrl('/webserver/j_security_check', theForm).notify(401, this, 'unauthorized').notify(this, 'verifyLogin2', target, action).send();
				//this.post_to_url('/webserver/j_security_check', {j_username:'jeff',j_password:'toto'}, 'POST');
				//SC.Request.getUrl('/webserver/resources/protected/opensession').json().notify(401, this, 'unauthorized').notify(this, 'verifyLogin2', target, action).send();             
				location.href='/webserver/protected/welcome.jsp';
//success = YES;
            //}
        } else {
            //other errors
            console.log("Error: " + response.get('status'));
        }
        if (action) {
            if (SC.typeOf(action) === SC.T_STRING) action = target[action];
            action.apply(target, [success, this.get('username')]);
        }
    },

	post_to_url: function (path, params, method) {
	    method = method || "post"; // Set method to post by default, if not specified.

	    // The rest of this code assumes you are not using a library.
	    // It can be made less wordy if you use one.
	    var form = document.createElement("form");
	    form.setAttribute("method", method);
	    form.setAttribute("action", path);

	    for(var key in params) {
	        var hiddenField = document.createElement("input");
	        hiddenField.setAttribute("type", "hidden");
	        hiddenField.setAttribute("name", key);
	        hiddenField.setAttribute("value", params[key]);
	        form.appendChild(hiddenField);
	    }
		console.log('submit:');
		console.log(form);
		SC.Request.postUrl('/webserver/j_security_check').notify(this, 'verifyLogin2').send('<form method="POST" action="/webserver/j_security_check"><input type="hidden" name="j_username" value="jeff"><input type="hidden" name="j_password" value="toto"></form>');             
	    //document.body.appendChild(form);    // Not entirely sure if this is necessary
	    //form.submit();
	},

	verifyLogin2: function(response, target, action) {
		console.log(response.rawRequest.responseText);
		console.log("2");
	},

    unauthorized: function(response) {
        console.log("Bad user name / password");
    },

    authData: function() {
        var username = this.get('username'),
        password = this.get('password');
        return username + ":" + password;
    }.property('username', 'password').cacheable(),

});
