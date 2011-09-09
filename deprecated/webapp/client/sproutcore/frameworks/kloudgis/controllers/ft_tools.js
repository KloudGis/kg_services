// ==========================================================================
// Project:   Kloudgis.ftToolsController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Controller for the picker panel to show the featuretypes and features

  @extends SC.Object
*/
Kloudgis.ftToolsController = SC.ObjectController.create(
/** @scope Kloudgis.ftToolsController.prototype */
 {

//key name for possible scene view
    FT_VIEW: 'ftView',
    FEATURE_VIEW: 'featureView',

//YES if the picker is visible
    activated: NO,
//the visible scene
    activeScene: null,
//the label to show for the scene
    activeLabel: "",

//value for bindings - VISIBILITY
    labelVisible: YES,
    progressVisible: NO,
    btnLeftVisible: NO,
    btnRightVisible: YES,
	btnRightValue: NO,

    //*** ACTIVE STATE OF THE PICKER
    activatedDidChanged: function() {
        console.log('activated did changed!');
		this.set('btnRightValue', NO);
		Kloudgis.featuretypeController.editVisibility(NO);
        var toolView = Kloudgis.activeRoute.currentPagePane.page.get('featurePickerView');		
        if (this.activated) {
            if (!this.activeScene) {
                this.activateFeaturetypes();
            }
			if(toolView){
            	toolView.showPicker();
			}else{
				this.set('activated', NO);
			}
        } else {
			if(toolView){
            	toolView.remove();
			}
        }
    }.observes('activated'),

//make the picker visible or not
    toggleActivated: function() {
        this.set('activated', !this.activated);
    },


    buttonLeftPressed: function() {
        if (this.activeScene === this.FEATURE_VIEW) {
            this.activateFeaturetypes();
        }
    },

	buttonRightPressed: function() {
        if (this.activeScene === this.FT_VIEW) {
            Kloudgis.featuretypeController.editVisibility(this.get('btnRightValue'));
        }else{
			Kloudgis.featuretypeController.editVisibility(NO);
		}
    }.observes('btnRightValue'),

    //*** SCENES
    activateFeatures: function() {
        //console.log('activate FEATURES!');
        this.set('activeScene', this.FEATURE_VIEW);
    },

    activateFeaturetypes: function() {
        // console.log('activate FT!');
        this.set('activeScene', this.FT_VIEW);
        this.activateProgress(NO);
    },

    activeSceneDidChanged: function() {
		//clear temporary highlight on the map
		Kloudgis.mapController.clearTempHighlight();  
		this.set('btnRightValue', NO);
        //console.log('Active scene changed to:' + this.get('activeScene'));
        if (this.activeScene === this.FT_VIEW) {			
            this.set('activeLabel', "_layers".loc());
			this.set('labelVisible', YES);
            this.set('btnLeftVisible', NO);
			this.set('btnRightVisible', YES);
        } else if (this.activeScene === this.FEATURE_VIEW) {
            var ft = Kloudgis.featuretypeController.getSelectedFeaturetype();
            var label = "";
            if (!SC.none(ft)) {
                label = ft.get('label_loc');
            }
            this.set('activeLabel', label);
			this.set('labelVisible', YES);
            this.set('btnLeftVisible', YES);
			this.set('btnRightVisible', NO);
        } 
    }.observes('activeScene'),
	
    //**** Progress
    activateProgress: function(bStart) {
        this.set('progressVisible', bStart);
    }

});
