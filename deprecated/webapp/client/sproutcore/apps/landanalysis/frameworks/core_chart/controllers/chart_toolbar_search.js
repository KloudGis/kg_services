// ==========================================================================
// Project:   CoreChart.chartToolbarSearchController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals CoreChart */

/** @class

  (Document Your Controller Here)

  @extends CoreChart.toolbarSearchController
*/
CoreChart.chartToolbarSearchController = Kloudgis.abstractSearchController.create(
/** @scope CoreChart.chartToolbarSearchController.prototype */
{
	
	chartQuery : SC.Query.local(Kloudgis.Featuretype, {
	    conditions: "hasChart = true",
	    orderBy: 'label_loc'
	}),

    selectionDidChanged: function() {
        var select = this.getSelectedFeaturetypeWrapper();
        if (select) {
            CoreChart.chartToolbarSearchFeatureController.featuretypeActivated(select, select.get('search_string'));
            this.activateFeatures();
        }
    }.observes('selection'),

    fetchSearchCountDone: function(query, count) {
        //its not an older query from a previous search
        if (query.get('searchCounter') === this.searchCounter) {
            this.decrementProperty('queryInprogress');
            if (count > 0) {
                var rType = query.get('recordType');
                if (rType) {
					var ft = rType.prototype.get('featuretype');
					var label;
					if(!SC.none(ft)){
						label = ft.get('label_loc') + " (%@)".fmt(count);
					}else{
						label = rType.prototype.get('featuretypeName') + "(" + count + ")";
					}
                    this.addObject(SC.Object.create({
                        recordType: rType,
                        search_string: query.get('search_string'),
                        count: count,
                        label_loc: label
                    }));
                }
                this.activateFeaturetypes();
                var pickView = Kloudgis.activeRoute.currentPagePane.page.get('chartSearchPickerView');
                if (pickView && !this.get('pickerShowing')) {
                    pickView.showPicker();
                    this.set('pickerShowing', YES);
                }
            }
            if (this.queryInprogress === 0) {
                this.activateProgress(NO);
            }
        }
    },

	_getFeaturetypes: function(){
		var fts = Kloudgis.store.find(Kloudgis.FEATURETYPE_LIST);
		return fts.find(this.chartQuery);
	},
});
