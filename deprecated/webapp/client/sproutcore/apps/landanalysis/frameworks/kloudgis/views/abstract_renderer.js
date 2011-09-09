// ==========================================================================
// Project:   Kloudgis.AbstractRendererView
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your View Here)

  @extends SC.View
*/
Kloudgis.AbstractRendererView = SC.View.extend(
/** @scope Kloudgis.AbstractRendererView.prototype */ {

	classNames: ['inspector-renderer'],
	
	featuretypeName: null,
	controller: function(){
		var ftname = this.get('featuretypeName');
		return Kloudgis.modelManager.getController(ftname);
	}.property('featuretypeName').cacheable(),
	
	init: function() {
		sc_super();
		var ftname = this.get('featuretypeName');
		var ctrl = Kloudgis.modelManager.getController(ftname);
		ctrl.addObserver('checkSecurity', this, this.updateSecurity);//force re-check
		ctrl.addObserver('selectedFeatureCtrl.content', this, this.updateSecurity); //feature changed
		this.updateSecurity();
	},
	
	destroy: function(){
		sc_super();
		var ftname = this.get('featuretypeName');
		var ctrl = Kloudgis.modelManager.getController(ftname);
		ctrl.removeObserver('checkSecurity', this, this.updateSecurity);
		ctrl.removeObserver('selectedFeatureCtrl.content', this, this.updateSecurity);
	},
	
	
	//to override
	updateSecurity: function(){
		//console.log('Security check');
	},

	testVisible: function(){	
		var ftname = this.get('featuretypeName');
		var ctrl = Kloudgis.modelManager.getController(ftname);		
		if(ctrl && ctrl.getPath('selectedFeatureCtrl.content')) {
			var attr = this.get('attribute');
			if(attr){
				var vis = ctrl.isVisibleAttr(attr);
				return vis;
			}
			return YES;
		}else{
			return YES;
		}				
	},
	
	testEnabled : function(){
		var ftname = this.get('featuretypeName');
		var ctrl = Kloudgis.modelManager.getController(ftname);
		if(ctrl && ctrl.getPath('selectedFeatureCtrl.content')) {
			var attr = this.get('attribute');
			if(attr){
				var en = ctrl.isEditableAttr(attr);
				return en;
			}
			return YES;
		}else{
			return NO;
		}
	},

});
