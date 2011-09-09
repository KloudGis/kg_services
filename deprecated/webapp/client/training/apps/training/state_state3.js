
Training.state3 = Ki.State.extend({ 
	
	enterState: function(){
		SC.Logger.warn('*** ENTER state3!');
	},
  
	gotoState1: function(sender) {
        this.gotoHistoryState('state1');//keep history on state1 (ie state11 or state12)
    }
	
});