/** @namespace

  The initial responder state.  Always transitions to the proper state.  If
  you have an account, shows the info.  Otherwise starts the signup 
  automatically..

  @extends SC.Responder
*/
Webadmin.START = SC.Responder.create({

    didBecomeFirstResponder: function() {
        console.log('Start Responder active!');
        //var cookie = SC.Cookie.find('cookieLogin');
        //console.log('Login cookie: ' + cookie);
       // if (!this.validateCookie(cookie)) {
        //    console.log("go to login");
        ///    location.href = "../login";
       // } else {
            var state = Webadmin.WELCOME;
            Webadmin.invokeLater(Webadmin.makeFirstResponder, 1, state)
       // }
    },

});
