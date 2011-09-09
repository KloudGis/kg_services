// ==========================================================================
// Project:   Kloudgis.abstractFeatureController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

 	super class for the inspector feature controllers.

  @extends SC.Object
*/
sc_require('models/featuretype')
 sc_require('managers/transaction')
 sc_require('views/progress_loop')
 Kloudgis.abstractFeatureController = SC.ArrayController.extend(
/** @scope Kloudgis.abstractFeatureController.prototype */
 {
	featuretype: null,
	
    allowsMultipleSelection: NO,
    //active feature wrapped in an object controller
    selectedFeatureCtrl: null,
    checkSecurity: YES,
    //storekey and tablename cache to look for when the callback happen to repaint the map
    geoModFeature: [],
    //feature info cache when the feature is BUSY and therefore cannot be modified
    tempFeatureStore: [],

    //property to force check security on the UI
    init: function() {
        sc_super();
        this.selectedFeatureCtrl = SC.ObjectController.create({});
        Kloudgis.transactionManager.addListener(this);
    },

    lengthDidChanged: function() {
        var activeFea = this.get('activeFeature');
        if (!activeFea && this.get('length') > 0) {
            var sel = this.get('firstObject');
            if (sel) {
                this.selectObject(sel);
            }
        }
        //	this.notifyPropertyChange('checkSecurity');
    }.observes('length'),

    featuresSelected: function(features) {
        this.set('content', features);
        var sel = features.get('firstObject');
        if (sel) {
            this.selectObject(sel);
        }
    },

    addFeatures: function(features) {
        var content = this.get('content');
        var array = [];
        if (content) {
            var len = content.get('length');
            var i;
            for (i = 0; i < len; i++) {
                var fea = content.objectAt(i);
                array.push(fea);
            }
        }
        var len = features.get('length');
        var i;
        for (i = 0; i < len; i++) {
            var fea = features.objectAt(i);
            if (array.indexOf(fea) === -1) {
                array.push(fea);
            }
        }
        this.set('content', array);
        //old + new features
        var sel = features.get('firstObject');
        //activate the first new feature
        if (sel) {
            this.selectObject(sel);
        }
    },

    removeFeatureByIds: function(featureIds) {
        var len = featureIds.get('length');
        var i;
        for (i = 0; i < len; i++) {
            var lenC = this.get('length');
            var j = 0;
            for (j = 0; j < len; j++) {
                var fea = this.objectAt(j);
                if (fea.get('id') === featureIds.objectAt(i)) {
                    this.removeAt(j);
                    break;
                }
            }
        }
    },

    removeFeatures: function(features) {
        var content = this.get('content');
        if (content) {
            content.removeObjects(features);
        }
    },

    selectionDidChanged: function() {
        var activeFea = this.get('activeFeature');
        if (activeFea) {
            if (activeFea.get('status') & SC.Record.READY) {
                activeFea.refresh();
                //reload  from the backend server
            }
        }
        this.selectedFeatureCtrl.set('content', activeFea);
        this.featureActivated(activeFea);
    }.observes('selection'),

    //to override for deeper cleanup
    clearSelection: function() {
        this.set('content', null);
    },

    //to override
    featureActivated: function(feature) {},

    activeFeature: function() {
        var _sel = this.get('selection');
        if (_sel) {
            var _fea = _sel.get('firstObject');
            return _fea;
        }
        return NO;
    }.property('selection').cacheable(),

    countLabel: function() {
        if (this.get('length') > 1) {
            var active = this.activeFeature();
            var index = -1;
            if (active) {
                index = this.indexOf(active);
            }
            return "_of".loc(index + 1, this.get('length'));
        } else {
            return '';
        }
    }.property('selection', 'length', 'content'),

    needPages: function() {
        return this.get('length') > 1;
    }.property('selection', 'length', 'content'),

    isVisibleAttr: function(attr) {
        if (this.get('featuretype')) {
            return this.get('featuretype').isVisibleAttr(attr);
        }
        return YES;
    },

    isEditableAttr: function(attr) {
        if (this.get('featuretype')) {
            return this.get('featuretype').isEditableAttr(attr);
        }
        return YES;
    },

    getLabelAttr: function(attr) {
        if (this.get('featuretype')) {
            return this.get('featuretype').getLabelAttr(attr);
        }
        return attr;
    },

    getHintAttr: function(attr) {
        if (this.get('featuretype')) {
            return this.get('featuretype').getHintAttr(attr);
        }
        return attr;
    },

    previousFeature: function() {	
        var active = this.activeFeature();
        var index = 0;
        if (active) {
            index = this.indexOf(active);
            if (index <= 0) {
                index = 0;
            } else {
                index = index - 1;
            }
        }
        var prev = this.objectAt(index);
        if (prev && prev != active) {
			Kloudgis.inspectorController.commitChanges();
            this.selectObject(prev);
        }
    },

    nextFeature: function() {
		
        var active = this.activeFeature();
        var index = 0;
        if (active) {
            index = this.indexOf(active);
            if (index >= (this.get('length') - 1)) {
                index = this.get('length') - 1;
            } else {
                index = index + 1;
            }
        }
        var next = this.objectAt(index);
        if (next && next !== active) {
			Kloudgis.inspectorController.commitChanges();
            this.selectObject(next);
        }
    },

    zoomFeature: function() {
        var active = this.activeFeature();
        if (active) {
            this.zoomExtent(active.get('boundsLonLat'));
        }
    },

    zoomExtent: function(bounds) {
        if (bounds) {
            Kloudgis.mapController.zoomExtentLonLat(bounds);
        }
    },

    //delete the active feature
    deleteFeature: function() {
        var active = this.activeFeature();
        if (active) {
            this.showConfirmPane(active);
        }
    },

    //perform the delete for the feature
    doDeleteFeature: function(feature) {
        if (feature) {
            this.geoModFeature.push({
                storekey: feature.get('storeKey'),
                featuretype: this.get('featuretype')
            });
			var len = this.get('length');
			if(len > 1){
				var ind = this.indexOf(feature);
				if(ind > 0){
					this.selectObject(this.objectAt(ind-1));
				}else{
					this.selectObject(this.objectAt(ind+1));
				}
			}
			feature.destroy();
            this.removeObject(feature);          
            Kloudgis.store.commitRecords();
        }
    },

    //confirm dialog for DELETE
    showConfirmPane: function(feature) {
        var sheet = SC.SheetPane.create({
            layout: {
                width: 400,
                height: 130,
                centerX: 0,
            },
            contentView: SC.View.extend({
                layout: {
                    top: 0,
                    left: 0,
                    bottom: 0,
                    right: 0
                },
                childViews: 'iconAlertView labelView yesButtonView noButtonView'.w(),
                iconAlertView: SC.ImageView.extend({
                    layout: {
                        centerY: -10,
                        height: 48,
                        left: 20,
                        width: 48
                    },
                    value: "sc-icon-alert-48",
                }),
                labelView: SC.LabelView.extend({
                    layout: {
                        centerY: 0,
                        height: 40,
                        left: 80,
                        right: 10
                    },
                    textAlign: SC.ALIGN_LEFT,
                    classNames: 'messages'.w(),
                    value: "_confirmDelete".loc(feature.get('label')),
                }),
                yesButtonView: SC.ButtonView.extend({
                    layout: {
                        width: 100,
                        bottom: 20,
                        height: 24,
                        centerX: -50
                    },
                    title: '_delete'.loc(),
                    feature: feature,
                    controller: this,

                    action: function() {
                        this.controller.doDeleteFeature(this.feature);
                        this.parentView.parentView.remove();
                    }
                }),
                noButtonView: SC.ButtonView.extend({
                    layout: {
                        width: 80,
                        bottom: 20,
                        height: 24,
                        centerX: 50
                    },
                    title: '_cancel'.loc(),
                    isDefault: YES,
                    isCancel: YES,
                    action: function() {
                        this.parentView.parentView.remove();
                    }
                })
            })
        });
        sheet.append();
    },

    //get a Kloudgis feature out of openlayers feature.
    getFeature: function(ol_feature) {
        var ft = this.get('featuretype');
        if (ft) {
            var idAttr = ft.get('idAttribute');
            if (SC.none(idAttr)) {
                idAttr = 'id';
            }
            var id = ol_feature.attributes[idAttr];
            if (SC.none(id)) {
                id = ol_feature.fid.split('.')[1];
            }
            var rType = ft.get('recordType');
            if (!SC.none(rType)) {
                return Kloudgis.store.find(rType, id);
            }
        }
        return NO;
    },

    //transaction callback - server side complete INSERT
    featureInserted: function(featuretype, storekey) {
        if (featuretype === this.get('featuretype')) {
            var index = this.indexOfStoreKey(storekey);
            if (index != -1) {
                var modInfo = this.geoModFeature.objectAt(index);
                this.geoModFeature.removeAt(index);
                this.refreshLayer(modInfo);
            }
        }
    },

    //transaction callback - server side complete UPDATE
    featureUpdated: function(featuretype, storekey) {
        if (featuretype === this.get('featuretype')) {
            var index = this.indexOfStoreKey(storekey);
            if (index != -1) {
                var modInfo = this.geoModFeature.objectAt(index);
                this.geoModFeature.removeAt(index);
                this.refreshLayer(modInfo);
            }
        }
    },

    //transaction callback - server side complete DELETE
    featureDeleted: function(featuretype, storekey) {
        if (featuretype === this.get('featuretype')) {
            var index = this.indexOfStoreKey(storekey);
            if (index != -1) {
                var modInfo = this.geoModFeature.objectAt(index);
                this.geoModFeature.removeAt(index);
                this.refreshLayer(modInfo);
            }
        }
    },

    //look into the array of modifications to match the storekey
    indexOfStoreKey: function(storekey) {
        var len = this.geoModFeature.get('length');
        var i = 0;
        for (i = 0; i < len; i++) {
            if (this.geoModFeature.objectAt(i).storekey === storekey) {
                return i;
            }
        }
        return - 1;
    },

    //ask the layer controller to refresh the layer that has been modified.
    //modInfo contains the table name or the featuretype to refresh
    refreshLayer: function(modInfo) {
        if (!SC.none(modInfo.tablename)) {
            Kloudgis.layerController.refreshLayerByTableName(modInfo.tablename);
        } else if (!SC.none(modInfo.featuretype)) {
            Kloudgis.layerController.refreshLayerByFeaturetype(modInfo.featuretype);
        }
		Kloudgis.mapController.refreshSelectionHighlight();
    }

});
