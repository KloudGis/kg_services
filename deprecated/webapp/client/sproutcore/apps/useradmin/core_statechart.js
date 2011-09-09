SC.mixin(Useradmin, {
    statechart: Ki.Statechart.create({
        //log trace
        trace: YES,

        initialState: 'checkLoginState',

        //check logged user
        //if server failed: goto errorServerState
        //if not super user: goto notAuthState
        //if access granted: goto runningState
        checkLoginState: Ki.State.design({

            timer: null,

            enterState: function() {
                //check if logged in
                Useradmin.checkLogin();
                this.timer = SC.Timer.schedule({
                    target: this,
                    action: 'timerFired',
                    interval: 10000,
                    repeats: NO
                });
            },

            exitState: function() {
                this.timer.invalidate();
                this.timer = undefined;
            },

            timerFired: function() {
                this.gotoState('errorState');
            },

            loginDidCheck: function(sender, userDescriptor) {
                if (Useradmin.get('appLoaded')) {
                    if (userDescriptor === 'ServerFailed') {
                        //server is DOWN
                        this.gotoState('errorState');
                    } else {
                        if (SC.none(userDescriptor)) {
                            //user not logged in
                            Useradmin.restartApp();
                        } else {
                            if (userDescriptor.get('isSuperUser')) {
                                //app loaded and access granted
                                this.gotoState('runningState');
                            } else {
                                //user not auth
                                this.gotoState('notAuthState');
                            }
                        }
                    }
                } else {
                    SC.Logger.warn('App is not loaded but has to be.');
                }
            }
        }),

        //The backend server failed: show a message
        //if the user hit Try Again: come back to checkLoginState
        //if the user hit Quit: logout the app
        errorState: Useradmin.abstractErrorServerState.design({

            tryAgain: function() {
                this.gotoState('checkLoginState');
            },

            quit: function() {
                Useradmin.logout();
            }

        }),

        //the user is logged in but is not a super user
        //on close, come back to login page.
        notAuthState: Ki.State.design({

            alertController: null,

            enterState: function() {
                this.alertController = SC.Object.create({

                    parentState: this,

                    alertPaneDidDismiss: function(pane, status) {
                        Useradmin.logout();
                    }
                });

                SC.AlertPane.warn("Security Access", "You don't have the rights to access this page.", "", "Quit", this.alertController);
            },

            exitState: function() {
                this.alertController = undefined;
            }
        }),

        //Main State
        //show the main view. The app is now usable
        runningState: Ki.State.plugin('Useradmin.runningState')
    })
});
