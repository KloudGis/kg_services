// ==========================================================================
// Project:   Kloudgis.toolbarController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends SC.Object
*/
sc_require('views/progress_loop')
Kloudgis.toolbarController = SC.ObjectController.create(
/** @scope Kloudgis.toolbarController.prototype */ {

	sheet: null,

	fullIndex: function() {
		var url = Kloudgis.context + '/resources/protected/admin/search/indexall'
		SC.Request.getUrl(url).json().notify(this, 'fullIndexCallback').send();
    },

	fullIndexCallback: function(response){
		var message;
		var icon;
		if (SC.ok(response)) {
			message = "L'indexation est terminee.";
			icon = "sc-icon-info-24";
		}else{
			message = "Erreur lors de l'indexation.";
			icon = "sc-icon-cancel-24";
		}
		this.closeSheet();
		this.sheet = SC.SheetPane.create({
            layout: {
                width: 300,
                height: 100,
                centerX: 0,
            },
            contentView: SC.View.extend({
                layout: {
                    top: 0,
                    left: 0,
                    bottom: 0,
                    right: 0
                },
                childViews: 'iconAlertView labelView okButtonView'.w(),

                iconAlertView: SC.ImageView.extend({
                    layout: {
                        centerY: -10,
                        height: 24,
                        left: 20,
                        width: 24
                    },
                    value: icon,
                }),

                labelView: SC.LabelView.extend({
                    layout: {
                        centerY: 0,
                        height: 40,
                        left: 60,
                        right: 0
                    },
                    textAlign: SC.ALIGN_CENTER,
                    classNames: 'messages'.w(),
                    value: message
                }),

                okButtonView: SC.ButtonView.extend({
                    layout: {
                        width: 80,
                        bottom: 20,
                        height: 24,
                        centerX: 0
                    },
                    title: 'Ok',
                    isDefault: YES,
                    isCancel: YES,
                    action: "closeSheet",
                    target: this
                })
            })
        });
        this.sheet.append();
	},
	
	closeSheet: function(){
		if(this.sheet){
			this.sheet.remove();
			this.sheet.destroy();
			this.sheet = null;
		}
	}

}) ;
