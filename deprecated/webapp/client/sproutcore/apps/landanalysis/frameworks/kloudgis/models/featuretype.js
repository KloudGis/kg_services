// ==========================================================================
// Project:   Kloudgis.Featuretype
// Copyright: ©2010 My Company, Inc.
// ==========================================================================
/*globals Kloudgis */

/** @class

  Model class for the Featuretypes. Joined on the AttrType.

  @extends SC.Record
  @version 0.1
*/

Kloudgis.Featuretype = SC.Record.extend(
/** @scope Kloudgis.Featuretype.prototype */
 {

    name: SC.Record.attr(String),
    class_name: SC.Record.attr(String),
    label: SC.Record.attr(String),
    hasGeometry: SC.Record.attr(Boolean),
    searchable: SC.Record.attr(Boolean),
    hasChart: SC.Record.attr(Boolean),
    selectionable: SC.Record.attr(Boolean),
    idAttribute: SC.Record.attr(String),
    table_name: SC.Record.attr(String),
    //attribute to modify the selection priority (by value)
    prioritySelectionAttr: SC.Record.attr(String),

    attrtypes: SC.Record.toMany("Kloudgis.Attrtype", {
        isMaster: YES
    }),

    label_loc: function() {
        return this.get('label').loc();
    }.property('label'),

    recordType: function() {
        var cName = this.get('class_name');
        if (cName && cName.length > 0) {
            return SC.objectForPropertyPath(cName);
        }
        return NO;
    }.property('class_name').cacheable(),

    getAttrType: function(name) {
        var attributes = this.get('attrtypes');
        var len = attributes.get('length');
        var i;
        for (i = 0; i < len; i++) {
            var wait = NO;
            var attribute = attributes.objectAt(i);
            var atName = attribute.get('name')
            if (atName === name) {
                return attribute;
            }
        }
    },

    isVisibleAttr: function(attr) {
        var attribute = this.getAttrType(attr);
        if (attribute) {
            return attribute.get('visible');
        }
        return YES;
        //default
    },

    isEditableAttr: function(attr) {
        var attribute = this.getAttrType(attr);
        if (attribute) {
            return attribute.get('editable');
        }
        return YES;
        //default
    },

    getLabelAttr: function(attr) {
        var attribute = this.getAttrType(attr);
        if (attribute) {
            return attribute.get('label_loc');
        }
        return attr;
        //default
    },

    getHintAttr: function(attr) {
        var attribute = this.getAttrType(attr);
        if (attribute) {
            return attribute.get('hint_loc');
        }
        return attr;
        //default
    },

    isEditable: function() {
        return this.get('isLayerEditVisibility') && Kloudgis.layerController.hasLayer(this.get('table_name'));
    }.property('isLayerEditVisibility'),

    isLayerEditVisibility: NO,

    isVisibleLayer: function(key, value) {
        // setter
        if (value !== undefined) {
            this.setVisibleLayer(value);
        }
        return Kloudgis.layerController.isVisibleLayer(this.get('table_name'));
    }.property(),

    setVisibleLayer: function(vis) {
        Kloudgis.layerController.setVisibleLayer(this.get('table_name'), vis);
        this.notifyPropertyChange('isVisibleLayer', vis);
    }

});
