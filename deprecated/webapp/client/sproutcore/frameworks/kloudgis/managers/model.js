
// ==========================================================================
// Project:   Kloudgis.modelManager
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Manage featuretypes bindings object which contains the FeatureType Record and its controller

  @extends Kloudgis.abstractManager
*/
sc_require('managers/abstract')
Kloudgis.modelManager = Kloudgis.abstractManager.create({
	
	featuretype_bindings: SC.ArrayController.create({}),
	featuretypesLoaded: NO,
	
	getFeaturetype: function(ftname){
		var array = this.featuretype_bindings;
		var len = array.get('length');
		var i;
		for(i=0; i < len; i++){
			var bind = array.objectAt(i);
			if(bind.get('featuretypeName') === ftname){
				return bind.get('featuretype');
			}
		}
	},	
	
	getController: function(ftname){
		var array = this.featuretype_bindings;
		var len = array.get('length');
		var i;
		for(i=0; i < len; i++){
			var bind = array.objectAt(i);
			if(bind.get('featuretypeName') === ftname){
				return bind.get('controller');
			}
		}
	},
	
	loadModel: function(){		
		this.loadFeaturetypes();
		this.loadAttrtypes();
	},
	
	loadFeaturetypes : function() {
		this.set('featuretypesLoaded', NO);
		var ftRecordArray = Kloudgis.store.find(Kloudgis.FEATURETYPE_LIST);
		if(ftRecordArray.get('status') & SC.Record.BUSY){
			ftRecordArray.addObserver('status', this, this.loadCallback, {});
		}else{
			var array = ftRecordArray.refresh();
			array.addObserver('status', this, this.loadCallback, {});
		}
	},
	
	loadCallback: function(recordArray){
		if(recordArray.get('status') & SC.Record.READY){
			recordArray.removeObserver('status', this, this.loadCallback);
			this.doLoad(recordArray);		
		}else if(recordArray.get('status') & SC.Record.BUSY){
			//still busy
		}else{
			//errors
			console.log('Error, cannot load featuretypes!');
			recordArray.removeObserver('status', this, this.loadCallback);
		}
	},
	
	doLoad : function(recordArray) {
		var len = recordArray.get('length');
		console.log('Loading %@ featuretypes!'.fmt(len));
		var i;
		var newList = [];
		for(i=0; i < len; i++){
			newList.push(Kloudgis.FeaturetypeBind.create({featuretype: recordArray.objectAt(i)}));
		}
		this.featuretype_bindings.set('content', newList);
		this.set('featuretypesLoaded', YES);
	},
	
	loadAttrtypes: function(){
		Kloudgis.store.find(Kloudgis.ATTRTYPE_LIST);
	},
	
	onFeaturetypesLoaded: function(callback, callbackMethod, params){
		if(this.get('featuretypesLoaded')){
			callbackMethod.call(callback, params);
		}else{
			this.addObserver('featuretypesLoaded', this, this.onFeaturetypesLoadedCallback, {callback: callback, callbackMethod: callbackMethod, params: params});
		}
	},
	
	onFeaturetypesLoadedCallback: function(manager, key, nothing, context){
		this.removeObserver('featuretypesLoaded', this, this.onFeaturetypeLoadedCallback, context);
		context.callbackMethod.call(context.callback, context.params);
	}
		
});