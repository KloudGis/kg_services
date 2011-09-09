// ==========================================================================
// Project:   Kloudgis.UserDescriptor
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  @extends SC.Object
  @version 0.1
*/
Kloudgis.FeaturetypeBind = SC.Object.extend({

    featuretype: null,

    _controller: null,

    init: function() {
        sc_super();
        this.get('controller'); //create controller instance
    },

    controller: function(key, value) {
        if (SC.none(this._controller)) {
            var rtype = this.get('featuretype').get('recordType');
            if (rtype) {
				var ctrl = rtype.prototype.controllerClassName;
                if (SC.none(ctrl)) {
                    //default controller
                    this._controller = Kloudgis.abstractFeatureController.create({
                        featuretype: this.get('featuretype')
                    });
                } else {
                    var classDef = SC.objectForPropertyPath(ctrl);
                    this._controller = classDef.create({
                        featuretype: this.get('featuretype')
                    });
                }
            }
        }
        return this._controller;
    }.property('featuretype'),

    featuretypeName: function() {
        return this.get('featuretype').get('name');
    }.property('featuretype').cacheable(),

    destroy: function() {
        sc_super();
        if (!SC.none(this._controller)) {
            this._controller.destroy();
        }
    }

});
