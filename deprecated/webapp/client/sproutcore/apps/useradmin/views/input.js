//need attributeLabel and attributeName properties
Useradmin.InputView = SC.View.extend({
	childViews: 'labelView textView'.w(),
	classNames: 'input-view'.w(),
 	labelView: SC.LabelView.design({
        layout: {
            centerY: 3,
            left: 8,
            width: 100,
            height: 24
        },
		textAlign: null,
        init: function() {
            sc_super();
            this.set('value', this.parentView.get('attributeLabel'));
        }
    }),

    textView: SC.TextFieldView.design({
	
		classNames: 'input-textfield'.w(),
        layout: {
            centerY: 0,
            left: 125,
            right: 3,
            height: 24
        },
		//late commit
		applyImmediately: NO,
		
        init: function() {
            sc_super();
            this.bind('value', SC.Binding.from("Useradmin.userSelectedController." + this.parentView.get('attributeName')));
			this.set('spellCheckEnabled', this.parentView.get('spellCheckEnabled'));
        },

		willLoseKeyResponderTo: function(responder){
			sc_super();
			Useradmin.statechart.sendEvent('inputLooseFocus', this.parentView.get('attributeName'));
		}
    })
});