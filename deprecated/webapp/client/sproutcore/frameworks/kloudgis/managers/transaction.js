// ==========================================================================
// Project:   Kloudgis.transactionManager
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends Kloudgis.abstractManager
*/
sc_require('managers/abstract')
Kloudgis.transactionManager = Kloudgis.abstractManager.create(
/** @scope Kloudgis.transactionManager.prototype */ {
	
	listeners: [],
	
	addListener: function(listener){
		this.listeners.push(listener);
	},
	
	removeListener: function(listener){
		this.listeners.remove(listener);
	},
	
	fireListeners: function(methodName, featuretype, storekey){
		var len= this.listeners.length;
		var i=0;
		for(i=0; i < len; i++){
			var method = this.listeners[i].get(methodName);
			if(method){
				method.call(this.listeners[i], featuretype,storekey);
			}			
		}
	},
	
	featureInserted: function(featuretype, storekey){
		this.fireListeners('featureInserted', featuretype, storekey);
	},
	
	featureUpdated: function(featuretype, storekey){
		this.fireListeners('featureUpdated', featuretype, storekey);
	},
	
	featureDeleted: function(featuretype, storekey){
		this.fireListeners('featureDeleted', featuretype, storekey);
	}

}) ;
