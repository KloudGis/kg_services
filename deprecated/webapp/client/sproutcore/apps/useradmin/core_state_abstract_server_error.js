Useradmin.abstractErrorServerState = Ki.State.extend({

    alertController: null,

    enterState: function() {
        this.alertController = SC.Object.create({

            parentState: this,

            alertPaneDidDismiss: function(pane, status) {
                switch (status) {
                case SC.BUTTON1_STATUS:
                    Useradmin.statechart.sendEvent('tryAgain');
                    break;

                case SC.BUTTON2_STATUS:
                    Useradmin.statechart.sendEvent('quit');
                    break;
                }
            }
        });

        SC.AlertPane.warn("Connection cannot be established", "Your internet connection may be unavailable or our servers may be down.", "Try again in a few minutes.", "Try Again", "Quit", this.alertController);
    },

    exitState: function() {
        this.alertController = undefined;
    },

	//to override
	tryAgain: function(){		
	},
	
	//to override
	quit: function(){
	}

});

