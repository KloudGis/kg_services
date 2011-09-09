// ==========================================================================
// Project:   Kloudgis.toolbarSearchController
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  (Document Your Controller Here)

  @extends Kloudgis.abstractSearchController
*/
sc_require('controllers/abstract_search')
Kloudgis.toolbarSearchController = Kloudgis.abstractSearchController.create(
/** @scope Kloudgis.toolbarSearchController.prototype */
{

    selectionDidChanged: function() {
        var select = this.getSelectedFeaturetypeWrapper();
        if (select) {
            Kloudgis.toolbarSearchFeatureController.featuretypeActivated(select, select.get('search_string'));
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
                    this.showResults();
                }
            }
            if (this.queryInprogress === 0) {
                this.activateProgress(NO);
            }
        }
    },

    showResults: function() {
        this.activateFeaturetypes();
        var pickView = Kloudgis.activeRoute.currentPagePane.page.get('searchPickerView');
        if (pickView && !this.get('pickerShowing')) {
            pickView.showPicker();
            this.set('pickerShowing', YES);
        }
    },

});
