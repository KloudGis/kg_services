// ==========================================================================
// Project:   Landanalysis.noteController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Landanalysis */

/** @class

  (Document Your Controller Here)

  @extends Kloudgis.abstractFeatureController
*/
Landanalysis.noteController = Kloudgis.abstractFeatureController.extend(
/** @scope Landanalysis.noteController.prototype */
{

    handlePointAndLineCreation: function() {
		Kloudgis.mapControlController.set('editToolHandler', this);
    },

    editHandlerCreation: function(lonlatWkt, ol_feature) {
        var note = Landanalysis.store.createRecord(Landanalysis.Note, {
            newgeowkt: lonlatWkt,
        });
		this.addFeatures(note);
        Kloudgis.inspectorController.featureSelected(note);
        this.geoModFeature.push({
            storekey: note.get('storeKey'),
            tablename: note.get('table_name')
        });
    },

    editHandlerEdition: function(lonlatWkt, ol_feature) {
        var kfeature = this.getFeature(ol_feature);
        if (kfeature) {
            //cannot update the feature if its still in the loading status.
            //add an observer to be triggered when the status changed and then modify the feature
            if (kfeature.get('status') & SC.Record.BUSY) {
                kfeature.addObserver('status', this, this.doUpdateGeoObserver);
                this.tempFeatureStore.push({
                    feature: kfeature,
                    geo: lonlatWkt
                })
            } else {
                this.doUpdateGeo(kfeature, lonlatWkt);
            }
            this.geoModFeature.push({
                storekey: kfeature.get('storeKey'),
                tablename: kfeature.get('table_name')
            });
        }
    },

	editHandlerEditable: function(ol_feature){
		if(ol_feature){
			var ft  = this.get('featuretype');
			if(ft){
				if(ol_feature.gml){
					if(ol_feature.gml.featureType === ft.get('table_name')){
						return YES;
					}
				}else if(ol_feature.fid){
					var ftN = ol_feature.fid.split('.')[0];
					if(ftN === ft.get('table_name')){
						return YES;
					}
				}				
			}
		}
		return NO;
	},
	
	editHandlerGetActiveFeature: function(){
		return this.activeFeature();
	},

    doUpdateGeo: function(kfeature, lonlatWkt) {
        kfeature.set('newgeowkt', lonlatWkt);
		Kloudgis.store.commitRecords();
    },

    doUpdateGeoObserver: function() {
        var notYetReady = [];
        var ready = [];
        var len = this.tempFeatureStore.length;
        var i;
        for (i = 0; i < len; i++) {
            var tFeature = this.tempFeatureStore[i];
            if (tFeature.feature.get('status') & SC.Record.BUSY) {
                notYetReady.push(tFeature);
            } else {
                ready.push(tFeature);
            }
        }
        this.tempFeatureStore = notYetReady;
        len = ready.length;
        for (i = 0; i < len; i++) {
            var tFeature = ready[i];
            tFeature.feature.removeObserver('status', this, this.doUpdateGeoObserver);
            this.doUpdateGeo(tFeature.feature, tFeature.geo);
        }
    }
});
