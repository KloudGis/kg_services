Useradmin.ToolbarButtonView = SC.ButtonView.extend({

    classNames: 'toolbar-button-view'.w(),
    hasIcon: YES,
    titleMinWidth: 0,

    _iconNormal: undefined,
    iconSelected: undefined,

    isSelectedChanged: function() {
        if (this.get('isSelected')) {
            if (this.get('iconSelected') !== undefined) {
				if(this._iconNormal === undefined){
                	this._iconNormal = this.get('icon');
				}
                this.set('icon', this.get('iconSelected'));
            }
        } else {
            if (this._iconNormal !== undefined) {
                this.set('icon', this._iconNormal);
            }
        }
    }.observes('isSelected'),


    mouseEntered: function(evt) {
        sc_super();
        if (!this.get('isSelected') && this.get('iconSelected') !== undefined && this.get('icon') !== this.get('iconSelected')) {
            this._iconNormal = this.get('icon');
            this.set('icon', this.get('iconSelected'));
        }
    },

	mouseExited: function(evt) {
		sc_super();
		if (!this.get('isSelected') && this.get('icon') === this.get('iconSelected')) {
            this.set('icon', this._iconNormal);
        }
	}

});
